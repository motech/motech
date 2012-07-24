package org.motechproject.admin.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'AdminMapping'")
public class AdminMapping extends MotechBaseDataObject {

    private String bundleName;
    private String destination;

    public AdminMapping() {
        this(null, null);
    }

    public AdminMapping(String bundleName, String destination) {
        this.bundleName = bundleName;
        this.destination = destination;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
