package eu.nimble.service.trackingAnalysis.impl.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * ProductionProcessStep
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-25T13:08:37.265Z")

public class ProductionProcessStep {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("hasPrev")
    private String hasPrev = null;

    @JsonProperty("readPoint")
    private String readPoint = null;

    @JsonProperty("bizLocation")
    private String bizLocation = null;

    @JsonProperty("bizStep")
    private String bizStep = null;

    @JsonProperty("hasNext")
    private String hasNext = null;

    @JsonProperty("durationToNext")
    private String durationToNext = null;

    // Time unit can be one of the following values:
    // "DAYS" "HOURS" "MICROSECONDS" "MILLISECONDS" "MINUTES" "NANOSECONDS" "SECONDS"
    @JsonProperty("durationTimeUnit")
    private String durationTimeUnit = null;

    // attributes to keep T&T analysis result
    private boolean isStepTriggered = true;
    private EPCISObjectEventRecordTime estimatedEventTime = null;


    public ProductionProcessStep id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(value = "")


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EPCISObjectEventRecordTime getEstimatedEventTime() {
        return estimatedEventTime;
    }

    public void setEstimatedEventTime(EPCISObjectEventRecordTime estimatedEventTime) {
        this.estimatedEventTime = estimatedEventTime;
    }

    public void setEstimatedEventTime(long estimatedEventTime) {
        EPCISObjectEventRecordTime time = new EPCISObjectEventRecordTime();
        time.date(estimatedEventTime);
        this.estimatedEventTime = time;
    }

    public boolean isStepTriggered() {
        return isStepTriggered;
    }

    public void setStepTriggered(boolean isReached) {
        this.isStepTriggered = isReached;
    }

    public ProductionProcessStep hasPrev(String hasPrev) {
        this.hasPrev = hasPrev;
        return this;
    }

    /**
     * Get hasPrev
     *
     * @return hasPrev
     **/
    @ApiModelProperty(value = "")


    public String getHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(String hasPrev) {
        this.hasPrev = hasPrev;
    }

    public ProductionProcessStep readPoint(String readPoint) {
        this.readPoint = readPoint;
        return this;
    }

    /**
     * Get readPoint
     *
     * @return readPoint
     **/
    @ApiModelProperty(value = "")


    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    public ProductionProcessStep bizLocation(String bizLocation) {
        this.bizLocation = bizLocation;
        return this;
    }

    /**
     * Get bizLocation
     *
     * @return bizLocation
     **/
    @ApiModelProperty(value = "")


    public String getBizLocation() {
        return bizLocation;
    }

    public void setBizLocation(String bizLocation) {
        this.bizLocation = bizLocation;
    }

    public ProductionProcessStep bizStep(String bizStep) {
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

    public ProductionProcessStep hasNext(String hasNext) {
        this.hasNext = hasNext;
        return this;
    }

    /**
     * Get hasNext
     *
     * @return hasNext
     **/
    @ApiModelProperty(value = "")


    public String getHasNext() {
        return hasNext;
    }

    public void setHasNext(String hasNext) {
        this.hasNext = hasNext;
    }

    /**
     * Get durationToNext
     *
     * @return durationToNext
     **/
    @ApiModelProperty(value = "")

    public String getDurationToNext() {
        return durationToNext;
    }

    public void setDurationToNext(String durationToNext) {
        this.durationToNext = durationToNext;
    }

    /**
     * Get durationTimeUnit
     *
     * @return durationTimeUnit
     **/
    @ApiModelProperty(value = "")
    public String getDurationTimeUnit() {
        return durationTimeUnit;
    }

    public void setDurationTimeUnit(String durationTimeUnit) {
        this.durationTimeUnit = durationTimeUnit;
    }

    /**
     * Convert specified durationToNext to MICROSECONDS
     *
     * @return a positve long value when the durationToNext and durationTimeUnit are correctly specified; -1, otherwise
     */
    public long getDurationToNextInMS() {
        long FAILED = -1;
        if (durationToNext == null || durationTimeUnit == null) {
            return FAILED;
        }

        TimeUnit durationUnit = TimeUnit.valueOf(durationTimeUnit.toUpperCase());
        long duration = Long.valueOf(durationToNext);

        return durationUnit.toMillis(duration);
    }

    /**
     * Check if the current step the final step in production process template.
     * Final step is the step which does not have next step.
     *
     * @return true, when it is final step; false, otherwise
     */
    public boolean isFinalStep() {
        return this.getHasNext().trim().isEmpty();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductionProcessStep productionProcessStep = (ProductionProcessStep) o;
        return Objects.equals(this.id, productionProcessStep.id) &&
                Objects.equals(this.hasPrev, productionProcessStep.hasPrev) &&
                Objects.equals(this.readPoint, productionProcessStep.readPoint) &&
                Objects.equals(this.bizLocation, productionProcessStep.bizLocation) &&
                Objects.equals(this.bizStep, productionProcessStep.bizStep) &&
                Objects.equals(this.hasNext, productionProcessStep.hasNext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hasPrev, readPoint, bizLocation, bizStep, hasNext);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProductionProcessStep {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    hasPrev: ").append(toIndentedString(hasPrev)).append("\n");
        sb.append("    readPoint: ").append(toIndentedString(readPoint)).append("\n");
        sb.append("    bizLocation: ").append(toIndentedString(bizLocation)).append("\n");
        sb.append("    bizStep: ").append(toIndentedString(bizStep)).append("\n");
        sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
        sb.append("    isStepTriggered: ").append(toIndentedString(isStepTriggered)).append("\n");
        sb.append("    estimatedEventTime: ").append(toIndentedString(estimatedEventTime)).append("\n");
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

