/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.outbox.dao.couchdb;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.outbox.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.model.OutboundVoiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author yyonkov
 *
 */
@Component
public class OutboundVoiceMessageDaoImpl extends
		MotechAuditableRepository<OutboundVoiceMessage> implements
		OutboundVoiceMessageDao {
	@Autowired
	protected OutboundVoiceMessageDaoImpl( @Qualifier("outboxDatabase") CouchDbConnector db) {
		super(OutboundVoiceMessage.class, db);
		initStandardDesignDocument();
	}
	/* (non-Javadoc)
	 * @see org.motechproject.outbox.dao.OutboundVoiceMessageDao#getPendingMessages(java.lang.String)
	 */
	@Override
	@View( name = "getPendingMessages", map = "function(doc) { if (doc.partyId && doc.status=='PENDING') { emit([doc.partyId, doc.expirationDate], doc._id); } }")
	public List<OutboundVoiceMessage> getPendingMessages(String partyId) {
		ComplexKey startKey = ComplexKey.of(partyId, new Date());
		char[] chars = partyId.toCharArray();
		chars[chars.length-1]++;
		ComplexKey endKey = ComplexKey.of(new String(chars));
		ViewQuery q = createQuery("getPendingMessages").startKey(startKey).endKey(endKey).includeDocs(true);
		List<OutboundVoiceMessage> messages = db.queryView(q, OutboundVoiceMessage.class);
		if(messages.size()>0) {
			Collections.sort(messages, new Comparator<OutboundVoiceMessage>() {
				@Override
				public int compare(OutboundVoiceMessage m1, OutboundVoiceMessage m2) {
					int dateComp = m2.getCreationTime().compareTo(m1.getCreationTime());
					if(dateComp!=0) {
						return dateComp;
					}
					return m2.getVoiceMessageType().getPriority().compareTo(m1.getVoiceMessageType().getPriority());
				}
			});
		}
		return messages;
	}

}
