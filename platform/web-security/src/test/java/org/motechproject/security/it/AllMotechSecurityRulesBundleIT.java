package org.motechproject.security.it;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.repository.AllMotechSecurityRules;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllMotechSecurityRulesBundleIT extends BaseIT {

    private AllMotechSecurityRules allSecurityRules;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        allSecurityRules = getFromContext(AllMotechSecurityRules.class);
        getSecurityRuleDataService().deleteAll();
    }

    @Test
    public void testSavingConfig() {
        assertEquals(0, allSecurityRules.getRules().size());

        List<MotechURLSecurityRule> securityRules = new ArrayList<>();
        addRules(securityRules);

        MotechSecurityConfiguration config = new MotechSecurityConfiguration(securityRules);
        allSecurityRules.addOrUpdate(config);

        assertEquals(3, allSecurityRules.getRules().size());
    }

    private void addRules(List<MotechURLSecurityRule> securityRules) {
        MotechURLSecurityRule rule1 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule2 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule3 = new MotechURLSecurityRule();

        rule1.setPattern("/**");
        rule2.setPattern("/test/**");
        rule3.setPattern("/something/else.html");

        securityRules.add(rule1);
        securityRules.add(rule2);
        securityRules.add(rule3);

    }

}
