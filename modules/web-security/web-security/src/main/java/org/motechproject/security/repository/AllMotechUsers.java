package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechUser;

import java.util.List;

public interface AllMotechUsers {

    MotechUser findByUserName(String userName);

    MotechUser findUserByOpenId(String openId);

    List<? extends MotechUser> findByRole(String role);

    void add(MotechUser user);

    void addOpenIdUser(MotechUser user);

    void update(MotechUser motechUser);

    void remove(MotechUser motechUser);

    List<MotechUser> getUsers();

    boolean checkUserAuthorisation(String userName, String password);

    List<MotechUser> getOpenIdUsers();
}
