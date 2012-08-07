package org.motechproject.commcare.request;

public class CommcareRequestData {
    private String xmlns;
    private MetaElement meta;
    private CaseRequest ccCase;

    public CommcareRequestData(String xmlns, MetaElement meta,
            CaseRequest ccCase) {
        this.xmlns = xmlns;
        this.meta = meta;
        this.ccCase = ccCase;
    }

    public CaseRequest getCcCase() {
        return this.ccCase;
    }

    public MetaElement getMeta() {
        return this.meta;
    }

    public String getXmlns() {
        return this.xmlns;
    }
}
