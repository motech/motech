package org.motechproject.security.service;

import org.motechproject.security.constants.WebSecurityRoles;
import org.motechproject.security.model.RoleDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing MOTECH roles
 */
public interface MotechRoleService {

    /**
     * Returns all roles
     *
     * @return list that contains roles
     */
    @PreAuthorize("hasAnyRole('manageRoleAndPermission', 'manageUser')")
    List<RoleDto> getRoles();

    /**
     * Returns role with given name
     *
     * @param roleName name of the role that should be returned
     * @return role with given name
     */
    RoleDto getRole(String roleName);

    /**
     * Updates given role
     *
     * @param role to be updated
     */
    void updateRole(RoleDto role);

    /**
     * Deletes given role
     *
     * @param role to be deleted
     */
    @PreAuthorize(WebSecurityRoles.HAS_MANAGE_ROLE_AND_PERMISSION)
    void deleteRole(RoleDto role);

    /**
     * Creates new role
     *
     * @param role to be created
     */
    void createRole(RoleDto role);
}
