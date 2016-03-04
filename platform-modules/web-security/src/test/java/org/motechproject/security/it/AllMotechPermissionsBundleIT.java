package org.motechproject.security.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.MotechPermissionsDataService;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AllMotechPermissionsBundleIT extends BaseIT {

    @Inject
    private MotechPermissionsDataService permissionsDataService;

    private AllMotechPermissions allMotechPermissions;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        allMotechPermissions = getFromContext(AllMotechPermissions.class);
        permissionsDataService.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        permissionsDataService.deleteAll();
    }

    @Test
    public void findByPermissionName() {
        allMotechPermissions.add(new MotechPermission("testPermission", "testBundle"));
        MotechPermission testPermission = allMotechPermissions.findByPermissionName("testPermission");
        assertEquals("testPermission", testPermission.getPermissionName());
    }

    @Test
    public void shouldNotCreateNewPermissionIfPermissionAlreadyExists() {
        final String permissionName = "samePersmission";
        allMotechPermissions.add(new MotechPermission(permissionName, "test1"));
        allMotechPermissions.add(new MotechPermission(permissionName, "test2"));

        MotechPermission motechPermission = allMotechPermissions.findByPermissionName(permissionName);
        List<MotechPermission> allPermission = permissionsDataService.retrieveAll();
        int numberOfPermissionWithSameName = 0;

        for (MotechPermission permission : allPermission) {
            if (permissionName.equalsIgnoreCase(permission.getPermissionName())) {
                ++numberOfPermissionWithSameName;
            }
        }

        assertEquals(1, numberOfPermissionWithSameName);
        assertEquals("test1", motechPermission.getBundleName());
    }

    @Test
    public void shouldDeletePermissions() {
        allMotechPermissions.add(new MotechPermission("testPermission", "testBundle"));

        MotechPermission permission = allMotechPermissions.findByPermissionName("testPermission");
        assertNotNull(permission);

        allMotechPermissions.delete(permission);

        List<MotechPermission> permissions = allMotechPermissions.getPermissions();

        assertTrue("There should not be permissions but found: " + permissions, permissions.isEmpty());
    }

}
