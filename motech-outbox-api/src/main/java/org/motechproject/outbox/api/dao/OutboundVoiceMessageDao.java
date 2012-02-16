package org.motechproject.outbox.api.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;

import java.util.List;

/**
 * DAO for OutboundVoiceMessage
 *
 * @author yyonkov
 */
public interface OutboundVoiceMessageDao extends BaseDao<OutboundVoiceMessage> {
    List<OutboundVoiceMessage> getPendingMessages(String partyId);

    List<OutboundVoiceMessage> getSavedMessages(String partyId);

    int getPendingMessagesCount(String partyId);

    int getPendingMessagesCount(String partyId, String voiceMessageTypeName);
}
