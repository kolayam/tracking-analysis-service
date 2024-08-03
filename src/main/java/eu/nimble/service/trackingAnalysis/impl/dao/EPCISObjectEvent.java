package eu.nimble.service.trackingAnalysis.impl.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * EPCISObjectEvent
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-25T13:08:37.265Z")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EPCISObjectEvent { //extends HashMap<String, Object>  {

    private static Logger log = LoggerFactory.getLogger(EPCISObjectEvent.class);

    @JsonProperty("eventTimeZoneOffset")
    private String eventTimeZoneOffset = null;

    @JsonProperty("bizStep")
    private String bizStep = null;

    @JsonProperty("recordTime")
    private EPCISObjectEventRecordTime recordTime = null;

    @JsonProperty("readPoint")
    private EPCISObjectEventReadPoint readPoint = null;

    @JsonProperty("eventTime")
    private EPCISObjectEventRecordTime eventTime = null;

    @JsonProperty("action")
    private String action = null;

    @JsonProperty("bizLocation")
    private EPCISObjectEventReadPoint bizLocation = null;

    @JsonProperty("eventType")
    private String eventType = null;

    @JsonProperty("epcList")
    @Valid
    private List<EPCISObjectEventEpcList> epcList = null;

    public EPCISObjectEvent eventTimeZoneOffset(String eventTimeZoneOffset) {
        this.eventTimeZoneOffset = eventTimeZoneOffset;
        return this;
    }

    /**
     * Get eventTimeZoneOffset
     *
     * @return eventTimeZoneOffset
     **/
    @ApiModelProperty(value = "")


    public String getEventTimeZoneOffset() {
        return eventTimeZoneOffset;
    }

    public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
        this.eventTimeZoneOffset = eventTimeZoneOffset;
    }

    public EPCISObjectEvent bizStep(String bizStep) {
        this.bizStep = bizStep;
        return this;
    }

    /**
     * Get bizStep
     *
     * @return bizStep
     **/
    @ApiModelProperty(value = "")


    public String getBizStep() {
        return bizStep;
    }

    public void setBizStep(String bizStep) {
        this.bizStep = bizStep;
    }

    public EPCISObjectEvent recordTime(EPCISObjectEventRecordTime recordTime) {
        this.recordTime = recordTime;
        return this;
    }

    /**
     * Get recordTime
     *
     * @return recordTime
     **/
    @ApiModelProperty(value = "")

    @Valid

    public EPCISObjectEventRecordTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(EPCISObjectEventRecordTime recordTime) {
        this.recordTime = recordTime;
    }

    public EPCISObjectEvent readPoint(EPCISObjectEventReadPoint readPoint) {
        this.readPoint = readPoint;
        return this;
    }

    /**
     * Get readPoint
     *
     * @return readPoint
     **/
    @ApiModelProperty(value = "")

    @Valid

    public EPCISObjectEventReadPoint getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(EPCISObjectEventReadPoint readPoint) {
        this.readPoint = readPoint;
    }

    public EPCISObjectEvent eventTime(EPCISObjectEventRecordTime eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    /**
     * Get eventTime
     *
     * @return eventTime
     **/
    @ApiModelProperty(value = "")

    @Valid

    public EPCISObjectEventRecordTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(EPCISObjectEventRecordTime eventTime) {
        this.eventTime = eventTime;
    }

    public EPCISObjectEvent action(String action) {
        this.action = action;
        return this;
    }

    /**
     * Get action
     *
     * @return action
     **/
    @ApiModelProperty(value = "")


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public EPCISObjectEvent bizLocation(EPCISObjectEventReadPoint bizLocation) {
        this.bizLocation = bizLocation;
        return this;
    }

    /**
     * Get bizLocation
     *
     * @return bizLocation
     **/
    @ApiModelProperty(value = "")

    @Valid

    public EPCISObjectEventReadPoint getBizLocation() {
        return bizLocation;
    }

    public void setBizLocation(EPCISObjectEventReadPoint bizLocation) {
        this.bizLocation = bizLocation;
    }

    public EPCISObjectEvent eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * Get eventType
     *
     * @return eventType
     **/
    @ApiModelProperty(value = "")


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public EPCISObjectEvent epcList(List<EPCISObjectEventEpcList> epcList) {
        this.epcList = epcList;
        return this;
    }

    public EPCISObjectEvent addEpcListItem(EPCISObjectEventEpcList epcListItem) {
        if (this.epcList == null) {
            this.epcList = new ArrayList<EPCISObjectEventEpcList>();
        }
        this.epcList.add(epcListItem);
        return this;
    }

    /**
     * Get epcList
     *
     * @return epcList
     **/
    @ApiModelProperty(value = "")

    @Valid

    public List<EPCISObjectEventEpcList> getEpcList() {
        return epcList;
    }

    public void setEpcList(List<EPCISObjectEventEpcList> epcList) {
        this.epcList = epcList;
    }

    /**
     * Check if the current EPCISObjectEvent is a valid event.
     * <p>
     * A valid EPCISObjectEvent must have at least the following properties, similar as steps defined in the NIMBLE Process Template.
     * 1). BizLocation
     * 2). ReadPoint
     * 3). BizStep
     * 4). EventTime
     */
    public boolean isValidEvent() {
        if (this.bizLocation == null || !StringUtils.isNotBlank(this.bizLocation.getId())) {
            log.error("bizLocation in the event is invalid!");
            return false;
        }

        if (this.readPoint == null || !StringUtils.isNotBlank(this.readPoint.getId())) {
            log.error("readPoint in the event is invalid!");
            return false;
        }

        if (!StringUtils.isNotBlank(this.bizStep)) {
            log.error("bizStep in the event is invalid!");
            return false;
        }

        if (this.eventTime == null) {
            log.error("eventTime in the event is invalid!");
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EPCISObjectEvent epCISObjectEvent = (EPCISObjectEvent) o;
        return Objects.equals(this.eventTimeZoneOffset, epCISObjectEvent.eventTimeZoneOffset) &&
                Objects.equals(this.bizStep, epCISObjectEvent.bizStep) &&
                Objects.equals(this.recordTime, epCISObjectEvent.recordTime) &&
                Objects.equals(this.readPoint, epCISObjectEvent.readPoint) &&
                Objects.equals(this.eventTime, epCISObjectEvent.eventTime) &&
                Objects.equals(this.action, epCISObjectEvent.action) &&
                Objects.equals(this.bizLocation, epCISObjectEvent.bizLocation) &&
                Objects.equals(this.eventType, epCISObjectEvent.eventType) &&
                Objects.equals(this.epcList, epCISObjectEvent.epcList) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTimeZoneOffset, bizStep, recordTime, readPoint, eventTime, action, bizLocation, eventType, epcList, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EPCISObjectEvent {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    eventTimeZoneOffset: ").append(toIndentedString(eventTimeZoneOffset)).append("\n");
        sb.append("    bizStep: ").append(toIndentedString(bizStep)).append("\n");
        sb.append("    recordTime: ").append(toIndentedString(recordTime)).append("\n");
        sb.append("    readPoint: ").append(toIndentedString(readPoint)).append("\n");
        sb.append("    eventTime: ").append(toIndentedString(eventTime)).append("\n");
        sb.append("    action: ").append(toIndentedString(action)).append("\n");
        sb.append("    bizLocation: ").append(toIndentedString(bizLocation)).append("\n");
        sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
        sb.append("    epcList: ").append(toIndentedString(epcList)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

