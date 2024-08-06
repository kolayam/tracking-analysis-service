package eu.nimble.service.trackingAnalysis.impl.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nimble.service.trackingAnalysis.impl.dao.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This service is used to get estimated duration of each production process step based on
 * the analysis on the tracking records of history product items.
 * It assume that each product class has one production process template, and each product items belongs to the same product
 * class has therefore the same production process template.
 * It runs on the NIMBLE platform and will call the T&T APIs that defined as interface between companies and the NIMBLE platform.
 * Definition of the T&T API can be found under: https://app.swaggerhub.com/apis/BIB1/NIMBLE_Tracking/0.0.2
 * The T&T APIs should be implemented by companies and published to NIMBLE platform during product catalogue publishing.
 * <p>
 * <p>
 * For example, we have now an order with product EPC code "Product_1", which belongs to published product
 * "Product_bathroom", and has the process template: enter-->production--->shipping--->Delivered. And according to the T
 * & T Events, it is at Moment in the step "production", and we need to use T & T analysis service to calculate the
 * duration from "production" to "shipping" , as well as to "Delivered".
 * <p>
 * In history orders in NIMBLE platform, there are an order with product "Product_bathroom" with EPC Code "Product_2",
 * "Product_3", and another order with product "NIMBLE_BATH" with EPC Code "NIMBLE_MAGIC". In this case, NIMBLE platform
 * know that the Code "Product_2",  "Product_3" belongs to the same product and share the same process template, but not
 * "NIMBLE_MAGIC". So the NIMBLE platform i.e. your API send us the code "Product_2",  "Product_3" . We can then gather
 * the T & T Events from "Product_2",  "Product_3" to have a calculation. For example, for code "Product_2", the step
 * "production" has taken 1 day, and for code  "Product_3"  it has taken 3 days, then we can estimate that for the
 * current code "Product_1", it can take around 2 days.
 *
 * @author dqu
 */
@Service
public class TrackingAnalysisService {

    private static Logger log = LoggerFactory.getLogger(TrackingAnalysisService.class);


    @Autowired
    private RestTemplate restTemplate;

    // Tracking service URL
    @Value("${spring.tracking.url}")
    private String trackingURL;

    /**
     * Estimate the end time of production process according to production process template for a given item
     *
     * @param itemID           EPC code for an item
     * @param procTemplateJson production process template json string
     * @param bearer           bearer token in NIMBLE
     * @return ProductionEndTimeEstimation with UTC timestamp as production end time
     */
    public ProductionEndTimeEstimation estimateProcEndTimeWithGivenDuration(String itemID, String procTemplateJson, String bearer) {

        String FAILED_ESTIMATION_TIME = "0";
        String msg = "";
        ProductionEndTimeEstimation endTimeEst = new ProductionEndTimeEstimation();
        endTimeEst.setEpc(itemID);
        endTimeEst.setProductionEndTime(FAILED_ESTIMATION_TIME);
        endTimeEst.setMessage(msg);

        // Current occurred step in the production process template
        ProductionProcessStep curProcTemplateStep = null;
        // Time of the last occurred event in the tracking record
        long lastOccurredObjEventTime;

        // Get Production Process Template
        ProductionProcessTemplate procTemplate = null;
        try {
            procTemplate = this.initProductionProcessTemplateWithJson(procTemplateJson);

            if (procTemplate.getSteps().isEmpty()) {
                msg = "Empty production process template!";
                log.error(msg);

                endTimeEst.setMessage(msg);
                return endTimeEst;
            }
        } catch (Exception e) {
            msg = "Fail to init production process template with the given JSON string!";
            log.error(msg, e);

            endTimeEst.setMessage(msg);
            return endTimeEst;
        }

        // Get Tracking events
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearer);
        ProductTrackingResult trackingResult = this.getProductTrackingResult(itemID, headers);


