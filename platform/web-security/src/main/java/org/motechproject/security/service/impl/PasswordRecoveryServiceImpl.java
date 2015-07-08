package org.motechproject.security.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.ex.NonAdminUserException;
import org.motechproject.security.ex.UserNotFoundException;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllPasswordRecoveries;
import org.motechproject.security.service.PasswordRecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationProvider;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link org.motechproject.security.service.PasswordRecoveryService}
 * Responsible for password recovery.
 */
@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryServiceImpl.class);

    private static final int TOKEN_LENGTH = 60;
    private static final int DEFAULT_EXPIRATION_HOURS = 3;

    private AllPasswordRecoveries allPasswordRecoveries;
    private AllMotechUsers allMotechUsers;
    private EmailSender emailSender;
    private MotechPasswordEncoder passwordEncoder;
    private OpenIDAuthenticationProvider authenticationManager;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void cleanUpExpiredRecoveries() {
        List<PasswordRecovery> expiredRecoveries = allPasswordRecoveries.getExpired();
        for (PasswordRecovery recovery : expiredRecoveries) {
            allPasswordRecoveries.remove(recovery);
        }
        LOGGER.info("Cleaned up all expired password recoveries");
    }

    @Override
    public boolean validateToken(String token) {
        PasswordRecovery recovery = allPasswordRecoveries.findForToken(token);
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
    public String oneTimeTokenOpenId(String email, DateTime expiration, boolean notify) throws UserNotFoundException, NonAdminUserException {
        MotechUser user = allMotechUsers.findUserByEmail(email);
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
        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(user.getUserName(), user.getEmail(),
                token, expirationDate, user.getLocale());

        if (notify) {
            emailSender.sendOneTimeToken(recovery);
        }

        LOGGER.info("Created a one time token for user " + user.getUserName());

        return token;
    }

    @Override
    public void validateTokenAndLoginUser(String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        PasswordRecovery recovery = allPasswordRecoveries.findForToken(token);
        if (validateRecovery(recovery)) {
            MotechUser user = allMotechUsers.findUserByEmail(recovery.getEmail());
            OpenIDAuthenticationToken openIDToken = new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS, user.getOpenId(), "one time login ", new ArrayList<OpenIDAttribute>());
            Authentication authentication = authenticationManager.authenticate(openIDToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            allPasswordRecoveries.remove(recovery);
            redirectStrategy.sendRedirect(request, response, "/server/home");
        } else {
            redirectStrategy.sendRedirect(request, response, "/server/login");
        }
    }

    @Override
    public String passwordRecoveryRequest(String email) throws UserNotFoundException {
        return passwordRecoveryRequest(email, true);
    }

    @Override
    public String passwordRecoveryRequest(String email, boolean notify) throws UserNotFoundException {
        return passwordRecoveryRequest(email, DateTime.now().plusHours(DEFAULT_EXPIRATION_HOURS), notify);
    }

    @Override
    public String passwordRecoveryRequest(String email, DateTime expiration) throws UserNotFoundException {
        return passwordRecoveryRequest(email, expiration, true);
    }

    @Override
    public String passwordRecoveryRequest(String email, DateTime expiration, boolean notify) throws UserNotFoundException {
        MotechUser user = allMotechUsers.findUserByEmail(email);
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
        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(user.getUserName(), user.getEmail(),
                token, expirationDate, user.getLocale());

        if (notify) {
            emailSender.sendRecoveryEmail(recovery);
        }

        LOGGER.info("Created a password recovery for user " + user.getUserName());

        return token;
    }

    @Override
    public void resetPassword(String token, String password, String passwordConfirmation) throws InvalidTokenException {
        if (!password.equals(passwordConfirmation)) {
            throw new IllegalArgumentException("Password and confirmation do not match");
        }

        PasswordRecovery recovery = allPasswordRecoveries.findForToken(token);

        if (!validateRecovery(recovery)) {
            throw new InvalidTokenException();
        }

        MotechUser user = allMotechUsers.findByUserName(recovery.getUsername());

        if (user == null) {
            throw new InvalidTokenException("This user has been deleted");
        }

        String encodedPassword = passwordEncoder.encodePassword(password);
        user.setPassword(encodedPassword);

        allMotechUsers.update(user);
        allPasswordRecoveries.remove(recovery);
    }

    private boolean validateRecovery(PasswordRecovery recovery) {
        return recovery != null && recovery.getExpirationDate().isAfter(DateUtil.now());
    }

    @Autowired
    public void setAllPasswordRecoveries(AllPasswordRecoveries allPasswordRecoveries) {
        this.allPasswordRecoveries = allPasswordRecoveries;
    }

    @Autowired
    public void setAllMotechUsers(AllMotechUsers allMotechUsers) {
        this.allMotechUsers = allMotechUsers;
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
