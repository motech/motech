package org.motechproject.security.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.constants.PermissionNames;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.model.SecurityConfigDto;
import org.motechproject.security.model.SecurityRuleDto;
import org.motechproject.security.repository.MotechRolesDataService;
import org.motechproject.security.repository.MotechUsersDataService;
import org.motechproject.security.service.MotechURLSecurityService;
import org.motechproject.security.service.MotechUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.motechproject.security.constants.HTTPMethod.ANY;
import static org.motechproject.security.constants.HTTPMethod.GET;
import static org.motechproject.security.constants.HTTPMethod.POST;
import static org.motechproject.security.constants.Protocol.HTTP;
import static org.motechproject.security.constants.Protocol.HTTPS;
import static org.motechproject.security.constants.Scheme.BASIC;
import static org.motechproject.security.constants.Scheme.NO_SECURITY;
import static org.motechproject.security.constants.Scheme.OATH;

public class MotechURLSecurityServiceBundleIT extends BaseIT {

    private static final String SECURITY_MANAGE_ADMIN = "SECURITY_MANAGE_ADMIN";

    @Inject
    private MotechUserService motechUserService;

    @Inject
    private MotechUsersDataService usersDataService;

    @Inject
    private MotechRolesDataService rolesDataService;

    private MotechURLSecurityService securityService;
    private AuthenticationManager authenticationManager;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        securityService = getFromContext(MotechURLSecurityService.class, "motechURLSecurityService");
        authenticationManager = getFromContext(AuthenticationManager.class, "authenticationManager");

        motechUserService.registerMotechAdmin("motech", "motech", "aaa@admin.com", Locale.ENGLISH);
        setUpSecurityContext("motech", "motech", getPermissions());

        getSecurityRuleDataService().deleteAll();
        usersDataService.deleteAll();
        rolesDataService.deleteAll();

        rolesDataService.create(new MotechRole(SECURITY_MANAGE_ADMIN, asList(PermissionNames.MANAGE_URL_PERMISSION), false));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        usersDataService.deleteAll();
        rolesDataService.deleteAll();

        clearSecurityContext();
    }

    @Test(expected = AccessDeniedException.class)
    public void testNoReadAccess() {
        motechUserService.register("admin", "admin", "admin@mail.com", "", Arrays.asList("noRole"), Locale.ENGLISH);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        securityService.findAllSecurityRules();
    }

    @Test
    public void testHasReadAccess() {
        motechUserService.register("admin", "admin", "admin@mail.com", "", asList(SECURITY_MANAGE_ADMIN), Locale.ENGLISH);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        securityService.findAllSecurityRules();
    }

    @Test
    public void testUpdateSecurity() {
        motechUserService.register("admin", "admin", "admin@mail.com", "", asList(SECURITY_MANAGE_ADMIN), Locale.ENGLISH);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        List<SecurityRuleDto> rules = new ArrayList<>();
        addRules(rules);

        SecurityConfigDto configuration = new SecurityConfigDto();
        configuration.setSecurityRules(rules);

        securityService.updateSecurityConfiguration(configuration);

        //Shouldn't return rule marked as deleted.
        assertEquals(3, securityService.findAllSecurityRules().size());
    }

    private void addRules(List<SecurityRuleDto> securityRules) {
        SecurityRuleDto rule1 = new SecurityRuleDto();
        SecurityRuleDto rule2 = new SecurityRuleDto();
        SecurityRuleDto rule3 = new SecurityRuleDto();
        SecurityRuleDto rule4 = new SecurityRuleDto();

        List<String> methodsRequired = Arrays.asList(ANY.toString());
        rule1.setPattern("/**");
        rule2.setPattern("/web-api/**");
        rule3.setPattern("/anything");
        rule4.setPattern("/unimportant/**");

        rule1.setProtocol(HTTPS.toString());
        rule2.setProtocol(HTTPS.toString());
        rule3.setProtocol(HTTP.toString());
        rule4.setProtocol(HTTP.toString());

        rule1.setMethodsRequired(methodsRequired);
        rule2.setMethodsRequired(methodsRequired);
        rule3.setMethodsRequired(Arrays.asList(GET.toString(), POST.toString()));
        rule4.setMethodsRequired(methodsRequired);

        rule1.setSupportedSchemes(Arrays.asList(BASIC.toString()));
        rule2.setSupportedSchemes(Arrays.asList(OATH.toString()));
        rule3.setSupportedSchemes(Arrays.asList(NO_SECURITY.toString()));
        rule4.setSupportedSchemes(Arrays.asList(NO_SECURITY.toString()));

        rule4.setDeleted(true);

        securityRules.add(rule1);
        securityRules.add(rule2);
        securityRules.add(rule3);
        securityRules.add(rule4);

    }

}
