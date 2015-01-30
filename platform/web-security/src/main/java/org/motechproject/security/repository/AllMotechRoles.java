package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementation of DAO interface that utilizes a MDS back-end for storage.
 * Class responsible for handling MotechRoles.
 */
@Repository
public class AllMotechRoles {
    private MotechRolesDataService dataService;

    /**
     * Returns all MotechRoles
     *
     * @return list that contains roles
     */
    public List<MotechRole> getRoles() {
        return dataService.retrieveAll();
    }

    /**
     * Creates MotechRole if it doesn't exists
     *
     * @param role to be created
     */
    public void add(MotechRole role) {
        if (findByRoleName(role.getRoleName()) == null) {
            dataService.create(role);
        }
    }

    /**
     * Looks for and returns MotechRole with given name
     *
     * @param roleName name of MotechRole
     * @return MotechRole or null if name is a null
     */
    public MotechRole findByRoleName(String roleName) {
        return null == roleName ? null : dataService.findByRoleName(roleName);
    }

    /**
     * Removes given MotechRole
     *
     * @param motechRole to be removed
     */
    public void remove(MotechRole motechRole) {
        dataService.delete(motechRole);
    }

    /**
     * Updates given MotechRole
     *
     * @param motechRole to be updated
     */
    public void update(MotechRole motechRole) {
        dataService.update(motechRole);
    }

    @Autowired
    public void setDataService(MotechRolesDataService dataService) {
        this.dataService = dataService;
    }
}
