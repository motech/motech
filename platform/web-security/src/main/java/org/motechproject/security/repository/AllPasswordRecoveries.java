package org.motechproject.security.repository;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.domain.PasswordRecovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of DAO interface that utilizes a MDS back-end for storage.
 * Class responsible for handling PasswordRecoveries.
 */
@Repository
public class AllPasswordRecoveries {
    private PasswordRecoveriesDataService dataService;

    /**
     * Returns all expired PasswordRecoveries
     *
     * @return list that contains recoveries
     */
    @Transactional
    public List<PasswordRecovery> getExpired() {
        Range<DateTime> range = new Range<>(new DateTime(0), DateUtil.now());
        return dataService.findByExpirationDate(range);
    }

    /**
     * Returns all PasswordRecoveries
     *
     * @return list that contains recoveries
     */
    @Transactional
    public List<PasswordRecovery> allRecoveries() {
        return dataService.retrieveAll();
    }

    /**
     * Gets PasswordRecovery for user with given name
     *
     * @param username name of user
     * @return recovery for given name or null in case when username is a null
     */
    @Transactional
    public PasswordRecovery findForUser(String username) {
        return null == username ? null : dataService.findForUser(username);
    }

    /**
     * Gets PasswordRecovery for given token
     *
     * @param token for recovery
     * @return recovery for given token or null in case when token is a null
     */
    @Transactional
    public PasswordRecovery findForToken(String token) {
        return null == token ? null : dataService.findForToken(token);
    }

    /**
     * Creates PasswordRecovery for given informations and return it
     *
     * @param username for recovery
     * @param email for recovery
     * @param token for recovery
     * @param expirationDate for recovery
     * @param locale for recovery
     * @return recovery with given informations
     */
    @Transactional
    public PasswordRecovery createRecovery(String username, String email, String token, DateTime expirationDate, Locale locale) {
        PasswordRecovery oldRecovery = findForUser(username);

        if (oldRecovery != null) {
            remove(oldRecovery);
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

    /**
     * Updates given PasswordRecovery
     *
     * @param passwordRecovery to be updated
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(PasswordRecovery passwordRecovery) {
        dataService.update(passwordRecovery);
    }

    /**
     * Adds given PasswordRecovery provided tha one doesn't exist yet for the user
     *
     * @param passwordRecovery to be added
     */
    @Transactional
    public void add(PasswordRecovery passwordRecovery) {
        if (findForUser(passwordRecovery.getUsername()) == null) {
            dataService.create(passwordRecovery);
        }
    }

    /**
     * Deletes given PasswordRecovery
     *
     * @param passwordRecovery to be removed
     */
    @Transactional
    public void remove(PasswordRecovery passwordRecovery) {
        dataService.delete(passwordRecovery);
    }

    @Autowired
    public void setDataService(PasswordRecoveriesDataService dataService) {
        this.dataService = dataService;
    }
}
