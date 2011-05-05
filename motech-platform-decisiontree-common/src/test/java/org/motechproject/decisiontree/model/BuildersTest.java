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
package org.motechproject.decisiontree.model;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;


/**
 * Test model Builders
 * @author yyonkov
 *
 */
public class BuildersTest {
	/**
	 * Example of how to use Builders for easy tree building
	 */
	@Test
	public void testTreeBuilder() {
		Tree t = Tree.newBuilder()
					.setName("tree1")
					.setDescription("desc")
					.setRootNode(Node.newBuilder()
							.setActionsBefore(Arrays.asList(Action.newBuilder()
									.setEventId("event_x")
									.build()))
							.setPrompts(Arrays.asList(TextToSpeechPrompt.newBuilder()
									.setMessage("haha")
									.build()))
							.setTransitions(new Object[][]{
									{"1",Transition.newBuilder()
										.setName("sick")
										.build()},
									{"2",Transition.newBuilder()
										.setName("healthy")
										.build()}
									})
							.build())
					.build();
//		System.out.print(t);
		assertNotNull(t);
		assertEquals("tree1", t.getName());
		assertEquals("desc",t.getDescription());
		assertNotNull(t.getRootNode());
		assertNotNull(t.getRootNode().getTransitions());
		assertEquals(2,t.getRootNode().getTransitions().size());		
	}

}
