package org.motechproject.security.repository;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.domain.PasswordRecovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
public class AllPasswordRecoveries {
    private PasswordRecoveriesDataService dataService;

    public List<PasswordRecovery> getExpired() {
        Range<DateTime> range = new Range<>(new DateTime(0), DateUtil.now());
        return dataService.findByExpirationDate(range);
    }

    public List<PasswordRecovery> allRecoveries() {
        return dataService.retrieveAll();
    }

    public PasswordRecovery findForUser(String username) {
        return null == username ? null : dataService.findForUser(username);
    }

    public PasswordRecovery findForToken(String token) {
        return null == token ? null : dataService.findForToken(token);
    }

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

    public void update(PasswordRecovery passwordRecovery) {
        dataService.update(passwordRecovery);
    }

    public void add(PasswordRecovery passwordRecovery) {
        if (findForUser(passwordRecovery.getUsername()) == null) {
            dataService.create(passwordRecovery);
        }
    }

    public void remove(PasswordRecovery passwordRecovery) {
        dataService.delete(passwordRecovery);
    }

    @Autowired
    public void setDataService(PasswordRecoveriesDataService dataService) {
        this.dataService = dataService;
    }
}
