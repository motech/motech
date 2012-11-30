package org.motechproject.security.repository;

import ch.lambdaj.Lambda;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;

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
        final List<MotechRoleCouchdbImpl> allRoles = ((AllMotechRolesCouchdbImpl) allMotechRoles).getAll();
        final int numberOfRoles = Lambda.select(allRoles, HasPropertyWithValue.hasProperty("roleName", equalTo(roleName))).size();
        assertEquals(1, numberOfRoles);
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

    @Before
    public void setUp() {
        ((AllMotechRolesCouchdbImpl) allMotechRoles).removeAll();
    }

}
