package org.motechproject.security.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.security.constants.UserRoleNames;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.security.service.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

/**
 * Implementation of MotechUserService. Allows to search and manage users.
 *
 * @see org.motechproject.security.service.MotechUserService
 */
@Service("motechUserService")
public class MotechUserServiceImpl implements MotechUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechUserServiceImpl.class);

    private AllMotechUsers allMotechUsers;
    private MotechPasswordEncoder passwordEncoder;
    private EmailSender emailSender;
    private UserContextService userContextsService;

    @Override
    public void register(String username, String password, String email, String externalId, List<String> roles,
                         Locale locale) {
        this.register(username, password, email, externalId, roles, locale, true, "");
    }

    @Override
    public void register(String username, String password, // NO CHECKSTYLE More than 7 parameters (found 8).
                         String email, String externalId, List<String> roles, Locale locale, boolean isActive,
                         String openId) {
        LOGGER.info("Registering new user: {}", username);
        if (isBlank(username) || isBlank(password)) {
            throw new IllegalArgumentException("Username or password cannot be empty");
        }

        String encodePassword = passwordEncoder.encodePassword(password);
        MotechUser user = new MotechUser(username, encodePassword, email, externalId, roles,
                openId, locale);
        user.setActive(isActive);
        allMotechUsers.add(user);
        LOGGER.info("Registered new user: {}", username);
    }

    @Override
    public void registerMotechAdmin(String username, String password, String email, Locale locale) {
        if (!hasActiveMotechAdmin()) {
            List<String> roles = Arrays.asList(MOTECH_ADMIN);
            this.register(username, password, email, null, roles, locale);
            LOGGER.info("User {} has been registered as the MOTECH Admin", username);
        } else {
            throw new IllegalStateException("The MOTECH Platform has already got an active admin user. The registration of " +
                    "an admin user with this method is only possible, when there are no active admin users. Please use the" +
                    "register() method to register more users.");
        }
    }

    @Override
    public void activateUser(String username) {
        LOGGER.info("Activating user: {}", username);
        MotechUser motechUser = allMotechUsers.findByUserName(username);
        if (motechUser != null) {
            motechUser.setActive(true);
            allMotechUsers.update(motechUser);
        }
        LOGGER.info("Activated user: {}", username);
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
    public MotechUserProfile changePassword(String oldPassword, String newPassword) {
        UserDto currentUser = getCurrentUser();
        return changePassword(currentUser.getUserName(), oldPassword, newPassword);
    }

    @Override
    public void changeEmail(String email) {
        UserDto currentUser = getCurrentUser();
        currentUser.setEmail(email);
        updateUserDetailsWithoutPassword(currentUser);
    }

    @Override
    public MotechUserProfile changePassword(String userName, String oldPassword, String newPassword) {
        MotechUser motechUser = allMotechUsers.findByUserName(userName);

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
    public boolean hasEmail(String email) {
        MotechUser user = allMotechUsers.findUserByEmail(email);
        return user != null;
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
    public UserDto getUserByEmail(String email) {
        MotechUser user = allMotechUsers.findUserByEmail(email);
        return user == null ? null : new UserDto(user);
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userInSession = (authentication == null) ? null : (User) authentication.getPrincipal();
        if (userInSession == null) {
            return null;
        }

        String currentUserName = userInSession.getUsername();
        MotechUser user = allMotechUsers.findByUserName(currentUserName);
        return new UserDto(user);
    }

    @Override
    public Locale getLocale(String userName) {
        return allMotechUsers.findByUserName(userName).getLocale();
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
    public void updateUserDetailsWithoutPassword(UserDto user) {
        MotechUser motechUser = allMotechUsers.findByUserName(user.getUserName());
        motechUser.setEmail(user.getEmail());
        motechUser.setActive(user.isActive());
        motechUser.setRoles(user.getRoles());
        motechUser.setLocale(user.getLocale());
        allMotechUsers.update(motechUser);
        userContextsService.refreshUserContextIfActive(motechUser.getUserName());
    }

    @Override
    public void updateUserDetailsWithPassword(UserDto user) {
        MotechUser motechUser = allMotechUsers.findByUserName(user.getUserName());
        motechUser.setEmail(user.getEmail());
        motechUser.setActive(user.isActive());
        if (!StringUtils.isEmpty(user.getPassword())) {
            motechUser.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            throw new IllegalArgumentException("User password cannot be empty. If you wish to omit changing user password, " +
                    "please call method updateUserDetailsWithoutPassword.");
        }
        motechUser.setRoles(user.getRoles());
        motechUser.setLocale(user.getLocale());
        allMotechUsers.update(motechUser);
        userContextsService.refreshUserContextIfActive(motechUser.getUserName());
    }

    @Override
    public void deleteUser(UserDto user) {
        LOGGER.info("Deleting user: {}", user.getUserName());
        MotechUser motechUser = allMotechUsers.findByUserName(user.getUserName());
        allMotechUsers.remove(motechUser);
        LOGGER.info("Deleted user: {}", user.getUserName());
    }

    @Override
    public void sendLoginInformation(String userName, String password) {
        MotechUser user = allMotechUsers.findByUserName(userName);
        emailSender.sendLoginInfo(user, password);
    }

    @Override
    public void setLocale(Locale locale) {
        UserDto currentUser = getCurrentUser();
        MotechUser user = allMotechUsers.findByUserName(currentUser.getUserName());
        user.setLocale(locale);
        updateUserDetailsWithoutPassword(new UserDto(user));
    }

    @Override
    public List<String> getRoles(String userName) {
        MotechUser user = allMotechUsers.findByUserName(userName);
        return (user == null) ? Collections.<String>emptyList() : user.getRoles();
    }

    @Override
    public boolean hasActiveMotechAdmin() {
        List<MotechUserProfile> users = getUsers();
        MotechUserProfile motechUser = (MotechUserProfile) CollectionUtils.find(users, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                MotechUserProfile user = (MotechUserProfile) object;
                return user.isActive() && user.hasRole(UserRoleNames.MOTECH_ADMIN);
            }
        });
        return motechUser != null;
    }

    @Autowired
    public void setAllMotechUsers(AllMotechUsers allMotechUsers) {
        this.allMotechUsers = allMotechUsers;
    }

    @Autowired
    public void setPasswordEncoder(MotechPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Autowired
    public void setUserContextsService(UserContextService userContextsService) {
        this.userContextsService = userContextsService;
    }
}

