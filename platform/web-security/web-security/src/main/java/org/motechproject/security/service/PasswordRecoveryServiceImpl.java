package org.motechproject.security.service;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.password.InvalidTokenException;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllPasswordRecoveries;
import org.motechproject.security.password.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void passwordRecoveryRequest(String email) throws UserNotFoundException {
        MotechUser user = allMotechUsers.findUserByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User with email not found: " + email);
        }

        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        DateTime expirationDate = DateTimeSourceUtil.now().plusHours(EXPIRATION_HOURS);

        PasswordRecovery recovery = allPasswordRecoveries.createRecovery(user.getUserName(), user.getEmail(),
                token, expirationDate);

        emailSender.sendResecoveryEmail(recovery);

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
