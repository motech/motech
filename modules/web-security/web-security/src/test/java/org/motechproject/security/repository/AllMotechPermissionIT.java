package org.motechproject.security.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllMotechPermissionIT {

    @Autowired
    AllMotechPermissions allMotechPermissions;

    @Test
    public void findByPermissionName() {
        allMotechPermissions.add(new MotechPermissionCouchdbImpl("testPermission", "testBundle"));
        MotechPermission testPermission = allMotechPermissions.findByPermissionName("testPermission");
        assertEquals("testPermission", testPermission.getPermissionName());
    }

    @Test
    public void shouldNotCreateNewPermissionIfPermissionAlreadyExists() {
        String permissionName = "samePersmission";
        allMotechPermissions.add(new MotechPermissionCouchdbImpl(permissionName, "test1"));
        allMotechPermissions.add(new MotechPermissionCouchdbImpl(permissionName, "test2"));

        MotechPermission motechPermission = allMotechPermissions.findByPermissionName(permissionName);
        assertEquals(1, ((AllMotechPermissionsCouchdbImpl) allMotechPermissions).getAll().size());
        assertEquals("test1", motechPermission.getBundleName());
    }

    @After
    public void tearDown() {
        ((AllMotechPermissionsCouchdbImpl) allMotechPermissions).removeAll();
    }

}