        // In case of no tracking records, we assume the production process will immediately start
        if (trackingResult.getEpcisObjEvents().isEmpty()) {
            log.info("No tracking records for the item with EPC:" + itemID);

            // The current step is the first step, and it will immediately start
            curProcTemplateStep = procTemplate.getFirstStep();

            lastOccurredObjEventTime = System.currentTimeMillis();
        } else {
            EPCISObjectEvent lastOccuredObjEvent = trackingResult.getLastEvent();
            lastOccurredObjEventTime = lastOccuredObjEvent.getEventTime().getDate();

            ProductionProcessStep lastOccuredProcStep = trackingResult.getMatchedProcStepForEvent(lastOccuredObjEvent, procTemplate);
            if (lastOccuredProcStep == null) {
                msg = "Tracking history cannot match the given production process template! /n"
                        + "The last event in the tracking history does not match any step in the process template, epc:" + itemID;
                log.error(msg);

                endTimeEst.setMessage(msg);
                return endTimeEst;
            }

            curProcTemplateStep = lastOccuredProcStep;

            // When the next step should have already been started according to the duration in process template, but it has not yet started,
            // then we assume the next step will immediately start
            if (!lastOccuredProcStep.isFinalStep()) {
                long expectedNextStepStartTime = lastOccurredObjEventTime + lastOccuredProcStep.getDurationToNextInMS();
                if (System.currentTimeMillis() > expectedNextStepStartTime) {
                    lastOccurredObjEventTime = System.currentTimeMillis();
                    curProcTemplateStep = procTemplate.getNextStepOf(lastOccuredProcStep);
                }
            }
        }

        // In case production process steps are finished
        if (curProcTemplateStep.isFinalStep()) {
            endTimeEst.setProductionEndTime(Long.toString(lastOccurredObjEventTime));
            endTimeEst.setMessage("Production process is already finished!");
            return endTimeEst;
        }

        // Calculate estimated time
        ProductionProcessStep nextStep = curProcTemplateStep;
        long nextEventTime = lastOccurredObjEventTime;
        while (!nextStep.isFinalStep()) {
            long stepDuration = nextStep.getDurationToNextInMS();
            if (stepDuration < 0) {
                endTimeEst.setMessage("durationToNext or durationTimeUnit is not correctly specified in the production process template!");
                return endTimeEst;
            }

            nextEventTime = nextEventTime + stepDuration;
            nextStep = procTemplate.getNextStepOf(nextStep);
        }

        endTimeEst.setProductionEndTime(Long.toString(nextEventTime));
        endTimeEst.setMessage("Estimated production end time!");
        return endTimeEst;
    }


    /**
     * Get tracking events with the tracking API URL provided by company.
     *
     * @param itemID
     * @return
     */
    public ProductTrackingResult getProductTrackingResult(String itemID, HttpHeaders headers) {
        ProductTrackingResult trackingRes = new ProductTrackingResult();

        String url = trackingURL.trim();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        url = url + "simpleTracking/" + itemID;


        log.info("itemID:" + itemID);
        log.info("URL:" + url);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<EPCISObjectEvent[]> response = restTemplate.exchange(url, HttpMethod.GET, request, EPCISObjectEvent[].class);

        List<EPCISObjectEvent> result = Arrays.asList(response.getBody());
        trackingRes.setEpcisObjEvents(result);

        return trackingRes;
    }

    public ProductionProcessTemplate initProductionProcessTemplateWithJson(String procTemplateJson) throws JsonParseException, JsonMappingException, IOException {
        JSONObject procTemplateObj = new JSONObject(procTemplateJson);
        String proStepsJsonArray = procTemplateObj.getJSONArray("productionProcessTemplate").toString();

        ObjectMapper mapper = new ObjectMapper();
        List<ProductionProcessStep> procStepList = Arrays.asList(mapper.readValue(proStepsJsonArray, ProductionProcessStep[].class));

        ProductionProcessTemplate procTemplate = initProductionProcessTemplateWithSteps(procStepList);

        return procTemplate;
    }

    public ProductionProcessTemplate initProductionProcessTemplateWithSteps(List<ProductionProcessStep> procStepList) {
        ProductionProcessTemplate procTemplate = new ProductionProcessTemplate();
        procTemplate.setSteps(procStepList);

        return procTemplate;
    }

