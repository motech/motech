package org.motechproject.security.web.controllers;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Contains;
import org.motechproject.osgi.web.LocaleService;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.ex.PasswordTooShortException;
import org.motechproject.security.ex.PasswordValidatorException;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.security.validator.PasswordValidator;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController = new UserController();

    @Mock
    private MotechUserService userService;

    @Mock
    private SettingService settingService;

    @Mock
    private PasswordValidator validator;

    @Mock
    private LocaleService localeService;

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

    @Test
    public void shouldPrintErrorsFromValidators() throws Exception {
        when(localeService.getUserLocale(any(HttpServletRequest.class))).thenReturn(Locale.GERMAN);
        when(settingService.getPasswordValidator()).thenReturn(validator);
        when(validator.getValidationError(Locale.GERMAN)).thenReturn("Error from validator");

        doThrow(new PasswordValidatorException("error")).when(userService)
                .register(eq("john"), eq("invalid"), eq("john@gmail.com"), eq(""), anyListOf(String.class), any(Locale.class));

        UserDto userDto = new UserDto();
        userDto.setUserName("john");
        userDto.setEmail("john@gmail.com");
        userDto.setPassword("invalid");

        mockMvc.perform(post("/users/create").body(new ObjectMapper().writeValueAsBytes(userDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("literal:Error from validator"));
    }

    @Test
    public void shouldPrintMinPasswordLengthError() throws Exception {
        when(localeService.getUserLocale(any(HttpServletRequest.class))).thenReturn(Locale.GERMAN);
        when(settingService.getPasswordValidator()).thenReturn(validator);
        when(validator.getValidationError(Locale.GERMAN)).thenReturn("Error from validator");

        doThrow(new PasswordTooShortException(20)).when(userService)
                .register(eq("john"), eq("invalid"), eq("john@gmail.com"), eq(""), anyListOf(String.class), any(Locale.class));

        UserDto userDto = new UserDto();
        userDto.setUserName("john");
        userDto.setEmail("john@gmail.com");
        userDto.setPassword("invalid");

        mockMvc.perform(post("/users/create").body(new ObjectMapper().writeValueAsBytes(userDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("key:security.validator.error.min_length\nparams:20"));
    }

    @Test
    public void shouldGeneratePasswordRespectingMinLength() throws Exception {
        when(settingService.getMinPasswordLength()).thenReturn(20);

        UserDto userDto = new UserDto();
        userDto.setGeneratePassword(true);
        userDto.setUserName("john");
        userDto.setEmail("john@email.com");
        userDto.setLocale(Locale.CANADA_FRENCH);

        mockMvc.perform(post("/users/create").body(new ObjectMapper().writeValueAsBytes(userDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).register(eq("john"), passwordCaptor.capture(), eq("john@email.com"), eq(""),
                eq((List<String>) null), eq(Locale.CANADA_FRENCH));

        assertNotNull(passwordCaptor.getValue());
        assertTrue(passwordCaptor.getValue().length() >= 20);

        verify(userService).sendLoginInformation("john", passwordCaptor.getValue());
    }
}
