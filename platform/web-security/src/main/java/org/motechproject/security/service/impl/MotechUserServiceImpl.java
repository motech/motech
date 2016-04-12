package org.motechproject.security.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.constants.UserRoleNames;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.exception.NonAdminUserException;
import org.motechproject.security.exception.UserNotFoundException;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.repository.MotechUsersDao;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.security.service.UserContextService;
import org.motechproject.security.validator.PasswordValidator;
import org.motechproject.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

/**
 * Implementation of the {@link org.motechproject.security.service.MotechUserService}
 * Allows to search and manage users.
 */
@Service("motechUserService")
public class MotechUserServiceImpl implements MotechUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechUserServiceImpl.class);

    private MotechUsersDao motechUsersDao;
    private MotechPasswordEncoder passwordEncoder;
    private EmailSender emailSender;
    private UserContextService userContextsService;
    private SettingService settingService;
    private SettingsFacade settingsFacade;
    private PasswordRecoveryService passwordRecoveryService;

    @Override
    @Transactional
    public void register(String username, String password, String email, String externalId, List<String> roles,
                         Locale locale) {
        this.register(username, password, email, externalId, roles, locale, UserStatus.ACTIVE , null);
    }

    @Override
    @Transactional
    public void register(String username, String password, // NO CHECKSTYLE More than 7 parameters (found 8).
                         String email, String externalId, List<String> roles, Locale locale, UserStatus userStatus,
                         String openId) {
        LOGGER.info("Registering new user: {}", username);
        if (isBlank(username) || isBlank(password)) {
            throw new IllegalArgumentException("Username or password cannot be empty");
        }

        validatePassword(password);

        String encodePassword = passwordEncoder.encodePassword(password);
        MotechUser user = new MotechUser(username, encodePassword, email, externalId, roles,
                openId, locale);
        user.setUserStatus(userStatus);
        motechUsersDao.add(user);
        LOGGER.info("Registered new user: {}", username);
    }

    @Override
    @Transactional
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
    @Transactional
    public void activateUser(String username) {
        LOGGER.info("Activating user: {}", username);
        MotechUser motechUser = motechUsersDao.findByUserName(username);
        if (motechUser != null) {
            motechUser.setUserStatus(UserStatus.ACTIVE);
            motechUsersDao.update(motechUser);
        }
        LOGGER.info("Activated user: {}", username);
    }

    @Override
    @Transactional
    public MotechUserProfile retrieveUserByCredentials(String username, String password) {
        MotechUser user = motechUsersDao.findByUserName(username);
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
    @Transactional
    public MotechUserProfile changeExpiredPassword(String userName, String oldPassword, String newPassword) {
        MotechUser motechUser = motechUsersDao.findByUserName(userName);

        validatePassword(newPassword);

        if (motechUser != null && UserStatus.MUST_CHANGE_PASSWORD.equals(motechUser.getUserStatus()) &&
                passwordEncoder.isPasswordValid(motechUser.getPassword(), oldPassword)) {
            //The new password and the old password cannot be the same
            if (passwordEncoder.isPasswordValid(motechUser.getPassword(), newPassword)) {
                return null;
            }
            motechUser.setPassword(passwordEncoder.encodePassword(newPassword));
            motechUser.setUserStatus(UserStatus.ACTIVE);
            motechUsersDao.update(motechUser);
            return new MotechUserProfile(motechUser);
        } else {
            //Wrong password
            incrementFailureLoginCount(motechUser);
        }

        return null;
    }

    @Override
    @Transactional
    public MotechUserProfile changePassword(String userName, String oldPassword, String newPassword) {
        MotechUser motechUser = motechUsersDao.findByUserName(userName);

        validatePassword(newPassword);

        if (motechUser != null && passwordEncoder.isPasswordValid(motechUser.getPassword(), oldPassword)) {
            motechUser.setPassword(passwordEncoder.encodePassword(newPassword));
            motechUsersDao.update(motechUser);
            return new MotechUserProfile(motechUser);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean hasUser(String username) {
        return motechUsersDao.findByUserName(username) != null;
    }

    @Override
    @Transactional
    public boolean hasEmail(String email) {
        MotechUser user = motechUsersDao.findUserByEmail(email);
        return user != null;
    }

    @Override
    @Transactional
    public List<MotechUserProfile> getUsers() {
        List<MotechUserProfile> users = new ArrayList<>();
        for (MotechUser user : motechUsersDao.getUsers()) {
            users.add(new MotechUserProfile(user));
        }
        return users;
    }

    @Override
    @Transactional
    public UserDto getUser(String userName) {
        MotechUser user = motechUsersDao.findByUserName(userName);
        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto getUserByEmail(String email) {
        MotechUser user = motechUsersDao.findUserByEmail(email);
        return user == null ? null : new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userInSession = (authentication == null) ? null : (User) authentication.getPrincipal();
        if (userInSession == null) {
            return null;
        }

        String currentUserName = userInSession.getUsername();
        MotechUser user = motechUsersDao.findByUserName(currentUserName);
        return new UserDto(user);
    }

    @Override
    @Transactional
    public Locale getLocale(String userName) {
        return motechUsersDao.findByUserName(userName).getLocale();
    }

    @Override
    @Transactional
    public List<MotechUserProfile> getOpenIdUsers() {
        List<MotechUserProfile> users = new ArrayList<>();
        for (MotechUser user : motechUsersDao.getOpenIdUsers()) {
            users.add(new MotechUserProfile(user));
        }
        return users;
    }

    @Override
    @Transactional
    public void updateUserDetailsWithoutPassword(UserDto user) {
        MotechUser motechUser = motechUsersDao.findByUserName(user.getUserName());
        motechUser.setEmail(user.getEmail());
        motechUser.setUserStatus(user.getUserStatus());
        motechUser.setRoles(user.getRoles());
        motechUser.setLocale(user.getLocale());
        motechUsersDao.update(motechUser);
        userContextsService.refreshUserContextIfActive(motechUser.getUserName());
    }

    @Override
    @Transactional
    public void updateUserDetailsWithPassword(UserDto user) {
        if (!StringUtils.isEmpty(user.getPassword())) {
            validatePassword(user.getPassword());

            MotechUser motechUser = motechUsersDao.findByUserName(user.getUserName());
            motechUser.setEmail(user.getEmail());
            motechUser.setUserStatus(user.getUserStatus());

            motechUser.setPassword(passwordEncoder.encode(user.getPassword()));

            motechUser.setRoles(user.getRoles());
            motechUser.setLocale(user.getLocale());
            motechUsersDao.update(motechUser);
            userContextsService.refreshUserContextIfActive(motechUser.getUserName());
        } else {
            throw new IllegalArgumentException("User password cannot be empty. If you wish to omit changing user password, " +
                    "please call method updateUserDetailsWithoutPassword.");
        }
    }

    @Override
    @Transactional
    public void deleteUser(UserDto user) {
        LOGGER.info("Deleting user: {}", user.getUserName());

        MotechUser motechUser = motechUsersDao.findByUserName(user.getUserName());
        motechUsersDao.remove(motechUser);

        userContextsService.logoutUser(user.getUserName());

        LOGGER.info("Deleted user: {}", user.getUserName());
    }

    @Override
    @Transactional
    public void sendLoginInformation(String userName) throws UserNotFoundException, NonAdminUserException {
        String token;
        MotechUser user = motechUsersDao.findByUserName(userName);

        if (settingsFacade.getPlatformSettings().getLoginMode().isRepository()) {
            token = passwordRecoveryService.passwordRecoveryRequest(user.getEmail(), false);
        } else {
            token = passwordRecoveryService.oneTimeTokenOpenId(user.getEmail(), false);
        }

        emailSender.sendLoginInfo(user, token);
    }

    @Override
    @Transactional
    public void setLocale(Locale locale) {
        UserDto currentUser = getCurrentUser();
        MotechUser user = motechUsersDao.findByUserName(currentUser.getUserName());
        user.setLocale(locale);
        updateUserDetailsWithoutPassword(new UserDto(user));
    }

    @Override
    @Transactional
    public List<String> getRoles(String userName) {
        MotechUser user = motechUsersDao.findByUserName(userName);
        return (user == null) ? Collections.<String>emptyList() : user.getRoles();
    }

    @Override
    @Transactional
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

    @Override
    public void validatePassword(String password) {
        PasswordValidator validator = settingService.getPasswordValidator();
        validator.validate(password);
    }

    private void incrementFailureLoginCount(MotechUser user) {
        if (user != null && settingService.getFailureLoginLimit() > 0) {
            user.incrementFailureLoginCounter();
            if (user.getFailureLoginCounter() > settingService.getFailureLoginLimit()) {
                user.setUserStatus(UserStatus.BLOCKED);
            }
            motechUsersDao.update(user);
            if (UserStatus.BLOCKED.equals(user.getUserStatus())) {
                throw new LockedException("User has been blocked");
            }
        }
    }

    @Autowired
    public void setMotechUsersDao(MotechUsersDao motechUsersDao) {
        this.motechUsersDao = motechUsersDao;
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

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @Autowired
    public void setPasswordRecoveryService(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }
}

