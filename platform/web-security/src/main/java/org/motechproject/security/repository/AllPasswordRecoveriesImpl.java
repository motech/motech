package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.domain.PasswordRecoveryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@Views(value = {
        @View(name = "all", map = "function(doc) { emit(doc._id, doc); }"),
        @View(name = "by_username", map = "function(doc) { if (doc.type ==='PasswordRecovery') { emit(doc.username, doc._id); }}"),
        @View(name = "by_token", map = "function(doc) { if (doc.type ==='PasswordRecovery') { emit(doc.token, doc._id); }}")
})
public class AllPasswordRecoveriesImpl extends MotechBaseRepository<PasswordRecoveryImpl>
        implements AllPasswordRecoveries {

    @Autowired
    public AllPasswordRecoveriesImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(PasswordRecoveryImpl.class, db);
    }

    @Override
    public List<PasswordRecovery> getExpired() {
        List<PasswordRecovery> expired = new ArrayList<>();

        for (PasswordRecovery recovery : getAll()) {
            if (recovery.getExpirationDate().isBefore(DateUtil.now())) {
                expired.add(recovery);
            }
        }

        return expired;
    }

    @Override
    public List<PasswordRecovery> allRecoveries() {
        return new ArrayList<PasswordRecovery>(getAll());
    }

    @Override
    public PasswordRecovery findForUser(String username) {
        if (username == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_username").key(username).includeDocs(true);
        return singleResult(db.queryView(viewQuery, PasswordRecoveryImpl.class));
    }

    @Override
    public PasswordRecovery findForToken(String token) {
        if (token == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_token").key(token).includeDocs(true);
        return singleResult(db.queryView(viewQuery, PasswordRecoveryImpl.class));
    }

    @Override
    public PasswordRecovery createRecovery(String username, String email, String token, DateTime expirationDate, Locale locale) {
        PasswordRecovery oldRecovery = findForUser(username);

        if (oldRecovery != null) {
            remove(oldRecovery);
        }

        PasswordRecovery recovery = new PasswordRecoveryImpl();

        recovery.setUsername(username);
        recovery.setEmail(email);
        recovery.setToken(token);
        recovery.setExpirationDate(expirationDate);
        recovery.setLocale(locale);

        add(recovery);

        return recovery;
    }

    @Override
    public void update(PasswordRecovery passwordRecovery) {
        super.update((PasswordRecoveryImpl) passwordRecovery);
    }

    @Override
    public void add(PasswordRecovery passwordRecovery) {
        if (findForUser(passwordRecovery.getUsername()) != null) {
            return;
        }
        super.add((PasswordRecoveryImpl) passwordRecovery);
    }

    @Override
    public void remove(PasswordRecovery passwordRecovery) {
        super.remove((PasswordRecoveryImpl) passwordRecovery);
    }
}
