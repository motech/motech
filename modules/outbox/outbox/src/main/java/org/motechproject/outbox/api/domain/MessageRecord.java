package org.motechproject.outbox.api.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'MessageRecord'")
public class MessageRecord extends MotechBaseDataObject {

    private String externalId;
    private String jobId;

    public MessageRecord() {
    }

    public MessageRecord(String messageExternalId, String jobId) {
        this.externalId = messageExternalId;
        this.jobId = jobId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
