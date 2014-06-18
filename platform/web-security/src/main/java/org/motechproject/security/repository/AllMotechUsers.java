package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.ex.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Repository
public class AllMotechUsers {
    private MotechUsersDataService dataService;

    public MotechUser findByUserName(String userName) {
        return null == userName ? null : dataService.findByUserName(userName.toLowerCase());
    }

    public MotechUser findUserByOpenId(String openId) {
        return null == openId ? null : dataService.findByOpenId(openId);
    }

    public MotechUser findUserByEmail(String email) {
        return null == email ? null : dataService.findByEmail(email);
    }

    public List<MotechUser> findByRole(String role) {
        return null == role ? null : dataService.findByRole(role);
    }

    public void add(MotechUser user) {
        if (findByUserName(user.getUserName()) == null) {
            if (findUserByEmail(user.getEmail()) != null) {
                throw new EmailExistsException("User with email " + user.getEmail() + " already exists");
            }

            dataService.create(user);
        }
    }

    public void addOpenIdUser(MotechUser user) {
        if (findUserByOpenId(user.getOpenId()) == null) {
            dataService.create(user);
        }
    }

    public void update(MotechUser motechUser) {
        String email = motechUser.getEmail();
        MotechUser otherWithSameEmail = findUserByEmail(email);

        if (null != otherWithSameEmail && !otherWithSameEmail.getUserName().equals(motechUser.getUserName())) {
            throw new EmailExistsException("User with email " + email + " already exists");
        }

        dataService.update(motechUser);
    }

    public void remove(MotechUser motechUser) {
        dataService.delete(motechUser);
    }

    public List<MotechUser> getUsers() {
        List<MotechUser> users = dataService.retrieveAll();
        Iterator<MotechUser> iterator = users.iterator();

        while (iterator.hasNext()) {
            MotechUser user = iterator.next();

            if (isNotBlank(user.getOpenId())) {
                iterator.remove();
            }
        }

        return users;
    }

    public List<MotechUser> getOpenIdUsers() {
        List<MotechUser> users = dataService.retrieveAll();
        Iterator<MotechUser> iterator = users.iterator();

        while (iterator.hasNext()) {
            MotechUser user = iterator.next();

            if (isBlank(user.getOpenId())) {
                iterator.remove();
            }
        }

        return users;
    }

    @Autowired
    public void setDataService(MotechUsersDataService dataService) {
        this.dataService = dataService;
    }
}
