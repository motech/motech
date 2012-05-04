package org.motechproject.sms.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.sms.InboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllInboundSMS extends MotechBaseRepository<InboundSMS> {

    @Autowired
    protected AllInboundSMS(@Qualifier("smppDBConnector") CouchDbConnector db) {
        super(InboundSMS.class, db);
    }

    @View(name = "by_recipient", map = "function(doc) {  if (doc.type === 'InboundSMS') emit(doc.phoneNumber, doc) }")
    public List<InboundSMS> findBy(String phoneNumber) {
        return queryView("by_recipient", phoneNumber);
    }

    @View(name = "within_message_time_range", map = "function(doc) {  if (doc.type === 'InboundSMS') emit(doc.messageTime, doc) }")
    public List<InboundSMS> messagesReceivedBetween(DateTime from, DateTime to) {
        return db.queryView(createQuery("within_message_time_range").startKey(from).endKey(to).includeDocs(true), InboundSMS.class);
    }
}
