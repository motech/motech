package org.motechproject.server.ruleengine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URLDecoder;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.dao.AllRules;
import org.motechproject.model.Rule;


public class KnowledgeBaseManagerTest {

    @Test
    @Ignore
    public void addOrUpdateRuleTest() throws Exception {
        String ruleFolder = "/rules";
        String ruleFile = "test.drl";

        KnowledgeBaseManager kbm = new KnowledgeBaseManager();
        AllRules repo = mock(AllRules.class);
        kbm.setAllRules(repo);

        File file = new File(URLDecoder.decode(getClass().getResource(ruleFolder + "/" + ruleFile).getFile(), "UTF-8"));

        kbm.addOrUpdateRule(file);

        verify(repo).contains(ruleFile);
        verify(repo).add(any(Rule.class));

        KnowledgeBase kbase = kbm.getKnowledgeBase(ruleFile);
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        Applicant applicant = new Applicant("Mr John Smith", 16);
        assertTrue(applicant.isValid());
        ksession.execute(applicant);
        assertFalse(applicant.isValid());
    }

}

