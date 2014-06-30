package org.motechproject.security.osgi;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.commons.date.util.datetime.DateTimeSource;
import org.motechproject.commons.date.util.datetime.DefaultDateTimeSource;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.repository.AllPasswordRecoveries;
import org.motechproject.security.repository.PasswordRecoveriesDataService;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class AllPasswordRecoveriesIT extends BaseIT {
    private static final DateTimeSource DATE_TIME_SOURCE = new DefaultDateTimeSource();

    private static final String EMAIL = "e@mail.com";
    private static final String OTHER_EMAIL = "other@mail.com";
    private static final String TOKEN = "token";
    private static final String OTHER_TOKEN = "other-token";
    private static final String USERNAME = "username";
    private static final String OTHER_USERNAME = "other-user";

    private final DateTime EXPIRATION = DateUtil.nowUTC();

    @Inject
    private PasswordRecoveriesDataService recoveriesDataService;

    private AllPasswordRecoveries allPasswordRecoveries;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        allPasswordRecoveries = getFromContext(AllPasswordRecoveries.class);

        recoveriesDataService.deleteAll();

        DateTimeSourceUtil.setSourceInstance(new DateTimeSource() {
            private DateTime dateTime = EXPIRATION.minusHours(1);

            @Override
            public DateTimeZone timeZone() {
                return dateTime.getZone();
            }

            @Override
            public DateTime now() {
                return dateTime;
            }

            @Override
            public LocalDate today() {
                return dateTime.toLocalDate();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        DateTimeSourceUtil.setSourceInstance(DATE_TIME_SOURCE);
    }

    @Test
    public void testFindForUser() {
        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION, Locale.ENGLISH);
        verifyDefaultRecovery(recovery);

        recovery = allPasswordRecoveries.findForUser(USERNAME);
        verifyDefaultRecovery(recovery);
    }

    @Test
    public void testFindForToken() {
        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION, Locale.ENGLISH);
        verifyDefaultRecovery(recovery);

        recovery = allPasswordRecoveries.findForToken(TOKEN);
        verifyDefaultRecovery(recovery);
    }

    @Test
    public void testRemoveOldRecovery() {
        allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION, Locale.ENGLISH);
        allPasswordRecoveries.createRecovery(OTHER_USERNAME, OTHER_EMAIL, OTHER_TOKEN, EXPIRATION.minusHours(2), Locale.ENGLISH);

        List<PasswordRecovery> recoveries = allPasswordRecoveries.allRecoveries();

        assertEquals(2, recoveries.size());
        verifyDefaultRecovery(recoveries.get(0));
        verifyRecovery(recoveries.get(1), OTHER_TOKEN, OTHER_USERNAME, OTHER_EMAIL, EXPIRATION.minusHours(2), Locale.ENGLISH);

        for (PasswordRecovery recovery : allPasswordRecoveries.getExpired()) {
            allPasswordRecoveries.remove(recovery);
        }

        recoveries = allPasswordRecoveries.allRecoveries();

        assertEquals(1, recoveries.size());
        verifyDefaultRecovery(recoveries.get(0));
    }

    @Test
    public void testGetExpired() {
        allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION, Locale.ENGLISH);
        allPasswordRecoveries.createRecovery(OTHER_USERNAME, OTHER_EMAIL, OTHER_TOKEN, EXPIRATION.minusHours(10), Locale.ENGLISH);

        List<PasswordRecovery> recoveries = allPasswordRecoveries.getExpired();

        assertEquals(1, recoveries.size());
        PasswordRecovery recovery = recoveries.get(0);
        verifyRecovery(recovery, OTHER_TOKEN, OTHER_USERNAME, OTHER_EMAIL, EXPIRATION.minusHours(10), Locale.ENGLISH);
    }

    private void verifyDefaultRecovery(PasswordRecovery recovery) {
        verifyRecovery(recovery, TOKEN, USERNAME, EMAIL, EXPIRATION, Locale.ENGLISH);
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
