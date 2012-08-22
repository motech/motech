package org.motechproject.model;

import org.ektorp.support.TypeDiscriminator;

public class Rule extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @TypeDiscriminator
    private String content;

    private String bundleSymbolicName;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public void setBundleSymbolicName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

}
