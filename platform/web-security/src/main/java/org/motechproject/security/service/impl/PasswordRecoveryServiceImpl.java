package org.motechproject.security.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.exception.InvalidTokenException;
import org.motechproject.security.exception.NonAdminUserException;
import org.motechproject.security.exception.UserNotFoundException;
import org.motechproject.security.repository.MotechUsersDao;
import org.motechproject.security.service.mds.PasswordRecoveriesDataService;
import org.motechproject.security.service.PasswordRecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.openid.OpenIDAuthenticationProvider;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of the {@link org.motechproject.security.service.PasswordRecoveryService}
 * Responsible for password recovery.
 */
@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryServiceImpl.class);

    private static final int TOKEN_LENGTH = 60;
    private static final int DEFAULT_EXPIRATION_HOURS = 3;

    private PasswordRecoveriesDataService passwordRecoveriesDataService;
    private MotechUsersDao motechUsersDao;
    private EmailSender emailSender;
    private MotechPasswordEncoder passwordEncoder;
    private OpenIDAuthenticationProvider authenticationManager;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    @Transactional
    public void cleanUpExpiredRecoveries() {
        Range<DateTime> range = new Range<>(new DateTime(0), DateUtil.now());
        List<PasswordRecovery> expiredRecoveries = passwordRecoveriesDataService.findByExpirationDate(range);

        for (PasswordRecovery recovery : expiredRecoveries) {
            passwordRecoveriesDataService.delete(recovery);
        }
        LOGGER.info("Cleaned up all expired password recoveries");
    }

    @Override
    @Transactional
    public boolean validateToken(String token) {
        PasswordRecovery recovery = findForToken(token);
        return validateRecovery(recovery);
    }

    @Override
    public String oneTimeTokenOpenId(String email) throws UserNotFoundException, NonAdminUserException {
        return oneTimeTokenOpenId(email, true);
    }

    @Override
    public String oneTimeTokenOpenId(String email, boolean notify) throws UserNotFoundException, NonAdminUserException {
        return oneTimeTokenOpenId(email, DateTime.now().plusHours(DEFAULT_EXPIRATION_HOURS), notify);
    }

    @Override
    @Transactional
    public String oneTimeTokenOpenId(String email, DateTime expiration, boolean notify) throws UserNotFoundException, NonAdminUserException {
        MotechUser user = motechUsersDao.findUserByEmail(email);
        DateTime expirationDate = expiration;

        if (expirationDate == null) {
            expirationDate = DateTime.now().plusHours(DEFAULT_EXPIRATION_HOURS);
        } else if (expirationDate.isBefore(DateTime.now())) {
            throw new IllegalArgumentException("The expiration date shouldn't be a past date!");
        }

        if (user == null) {
            throw new UserNotFoundException("User with email not found: " + email);
        }

        List<String> roles = user.getRoles();
        boolean isAdminUser = false;
        for (String role : roles) {
            if (role.toLowerCase().contains("admin")) {
                isAdminUser = true;
            }
        }
        if (!isAdminUser) {
            throw new NonAdminUserException("You are not admin User: " + user.getUserName());
        }

        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        PasswordRecovery recovery = createRecovery(user.getUserName(), user.getEmail(), token, expirationDate, user.getLocale());

        if (notify) {
            emailSender.sendOneTimeToken(recovery);
        }

        LOGGER.info("Created a one time token for user " + user.getUserName());

        return token;
    }

    @Override
    @Transactional
    public void validateTokenAndLoginUser(String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        PasswordRecovery recovery = findForToken(token);
        if (validateRecovery(recovery)) {
            MotechUser user = motechUsersDao.findUserByEmail(recovery.getEmail());
            OpenIDAuthenticationToken openIDToken = new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS, user.getOpenId(), "one time login ", new ArrayList<>());
            Authentication authentication = authenticationManager.authenticate(openIDToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            passwordRecoveriesDataService.delete(recovery);
            redirectStrategy.sendRedirect(request, response, "/server/home");
        } else {
            redirectStrategy.sendRedirect(request, response, "/server/login");
        }
    }

    @Override
    @Transactional
    public String passwordRecoveryRequest(String email) throws UserNotFoundException {
        return passwordRecoveryRequest(email, true);
    }

    @Override
    @Transactional
    public String passwordRecoveryRequest(String email, boolean notify) throws UserNotFoundException {
        return passwordRecoveryRequest(email, DateTime.now().plusHours(DEFAULT_EXPIRATION_HOURS), notify);
    }

    @Override
    @Transactional
    public String passwordRecoveryRequest(String email, DateTime expiration) throws UserNotFoundException {
        return passwordRecoveryRequest(email, expiration, true);
    }

    @Override
    @Transactional
    public String passwordRecoveryRequest(String email, DateTime expiration, boolean notify) throws UserNotFoundException {
        MotechUser user = motechUsersDao.findUserByEmail(email);
        DateTime expirationDate = expiration;

        if (expirationDate == null) {
            expirationDate = DateTime.now().plusHours(DEFAULT_EXPIRATION_HOURS);
        } else if (expirationDate.isBefore(DateTime.now())) {
            throw new IllegalArgumentException("The expiration date shouldn't be a past date!");
        }

        if (user == null) {
            throw new UserNotFoundException("User with email not found: " + email);
        }

        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        PasswordRecovery recovery = createRecovery(user.getUserName(), user.getEmail(), token, expirationDate, user.getLocale());

        if (notify) {
            emailSender.sendRecoveryEmail(recovery);
        }

        LOGGER.info("Created a password recovery for user " + user.getUserName());

        return token;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password, String passwordConfirmation) throws InvalidTokenException {
        if (!password.equals(passwordConfirmation)) {
            throw new IllegalArgumentException("Password and confirmation do not match");
        }

        PasswordRecovery recovery = findForToken(token);

        if (!validateRecovery(recovery)) {
            throw new InvalidTokenException();
        }

        MotechUser user = motechUsersDao.findByUserName(recovery.getUsername());

        if (user == null) {
            throw new InvalidTokenException("This user has been deleted");
        }

        String encodedPassword = passwordEncoder.encodePassword(password);
        user.setPassword(encodedPassword);

        motechUsersDao.update(user);
        passwordRecoveriesDataService.delete(recovery);
    }

    private PasswordRecovery createRecovery(String username, String email, String token, DateTime expirationDate, Locale locale) {
        PasswordRecovery oldRecovery = findForUser(username);

        if (oldRecovery != null) {
            passwordRecoveriesDataService.delete(oldRecovery);
        }

        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setUsername(username);
        recovery.setEmail(email);
        recovery.setToken(token);
        recovery.setExpirationDate(expirationDate);
        recovery.setLocale(locale);

        add(recovery);

        return recovery;
    }

    private void add(PasswordRecovery passwordRecovery) {
        if (findForUser(passwordRecovery.getUsername()) == null) {
            passwordRecoveriesDataService.create(passwordRecovery);
        }
    }

    private boolean validateRecovery(PasswordRecovery recovery) {
        return recovery != null && recovery.getExpirationDate().isAfter(DateUtil.now());
    }

    private PasswordRecovery findForUser(String username) {
        return null == username ? null : passwordRecoveriesDataService.findForUser(username);
    }

    private PasswordRecovery findForToken(String token) {
        return null == token ? null : passwordRecoveriesDataService.findForToken(token);
    }

    @Autowired
    public void setPasswordRecoveriesDataService(PasswordRecoveriesDataService passwordRecoveriesDataService) {
        this.passwordRecoveriesDataService = passwordRecoveriesDataService;
    }

    @Autowired
    public void setMotechUsersDao(MotechUsersDao motechUsersDao) {
        this.motechUsersDao = motechUsersDao;
    }

    @Autowired
    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Autowired
    public void setPasswordEncoder(MotechPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(OpenIDAuthenticationProvider authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
