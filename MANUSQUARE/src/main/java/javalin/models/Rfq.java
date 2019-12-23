package javalin.models;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class Rfq {
    private String nda;
    private String projectName;
    private String projectDescription;
    private String selectionType;
    private String supplierMaxDistance;
    private String servicePolicy;
    private String projectID;
    private String id;
    private String projectType;
    private ProjectAttribute[] projectAttributes;
    private SupplierAttribute[] supplierAttributes;

    @JsonProperty("nda")
    public String getNda() { return nda; }
    @JsonProperty("nda")
    public void setNda(String value) { this.nda = value; }

    @JsonProperty("projectName")
    public String getProjectName() { return projectName; }
    @JsonProperty("projectName")
    public void setProjectName(String value) { this.projectName = value; }

    @JsonProperty("projectDescription")
    public String getProjectDescription() { return projectDescription; }
    @JsonProperty("projectDescription")
    public void setProjectDescription(String value) { this.projectDescription = value; }

    @JsonProperty("selectionType")
    public String getSelectionType() { return selectionType; }
    @JsonProperty("selectionType")
    public void setSelectionType(String value) { this.selectionType = value; }

    @JsonProperty("supplierMaxDistance")
    public String getSupplierMaxDistance() { return supplierMaxDistance; }
    @JsonProperty("supplierMaxDistance")
    public void setSupplierMaxDistance(String value) { this.supplierMaxDistance = value; }

    @JsonProperty("servicePolicy")
    public String getServicePolicy() { return servicePolicy; }
    @JsonProperty("servicePolicy")
    public void setServicePolicy(String value) { this.servicePolicy = value; }

    @JsonProperty("projectId")
    public String getProjectID() { return projectID; }
    @JsonProperty("projectId")
    public void setProjectID(String value) { this.projectID = value; }

    @JsonProperty("id")
    public String getID() { return id; }
    @JsonProperty("id")
    public void setID(String value) { this.id = value; }

    @JsonProperty("projectType")
    public String getProjectType() { return projectType; }
    @JsonProperty("projectType")
    public void setProjectType(String value) { this.projectType = value; }

    @JsonProperty("projectAttributes")
    public ProjectAttribute[] getProjectAttributes() { return projectAttributes; }
    @JsonProperty("projectAttributes")
    public void setProjectAttributes(ProjectAttribute[] value) { this.projectAttributes = value; }

    @JsonProperty("supplierAttributes")
    public SupplierAttribute[] getSupplierAttributes() { return supplierAttributes; }
    @JsonProperty("supplierAttributes")
    public void setSupplierAttributes(SupplierAttribute[] value) { this.supplierAttributes = value; }
}
