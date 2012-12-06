package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'OpenIdProvider'")
public class OpenIdProviderCouchDbImpl extends MotechBaseDataObject implements OpenIdProvider {

    public static final String DOCTYPE = "OpenIdProvider";

    @JsonProperty
    private String providerName;

    @JsonProperty
    private String providerUrl;

    public OpenIdProviderCouchDbImpl() {
        super();
        setType(DOCTYPE);
    }

    public OpenIdProviderCouchDbImpl(String providerName, String providerUrl) {
        this();
        this.providerName = providerName;
        this.providerUrl = providerUrl;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }
}
