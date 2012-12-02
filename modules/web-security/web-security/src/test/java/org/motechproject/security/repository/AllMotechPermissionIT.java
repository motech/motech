package org.motechproject.security.repository;

import ch.lambdaj.Lambda;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllMotechPermissionIT {

    @Autowired
    private AllMotechPermissions allMotechPermissions;

    @Test
    public void findByPermissionName() {
        allMotechPermissions.add(new MotechPermissionCouchdbImpl("testPermission", "testBundle"));
        MotechPermission testPermission = allMotechPermissions.findByPermissionName("testPermission");
        assertEquals("testPermission", testPermission.getPermissionName());
    }

    @Test
    public void shouldNotCreateNewPermissionIfPermissionAlreadyExists() {
        final String permissionName = "samePersmission";
        allMotechPermissions.add(new MotechPermissionCouchdbImpl(permissionName, "test1"));
        allMotechPermissions.add(new MotechPermissionCouchdbImpl(permissionName, "test2"));

        MotechPermission motechPermission = allMotechPermissions.findByPermissionName(permissionName);
        final List<MotechPermissionCouchdbImpl> allPermission = ((AllMotechPermissionsCouchdbImpl) allMotechPermissions).getAll();
        final int numberOfPermissionWithSameName = Lambda.select(allPermission, HasPropertyWithValue.hasProperty("permissionName", equalTo(permissionName))).size();
        assertEquals(1, numberOfPermissionWithSameName);
        assertEquals("test1", motechPermission.getBundleName());
    }

    @Before
    public void setUp() {
        ((AllMotechPermissionsCouchdbImpl) allMotechPermissions).removeAll();
    }

}
