package org.motechproject.security.builder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.exception.SecurityConfigException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.motechproject.security.constants.HTTPMethod.ANY;
import static org.motechproject.security.constants.HTTPMethod.GET;
import static org.motechproject.security.constants.Protocol.HTTP;
import static org.motechproject.security.constants.Scheme.USERNAME_PASSWORD;

@RunWith(MockitoJUnitRunner.class)
public class SecurityRuleBuilderTest {

    @Rule
    public ExpectedException configException = ExpectedException.none();

    @Mock
    public AuthenticationEntryPoint entryPoint;

    @InjectMocks
    private SecurityRuleBuilder securityBuilder = new SecurityRuleBuilder();

    @Test
    public void testShouldRequirePattern() {

        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_PATTERN_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testShouldRequireProtocol() {

        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_PROTOCOL_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testShouldRequireSupportedScheme() {
        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_SUPPORTED_SCHEMES_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        securityRule.setProtocol(HTTP);

        securityBuilder.buildSecurityChain(securityRule, GET);
    }

    @Test
    public void testShouldRequireMethodsSupported() {

        configException.expect(SecurityConfigException.class);
        configException.expectMessage(SecurityRuleBuilder.NO_METHODS_REQUIRED_EXCEPTION_MESSAGE);

        MotechURLSecurityRule securityRule = new MotechURLSecurityRule();
        securityRule.setPattern("pattern");
        securityRule.setProtocol(HTTP);
        securityRule.setSupportedSchemes(Arrays.asList(USERNAME_PASSWORD));

        securityBuilder.buildSecurityChain(securityRule, GET);
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
