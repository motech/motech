package org.motechproject.security.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Contains;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class UserControllerTest {


    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController = new UserController();

    @Mock
    private MotechUserService userService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldReturnCurrentUserDetails() throws Exception {
        User user = new User("john", "password", Arrays.asList(new SimpleGrantedAuthority("admin")));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, user.getPassword());

        UserDto userDto = new UserDto();
        userDto.setUserName("john");
        userDto.setEmail("john@gmail.com");

        when(userService.getCurrentUser()).thenReturn(userDto);

        mockMvc.perform(get("/users/current").principal(authenticationToken))
                .andExpect(status().isOk())
                .andExpect(content().string(new Contains("\"userName\":\"john\"")));
    }

}
