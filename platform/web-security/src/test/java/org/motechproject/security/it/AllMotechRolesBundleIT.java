package org.motechproject.security.it;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.MotechRolesDataService;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AllMotechRolesBundleIT extends BaseIT {

    @Inject
    private MotechRolesDataService rolesDataService;

    private AllMotechRoles allMotechRoles;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        allMotechRoles = getFromContext(AllMotechRoles.class);
        rolesDataService.deleteAll();
    }

    @Test
    public void findByUserName() {
        allMotechRoles.add(new MotechRole("testRole", asList("per1", "per2"), false));
        MotechRole testRole = allMotechRoles.findByRoleName("testRole");
        assertEquals("testRole", testRole.getRoleName());
    }

    @Test
    public void shouldNotCreateNewRoleIfRoleAlreadyExists() {
        String roleName = "sameRole";
        allMotechRoles.add(new MotechRole(roleName, asList("per1", "per2"), false));
        allMotechRoles.add(new MotechRole(roleName, asList("per3", "per4"), false));

        MotechRole motechRole = allMotechRoles.findByRoleName(roleName);
        List<MotechRole> allRoles = rolesDataService.retrieveAll();
        int numberOfRoles = 0;

        for (MotechRole role : allRoles) {
            if (roleName.equalsIgnoreCase(role.getRoleName())) {
                ++numberOfRoles;
            }
        }

        assertEquals(1, numberOfRoles);
        assertEquals("per1", motechRole.getPermissionNames().get(0));
        assertEquals("per2", motechRole.getPermissionNames().get(1));
    }

    @Test
    public void editRole() {
        MotechRole motechRoleBeforeEdit = new MotechRole("roleBeforeEdit", asList("per1", "per2"), false);
        allMotechRoles.add(motechRoleBeforeEdit);

        rolesDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                MotechRole motechRoleAfterEdit = allMotechRoles.findByRoleName("roleBeforeEdit");
                motechRoleAfterEdit.setRoleName("roleAfterEdit");
                allMotechRoles.update(motechRoleAfterEdit);
                assertEquals(null, allMotechRoles.findByRoleName("roleBeforeEdit"));
                assertEquals("roleAfterEdit", allMotechRoles.findByRoleName("roleAfterEdit").getRoleName());
            }
        });
    }

    @Test
    public void removeRole() {
        MotechRole motechRoleToRemove = new MotechRole("roleToRemove", asList("per1", "per2"), false);
        allMotechRoles.add(motechRoleToRemove);
        assertEquals("roleToRemove", allMotechRoles.findByRoleName("roleToRemove").getRoleName());
        allMotechRoles.remove(motechRoleToRemove);
        assertEquals(null, allMotechRoles.findByRoleName("roleToRemove"));
    }

}
