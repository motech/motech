package org.motechproject.security.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

public class MotechUserTest {

    @Test
    public void userNameShouldBeSetToLowercase() {
        MotechUser user = new MotechUser("TestUser", "p@ssw0rd", "", "", null, "", Locale.ENGLISH);
        assertEquals("testuser", user.getUserName());
    }

    @Test
    public void shouldHandleNullValueForUserName() {
        MotechUser user = new MotechUser(null, "p@ssw0rd", "", "", null, "", Locale.ENGLISH);
        assertEquals(null, user.getUserName());
    }

    @Test
    public void shouldReturnTrueIfUserHasGivenRole() {
        List<String> roles = Arrays.asList("fooRole");
        MotechUser user = new MotechUser(null, "p@ssw0rd", "", "", roles, "", Locale.ENGLISH);
        assertTrue(user.hasRole("fooRole"));
        assertFalse(user.hasRole("barRole"));
    }

    @Test
    public void shouldUpdateLastPasswordChangeOnPasswordChange() {

        try {
            DateTime fakeOne = newDateTime(2015, 7, 13, 10, 0, 0);
            fakeNow(fakeOne);

            MotechUser user = new MotechUser(null, "p@ssw0rd", "", "", null, "", Locale.ENGLISH);

            DateTime lastPasswordChange = user.getSafeLastPasswordChange();
            assertEquals(fakeOne, lastPasswordChange);

            DateTime fakeTwo = newDateTime(2015, 8, 13, 10, 0, 0);
            fakeNow(fakeTwo);

            user.setPassword("0th3rP@ss");
            assertFalse(lastPasswordChange.equals(user.getSafeLastPasswordChange()));
            assertEquals(fakeTwo, user.getSafeLastPasswordChange());
        } finally {
            stopFakingTime();
        }
    }
}