// The following methods are no more valid, because the assumption is change. It is now supposed T&T event data is kept in NIMBLE platform rather than in each company. 
// So, there is not necessary to keep T&T URLs for each company i.e. each product. 
// And there is as well no more production process template for each product class. So, the estimation is as well no more based on T&T history of each product class. 
//    
//    @Autowired
//    EventTimeDataFrameCacheService eventTimeDataFrameCachSrv;
//	
//	// URL from the business process category service, 
//	// from which the tracking related urls fro a specific product class i.e. product item can be retrieved
//	@Value("${spring.bpForTracking.url}")
//	private String bpForTrackingURL;
//	
//    /**
//     * Get tracking events with the tracking API URL provided by company.
//     * @param itemID
//     * @return
//     */
//    public ProductTrackingResult getProductTrackingResult(String itemID, HttpHeaders headers)
//    {
//    	ProductTrackingResult trackingRes = new ProductTrackingResult();
//    	
//    	EPCTrackingMetaData tMetadata = this.getTrackingMetaDataForEPC(itemID, headers);
//    	String eventURL = tMetadata.getEventUrl();
//    	
//    	URL eventRetrievalURL = null;
//    	try {
//    		URL tmpURL = new URL(eventURL);
//    		eventRetrievalURL = new URL(tmpURL.getProtocol(), tmpURL.getHost(), tmpURL.getPort(),
//    				tmpURL.getFile() + "?epc=" + itemID, null);
//		} catch (MalformedURLException e) {
//			log.error("The given eventURL: " + eventURL + " for EPC code " + itemID + " is invalid", e);
//			
//			return new ProductTrackingResult();
//		}
//    	
//        String url = eventRetrievalURL.toString();
//
//        System.out.println("itemID:" + itemID);
//        System.out.println("URL:" + url);
//
//        ResponseEntity<EPCISObjectEvent[]> response = restTemplate.getForEntity(url, EPCISObjectEvent[].class);
//        //HttpEntity<String> request = new HttpEntity<String>(headers);
//        //ResponseEntity<EPCISObjectEvent[]> response = restTemplate.exchange(url, HttpMethod.GET,request, EPCISObjectEvent[].class);
//        
//        List<EPCISObjectEvent> result = Arrays.asList(response.getBody());
//        trackingRes.setEpcisObjEvents(result);
//        
//        return trackingRes;
//    }
//    
//
//    /**
//     * Get production process template for a given product id
//     * 
//     * @param epc product item ID
//     * @return production process template
//     * @throws IOException
//     */
//    public ProductionProcessTemplate getProductionProcessTemplateForEPC(String epc, HttpHeaders headers) {
//    	
//    	EPCTrackingMetaData tMetadata = this.getTrackingMetaDataForEPC(epc, headers);
//    	String procTemplateURL = tMetadata.getProductionProcessTemplate();
//    	        
//    	log.info("procTemplateURL:" + procTemplateURL);
//
//        ResponseEntity<ProductionProcessStep[]> response = restTemplate.getForEntity(procTemplateURL, ProductionProcessStep[].class);
//        //HttpEntity<String> request = new HttpEntity<String>(headers);
//        //ResponseEntity<ProductionProcessStep[]> response  = restTemplate.exchange(procTemplateURL, HttpMethod.GET,request, ProductionProcessStep[].class);
//        
//        
//        List<ProductionProcessStep> result = Arrays.asList(response.getBody());
//        
//        
//        ProductionProcessTemplate procTemplate = initProductionProcessTemplateWithSteps(result);
//        
//        return procTemplate;
//    }  
//    
// 
//    
//    // todo: in case no EPCTrackingMetaData for a product item. 
//    // Need to talk with suat, what is the return value, and then do updates on this method as well as on the service class.
//    /**
//     * Get tracking meta data for a given EPC code.
//     * 
//     * The meta data includes information about event data search URL, Master data search URL etc.
//     * @param epc
//     * @return tracking meta data
//     */
//    public EPCTrackingMetaData getTrackingMetaDataForEPC(String epc, HttpHeaders headers)
//    {
//    	log.info("EPC:" + epc);
//        
//        String url = bpForTrackingURL + "/business-process/t-t/epc-details?epc=" + epc;
//
//        log.info("URL:" + url);
//        
//        //ResponseEntity<EPCTrackingMetaData> response = restTemplate.getForEntity(url, EPCTrackingMetaData.class);
//        HttpEntity<String> request = new HttpEntity<String>(headers);
//        ResponseEntity<EPCTrackingMetaData> response = restTemplate.exchange(url, HttpMethod.GET, request,  EPCTrackingMetaData.class);
//
//    	return response.getBody();
//    }
//        
// 
//    /**
//     * Get all product IDs that belongs to a given product class. 
//     * 
//     * @param productClass product class name in the NIMBLE platform
//     * @return all product IDs that belongs to the given product class
//     */
//    public List<String> getAllTrackableProductIDsByClass(String productClass, HttpHeaders headers)
//    {
//    	log.info("productClass:" + productClass);
//        
//        String url = bpForTrackingURL + "/business-process/t-t/epc-codes?productId=" + productClass;
//
//        log.info("URL:" + url);
//        
//        
//        //ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
//        HttpEntity<String> request = new HttpEntity<String>(headers);
//        ResponseEntity<String[]> response = restTemplate.exchange(url, HttpMethod.GET, request, String[].class);
//        
//        List<String> epcList = Arrays.asList(response.getBody());
//        List<String> epcListListWithoutDuplicates = new ArrayList<>(
//        	      new HashSet<>(epcList));
//                 	
//    	return epcListListWithoutDuplicates;
//    }
//    
//    /**
//     * Build data frame for T & T analysis.
//     * Column label are the step IDs in the process template; rows are the product IDs.
//     * Values in a row are the event times of respective steps of a specific product ID.
//     * 
//     * @param productClass
//     * @return
//     */
//    private DataFrame<Long> getEventTimeDataFrame(String productClass, ProductionProcessTemplate procTemplate, HttpHeaders headers)
//    {   	
//    	if(null != eventTimeDataFrameCachSrv.get(productClass))
//    	{
//    		log.info("found cached T&T event dataframe for product Class"+ productClass);
//    		return eventTimeDataFrameCachSrv.get(productClass);
//    	}
//    	
//    	List<ProductionProcessStep> steps = procTemplate.getSteps();
//    	List<String> stepIDs = 
//    			steps.stream()
//    		              .map(ProductionProcessStep::getId)
//    		              .collect(Collectors.toList());
//    	
//    	DataFrame<Long> df = new DataFrame<Long>(stepIDs);
//    	
//    	List<String> productIDs = getAllTrackableProductIDsByClass(productClass, headers);
//    	for(String productID:productIDs)
//    	{
//    		List<Long> eventTimes = new ArrayList<Long>();
//    		ProductTrackingResult trackRes = this.getProductTrackingResult(productID, headers);
//    		
//    		for(ProductionProcessStep step : steps)
//        	{
//    			EPCISObjectEvent matchedEvent = trackRes.getMatchedEventForProcStep(step);
//    			if(matchedEvent != null)
//    			{
//    				eventTimes.add(matchedEvent.getEventTime().getDate());
//    			}
//    			else
//    			{
//    				eventTimes.add(null);
//    			}
//        	}
//    		
//    		df.append(productID, eventTimes);
//    	}
//    	
//    	log.info("Caching T&T event dataframe for product Class"+ productClass);
//    	log.info("Caching T&T event dataframe columns: " + df.columns());
//    	log.info("Caching T&T event dataframe columns: " + df.columns());
//    	log.info("Caching T&T event dataframe indexs: " + df.index());
//    	log.info("Caching T&T event dataframe flatten: " + df.flatten());
//    	
//    	eventTimeDataFrameCachSrv.putEventTimeDataFrame(productClass, df);
//    	
//    	return df;
//    }
//    
//    
//    
//    /**
//     * Get average duration for a single step in the process template.
//     * 
//     * In case of no history tracking data, the avarage duration is 0.
//     * 
//     * @param a single process step
//     * @return duration in mill seconds
//     */
//    public long getDurationBetweenProcessSteps(String productClass, ProductionProcessTemplate procTemplate, ProductionProcessStep stepStart, ProductionProcessStep stepStop, HttpHeaders headers)
//    {
//    	DataFrame<Long> df = getEventTimeDataFrame(productClass, procTemplate, headers);
//    	
//		List<Long> stepStartEventTimeList = df.col(stepStart.getId());
//		List<Long> stepStopEventTimeList = df.col(stepStop.getId());
//		
//		long avgDuration = 0;
//		long durationSum = 0;
//		int count = 0;
//		for (int i = 0; i < stepStartEventTimeList.size(); i++) {
//			Long eventTimeStart = stepStartEventTimeList.get(i);
//			Long eventTimeStop = stepStopEventTimeList.get(i);
//			
//		   if (eventTimeStart != null && eventTimeStop != null) { 
//			   count +=1;
//			   durationSum += (eventTimeStop - eventTimeStart);
//		   }
//		}
//		if(count != 0)
//		{
//			avgDuration = durationSum/count;
//		}
//		
//		String msg = MessageFormat.format("in product class {0} avg duration between step {1} and step {2} is {3} mill seconds", 
//				productClass, stepStart.getId(), stepStop.getId(), avgDuration);
//		log.info(msg);
//    	return avgDuration;
//    }
}
