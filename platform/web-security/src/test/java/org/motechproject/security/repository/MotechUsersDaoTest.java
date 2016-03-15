package org.motechproject.security.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.exception.EmailExistsException;
import org.motechproject.security.mds.MotechUsersDataService;

import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MotechUsersDaoTest {

    @Mock
    private MotechUsersDataService usersDataService;

    @InjectMocks
    private MotechUsersDao motechUsersDao = new MotechUsersDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateANewUser() {
        MotechUser motechUser = new MotechUser("testuser", "testpassword", "test@test.com", "id", asList("ADMIN"), "", Locale.ENGLISH);
        when(usersDataService.findByUserName("testuser")).thenReturn(null);
        when(usersDataService.findByEmail("test@test.com")).thenReturn(null);

        motechUsersDao.add(motechUser);

        verify(usersDataService).findByUserName("testuser");
        verify(usersDataService).findByEmail("test@test.com");
        verify(usersDataService).create(motechUser);
    }

    @Test
    public void findByUserNameShouldBeCaseInsensitive() {
        when(usersDataService.findByUserName("testuser")).thenReturn(null);

        motechUsersDao.findByUserName("TeStUsEr");
        verify(usersDataService).findByUserName("testuser");
    }

    @Test
    public void shouldNotCreateNewAccountIfUserAlreadyExists() {
        MotechUser motechUser = new MotechUser("testuser", "testpassword", "test@test.com", "id", asList("ADMIN"), "", Locale.ENGLISH);
        when(usersDataService.findByUserName("testuser")).thenReturn(motechUser);

        motechUsersDao.add(motechUser);

        verify(usersDataService).findByUserName("testuser");
        verify(usersDataService, never()).create(motechUser);
    }

    @Test(expected = EmailExistsException.class)
    public void shouldNotAllowDuplicateEmails() {
        MotechUser motechUser = new MotechUser("testuser", "testpassword", "test@test.com", "id", asList("ADMIN"), "", Locale.ENGLISH);
        when(usersDataService.findByUserName("testuser")).thenReturn(null);
        when(usersDataService.findByEmail("test@test.com")).thenReturn(motechUser);

        try {
            motechUsersDao.add(motechUser);
        } finally {
            verify(usersDataService).findByUserName("testuser");
            verify(usersDataService).findByEmail("test@test.com");
            verify(usersDataService, never()).create(motechUser);
        }
    }

    @Test
    public void findByUseridShouldReturnNullIfuserNameIsNull() {
        assertNull(null, motechUsersDao.findByUserName(null));
    }
}
