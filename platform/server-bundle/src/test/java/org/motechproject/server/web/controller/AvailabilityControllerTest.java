package org.motechproject.server.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static java.util.Arrays.asList;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class AvailabilityControllerTest {

    public static final String MANAGE_IVR_PERMISSION = "manageIVR";
    public static final String MANAGE_SMS_PERMISSION = "manageSMS";
    private static final String MANAGE_MTRAINING_PERMISSION = "manageMTraining";

    public static final String VIEW_IVR_LOGS_PERMISSION = "viewIVRLogs";
    public static final String VIEW_SMS_LOGS_PERMISSION = "viewSMSLogs";
    private static final String VIEW_MTRAINING_LOGS_PERMISSION = "viewMTrainingLogs";

    private AvailabilityController availabilityController = new AvailabilityController();

    private MockMvc controller;

    @Before
    public void setUp() {
        controller = MockMvcBuilders.standaloneSetup(availabilityController).build();
    }

    @Test
    public void shouldReturnProperTabsForManagePermissions() throws Exception {

        setUpSecurityContextWithManagePermissions();

        controller.perform(get("/available/ivr"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("templates", "settings"))
                ));

        controller.perform(get("/available/sms"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("send", "settings"))
                ));

        controller.perform(get("/available/mTraining"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("treeView", "courses", "chapters", "quizzes", "lessons"))
                ));
    }

    @Test
    public void shouldReturnProperTabsForViewLogsPermissions() throws Exception {

        setUpSecurityContextWithViewLogsPermissions();

        controller.perform(get("/available/ivr"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("log"))
                ));

        controller.perform(get("/available/sms"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("log"))
                ));

        controller.perform(get("/available/mTraining"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("activityRecords", "bookmarks"))
                ));
    }

    private void setUpSecurityContextWithManagePermissions() {

        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "testpassword",
                asList(new SimpleGrantedAuthority(MANAGE_IVR_PERMISSION),
                       new SimpleGrantedAuthority(MANAGE_SMS_PERMISSION),
                       new SimpleGrantedAuthority(MANAGE_MTRAINING_PERMISSION)));
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setUpSecurityContextWithViewLogsPermissions() {

        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "testpassword",
                asList(new SimpleGrantedAuthority(VIEW_IVR_LOGS_PERMISSION),
                       new SimpleGrantedAuthority(VIEW_SMS_LOGS_PERMISSION),
                       new SimpleGrantedAuthority(VIEW_MTRAINING_LOGS_PERMISSION)));
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }
}
