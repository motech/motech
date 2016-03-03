package org.motechproject.mds.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.service.MotechUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private MotechUserService motechUserService;

    @InjectMocks
    private UserController userController = new UserController();

    private MockMvc controller;

    @Before
    public void setUp() {
        controller = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldReturnUsers() throws Exception {
        when(motechUserService.getUsers()).thenReturn(users());

        controller.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(users())));
    }

    @Test
    public void shouldReturnEmptyListForUsersWithoutAccess() throws Exception {
        when(motechUserService.getUsers()).thenThrow(new AccessDeniedException("denied"));

        controller.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    private List<MotechUserProfile> users() {
        MotechUser user = new MotechUser("john", "pass", "john@doe.com", "ext", asList("role1", "role2"), null, Locale.ENGLISH);
        MotechUserProfile profile1 = new MotechUserProfile(user);

        user = new MotechUser("Bob", "pass2", "bob@example.com", "ext2", Collections.<String>emptyList(), null, Locale.GERMAN);
        MotechUserProfile profile2 = new MotechUserProfile(user);

        return asList(profile1, profile2);
    }
}
