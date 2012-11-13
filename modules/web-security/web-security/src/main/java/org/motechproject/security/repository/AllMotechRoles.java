package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechRole;

import java.util.List;

public interface AllMotechRoles {

    List<MotechRole> getRoles();

    void add(MotechRole role);

    MotechRole findByRoleName(String roleName);

    void remove(MotechRole motechRole);

    void update(MotechRole motechRole);
}
