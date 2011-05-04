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
package org.motechproject.server.decisiontree.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.context.Context;
import org.motechproject.context.EventContext;
import org.motechproject.decisiontree.model.Action;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;

/**
 * Responsible for emitting Tree events
 * 
 * @author yyonkov
 * 
 */
public class TreeEventProcessor {
	private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

	private void sendNodeActions(List<Action> actions, String path, String patientId) {
		for (Action action : actions) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("patientId", patientId);
			params.put("nodePath", path);
			MotechEvent event = new MotechEvent(action.getEventId(), params);
			eventRelay.sendEventMessage(event);
		}
	}

	public void sendActionsBefore(Node node, String path, String patientId) {
		if (node != null) {
			sendNodeActions(node.getActionsBefore(), path, patientId);
		}
	}

	public void sendActionsAfter(Node node, String path, String patientId) {
		if (node != null) {
			sendNodeActions(node.getActionsAfter(), path, patientId);
		}
	}

	public void sendTransitionActions(Transition transition, String patientId) {
		if (transition != null) {
			for (Action action : transition.getActions()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("patientId", patientId);
				params.put("transitionName", transition.getName());
				MotechEvent event = new MotechEvent(action.getEventId(), params);
				eventRelay.sendEventMessage(event);
			}
		}
	}
}
