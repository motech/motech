/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.server.ruleengine;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Ricky Wang
 */
public class FilesystemRuleLoader {

    private static Logger logger = LoggerFactory.getLogger(FilesystemRuleLoader.class);

    // default rule folder
    private String ruleFolder = "/rules";

    @Autowired
    private KnowledgeBaseManager knowledgeBaseManager;

    public void load() throws Exception {
        File[] ruleFiles = new File(URLDecoder.decode(getClass().getResource(ruleFolder).getFile(), "UTF-8")).listFiles();
        for (File file : ruleFiles) {
            if (file.getName().toLowerCase().endsWith(".drl")) {
                try {
                    knowledgeBaseManager.addOrUpdateRule(file);
                } catch (IOException e) {
                    logger.error("Failed to load the rule file [" + file.getName() + "]", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setRuleFolder(String ruleFolder) {
        this.ruleFolder = ruleFolder;
    }

    public void setKnowledgeBaseManager(KnowledgeBaseManager knowledgeBaseManager) {
        this.knowledgeBaseManager = knowledgeBaseManager;
    }
}
