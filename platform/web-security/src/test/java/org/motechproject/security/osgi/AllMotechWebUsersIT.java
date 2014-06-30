package org.motechproject.security.osgi;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.ex.EmailExistsException;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.MotechUsersDataService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class AllMotechWebUsersIT extends BaseIT {

    @Inject
    private MotechUsersDataService usersDataService;

    private AllMotechUsers allMotechUsers;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        allMotechUsers = getFromContext(AllMotechUsers.class);
        usersDataService.deleteAll();
    }

    @Test
    public void findByUserName() {
        MotechUser motechUser = new MotechUser("testuser", "testpassword", "", "id", asList("ADMIN"), "", Locale.ENGLISH);
        allMotechUsers.add(motechUser);
        MotechUser testUser = allMotechUsers.findByUserName("testuser");
        assertEquals("testuser", testUser.getUserName());
    }

    @Test
    public void findByUserNameShouldBeCaseInsensitive() {
        String userName = "TestUser";
        allMotechUsers.add(new MotechUser(userName, "testpassword", "", "id", asList("ADMIN"), "", Locale.ENGLISH));
        assertNotNull(allMotechUsers.findByUserName("TESTUSER"));
    }

    @Test
    public void shouldNotCreateNewAccountIfUserAlreadyExists() {
        String userName = "username";
        allMotechUsers.add(new MotechUser(userName, "testpassword", "", "id", asList("ADMIN"), "", Locale.ENGLISH));
        allMotechUsers.add(new MotechUser(userName, "testpassword1", "", "id2", asList("ADMIN"), "", Locale.ENGLISH));
        MotechUser motechUser = allMotechUsers.findByUserName("userName");
        List<MotechUser> allWebUsers = usersDataService.retrieveAll();
        int numberOfUsersWithSameUserName = 0;

        for (MotechUser user : allWebUsers) {
            if (userName.equalsIgnoreCase(user.getUserName())) {
                ++numberOfUsersWithSameUserName;
            }
        }

        assertEquals(1, numberOfUsersWithSameUserName);
        assertEquals("testpassword", motechUser.getPassword());
        assertEquals("id", motechUser.getExternalId());
    }

    @Test
    public void shouldListWebUsersByRole() {
        MotechUser provider1 = new MotechUser("provider1", "testpassword", "email1@example.com", "id1", asList("PROVIDER"), "", Locale.ENGLISH);
        MotechUser provider2 = new MotechUser("provider2", "testpassword", "email12@example.com", "id2", asList("PROVIDER"), "", Locale.ENGLISH);
        MotechUser cmfAdmin = new MotechUser("cmfadmin", "testpassword", "email13@example.com", "id3", asList("CMFADMIN"), "", Locale.ENGLISH);
        MotechUser itAdmin = new MotechUser("itadmin", "testpassword", "email4@example.com", "id4", asList("ITADMIN"), "", Locale.ENGLISH);
        allMotechUsers.add(provider1);
        allMotechUsers.add(provider2);
        allMotechUsers.add(cmfAdmin);
        allMotechUsers.add(itAdmin);
        List<? extends MotechUser> providers = allMotechUsers.findByRole("PROVIDER");
        List<String> externalIds = new ArrayList<>();

        for (MotechUser user : providers) {
            externalIds.add(user.getExternalId());
        }


        assertEquals(asList("id1", "id2"), externalIds);
    }

    @Test(expected = EmailExistsException.class)
    public void shouldNotAllowDuplicateEmails() {
        allMotechUsers.add(new MotechUser("user1", "testpassword", "email1@example.com", "id", asList("ADMIN"), "", Locale.ENGLISH));
        allMotechUsers.add(new MotechUser("user2", "testpassword1", "email1@example.com", "id2", asList("ADMIN"), "", Locale.ENGLISH));
    }

    @Test
    public void findByUseridShouldReturnNullIfuserNameIsNull() {
        assertNull(null, allMotechUsers.findByUserName(null));
    }

}
