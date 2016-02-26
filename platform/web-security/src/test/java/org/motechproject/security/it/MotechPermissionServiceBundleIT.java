package org.motechproject.security.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.constants.PermissionNames;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.service.mds.MotechPermissionsDataService;
import org.motechproject.security.service.MotechPermissionService;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MotechPermissionServiceBundleIT extends BaseIT {

    @Inject
    private MotechPermissionsDataService permissionsDataService;

    @Inject
    private MotechPermissionService permissionService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        permissionsDataService.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        permissionsDataService.deleteAll();
    }

    @Test
    public void shouldFindByPermissionName() {
        permissionsDataService.create(new MotechPermission("testPermission", "testBundle"));

        PermissionDto testPermission = permissionService.findPermissionByName("testPermission");
        assertEquals("testPermission", testPermission.getPermissionName());
    }

    @Test
    public void shouldNotCreateNewPermissionIfPermissionAlreadyExists() {
        final String permissionName = "samePersmission";
        permissionService.addPermission(new PermissionDto(new MotechPermission(permissionName, "test1")));
        permissionService.addPermission(new PermissionDto(new MotechPermission(permissionName, "test2")));

        PermissionDto motechPermission = permissionService.findPermissionByName(permissionName);

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
        permissionsDataService.create(new MotechPermission("testPermission", "testBundle"));

        MotechPermission permission = permissionsDataService.findByPermissionName("testPermission");
        assertNotNull(permission);

        setUpSecurityContextForDefaultUser(PermissionNames.MANAGE_ROLE_AND_PERMISSION_PERMISSION);

        // When
        permissionService.deletePermission(permission.getPermissionName());

        // Then
        List<MotechPermission> permissions = permissionsDataService.retrieveAll();
        assertTrue("There shouldn't be any permissions, but found: " + permissions.size(), permissions.isEmpty());
    }

}
