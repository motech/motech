package org.motechproject.openmrs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationOpenmrsAPI.xml"})

public class ContextIT {
    @Autowired
    private UserService userService;



    @Test
    public void shouldLoadUser() {
        assertNotNull(userService.getUserByUsername("admin"));
    }
}
