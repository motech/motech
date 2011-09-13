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
package org.motechproject.decisiontree.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * DAO IT test findByName()
 *
 * @author yyonkov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testDecisionTreeCommon.xml"})
public class TreeDaoTestIT {
    @Autowired
    private TreeDao treeDao;
    private final String NAME = "TREE";

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 10; i++) {
            Tree tree = Tree.newBuilder()
                    .setRootNode(new Node()
                            .setPrompts(Arrays.<Prompt>asList(TextToSpeechPrompt.newBuilder().setMessage("if you are you sick select 1, if not select 2").build()))
                            .setTransitions(new Object[][]{
                                    {"1", Transition.newBuilder().setName("pressed1")
                                            .setDestinationNode(new Node()
                                                    .setPrompts(Arrays.<Prompt>asList(TextToSpeechPrompt.newBuilder().setMessage("if you are dying select 1, if not select 3").build()))
                                                    .setTransitions(new Object[][]{
                                                            {"1", Transition.newBuilder().setName("pressed1").setDestinationNode(
                                                                    new Node().setPrompts(Arrays.<Prompt>asList(
                                                                            TextToSpeechPrompt.newBuilder().setMessage("come to the hospital now").build()
                                                                    ))
                                                            ).build()},
                                                            {"3", Transition.newBuilder().setName("pressed3").setDestinationNode(
                                                                    new Node().setPrompts(Arrays.<Prompt>asList(
                                                                            TextToSpeechPrompt.newBuilder().setMessage("be patient, we will call you").build()
                                                                    ))
                                                            ).build()}
                                                    })
                                            ).build()},
                                    {"2", Transition.newBuilder().setName("pressed2")
                                            .setDestinationNode(new Node().setPrompts(Arrays.<Prompt>asList(TextToSpeechPrompt.newBuilder().setMessage("Check with us again").build())))
                                            .build()}
                            }))
                    .build();
            tree.setName(NAME + i);
            treeDao.add(tree);
        }
    }

    @After
    public void tearDown() throws Exception {
        for (Tree t : treeDao.getAll()) {
            treeDao.remove(t);
        }
    }

    @Test
    public void testFindByName() {
        List<Tree> trees = treeDao.findByName(NAME + 5);
        System.out.print(trees.get(0));
        assertEquals(1, trees.size());
    }
}
