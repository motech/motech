package org.motechproject.security.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

/**
 * The MotechSecurityConfiguration is a single document
 * that contains all of the URL security rule configuration. The
 * configuration was designed as one document because the entire filter
 * chain must be reconstructed each time it is updated, therefore
 * managing many references is unnecessary.
 */
@TypeDiscriminator("doc.type == 'MotechSecurityConfiguration'")
public class MotechSecurityConfiguration extends MotechBaseDataObject {

    public static final String DOC_TYPE = "MotechSecurityConfiguration";

    @JsonProperty
    private List<MotechURLSecurityRule> securityRules;

    public MotechSecurityConfiguration() {}

    public MotechSecurityConfiguration(List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }

    public List<MotechURLSecurityRule> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }
}
