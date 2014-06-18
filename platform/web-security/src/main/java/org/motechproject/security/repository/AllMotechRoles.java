package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMotechRoles {
    private MotechRolesDataService dataService;

    public List<MotechRole> getRoles() {
        return dataService.retrieveAll();
    }

    public void add(MotechRole role) {
        if (findByRoleName(role.getRoleName()) == null) {
            dataService.create(role);
        }
    }

    public MotechRole findByRoleName(String roleName) {
        return null == roleName ? null : dataService.findByRoleName(roleName);
    }

    public void remove(MotechRole motechRole) {
        dataService.delete(motechRole);
    }

    public void update(MotechRole motechRole) {
        dataService.update(motechRole);
    }

    @Autowired
    public void setDataService(MotechRolesDataService dataService) {
        this.dataService = dataService;
    }
}
