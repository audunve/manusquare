package javalin.models;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class ProjectAttribute {
    private String attributeID;
    private String processName;
    private String attributeKey;
    private String attributeValue;

    @JsonProperty("attributeId")
    public String getAttributeID() { return attributeID; }
    @JsonProperty("attributeId")
    public void setAttributeID(String value) { this.attributeID = value; }

    @JsonProperty("processName")
    public String getProcessName() { return processName; }
    @JsonProperty("processName")
    public void setProcessName(String value) { this.processName = value; }

    @JsonProperty("attributeKey")
    public String getAttributeKey() { return attributeKey; }
    @JsonProperty("attributeKey")
    public void setAttributeKey(String value) { this.attributeKey = value; }

    @JsonProperty("attributeValue")
    public String getAttributeValue() { return attributeValue; }
    @JsonProperty("attributeValue")
    public void setAttributeValue(String value) { this.attributeValue = value; }
}
