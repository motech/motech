package org.motechproject.security.service.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.api.Range;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.email.impl.EmailSenderImpl;
import org.motechproject.security.exception.InvalidTokenException;
import org.motechproject.security.exception.NonAdminUserException;
import org.motechproject.security.exception.UserNotFoundException;
import org.motechproject.security.repository.MotechUsersDao;
import org.motechproject.security.mds.PasswordRecoveriesDataService;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.security.velocity.VelocityTemplateParser;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;

public class PasswordRecoveryServiceTest {

    private static final int EXPIRATION_HOURS = 5;
    private static final String USERNAME = "username";
    private static final String EMAIL = "username@domain.net";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String ENCODED_PASSWORD = "p455w012d";
    private static final List<String> ROLES = Arrays.asList("admin");

    @Mock
    private MotechUser user;

    @Mock
    private MotechUsersDao motechUsersDao;

    @Mock
    private PasswordRecovery recovery;

    @Mock
    private EmailSender emailSender;

    @Mock
    private MotechPasswordEncoder passwordEncoder;

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private PasswordRecoveriesDataService passwordRecoveriesDataService;

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
    private ResourceBundleMessageSource messageSource;

    @Before
    public void setUp() {
        initMocks(this);
        // Fake DateTime.now()
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        prepareMessageSource();
        prepareEmailSender();
    }

