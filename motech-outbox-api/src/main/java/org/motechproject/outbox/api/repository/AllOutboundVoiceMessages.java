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
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
@Views({
        @View(name = "getPendingMessages", map = "function(doc) { if (doc.partyId && doc.status=='PENDING') { emit([doc.partyId, doc.expirationDate], doc._id); } }"),
        @View(name = "getPendingMessagesWithMessageTypeName", map = "function(doc) { if (doc.partyId && doc.status=='PENDING' && doc.voiceMessageType) { emit([doc.partyId, doc.voiceMessageType.voiceMessageTypeName, doc.expirationDate], doc._id); } }"),
        @View(name = "getSavedMessages", map = "function(doc) { if (doc.partyId && doc.status=='SAVED') { emit([doc.partyId, doc.expirationDate], doc._id); } }")
})
public class AllOutboundVoiceMessages extends
        MotechBaseRepository<OutboundVoiceMessage> {
    @Autowired
    protected AllOutboundVoiceMessages(@Qualifier("outboxDatabase") CouchDbConnector db) {
        super(OutboundVoiceMessage.class, db);
    }

    private List<OutboundVoiceMessage> getMessages(String view, String partyId) {
        ComplexKey startKey = ComplexKey.of(partyId, new Date());
        ComplexKey endKey = ComplexKey.of(partyId, ComplexKey.emptyObject());
        ViewQuery q = createQuery(view).startKey(startKey).endKey(endKey).includeDocs(true);
        List<OutboundVoiceMessage> messages = db.queryView(q, OutboundVoiceMessage.class);
        if (messages.size() > 0) {
            Collections.sort(messages, new Comparator<OutboundVoiceMessage>() {
                @Override
                public int compare(OutboundVoiceMessage m1, OutboundVoiceMessage m2) {
                    if (m1.getCreationTime() == null) {
                        throw new InvalidDataException("Invalid object: " + m1 + " Creation time in OutboundVoiceMessage can not be null");
                    }

                    if (m2.getCreationTime() == null) {
                        throw new InvalidDataException("Invalid object: " + m2 + " Creation time in OutboundVoiceMessage can not be null");
                    }
                    int dateComp = m2.getCreationTime().compareTo(m1.getCreationTime());
                    if (dateComp != 0) {
                        return dateComp;
                    }

                    if (m1.getVoiceMessageType() == null) {
                        throw new InvalidDataException("Invalid object: " + m1 + " Voice Message Type in OutboundVoiceMessage can not be null");
                    }

                    if (m2.getVoiceMessageType() == null) {
                        throw new InvalidDataException("Invalid object: " + m2 + " Voice Message Type in OutboundVoiceMessage can not be null");
                    }
                    return m2.getVoiceMessageType().getPriority().compareTo(m1.getVoiceMessageType().getPriority());
                }
            });
        }
        return messages;
    }

    public List<OutboundVoiceMessage> getPendingMessages(String partyId) {
        return getMessages("getPendingMessages", partyId);
    }

    public List<OutboundVoiceMessage> getSavedMessages(String partyId) {
        return getMessages("getSavedMessages", partyId);
    }

    public int getPendingMessagesCount(String partyId) {
        ComplexKey startKey = ComplexKey.of(partyId, new Date());
        ComplexKey endKey = ComplexKey.of(partyId, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getPendingMessages").startKey(startKey).endKey(endKey);
        return db.queryView(q).getSize();
    }

    public int getPendingMessagesCount(String partyId, String voiceMessageTypeName) {
        ComplexKey startKey = ComplexKey.of(partyId, voiceMessageTypeName, new Date());
        ComplexKey endKey = ComplexKey.of(partyId, voiceMessageTypeName, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getPendingMessagesWithMessageTypeName").startKey(startKey).endKey(endKey);
        return db.queryView(q).getSize();
    }
}
