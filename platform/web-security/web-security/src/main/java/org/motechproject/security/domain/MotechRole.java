package org.motechproject.security.domain;

import java.util.List;

public interface MotechRole {

    String getRoleName();

    List<String> getPermissionNames();

    boolean isDeletable();

    void setRoleName(String roleName);

    void setPermissionNames(List<String> perrmissionNames);

    void setDeletable(boolean deletable);
}
