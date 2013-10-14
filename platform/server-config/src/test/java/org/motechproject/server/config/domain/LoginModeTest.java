package org.motechproject.server.config.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginModeTest {

    @Test
    public void testRepositoryLoginMode() {
        assertTrue(LoginMode.REPOSITORY.isRepository());
    }

    @Test
    public void testOpenIdLoginMode() {
        assertTrue(LoginMode.OPEN_ID.isOpenId());
    }

    @Test
    public void testLoginModeNameForRepository() {
        assertEquals("server.repository", LoginMode.REPOSITORY.getName());
    }

    @Test
    public void testLoginModeNameForOpenId() {
        assertEquals("server.openId", LoginMode.OPEN_ID.getName());
    }

    @Test
    public void testValueOfForRepositoryLoginMode() {
        assertEquals(LoginMode.REPOSITORY, LoginMode.valueOf("server.repository"));
    }

    @Test
    public void testValueOfForOpenIdLoginMode() {
        assertEquals(LoginMode.OPEN_ID, LoginMode.valueOf("server.openId"));
    }

    @Test
    public void testValueOfForInvalidLoginMode() {
        assertEquals(null, LoginMode.valueOf("scramble"));
    }

    @Test
    public void testValueOfForNullLoginMode() {
        assertEquals(null, LoginMode.valueOf(null));
    }
}

