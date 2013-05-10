package org.motechproject.security.service;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.ex.UserNotFoundException;
import org.motechproject.security.password.NonAdminUserException;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllPasswordRecoveries;
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
import java.util.Locale;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordRecoveryServiceImpl.class);

    private static final int TOKEN_LENGTH = 60;
    private static final int EXPIRATION_HOURS = 1;

    @Autowired
    private AllPasswordRecoveries allPasswordRecoveries;

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private MotechPasswordEncoder passwordEncoder;

    @Autowired
    private OpenIDAuthenticationProvider authenticationManager;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void cleanUpExpiredRecoveries() {
        List<PasswordRecovery> expiredRecoveries = allPasswordRecoveries.getExpired();
        for (PasswordRecovery recovery : expiredRecoveries) {
            allPasswordRecoveries.remove(recovery);
        }
        LOG.info("Cleaned up all expired password recoveries");
    }

    @Override
    public boolean validateToken(String token) {
        PasswordRecovery recovery = allPasswordRecoveries.findForToken(token);
        return validateRecovery(recovery);
    }

    @Override
    public void oneTimeTokenOpenId(String email, Locale locale) throws UserNotFoundException, NonAdminUserException {
        MotechUser user = allMotechUsers.findUserByEmail(email);

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
        DateTime expirationDate = DateTimeSourceUtil.now().plusHours(EXPIRATION_HOURS);

        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(user.getUserName(), user.getEmail(),
                token, expirationDate);

        emailSender.sendOneTimeToken(recovery, locale);

        LOG.info("Created a one time token for user " + user.getUserName());
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
    public void passwordRecoveryRequest(String email, Locale locale) throws UserNotFoundException {
        MotechUser user = allMotechUsers.findUserByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User with email not found: " + email);
        }

        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        DateTime expirationDate = DateTimeSourceUtil.now().plusHours(EXPIRATION_HOURS);

        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(user.getUserName(), user.getEmail(),
                token, expirationDate);

        emailSender.sendResecoveryEmail(recovery,locale);

        LOG.info("Created a password recovery for user " + user.getUserName());
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
}
