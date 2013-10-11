package org.motechproject.security.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.motechproject.security.domain.MotechUserProfile;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertEquals;

public class MotechAccessVoterTest {

    @Test
    public void shouldVoteAffirmativeIfUserHasAccess() {
        List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
        attributes.add(new SecurityConfig("access_testuser"));
        attributes.add(new SecurityConfig("access_motechUser"));

        MotechUserProfile userProfile = new MotechUserProfile(new MotechUserCouchdbImpl("TestUser", "p@ssw0rd", "", "", null, "", Locale.ENGLISH));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("TestUser", "p@ssw0rd");
        authentication.setDetails(userProfile);
        MotechAccessVoter voter = new MotechAccessVoter();
        assertEquals(1, voter.vote(authentication, null, attributes));
    }

    public void shouldVoteNegativeIfUserDoesNotHaveAccess() {
        List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
        attributes.add(new SecurityConfig("access_otheruser"));
        attributes.add(new SecurityConfig("access_motechUser"));

        MotechUserProfile userProfile = new MotechUserProfile(new MotechUserCouchdbImpl("TestUser", "p@ssw0rd", "", "", null, "", Locale.ENGLISH));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("TestUser", "p@ssw0rd");
        authentication.setDetails(userProfile);
        MotechAccessVoter voter = new MotechAccessVoter();
        assertEquals(-1, voter.vote(authentication, null, attributes));
    }

    public void shouldAbstrainIfNoAccessAttributes() {
        List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();

        MotechUserProfile userProfile = new MotechUserProfile(new MotechUserCouchdbImpl("TestUser", "p@ssw0rd", "", "", null, "", Locale.ENGLISH));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("TestUser", "p@ssw0rd");
        authentication.setDetails(userProfile);
        MotechAccessVoter voter = new MotechAccessVoter();
        assertEquals(0, voter.vote(authentication, null, attributes));
    }

    @Test
    public void testConfigAttSupport() {
        MotechAccessVoter voter = new MotechAccessVoter();

        assertTrue(voter.supports(Object.class));
        assertTrue(voter.supports(ConfigAttribute.class));

        assertTrue(voter.supports(new SecurityConfig("access_motech")));
        assertFalse(voter.supports(new SecurityConfig("aCCESS_motech")));
        assertFalse(voter.supports(new SecurityConfig("nosupport")));
    }
}
