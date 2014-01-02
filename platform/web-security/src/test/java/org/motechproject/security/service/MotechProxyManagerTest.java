package org.motechproject.security.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.builder.SecurityRuleBuilder;
import org.motechproject.security.domain.MotechURLSecurityRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class MotechProxyManagerTest {

    @InjectMocks
    private MotechProxyManager motechProxyManager = new MotechProxyManager();

    @Mock
    private MotechURLSecurityService motechURLSecurityService;

    @Mock
    private SecurityRuleBuilder securityRuleBuilder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldOrderRulesByPriority() {
        List<MotechURLSecurityRule> rules = new ArrayList<>();

        rules.add(buildRule("priority-3", 3));
        rules.add(buildRule("catchall", 0));
        rules.add(buildRule("priority-1", 1));

        when(motechURLSecurityService.findAllSecurityRules()).thenReturn(rules);

        motechProxyManager.rebuildProxyChain();

        // we test on the security rule builder for simplicity
        InOrder inOrder = inOrder(securityRuleBuilder);
        inOrder.verify(securityRuleBuilder).buildSecurityChain(argThat(ruleMatcher("priority-3")), eq("GET"));
        inOrder.verify(securityRuleBuilder).buildSecurityChain(argThat(ruleMatcher("priority-1")), eq("GET"));
        inOrder.verify(securityRuleBuilder).buildSecurityChain(argThat(ruleMatcher("catchall")), eq("GET"));
    }

    private MotechURLSecurityRule buildRule(String pattern, int priority) {
        MotechURLSecurityRule rule = new MotechURLSecurityRule();
        rule.setPattern(pattern);
        rule.setPriority(priority);
        rule.setActive(true);
        rule.setMethodsRequired(new HashSet<>(Arrays.asList("GET")));
        return rule;
    }

    private ArgumentMatcher<MotechURLSecurityRule> ruleMatcher(final String pattern) {
        return new ArgumentMatcher<MotechURLSecurityRule>() {
            @Override
            public boolean matches(Object other) {
                MotechURLSecurityRule otherRule = (MotechURLSecurityRule) other;
                return StringUtils.equals(pattern, otherRule.getPattern());
            }
        };
    }
}
