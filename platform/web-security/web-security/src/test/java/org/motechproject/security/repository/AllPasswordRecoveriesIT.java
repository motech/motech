package org.motechproject.security.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllPasswordRecoveriesIT extends BaseUnitTest {

    private static final String EMAIL = "e@mail.com";
    private static final String OTHER_EMAIL = "other@mail.com";
    private static final String TOKEN = "token";
    private static final String OTHER_TOKEN = "other-token";
    private static final String USERNAME = "username";
    private static final String OTHER_USERNAME = "other-user";

    private final DateTime EXPIRATION = DateTime.now();

    @Autowired
    private AllPasswordRecoveries allPasswordRecoveries;

    @Autowired
    private MotechPasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        ((AllPasswordRecoveriesCouchDbImpl) allPasswordRecoveries).removeAll();
        mockCurrentDate(EXPIRATION.minusHours(1));
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testFindForUser() {
        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION);
        verifyDefaultRecovery(recovery);

        recovery = allPasswordRecoveries.findForUser(USERNAME);
        verifyDefaultRecovery(recovery);
    }

    @Test
    public void testFindForToken() {
        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION);
        verifyDefaultRecovery(recovery);

        recovery = allPasswordRecoveries.findForToken(TOKEN);
        verifyDefaultRecovery(recovery);
    }

    @Test
    public void testRemoveOldRecovery() {
        allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION);
        allPasswordRecoveries.createRecovery(OTHER_USERNAME, OTHER_EMAIL, OTHER_TOKEN, EXPIRATION.minusHours(2));

        List<PasswordRecovery> recoveries = allPasswordRecoveries.allRecoveries();

        assertEquals(2, recoveries.size());
        verifyDefaultRecovery(recoveries.get(0));
        verifyRecovery(recoveries.get(1), OTHER_TOKEN, OTHER_USERNAME, OTHER_EMAIL, EXPIRATION.minusHours(2));

        for (PasswordRecovery recovery : allPasswordRecoveries.getExpired()) {
            allPasswordRecoveries.remove(recovery);
        }

        recoveries = allPasswordRecoveries.allRecoveries();

        assertEquals(1, recoveries.size());
        verifyDefaultRecovery(recoveries.get(0));
    }

    @Test
    public void testGetExpired() {
        allPasswordRecoveries.createRecovery(USERNAME, EMAIL, TOKEN, EXPIRATION);
        allPasswordRecoveries.createRecovery(OTHER_USERNAME, OTHER_EMAIL, OTHER_TOKEN, EXPIRATION.minusHours(10));

        List<PasswordRecovery> recoveries = allPasswordRecoveries.getExpired();

        assertEquals(1, recoveries.size());
        PasswordRecovery recovery = recoveries.get(0);
        verifyRecovery(recovery, OTHER_TOKEN, OTHER_USERNAME, OTHER_EMAIL, EXPIRATION.minusHours(10));
    }

    private void verifyDefaultRecovery(PasswordRecovery recovery) {
        verifyRecovery(recovery, TOKEN, USERNAME, EMAIL, EXPIRATION);
    }

    private void verifyRecovery(PasswordRecovery recovery, String token, String username, String email,
                                DateTime expiration) {
        assertNotNull(recovery);
        assertEquals(email, recovery.getEmail());
        assertEquals(username, recovery.getUsername());
        assertEquals(token, recovery.getToken());
        assertEquals(expiration, recovery.getExpirationDate());
    }
}