    @After
    public void tearDown() {
        // Stop faking time
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldCreateRecoveryAndSendEmail() throws UserNotFoundException {
        final DateTime now = DateTime.now();
        final int expiration = EXPIRATION_HOURS;

        testCreateRecoveryTemplate(now, EMAIL, now.plusHours(expiration), true);
        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        verify(passwordRecoveriesDataService).create(captor.capture());

        PasswordRecovery createdRecovery = captor.getValue();

        assertEquals(USERNAME, createdRecovery.getUsername());
        assertEquals(EMAIL, createdRecovery.getEmail());
        assertEquals(now.plusHours(expiration), createdRecovery.getExpirationDate());
        assertEquals(Locale.ENGLISH, createdRecovery.getLocale());
        assertEquals(60, createdRecovery.getToken().length());

        verify(emailSender).sendRecoveryEmail(createdRecovery);
    }

    @Test
    public void shouldCreateRecoveryWithoutNotification() throws UserNotFoundException {
        final DateTime now = DateTime.now();
        final int expiration = EXPIRATION_HOURS;

        testCreateRecoveryTemplate(now, EMAIL, now.plusHours(expiration), false);
        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        verify(passwordRecoveriesDataService).create(captor.capture());

        PasswordRecovery createdRecovery = captor.getValue();

        assertEquals(USERNAME, createdRecovery.getUsername());
        assertEquals(EMAIL, createdRecovery.getEmail());
        assertEquals(now.plusHours(expiration), createdRecovery.getExpirationDate());
        assertEquals(Locale.ENGLISH, createdRecovery.getLocale());
        assertEquals(60, createdRecovery.getToken().length());

        verifyZeroInteractions(emailSender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExcetionForCreatingRecoveryWithExpirationDateInPast() throws UserNotFoundException {
        final DateTime now = DateTime.now();
        final int expiration = -2;

        testCreateRecoveryTemplate(now, EMAIL, now.plusHours(expiration), true);
    }

    @Test
    public void shouldCreateRecoveryWithDefaultExpirationTimeIfNoneWasProvided() throws UserNotFoundException {
        final DateTime now = DateTime.now();

        testCreateRecoveryTemplate(now, EMAIL, null, true);
        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        verify(passwordRecoveriesDataService).create(captor.capture());

        PasswordRecovery createdRecovery = captor.getValue();

        assertEquals(USERNAME, createdRecovery.getUsername());
        assertEquals(EMAIL, createdRecovery.getEmail());
        // 3 is the default set in PasswordRecoveryServiceImpl
        assertEquals(now.plusHours(3), createdRecovery.getExpirationDate());
        assertEquals(Locale.ENGLISH, createdRecovery.getLocale());
        assertEquals(60, createdRecovery.getToken().length());

        verify(emailSender).sendRecoveryEmail(createdRecovery);
    }

    @Test(expected = UserNotFoundException.class)
    public void shouldThrowExceptionWhenUserCannotBeFoundWhileRequestingPasswordRecovery() throws UserNotFoundException {
        when(motechUsersDao.findUserByEmail(EMAIL)).thenReturn(null);
        recoveryService.passwordRecoveryRequest(EMAIL);
    }

    @Test
    public void shouldCleanUpExpiredPasswordRecoveries() {
        when(passwordRecoveriesDataService.findByExpirationDate(any(Range.class))).thenReturn(Arrays.asList(recovery));
        recoveryService.cleanUpExpiredRecoveries();
        verify(passwordRecoveriesDataService).delete(recovery);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenInvalidConfirmationPasswordIsProvidedAtPasswordReset() throws InvalidTokenException {
        recoveryService.resetPassword(TOKEN, PASSWORD, "ppassword");
    }

    @Test(expected = InvalidTokenException.class)
    public void shouldThrowExceptionWhenInvalidTokenIsProvidedAtPasswordReset() throws InvalidTokenException {
        when(passwordRecoveriesDataService.findForToken(TOKEN)).thenReturn(null);
        recoveryService.resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test(expected = InvalidTokenException.class)
    public void shouldThrowExceptionWhenAttemptingToUseExpiredToken() throws InvalidTokenException {
        final DateTime now = DateTime.now();
        fakeNow(now);
        when(passwordRecoveriesDataService.findForToken(TOKEN)).thenReturn(recovery);
        when(recovery.getExpirationDate()).thenReturn(now);

        recoveryService.resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void shouldResetPassword() throws InvalidTokenException {
        final DateTime now = DateTime.now();
        fakeNow(now);
        when(passwordRecoveriesDataService.findForToken(TOKEN)).thenReturn(recovery);
        when(recovery.getExpirationDate()).thenReturn(now.plusMinutes(30));
        when(recovery.getUsername()).thenReturn(USERNAME);
        when(motechUsersDao.findByUserName(USERNAME)).thenReturn(user);
        when(passwordEncoder.encodePassword(PASSWORD)).thenReturn(ENCODED_PASSWORD);

        recoveryService.resetPassword(TOKEN, PASSWORD, PASSWORD);

        verify(user).setPassword(ENCODED_PASSWORD);
        verify(motechUsersDao).update(user);
        verify(passwordRecoveriesDataService).delete(recovery);
    }

    @Test
    public void shouldCreatePasswordRecoveryForOpenID() throws UserNotFoundException, NonAdminUserException {
        final DateTime now = DateTime.now();
        final int expiration = EXPIRATION_HOURS;

        testCreateOpenIDRecoveryTemplate(now, EMAIL, now.plusHours(expiration));
        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        verify(passwordRecoveriesDataService).create(captor.capture());

        PasswordRecovery createdRecovery = captor.getValue();

        assertEquals(USERNAME, createdRecovery.getUsername());
        assertEquals(EMAIL, createdRecovery.getEmail());
        assertEquals(now.plusHours(expiration), createdRecovery.getExpirationDate());
        assertEquals(Locale.ENGLISH, createdRecovery.getLocale());
        assertEquals(60, createdRecovery.getToken().length());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionWhenRequestingRecoveryForOpenIDWithPastDate() throws UserNotFoundException, NonAdminUserException {
        final DateTime now = DateTime.now();
        final int expiration = -2;

        testCreateOpenIDRecoveryTemplate(now, EMAIL, now.plusHours(expiration));
    }

    @Test
    public void shouldCreateRecoveryWithDefaultExpirationTimeForOpenIDIfNoneWasProvided() throws UserNotFoundException, NonAdminUserException {
        final DateTime now = DateTime.now();

        testCreateOpenIDRecoveryTemplate(now, EMAIL, null);
        ArgumentCaptor<PasswordRecovery> captor = ArgumentCaptor.forClass(PasswordRecovery.class);
        verify(passwordRecoveriesDataService).create(captor.capture());

        PasswordRecovery createdRecovery = captor.getValue();

        assertEquals(USERNAME, createdRecovery.getUsername());
        assertEquals(EMAIL, createdRecovery.getEmail());
        // 3 is the default set in PasswordRecoveryServiceImpl
        assertEquals(now.plusHours(3), createdRecovery.getExpirationDate());
        assertEquals(Locale.ENGLISH, createdRecovery.getLocale());
        assertEquals(60, createdRecovery.getToken().length());

        verify(emailSender).sendOneTimeToken(createdRecovery);
    }

    @Test
    public void shouldSendRecoveryEmail() {
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
        when(motechUsersDao.findUserByEmail(EMAIL)).thenReturn(user);
        when(user.getRoles()).thenReturn(Arrays.asList("user"));
        recoveryService.oneTimeTokenOpenId(EMAIL);
    }

    private void testCreateRecoveryTemplate(final DateTime now, String email, DateTime expiration, boolean notify)
            throws UserNotFoundException {
        fakeNow(now);

        when(motechUsersDao.findUserByEmail(EMAIL)).thenReturn(user);
        when(user.getUserName()).thenReturn(USERNAME);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(passwordRecoveriesDataService.create(any(PasswordRecovery.class))).thenReturn(recovery);

        recoveryService.passwordRecoveryRequest(email, expiration, notify);
    }

    private void testCreateOpenIDRecoveryTemplate(final DateTime now, String email, DateTime expiration)
            throws UserNotFoundException, NonAdminUserException {
        fakeNow(now);

        when(motechUsersDao.findUserByEmail(EMAIL)).thenReturn(user);

        when(user.getUserName()).thenReturn(USERNAME);
        when(user.getRoles()).thenReturn(ROLES);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(passwordRecoveriesDataService.create(any(PasswordRecovery.class))).thenReturn(recovery);

        recoveryService.oneTimeTokenOpenId(email, expiration, true);
    }

    private void prepareMessageSource() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
    }

    private void prepareEmailSender() {
        EmailSenderImpl emailSender = new EmailSenderImpl();
        emailSender.setSettingsFacade(settingsFacade);
        emailSender.setTemplateParser(templateParser);
        emailSender.setMessageSource(messageSource);
        emailSender.setEventRelay(eventRelay);
        emailSenderInjected = emailSender;
    }
}
