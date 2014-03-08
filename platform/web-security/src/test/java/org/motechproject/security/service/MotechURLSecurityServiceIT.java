package org.motechproject.security.service;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechRolesCouchdbImpl;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.motechproject.security.repository.AllMotechSecurityRulesCouchdbImpl;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllMotechUsersCouchdbImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={ "classpath*:/META-INF/motech/*.xml", "classpath*:/META-INF/security/*.xml"})
public class MotechURLSecurityServiceIT extends SpringIntegrationTest {

    @Autowired
    @Qualifier("webSecurityDbConnector")
    private CouchDbConnector connector;

    @Autowired
    private MotechURLSecurityService securityService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Before
    public void onStartUp() {
        ((AllMotechUsersCouchdbImpl) allMotechUsers).removeAll();
        ((AllMotechRolesCouchdbImpl) allMotechRoles).removeAll();
        allMotechRoles.add(new MotechRoleCouchdbImpl("SECURITY_VIEW_ADMIN", asList("viewSecurity"), false));
        allMotechRoles.add(new MotechRoleCouchdbImpl("SECURITY_UPDATE_ADMIN", asList("updateSecurity"), false));

    }

    @Test(expected=AccessDeniedException.class)
    public void testNoReadAccess() {
        motechUserService.register("admin", "admin", "admin@mail.com", "", Arrays.asList("noRole"), Locale.ENGLISH);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        assertEquals(0, securityService.findAllSecurityRules());
    }

    @Test
    public void testHasReadAccess() {
        motechUserService.register("admin", "admin", "admin@mail.com", "", asList("SECURITY_VIEW_ADMIN"), Locale.ENGLISH);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        assertEquals(0, securityService.findAllSecurityRules().size());
    }

    @Test
    public void  testUpdateSecurity() {
        motechUserService.register("admin", "admin", "admin@mail.com", "", asList("SECURITY_UPDATE_ADMIN", "SECURITY_VIEW_ADMIN"), Locale.ENGLISH);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        List<MotechURLSecurityRule> rules = new ArrayList<MotechURLSecurityRule>();
        addRules(rules);

        securityService.updateSecurityConfiguration(new MotechSecurityConfiguration(rules));

        //Shouldn't return rule marked as deleted.
        assertEquals(3, securityService.findAllSecurityRules().size());
    }

    private void addRules(List<MotechURLSecurityRule> securityRules) {
        MotechURLSecurityRule rule1 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule2 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule3 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule4 = new MotechURLSecurityRule();

        List<String> methodsRequired = Arrays.asList("ANY");
        rule1.setPattern("/**");
        rule2.setPattern("/web-api/**");
        rule3.setPattern("/anything");
        rule4.setPattern("/unimportant/**");

        rule1.setProtocol("HTTPS");
        rule2.setProtocol("HTTPS");
        rule3.setProtocol("HTTP");
        rule4.setProtocol("HTTP");

        rule1.setMethodsRequired(new HashSet<String>(methodsRequired));
        rule2.setMethodsRequired(new HashSet<String>(methodsRequired));
        rule3.setMethodsRequired(new HashSet<String>(Arrays.asList("GET", "POST")));
        rule4.setMethodsRequired(new HashSet<String>(methodsRequired));

        rule1.setSupportedSchemes(Arrays.asList("BASIC"));
        rule2.setSupportedSchemes(Arrays.asList("OATH"));
        rule3.setSupportedSchemes(Arrays.asList("NO_SECURITY"));
        rule4.setSupportedSchemes(Arrays.asList("NO_SECURITY"));

        rule4.setDeleted(true);

        securityRules.add(rule1);
        securityRules.add(rule2);
        securityRules.add(rule3);
        securityRules.add(rule4);

    }
    @After
    public void tearDown() {
        ((AllMotechUsersCouchdbImpl) allMotechUsers).removeAll();
        ((AllMotechRolesCouchdbImpl) allMotechRoles).removeAll();
        ((AllMotechSecurityRulesCouchdbImpl) allSecurityRules).removeAll();
        super.tearDown();
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

}
