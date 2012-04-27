package org.motechproject.outbox.api.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
@Views({
        @View(name = "getPendingMessages", map = "function(doc) { if (doc.externalId && doc.status=='PENDING') { emit([doc.externalId, doc.expirationDate], doc._id); } }"),
        @View(name = "getPendingMessagesWithMessageTypeName", map = "function(doc) { if (doc.externalId && doc.status=='PENDING' && doc.voiceMessageType) { emit([doc.externalId, doc.voiceMessageType.voiceMessageTypeName, doc.expirationDate], doc._id); } }"),
        @View(name = "getSavedMessages", map = "function(doc) { if (doc.externalId && doc.status=='SAVED') { emit([doc.externalId, doc.expirationDate], doc._id); } }")
})
public class AllOutboundVoiceMessages extends MotechBaseRepository<OutboundVoiceMessage> {
    @Autowired
    protected AllOutboundVoiceMessages(@Qualifier("outboxDatabase") CouchDbConnector db) {
        super(OutboundVoiceMessage.class, db);
    }

    private List<OutboundVoiceMessage> getMessages(String view, String externalId) {
        ComplexKey startKey = ComplexKey.of(externalId, new Date());
        ComplexKey endKey = ComplexKey.of(externalId, ComplexKey.emptyObject());
        ViewQuery q = createQuery(view).startKey(startKey).endKey(endKey).includeDocs(true);
        List<OutboundVoiceMessage> messages = db.queryView(q, OutboundVoiceMessage.class);
        if (messages.size() > 0) {
            Collections.sort(messages, OutboundVoiceMessageComparator.getComparator());
        }
        return messages;
    }

    public List<OutboundVoiceMessage> getPendingMessages(String externalId) {
        return getMessages("getPendingMessages", externalId);
    }

    public List<OutboundVoiceMessage> getSavedMessages(String externalId) {
        return getMessages("getSavedMessages", externalId);
    }

    public int getPendingMessagesCount(String externalId) {
        ComplexKey startKey = ComplexKey.of(externalId, new Date());
        ComplexKey endKey = ComplexKey.of(externalId, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getPendingMessages").startKey(startKey).endKey(endKey);
        return db.queryView(q).getSize();
    }

    public int getPendingMessagesCount(String externalId, String voiceMessageTypeName) {
        ComplexKey startKey = ComplexKey.of(externalId, voiceMessageTypeName, new Date());
        ComplexKey endKey = ComplexKey.of(externalId, voiceMessageTypeName, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getPendingMessagesWithMessageTypeName").startKey(startKey).endKey(endKey);
        return db.queryView(q).getSize();
    }

}
