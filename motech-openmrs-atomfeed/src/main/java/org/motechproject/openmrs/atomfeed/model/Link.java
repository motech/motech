package org.motechproject.openmrs.atomfeed.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("link")
public class Link {

    @XStreamAlias("href")
    @XStreamAsAttribute
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
