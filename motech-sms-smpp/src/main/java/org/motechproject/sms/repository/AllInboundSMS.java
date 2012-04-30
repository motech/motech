package org.motechproject.sms.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.sms.InboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class AllInboundSMS extends MotechBaseRepository<InboundSMS> {

    @Autowired
    protected AllInboundSMS(@Qualifier("smppDBConnector") CouchDbConnector db) {
        super(InboundSMS.class, db);
    }

    public void createOrReplace(InboundSMS inboundSMS) {
        InboundSMS sms = findBy(inboundSMS.getPhoneNumber(), inboundSMS.getUuid());
        if (null != sms) {
            update(copyDocumentInfo(inboundSMS, sms));
        } else {
            add(inboundSMS);
        }
    }

    private InboundSMS copyDocumentInfo(InboundSMS target, InboundSMS source) {
        target.setId(source.getId());
        target.setRevision(source.getRevision());
        return target;
    }

    public InboundSMS findBy(String phoneNumber, String uuid) {
        List<InboundSMS> smsList = findAllBy(phoneNumber, uuid);
        return CollectionUtils.isEmpty(smsList) ? null : smsList.get(0);
    }

    @View(name = "inbound_sms_by_recipient_and_uuid", map = "function(doc) {  if (doc.type === 'InboundSMS') emit([doc.phoneNumber, doc.uuid], doc) }")
    public List<InboundSMS> findAllBy(String phoneNumber, String uuid) {
        return queryView("inbound_sms_by_recipient_and_uuid", ComplexKey.of(phoneNumber, uuid));
    }
}
