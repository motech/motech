package org.motechproject.hub.mds;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class HubDistributionContent implements java.io.Serializable {

    private static final long serialVersionUID = -5048963496204264339L;

    @Field(required = true)
    private String content;

    @Field(required = true)
    private String contentType;

    public HubDistributionContent() {
        this(null, null);
    }

    public HubDistributionContent(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
