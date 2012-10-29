package org.motechproject.security.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllMotechWebUsersIT {

    @Autowired
    AllMotechUsers allMotechUsers;
    @Autowired
    MotechPasswordEncoder passwordEncoder;

    @Test
    public void findByUserName() {
        MotechUser motechUser = new MotechUserCouchdbImpl("testuser", "testpassword", "id", asList("ADMIN"));
        allMotechUsers.add(motechUser);

        MotechUser testUser = allMotechUsers.findByUserName("testuser");
        assertEquals("testuser", testUser.getUserName());
    }

    @Test
    public void findByUserNameShouldBeCaseInsensitive() {
        String userName = "TestUser";
        allMotechUsers.add(new MotechUserCouchdbImpl(userName, "testpassword", "id", asList("ADMIN")));

        assertNotNull(allMotechUsers.findByUserName("TESTUSER"));
    }

    @Test
    public void shouldNotCreateNewAccountIfUserAlreadyExists() {
        String userName = "username";
        allMotechUsers.add(new MotechUserCouchdbImpl(userName, "testpassword", "id", asList("ADMIN")));
        allMotechUsers.add(new MotechUserCouchdbImpl(userName, "testpassword1", "id2", asList("ADMIN")));

        MotechUser motechUser = allMotechUsers.findByUserName("userName");
        assertEquals(1, ((AllMotechUsersCouchdbImpl) allMotechUsers).getAll().size());
        assertEquals("testpassword", motechUser.getPassword());
        assertEquals("id", motechUser.getExternalId());
    }

    @Test
    public void shouldListWebUsersByRole() {
        MotechUser provider1 = new MotechUserCouchdbImpl("provider1", "testpassword", "id1", asList("PROVIDER"));
        MotechUser provider2 = new MotechUserCouchdbImpl("provider2", "testpassword", "id2", asList("PROVIDER"));
        MotechUser cmfAdmin = new MotechUserCouchdbImpl("cmfadmin", "testpassword", "id3", asList("CMFADMIN"));
        MotechUser itAdmin = new MotechUserCouchdbImpl("itadmin", "testpassword", "id4", asList("ITADMIN"));
        allMotechUsers.add(provider1);
        allMotechUsers.add(provider2);
        allMotechUsers.add(cmfAdmin);
        allMotechUsers.add(itAdmin);

        List<? extends MotechUser> providers = allMotechUsers.findByRole("PROVIDER");
        assertEquals(asList("id1", "id2"), extract(providers, on(MotechUser.class).getExternalId()));
    }

    @Test
    public void findByUseridShouldReturnNullIfuserNameIsNull() {
        assertNull(null, allMotechUsers.findByUserName(null));
    }

    @After
    public void tearDown() {
        ((AllMotechUsersCouchdbImpl) allMotechUsers).removeAll();
    }
}
