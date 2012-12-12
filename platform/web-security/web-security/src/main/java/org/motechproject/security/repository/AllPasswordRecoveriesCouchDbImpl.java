package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.domain.PasswordRecoveryCouchDbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllPasswordRecoveriesCouchDbImpl extends MotechBaseRepository<PasswordRecoveryCouchDbImpl>
        implements AllPasswordRecoveries {

    @Autowired
    public AllPasswordRecoveriesCouchDbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(PasswordRecoveryCouchDbImpl.class, db);
        initStandardDesignDocument();
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
    @View(name = "by_username", map = "function(doc) { if (doc.type ==='PasswordRecovery') { emit(doc.username, doc._id); }}")
    public PasswordRecovery findForUser(String username) {
        if (username == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_username").key(username).includeDocs(true);
        return singleResult(db.queryView(viewQuery, PasswordRecoveryCouchDbImpl.class));
    }

    @Override
    @View(name = "by_token", map = "function(doc) { if (doc.type ==='PasswordRecovery') { emit(doc.token, doc._id); }}")
    public PasswordRecovery findForToken(String token) {
        if (token == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_token").key(token).includeDocs(true);
        return singleResult(db.queryView(viewQuery, PasswordRecoveryCouchDbImpl.class));
    }

    @Override
    public PasswordRecovery createRecovery(String username, String email, String token, DateTime expirationDate) {
        PasswordRecovery oldRecovery = findForUser(username);

        if (oldRecovery != null) {
            remove(oldRecovery);
        }

        PasswordRecovery recovery = new PasswordRecoveryCouchDbImpl();

        recovery.setUsername(username);
        recovery.setEmail(email);
        recovery.setToken(token);
        recovery.setExpirationDate(expirationDate);

        add(recovery);

        return recovery;
    }

    @Override
    public void update(PasswordRecovery passwordRecovery) {
        super.update((PasswordRecoveryCouchDbImpl) passwordRecovery);
    }

    @Override
    public void add(PasswordRecovery passwordRecovery) {
        if (findForUser(passwordRecovery.getUsername()) != null) {
            return;
        }
        super.add((PasswordRecoveryCouchDbImpl) passwordRecovery);
    }

    @Override
    public void remove(PasswordRecovery passwordRecovery) {
        super.remove((PasswordRecoveryCouchDbImpl) passwordRecovery);
    }
}
