package org.motechproject.security.domain;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class MotechUserTest {

    @Test
    public void userNameShouldBeSetToLowercase() {
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl("TestUser", "p@ssw0rd", "", "", null, "", Locale.ENGLISH);
        assertEquals("testuser", user.getUserName());
    }

    @Test
    public void shouldHandleNullValueForUserName() {
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl(null, "p@ssw0rd", "", "", null, "", Locale.ENGLISH);
        assertEquals(null, user.getUserName());
    }

    @Test
    public void shouldReturnTrueIfUserHasGivenRole() {
        List<String> roles = Arrays.asList("fooRole");
        MotechUser user = new MotechUserCouchdbImpl(null, "p@ssw0rd", "", "", roles, "", Locale.ENGLISH);
        assertTrue(user.hasRole("fooRole"));
        assertFalse(user.hasRole("barRole"));
    }
}
