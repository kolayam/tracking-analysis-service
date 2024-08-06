package eu.nimble.service.trackingAnalysis.impl.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * EPCISObjectEventEpcList
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-25T13:08:37.265Z")

public class EPCISObjectEventEpcList {
    @JsonProperty("epc")
    private String epc = null;

    public EPCISObjectEventEpcList epc(String epc) {
        this.epc = epc;
        return this;
    }

    /**
     * Get epc
     *
     * @return epc
     **/
    @ApiModelProperty(value = "")


    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EPCISObjectEventEpcList epCISObjectEventEpcList = (EPCISObjectEventEpcList) o;
        return Objects.equals(this.epc, epCISObjectEventEpcList.epc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epc);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EPCISObjectEventEpcList {\n");

        sb.append("    epc: ").append(toIndentedString(epc)).append("\n");
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

