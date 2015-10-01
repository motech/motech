package org.motechproject.security.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.ex.PasswordValidatorException;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.impl.MotechUserServiceImpl;
import org.motechproject.security.validator.PasswordValidator;
import org.springframework.security.authentication.LockedException;

import java.util.Collections;
import java.util.Locale;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MotechUserServiceTest {

    private static final String USER = "user_1";
    private static final String PASSWORD = "old_password";
    private static final String NEW_PASSWORD = "new_password";

    @InjectMocks
    private MotechUserService motechUserService = new MotechUserServiceImpl();

    @Mock
    private PasswordValidator validator;

    @Mock
    private SettingService settingService;

    @Mock
    private AllMotechUsers allMotechUsers;

    @Mock
    private MotechUser user;

    @Mock
    MotechPasswordEncoder motechPasswordEncoder;

    @Before
    public void setUp() {
        when(settingService.getPasswordValidator()).thenReturn(validator);
    }

    @Test(expected = PasswordValidatorException.class)
    public void shouldValidatePasswordOnRegistration() {
        doThrow(new PasswordValidatorException("error")).when(validator).validate("wrong");
        motechUserService.register("user", "wrong", "email@gmail.com", "", Collections.<String>emptyList(), Locale.ENGLISH);
    }

    @Test(expected = PasswordValidatorException.class)
    public void shouldValidatePasswordOnChange() {
        when(allMotechUsers.findByUserName("user")).thenReturn(user);
        doThrow(new PasswordValidatorException("error")).when(validator).validate("wrong");

        motechUserService.changePassword("user", "old", "wrong");
    }

    @Test(expected = PasswordValidatorException.class)
    public void shouldValidatePasswordOnEdit() {
        UserDto userDto = new UserDto();
        userDto.setPassword("wrong");
        userDto.setUserName("user");
        when(allMotechUsers.findByUserName("user")).thenReturn(user);
        doThrow(new PasswordValidatorException("error")).when(validator).validate("wrong");

        motechUserService.updateUserDetailsWithPassword(userDto);
    }

    @Test(expected = LockedException.class)
    public void shouldBlockUserAfterCrossingTheFailureLoginCounter() {
        MotechUser motechUser = new MotechUser();
        motechUser.setUserStatus(UserStatus.MUST_CHANGE_PASSWORD);
        motechUser.setPassword(PASSWORD);
        motechUser.setUserName(USER);
        motechUser.setFailureLoginCounter(1);

        when(allMotechUsers.findByUserName(USER)).thenReturn(motechUser);
        when(motechPasswordEncoder.isPasswordValid(PASSWORD, PASSWORD)).thenReturn(false);
        when(motechPasswordEncoder.isPasswordValid(PASSWORD, NEW_PASSWORD)).thenReturn(false);
        when(settingService.getFailureLoginLimit()).thenReturn(1);

        motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

}
