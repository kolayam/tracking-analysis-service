package eu.nimble.service.trackingAnalysis.impl.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProductTrackingResult {

    private static Logger log = LoggerFactory.getLogger(ProductTrackingResult.class);


    private List<EPCISObjectEvent> epcisObjEvents;

    public List<EPCISObjectEvent> getEpcisObjEvents() {
        return epcisObjEvents;
    }

    public void setEpcisObjEvents(List<EPCISObjectEvent> epcisObjEvents) {

        boolean containInvalidEvent = false;
        for (EPCISObjectEvent event : epcisObjEvents) {
            if (event == null || !event.isValidEvent()) {
                containInvalidEvent = true;
                break;
            }
        }

        if (containInvalidEvent) {
            log.error("The returned product tracking results contains invalid events.");
            this.epcisObjEvents = new ArrayList<EPCISObjectEvent>();
        } else {
            this.epcisObjEvents = epcisObjEvents;
        }
    }

    /**
     * Get the last object event.
     *
     * @return the last object event.
     */
    public EPCISObjectEvent getLastEvent() {

        EPCISObjectEvent lastEvent = Collections.max(epcisObjEvents, Comparator.comparing(event -> event.getEventTime().getDate()));
        return lastEvent;
    }

    /**
     * Get the matched production process step in the given process template for the given event
     *
     * @param event
     * @param procTemplate
     * @return
     */
    public ProductionProcessStep getMatchedProcStepForEvent(EPCISObjectEvent event, ProductionProcessTemplate procTemplate) {
        Optional<ProductionProcessStep> matchingObject = procTemplate.getSteps().stream().
                filter(step -> step.getBizLocation().equals(event.getBizLocation().getId())
                        && step.getReadPoint().equals(event.getReadPoint().getId())
                        && step.getBizStep().equals(event.getBizStep())
                ).findFirst();

        return matchingObject.orElse(null);
    }

    /**
     * Get the matched event for the given production process step
     *
     * @param step
     * @return
     */
    public EPCISObjectEvent getMatchedEventForProcStep(ProductionProcessStep step) {
        Optional<EPCISObjectEvent> matchingObject = epcisObjEvents.stream().
                filter(event -> event.getBizLocation().getId().equals(step.getBizLocation())
                        && event.getReadPoint().getId().equals(step.getReadPoint())
                        && event.getBizStep().equals(step.getBizStep())
                ).findFirst();

        return matchingObject.orElse(null);
    }

}
