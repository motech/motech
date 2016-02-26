package org.motechproject.security.it;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.exception.UserNotFoundException;
import org.motechproject.security.service.mds.MotechUsersDataService;
import org.motechproject.security.service.mds.PasswordRecoveriesDataService;
import org.motechproject.security.service.PasswordRecoveryService;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class PasswordRecoveryServiceBundleIT extends BaseIT {

    private static final String EMAIL = "e@mail.com";
    private static final String OTHER_EMAIL = "other@mail.com";
    private static final String TOKEN = "token";
    private static final String OTHER_TOKEN = "other-token";
    private static final String USERNAME = "username";
    private static final String OTHER_USERNAME = "other-user";

    private final DateTime EXPIRATION = DateUtil.nowUTC().plusHours(1);

    @Inject
    private MotechUsersDataService usersDataService;

    @Inject
    private PasswordRecoveriesDataService recoveriesDataService;

    @Inject
    private PasswordRecoveryService passwordRecoveryService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        recoveriesDataService.deleteAll();

        usersDataService.create(new MotechUser(USERNAME, "test", EMAIL, null, null, null, Locale.ENGLISH));
        usersDataService.create(new MotechUser(OTHER_USERNAME, "test", OTHER_EMAIL, null, null, null, Locale.ENGLISH));

        // Fake time
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().minusHours(1).getMillis());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        recoveriesDataService.deleteAll();
        usersDataService.deleteAll();

        // Stop faking time
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldFindRecoveryByTokenAndUserName() throws UserNotFoundException {
        String token = passwordRecoveryService.passwordRecoveryRequest(EMAIL, EXPIRATION, true);

        PasswordRecovery recovery = recoveriesDataService.findForToken(token);
        verifyDefaultRecovery(recovery, token);

        recovery = recoveriesDataService.findForUser(USERNAME);
        verifyDefaultRecovery(recovery, token);
    }

    @Test
    public void shouldRemoveOldRecovery() throws UserNotFoundException {
        // Given
        PasswordRecovery recovery1 = new PasswordRecovery();
        recovery1.setEmail(EMAIL);
        recovery1.setToken(TOKEN);
        recovery1.setLocale(Locale.ENGLISH);
        recovery1.setUsername(USERNAME);
        recovery1.setExpirationDate(EXPIRATION);
        recoveriesDataService.create(recovery1);

        PasswordRecovery recovery2 = new PasswordRecovery();
        recovery2.setEmail(OTHER_EMAIL);
        recovery2.setToken(OTHER_TOKEN);
        recovery2.setLocale(Locale.ENGLISH);
        recovery2.setUsername(OTHER_USERNAME);
        recovery2.setExpirationDate(EXPIRATION.minusHours(2));
        recoveriesDataService.create(recovery2);

        List<PasswordRecovery> recoveries = recoveriesDataService.retrieveAll();

        assertEquals(2, recoveries.size());
        // When
        passwordRecoveryService.cleanUpExpiredRecoveries();

        // Then
        recoveries = recoveriesDataService.retrieveAll();

        // The expired recovery should no longer be present, the non-expired recovery should still be available
        assertEquals(1, recoveries.size());
        verifyDefaultRecovery(recoveries.get(0), TOKEN);
    }

    private void verifyDefaultRecovery(PasswordRecovery recovery, String generatedToken) {
        verifyRecovery(recovery, generatedToken, USERNAME, EMAIL, EXPIRATION, Locale.ENGLISH);
    }

    private void verifyRecovery(PasswordRecovery recovery, String token, String username, String email,
                                DateTime expiration, Locale locale) {
        assertNotNull(recovery);
        assertEquals(email, recovery.getEmail());
        assertEquals(username, recovery.getUsername());
        assertEquals(token, recovery.getToken());
        assertEquals(expiration, recovery.getExpirationDate());
        assertEquals(locale, recovery.getLocale());
    }
}
