package org.motechproject.server.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityControllerTest {

    public static final String MANAGE_PERMISSION = "managePermission";
    public static final String VIEW_LOGS_PERMISSION = "viewLogsPermission";

    public static final String MANAGER_PANEL = "managerPanel";
    public static final String SETTINGS_PANEL = "settingsPanel";
    public static final String LOGS_PANEL = "logsPanel";
    public static final String NO_PERM_PANEL = "noPermissionsPanel";

    public LinkedHashMap<String, List<String>> tabAccessMap;

    @Mock
    private ModuleRegistrationData moduleRegistrationData;

    @Mock
    private UIFrameworkService uiFrameworkService;

    @InjectMocks
    private AvailabilityController availabilityController = new AvailabilityController();

    private MockMvc controller;

    @Before
    public void setUp() {

        tabAccessMap = new LinkedHashMap<>();
        tabAccessMap.put(MANAGER_PANEL, Arrays.asList(MANAGE_PERMISSION));
        tabAccessMap.put(SETTINGS_PANEL, Arrays.asList(MANAGE_PERMISSION));
        tabAccessMap.put(LOGS_PANEL, Arrays.asList(MANAGE_PERMISSION, VIEW_LOGS_PERMISSION));
        tabAccessMap.put(NO_PERM_PANEL, Arrays.asList());

        when(uiFrameworkService.getModuleData(any(String.class))).thenReturn(moduleRegistrationData);
        when(moduleRegistrationData.getTabAccessMap()).thenReturn(tabAccessMap);
        controller = MockMvcBuilders.standaloneSetup(availabilityController).build();
    }

    @Test
    public void shouldReturnProperTabsForManagePermissions() throws Exception {

        setUpSecurityContext(asList(new SimpleGrantedAuthority(MANAGE_PERMISSION)));

        controller.perform(get("/available/someModule"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("managerPanel", "settingsPanel", "logsPanel", "noPermissionsPanel"))
                ));
    }

    @Test
    public void shouldReturnProperTabsForViewLogsPermissions() throws Exception {

        setUpSecurityContext(asList(new SimpleGrantedAuthority(VIEW_LOGS_PERMISSION)));

        Map<String, List<String>> tabAccessMap2 = moduleRegistrationData.getTabAccessMap();

        controller.perform(get("/available/someModule"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("logsPanel", "noPermissionsPanel"))
                ));
    }

    @Test
    public void shouldReturnProperTabsForMultiplePermissions() throws Exception {

        setUpSecurityContext(asList(new SimpleGrantedAuthority(MANAGE_PERMISSION),
                new SimpleGrantedAuthority(VIEW_LOGS_PERMISSION)));

        controller.perform(get("/available/someModule"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        new ObjectMapper().writeValueAsString(asList("managerPanel", "settingsPanel", "logsPanel", "noPermissionsPanel"))
                ));
    }

    private void setUpSecurityContext(List<SimpleGrantedAuthority> authorities) {

        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "testpassword", authorities);
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }
}
