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
package org.motechproject.server.tama;

import java.util.Arrays;
import java.util.List;

import org.motechproject.decisiontree.dao.TreeDao;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Logs all symptoms decision tree events
 * @author yyonkov
 *
 */
public class SymptomsNodeEventListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String UNDER50 = "age-under-50-or-non-regimen1-or-registered-less-than-180";
	private static final Tree tree = Tree.newBuilder()
										.setName(UNDER50)
										.setRootNode(Node.newBuilder()
												.setPrompts(Arrays.<Prompt>asList( TextToSpeechPrompt.newBuilder().setMessage("if you are you sick select 1, if not select 2").build()))
												.setTransitions(new Object[][] {
														{"1", 	Transition.newBuilder().setName("pressed1")
																	.setDestinationNode(Node.newBuilder()
																		.setPrompts(Arrays.<Prompt>asList( TextToSpeechPrompt.newBuilder().setMessage("if you are dying select 1, if not select 3").build()))
																		.setTransitions(new Object[][] {
																				{"1", 	Transition.newBuilder().setName("pressed1").setDestinationNode(
																							Node.newBuilder().setPrompts(Arrays.<Prompt>asList( 
																									TextToSpeechPrompt.newBuilder().setMessage("come to the hospital now").build()
																							)).build()
																						).build() },
																				{"3", 	Transition.newBuilder().setName("pressed3").setDestinationNode(
																							Node.newBuilder().setPrompts(Arrays.<Prompt>asList( 
																									TextToSpeechPrompt.newBuilder().setMessage("be patient, we will call you").build()
																							)).build()
																						).build() }
																	}).build()
																).build() },
														{"2",	Transition.newBuilder().setName("pressed2")
																	.setDestinationNode(Node.newBuilder().setPrompts(Arrays.<Prompt>asList(TextToSpeechPrompt.newBuilder().setMessage("Check with us again").build())).build())
																.build()}
												})
										.build())
									.build();

	@Autowired
	TreeDao treeDao;
	
	@MotechListener(subjects={"event_x", "event_y"})
	public void handleAllNodeEvents(MotechEvent event) {
		logger.info("Symptoms node event: "+event);
	}
	
	@MotechListener(subjects={"tama.initialize"})
	public void init(MotechEvent event) {
		logger.info("Initializing couchdb for decision tree module.");	
		List<Tree> trees = treeDao.findByName(UNDER50);
		if(!trees.isEmpty()) {
			logger.info("Tree with name: "+UNDER50+" found: "+trees.size());	
			return;
		}
		logger.info("Creating tree: "+tree);		
		treeDao.add(tree);
	}
}
