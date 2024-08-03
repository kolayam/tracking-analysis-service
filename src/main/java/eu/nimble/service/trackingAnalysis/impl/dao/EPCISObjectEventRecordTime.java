package eu.nimble.service.trackingAnalysis.impl.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * EPCISObjectEventRecordTime
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-25T13:08:37.265Z")

public class EPCISObjectEventRecordTime {
    // ISODate("2018-04-04T02:33:31.116Z")
    // $date: 1524156074162
    //@JsonProperty("$date")
    //private Long date = null;

    // $numberLong: "1530883056000"
    @JsonProperty("$date")
    private Long date = null;

    public EPCISObjectEventRecordTime date(Long date) {
        this.date = date;
        return this;
    }

    /**
     * Get date
     *
     * @return date
     **/
    @ApiModelProperty(value = "")


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EPCISObjectEventRecordTime epCISObjectEventRecordTime = (EPCISObjectEventRecordTime) o;
        return Objects.equals(this.date, epCISObjectEventRecordTime.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EPCISObjectEventRecordTime {\n");

        sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

