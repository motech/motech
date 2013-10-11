package org.motechproject.security.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllMotechSecurityRulesIT {

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @Test
    public void testSavingConfig() {
        assertEquals(0, allSecurityRules.getRules().size());

        List<MotechURLSecurityRule> securityRules = new ArrayList<MotechURLSecurityRule>();
        addRules(securityRules);

        MotechSecurityConfiguration config = new MotechSecurityConfiguration(securityRules);
        allSecurityRules.add(config);

        assertEquals(3, allSecurityRules.getRules().size());
    }

    private void addRules(List<MotechURLSecurityRule> securityRules) {
        MotechURLSecurityRule rule1 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule2 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule3 = new MotechURLSecurityRule();

        securityRules.add(rule1);
        securityRules.add(rule2);
        securityRules.add(rule3);

    }

    @Before
    public void setUp() {
        ((AllMotechSecurityRulesCouchdbImpl) allSecurityRules).removeAll();
    }

    @After
    public void tearDown() {
        ((AllMotechSecurityRulesCouchdbImpl) allSecurityRules).removeAll();

    }
}
