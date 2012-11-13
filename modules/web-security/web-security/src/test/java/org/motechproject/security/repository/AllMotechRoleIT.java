package org.motechproject.security.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllMotechRoleIT {

    @Autowired
    AllMotechRoles allMotechRoles;

    @Test
    public void findByUserName() {
        allMotechRoles.add(new MotechRoleCouchdbImpl("testRole", asList("per1", "per2")));
        MotechRole testRole = allMotechRoles.findByRoleName("testRole");
        assertEquals("testRole", testRole.getRoleName());
    }

    @Test
    public void shouldNotCreateNewRoleIfRoleAlreadyExists() {
        String roleName = "sameRole";
        allMotechRoles.add(new MotechRoleCouchdbImpl(roleName, asList("per1", "per2")));
        allMotechRoles.add(new MotechRoleCouchdbImpl(roleName, asList("per3", "per4")));

        MotechRole motechRole = allMotechRoles.findByRoleName(roleName);
        assertEquals(1, ((AllMotechRolesCouchdbImpl) allMotechRoles).getAll().size());
        assertEquals("per1", motechRole.getPermissionNames().get(0));
        assertEquals("per2", motechRole.getPermissionNames().get(1));
    }

    @Test
    public void editRole() {
        MotechRole motechRoleBeforeEdit = new MotechRoleCouchdbImpl("roleBeforeEdit", asList("per1", "per2"));
        allMotechRoles.add(motechRoleBeforeEdit);
        MotechRole motechRoleAfterEdit = allMotechRoles.findByRoleName("roleBeforeEdit");
        motechRoleAfterEdit.setRoleName("roleAfterEdit");
        allMotechRoles.update(motechRoleAfterEdit);
        assertEquals(null, allMotechRoles.findByRoleName("roleBeforeEdit"));
        assertEquals("roleAfterEdit", allMotechRoles.findByRoleName("roleAfterEdit").getRoleName());
    }

    @Test
    public void removeRole() {
        MotechRole motechRoleToRemove = new MotechRoleCouchdbImpl("roleToRemove", asList("per1", "per2"));
        allMotechRoles.add(motechRoleToRemove);
        assertEquals("roleToRemove", allMotechRoles.findByRoleName("roleToRemove").getRoleName());
        allMotechRoles.remove(motechRoleToRemove);
        assertEquals(null, allMotechRoles.findByRoleName("roleToRemove"));
    }

    @After
    public void tearDown() {
        ((AllMotechRolesCouchdbImpl) allMotechRoles).removeAll();
    }

}
