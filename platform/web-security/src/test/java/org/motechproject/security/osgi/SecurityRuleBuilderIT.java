package org.motechproject.security.osgi;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.security.builder.SecurityRuleBuilder;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.ex.SecurityConfigException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.motechproject.security.constants.HTTPMethod.ANY;
import static org.motechproject.security.constants.HTTPMethod.GET;
import static org.motechproject.security.constants.Protocol.HTTP;
import static org.motechproject.security.constants.Scheme.USERNAME_PASSWORD;

public class SecurityRuleBuilderIT extends BaseIT {

    @Rule
    public ExpectedException configException = ExpectedException.none();

    private SecurityRuleBuilder securityBuilder;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        securityBuilder = getFromContext(SecurityRuleBuilder.class);
    }

    @Test
    public void testShouldRequirePattern() {

        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_PATTERN_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        SecurityFilterChain filterChain = securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testShouldRequireProtocol() {

        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_PROTOCOL_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        SecurityFilterChain filterChain = securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testShouldRequireSupportedScheme() {
        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_SUPPORTED_SCHEMES_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        securityRule.setProtocol(HTTP);

        SecurityFilterChain filterChain = securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testShouldRequireMethodsSupported() {

        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_METHODS_REQUIRED_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        securityRule.setProtocol(HTTP);
        securityRule.setSupportedSchemes(Arrays.asList(USERNAME_PASSWORD));

        SecurityFilterChain filterChain = securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testMinimalRequirements() {

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        securityRule.setProtocol(HTTP);
        securityRule.setSupportedSchemes(Arrays.asList(USERNAME_PASSWORD));
        securityRule.setMethodsRequired(Arrays.asList(ANY));

        SecurityFilterChain filterChain = securityBuilder.buildSecurityChain(securityRule, GET);

        assertNotNull(filterChain);
        assertEquals(10, filterChain.getFilters().size());
    }

}
