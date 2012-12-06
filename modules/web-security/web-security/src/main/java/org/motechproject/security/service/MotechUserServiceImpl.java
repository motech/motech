package org.motechproject.security.service;

import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class MotechUserServiceImpl implements MotechUserService {

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Autowired
    private MotechPasswordEncoder passwordEncoder;

    @Override
    public void registerAdminUser(String username, String password, String email, List<String> roles, boolean isActive) {
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl(username, password, email, "", roles, "");
        user.setActive(isActive);
        allMotechUsers.add(user);
    }

    @Override
    public void register(String username, String password, String email, String externalId, List<String> roles) {
        this.register(username, password, email, externalId, roles, true, "");
    }

    @Override
    public void register(String username, String password, String email, String externalId, List<String> roles, boolean isActive, String openId) {
        if (isBlank(username) || isBlank(password)) {
            throw new IllegalArgumentException("Username or password cannot be empty");
        }

        String encodePassword = passwordEncoder.encodePassword(password);
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl(username, encodePassword, email, externalId, roles, openId);
        user.setActive(isActive);
        allMotechUsers.add(user);
    }

    @Override
    public void activateUser(String username) {
        MotechUser motechUser = allMotechUsers.findByUserName(username);
        if (motechUser != null) {
            motechUser.setActive(true);
            allMotechUsers.update(motechUser);
        }
    }

    @Override
    public MotechUserProfile retrieveUserByCredentials(String username, String password) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user != null && passwordEncoder.isPasswordValid(user.getPassword(), password)) {
            return new MotechUserProfile(user);
        }
        return null;
    }

    @Override
    public MotechUserProfile changePassword(String username, String oldPassword, String newPassword) {
        MotechUser motechUser = allMotechUsers.findByUserName(username);
        if (motechUser != null && passwordEncoder.isPasswordValid(motechUser.getPassword(), oldPassword)) {
            motechUser.setPassword(passwordEncoder.encodePassword(newPassword));
            allMotechUsers.update(motechUser);
            return new MotechUserProfile(motechUser);
        }
        return null;
    }

    @Override
    public boolean hasUser(String username) {
        return allMotechUsers.findByUserName(username) != null;
    }

    @Override
    public List<MotechUserProfile> getUsers() {
        List<MotechUserProfile> users = new ArrayList<>();
        for (MotechUser user : allMotechUsers.getUsers()) {
            users.add(new MotechUserProfile(user));
        }
         return users;
    }

    @Override
    public UserDto getUser(String userName) {
        MotechUser user = allMotechUsers.findByUserName(userName);
        return new UserDto(user);
    }

    @Override
    public List<MotechUserProfile> getOpenIdUsers() {
        List<MotechUserProfile> users = new ArrayList<>();
        for (MotechUser user : allMotechUsers.getOpenIdUsers()) {
            users.add(new MotechUserProfile(user));
        }
        return users;
    }

    @Override
    public void updateUser(UserDto user) {
        MotechUser motechUser = allMotechUsers.findByUserName(user.getUserName());
        motechUser.setEmail(user.getEmail());
        if (!"".equals(user.getPassword())) {
            motechUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        motechUser.setRoles(user.getRoles());
        allMotechUsers.update(motechUser);
    }

    @Override
    public void deleteUser(UserDto user) {
        MotechUser motechUser = allMotechUsers.findByUserName(user.getUserName());
        allMotechUsers.remove(motechUser);
    }
}

