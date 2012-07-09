package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechUser;

import java.util.List;

public interface AllMotechUsers {

    public MotechUser findByUserName(String userName);

    public List<? extends MotechUser> findByRole(String role);

    public void add(MotechUser user);

    public void update(MotechUser motechUser);

    public void remove(MotechUser motechUser);
}
