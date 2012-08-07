package org.motechproject.outbox.api.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository
@Views({
        @View(name = "getMessagesWithTypeName", map = "function(doc) { if (doc.externalId && doc.voiceMessageType) { emit([doc.externalId, doc.status, doc.voiceMessageType.voiceMessageTypeName, doc.expirationDate], doc._id); } }"),
        @View(name = "getMessages", map = "function(doc) { if (doc.externalId) { emit([doc.externalId, doc.status, doc.expirationDate], doc._id); } }")
})
public class AllOutboundVoiceMessages extends MotechBaseRepository<OutboundVoiceMessage> {

    private Map<SortKey, Comparator<OutboundVoiceMessage>> comparators = new java.util.HashMap<org.motechproject.outbox.api.contract.SortKey, java.util.Comparator<org.motechproject.outbox.api.domain.OutboundVoiceMessage>>() {
        {
            put(SortKey.CreationTime, new TimeBasedOutboundVoiceMessageComparator());
            put(SortKey.SequenceNumber, new SequenceBasedOutboundVoiceMessageComparator());
        }
    };


    @Autowired
    protected AllOutboundVoiceMessages(@Qualifier("outboxDatabase") CouchDbConnector db) {
        super(OutboundVoiceMessage.class, db);
    }

    public List<OutboundVoiceMessage> getMessages(String externalId, OutboundVoiceMessageStatus status, SortKey sortKey) {
        ComplexKey startKey = ComplexKey.of(externalId, status, new Date());
        ComplexKey endKey = ComplexKey.of(externalId, status, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getMessages").startKey(startKey).endKey(endKey).includeDocs(true);
        List<OutboundVoiceMessage> messages = db.queryView(q, OutboundVoiceMessage.class);
        if (messages.size() > 0) {
            Collections.sort(messages, comparators.get(sortKey));
        }
        return messages;
    }

    public int getMessagesCount(String externalId, OutboundVoiceMessageStatus messageStatus) {
        ComplexKey startKey = ComplexKey.of(externalId, messageStatus, new Date());
        ComplexKey endKey = ComplexKey.of(externalId, messageStatus, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getMessages").startKey(startKey).endKey(endKey);
        return db.queryView(q).getSize();
    }

    public int getMessagesCount(String externalId, OutboundVoiceMessageStatus messageStatus, String voiceMessageTypeName) {
        ComplexKey startKey = ComplexKey.of(externalId, messageStatus, voiceMessageTypeName, new Date());
        ComplexKey endKey = ComplexKey.of(externalId, messageStatus, voiceMessageTypeName, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getMessagesWithTypeName").startKey(startKey).endKey(endKey);
        return db.queryView(q).getSize();
    }
}
