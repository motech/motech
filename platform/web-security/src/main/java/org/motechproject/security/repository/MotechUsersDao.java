package org.motechproject.security.repository;

import org.motechproject.config.domain.LoginMode;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.exception.EmailExistsException;
import org.motechproject.security.mds.MotechUsersDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Implementation of DAO interface that utilizes a MDS back-end for storage.
 * Class responsible for handling MotechUsers.
 */
@Repository
public class MotechUsersDao {
    private MotechUsersDataService dataService;

    /**
     * Gets MotechUser with given name
     *
     * @param userName name of user
     * @return user with given name or null in case when userName == null
     */
    @Transactional
    public MotechUser findByUserName(String userName) {
        if (userName == null) {
            return null;
        }

        MotechUser retrievedUser = dataService.findByUserName(userName.toLowerCase());

        if (retrievedUser != null && retrievedUser.getRoles() == null) {
            return findByUserName(userName);
        }

        return retrievedUser;
    }

    /**
     * Gets MotechUser with given OpenId
     *
     * @param openId of user
     * @return user with given OpenId or null in case when openId == null
     */
    @Transactional
    public MotechUser findUserByOpenId(String openId) {
        return null == openId ? null : dataService.findByOpenId(openId);
    }

    /**
     * Gets MotechUser with given email
     *
     * @param email of user
     * @return user with given email or null in case when email == null
     */
    @Transactional
    public MotechUser findUserByEmail(String email) {
        return null == email ? null : dataService.findByEmail(email);
    }

    /**
     * Returns MotechUsers with given role
     *
     * @param role of users
     * @return list that contains users with given role or null in case when role == null
     */
    @Transactional
    public List<MotechUser> findByRole(String role) {
        return null == role ? null : dataService.findByRole(role);
    }

    /**
     * Adds new MotechUser if its name and email are not null
     *
     * @param user to be added
     */
    @Transactional
    public void add(MotechUser user) {
        if (findByUserName(user.getUserName()) == null) {
            if (findUserByEmail(user.getEmail()) != null) {
                throw new EmailExistsException("User with email " + user.getEmail() + " already exists");
            }

            dataService.create(user);
        }
    }

    /**
     * Adds new MotechUser with OpenId as long as its not a null
     *
     * @param user to be added
     */
    @Transactional
    public void addOpenIdUser(MotechUser user) {
        if (findUserByOpenId(user.getOpenId()) == null) {
            dataService.create(user);
        }
    }

    /**
     * Updates given MotechUser as long as his email is not used by another user
     *
     * @param motechUser to be updated
     */
    @Transactional
    public void update(MotechUser motechUser) {
        String email = motechUser.getEmail();
        MotechUser otherWithSameEmail = findUserByEmail(email);

        if (null != otherWithSameEmail && !otherWithSameEmail.getUserName().equals(motechUser.getUserName())) {
            throw new EmailExistsException("User with email " + email + " already exists");
        }

        dataService.update(motechUser);
    }

    /**
     * Deletes given MotechUser
     *
     * @param motechUser to be removed
     */
    @Transactional
    public void remove(MotechUser motechUser) {
        dataService.delete(motechUser);
    }

    /**
     * Returns all MotechUsers that comes from
     * {@link LoginMode#REPOSITORY}
     *
     * @return list that contains users
     */
    @Transactional
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

    /**
     * Returns all MotechUsers that comes from
     * {@link LoginMode#OPEN_ID}
     *
     * @return list that contains users
     */
    @Transactional
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
