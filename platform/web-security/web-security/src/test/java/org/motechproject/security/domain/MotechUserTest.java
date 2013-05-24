package org.motechproject.security.domain;


import org.junit.Test;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;

public class MotechUserTest {

    @Test
    public void userNameShouldBeSetToLowercase() {
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl("TestUser", "p@ssw0rd", "", "", null, "", Locale.ENGLISH);
        assertEquals("testuser", user.getUserName());
    }

    @Test
    public void shouldHandleNullValueForUserName() {
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl(null, "p@ssw0rd", "","", null, "", Locale.ENGLISH);
        assertEquals(null, user.getUserName());
    }

}
