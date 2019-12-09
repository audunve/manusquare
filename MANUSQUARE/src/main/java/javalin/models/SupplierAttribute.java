package javalin.models;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class SupplierAttribute {
    private String id;
    private String attributeKey;
    private String attributeValue;

    @JsonProperty("id")
    public String getID() { return id; }
    @JsonProperty("id")
    public void setID(String value) { this.id = value; }

    @JsonProperty("attributeKey")
    public String getAttributeKey() { return attributeKey; }
    @JsonProperty("attributeKey")
    public void setAttributeKey(String value) { this.attributeKey = value; }

    @JsonProperty("attributeValue")
    public String getAttributeValue() { return attributeValue; }
    @JsonProperty("attributeValue")
    public void setAttributeValue(String value) { this.attributeValue = value; }
}
