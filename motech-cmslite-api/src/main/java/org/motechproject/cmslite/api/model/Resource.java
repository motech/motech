package org.motechproject.cmslite.api.model;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.io.InputStream;

@TypeDiscriminator("doc.type === 'RESOURCE'")
public class Resource extends MotechAuditableDataObject {

    @JsonIgnore
    private InputStream inputStream;
    private String language;
    private String name;

    @JsonProperty("type") private final String type = "RESOURCE";

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public InputStream getResourceAsInputStream() {
        return inputStream;
    }
}
