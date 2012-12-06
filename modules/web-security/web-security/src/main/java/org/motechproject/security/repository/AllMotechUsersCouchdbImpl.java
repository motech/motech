package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMotechUsersCouchdbImpl extends MotechBaseRepository<MotechUserCouchdbImpl> implements AllMotechUsers {

    @Autowired
    protected AllMotechUsersCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechUserCouchdbImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_userName", map = "function(doc) { if (doc.type ==='MotechUser') { emit(doc.userName, doc._id); }}")
    public MotechUser findByUserName(String userName) {
        if (userName == null) { return null; }

        String lowerUserName = userName.toLowerCase();
        ViewQuery viewQuery = createQuery("by_userName").key(lowerUserName).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechUserCouchdbImpl.class));
    }

    @Override
    @View(name = "by_openId", map = "function(doc) { if (doc.type ==='MotechUser') { emit(doc.openId, doc._id); }}")
    public MotechUser findUserByOpenId(String openId) {
        if (openId == null) { return null; }
        ViewQuery viewQuery = createQuery("by_openId").key(openId).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechUserCouchdbImpl.class));
    }

    @Override
    @View(name = "find_by_role", map = "function(doc) {if (doc.type ==='MotechUser') {for(i in doc.roles) {emit(doc.roles[i], [doc._id]);}}}")
    public List<? extends MotechUser> findByRole(String role) {
        if (role == null) { return null; }

        ViewQuery viewQuery = createQuery("find_by_role").key(role).includeDocs(true);
        return db.queryView(viewQuery, MotechUserCouchdbImpl.class);
    }

    @Override
    public void add(MotechUser user) {
        if (findByUserName(user.getUserName()) != null) { return; }

        super.add((MotechUserCouchdbImpl) user);
    }

    @Override
    public void addOpenIdUser(MotechUser user) {
        if (findUserByOpenId(user.getOpenId()) != null) { return; }
        super.add((MotechUserCouchdbImpl) user);
    }

    @Override
    public void update(MotechUser motechUser) {
        super.update((MotechUserCouchdbImpl) motechUser);
    }

    @Override
    public void remove(MotechUser motechUser) {
        super.remove((MotechUserCouchdbImpl) motechUser);
    }

    @Override
    public List<MotechUser> getUsers() {
        List<MotechUser> users = new ArrayList<MotechUser>(getAll());
        List<MotechUser> noOpenIdUsers =new ArrayList<MotechUser>();
        for (MotechUser user : users) {
             if (user.getOpenId().isEmpty()) {
                 noOpenIdUsers.add(user);
             }
        }
        return noOpenIdUsers;
    }

    @Override
    public boolean checkUserAuthorisation(String userName, String password) {
        MotechUser user = findByUserName(userName);
        return password.equals(user.getPassword()) ? true : false;
    }

    @Override
    public List<MotechUser> getOpenIdUsers() {
        List<MotechUser> users = new ArrayList<MotechUser>(getAll());
        List<MotechUser> openIdUsers =new ArrayList<MotechUser>();
        for (MotechUser user : users) {
            if (!user.getOpenId().isEmpty()) {
                openIdUsers.add(user);
            }
        }
        return openIdUsers;
    }
}
