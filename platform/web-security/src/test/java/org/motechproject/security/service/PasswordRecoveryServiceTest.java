package org.motechproject.security.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.email.impl.EmailSenderImpl;
import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.ex.NonAdminUserException;
import org.motechproject.security.ex.UserNotFoundException;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllPasswordRecoveries;
import org.motechproject.security.service.impl.PasswordRecoveryServiceImpl;
import org.motechproject.security.velocity.VelocityTemplateParser;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PasswordRecoveryServiceTest extends BaseUnitTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "username@domain.net";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String ENCODED_PASSWORD = "p455w012d";
    private static final List<String> ROLES = Arrays.asList("admin");


    @Mock
    private MotechUser user;

    @Mock
    private AllMotechUsers allMotechUsers;

    @Mock
    private PasswordRecovery recovery;

    @Mock
    private EmailSender emailSender;

    @Mock
    private MotechPasswordEncoder passwordEncoder;

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private AllPasswordRecoveries allPasswordRecoveries;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private MotechEvent emailEvent;

    @Mock
    private VelocityTemplateParser templateParser;

    @InjectMocks
    private EmailSender emailSenderInjected = new EmailSenderImpl();

    @InjectMocks
    private PasswordRecoveryService recoveryService = new PasswordRecoveryServiceImpl();

    private MotechSettings motechSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testCreateRecovery() throws UserNotFoundException {
        final DateTime now = DateTime.now();
        mockCurrentDate(now);

        when(allMotechUsers.findUserByEmail(EMAIL)).thenReturn(user);
        when(user.getUserName()).thenReturn(USERNAME);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(allPasswordRecoveries.createRecovery(any(String.class), any(String.class),
                any(String.class), any(DateTime.class), any(Locale.class))).thenReturn(recovery);

        recoveryService.passwordRecoveryRequest(EMAIL);

        verify(allPasswordRecoveries).createRecovery(eq(USERNAME), eq(EMAIL), argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                String str = (String) argument;
                return str.length() == 60;
            }
        }), argThat(new ArgumentMatcher<DateTime>() {
            @Override
            public boolean matches(Object argument) {
                DateTime time = (DateTime) argument;
                return time.equals(now.plusHours(1));
            }
        }), eq(Locale.ENGLISH));
        verify(emailSender).sendRecoveryEmail(recovery);
    }


    @Test(expected = UserNotFoundException.class)
    public void testUserNotFound() throws UserNotFoundException {
        when(allMotechUsers.findUserByEmail(EMAIL)).thenReturn(null);
        recoveryService.passwordRecoveryRequest(EMAIL);
    }

    @Test
    public void testCleanUpExpired() {
        when(allPasswordRecoveries.getExpired()).thenReturn(Arrays.asList(recovery));
        recoveryService.cleanUpExpiredRecoveries();
        verify(allPasswordRecoveries).remove(recovery);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongConfirmation() throws InvalidTokenException {
        recoveryService.resetPassword(TOKEN, PASSWORD, "ppassword");
    }

    @Test(expected = InvalidTokenException.class)
    public void testInvalidToken() throws InvalidTokenException {
        when(allPasswordRecoveries.findForToken(TOKEN)).thenReturn(null);
        recoveryService.resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test(expected = InvalidTokenException.class)
    public void testExpiredRequest() throws InvalidTokenException {
        final DateTime now = DateTime.now();
        mockCurrentDate(now);
        when(allPasswordRecoveries.findForToken(TOKEN)).thenReturn(recovery);
        when(recovery.getExpirationDate()).thenReturn(now);

        recoveryService.resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void testResetPassword() throws InvalidTokenException {
        final DateTime now = DateTime.now();
        mockCurrentDate(now);
        when(allPasswordRecoveries.findForToken(TOKEN)).thenReturn(recovery);
        when(recovery.getExpirationDate()).thenReturn(now.plusMinutes(30));
        when(recovery.getUsername()).thenReturn(USERNAME);
        when(allMotechUsers.findByUserName(USERNAME)).thenReturn(user);
        when(passwordEncoder.encodePassword(PASSWORD)).thenReturn(ENCODED_PASSWORD);

        recoveryService.resetPassword(TOKEN, PASSWORD, PASSWORD);

        verify(user).setPassword(ENCODED_PASSWORD);
        verify(allMotechUsers).update(user);
        verify(allPasswordRecoveries).remove(recovery);
    }

    @Test
    public void testCreateOpenIDRecovery() throws UserNotFoundException, NonAdminUserException {
        final DateTime now = DateTime.now();
        mockCurrentDate(now);

        when(allMotechUsers.findUserByEmail(EMAIL)).thenReturn(user);

        when(user.getUserName()).thenReturn(USERNAME);
        when(user.getRoles()).thenReturn(ROLES);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(allPasswordRecoveries.createRecovery(any(String.class), any(String.class),
                any(String.class), any(DateTime.class), any(Locale.class))).thenReturn(recovery);

        recoveryService.oneTimeTokenOpenId(EMAIL);

        verify(allPasswordRecoveries).createRecovery(eq(USERNAME), eq(EMAIL), argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                String str = (String) argument;
                return str.length() == 60;
            }
        }), argThat(new ArgumentMatcher<DateTime>() {
            @Override
            public boolean matches(Object argument) {
                DateTime time = (DateTime) argument;
                return time.equals(now.plusHours(1));
            }
        }), eq(Locale.ENGLISH));
        verify(emailSender).sendOneTimeToken(recovery);
    }

    @Test
    public void testSendRecoveryEmail() {
        motechSettings = mock(MotechSettings.class);
        when(settingsFacade.getPlatformSettings()).thenReturn(motechSettings);
        when(motechSettings.getServerHost()).thenReturn("serverurl");
        when(motechSettings.getServerUrl()).thenReturn("http://serverurl");
        when(motechSettings.getLoginMode()).thenReturn(LoginMode.REPOSITORY);

        PasswordRecovery newRecovery = new PasswordRecovery();
        newRecovery.setUsername(USERNAME);
        newRecovery.setEmail(EMAIL);
        newRecovery.setToken(TOKEN);
        newRecovery.setExpirationDate(DateTime.now().plusHours(1));
        newRecovery.setLocale(Locale.ENGLISH);

        emailSenderInjected.sendRecoveryEmail(newRecovery);
        verify(eventRelay).sendEventMessage(any(emailEvent.getClass()));
    }

    @Test(expected = UserNotFoundException.class)
    public void testNoFindUserInOneTimeToken() throws UserNotFoundException, NonAdminUserException {
        when(user.getUserName()).thenReturn(null);
        recoveryService.oneTimeTokenOpenId(EMAIL);
    }

    @Test(expected = NonAdminUserException.class)
    public void testNonAdminUserInOneTimeToken() throws UserNotFoundException, NonAdminUserException {
        when(allMotechUsers.findUserByEmail(EMAIL)).thenReturn(user);
        when(user.getRoles()).thenReturn(Arrays.asList("user"));
        recoveryService.oneTimeTokenOpenId(EMAIL);
    }

}
