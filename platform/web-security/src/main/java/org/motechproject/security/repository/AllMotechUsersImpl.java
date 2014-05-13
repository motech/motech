package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserImpl;
import org.motechproject.security.ex.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Views(value = {
        @View(name = "all", map = "function(doc) { emit(doc._id, doc); }"),
        @View(name = "by_userName", map = "function(doc) { if (doc.type ==='MotechUser') { emit(doc.userName, doc._id); }}"),
        @View(name = "by_openId", map = "function(doc) { if (doc.type ==='MotechUser') { emit(doc.openId, doc._id); }}"),
        @View(name = "by_email", map = "function(doc) { if (doc.type ==='MotechUser') { emit(doc.email, doc._id); }}"),
        @View(name = "find_by_role", map = "function(doc) {if (doc.type ==='MotechUser') {for(i in doc.roles) {emit(doc.roles[i], [doc._id]);}}}")
})
public class AllMotechUsersImpl extends MotechBaseRepository<MotechUserImpl> implements AllMotechUsers {

    @Autowired
    protected AllMotechUsersImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechUserImpl.class, db);
    }

    @Override
    public MotechUser findByUserName(String userName) {
        if (userName == null) {
            return null;
        }

        String lowerUserName = userName.toLowerCase();
        ViewQuery viewQuery = createQuery("by_userName").key(lowerUserName).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechUserImpl.class));
    }

    @Override
    public MotechUser findUserByOpenId(String openId) {
        if (openId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_openId").key(openId).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechUserImpl.class));
    }

    @Override
    public MotechUser findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_email").key(email).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechUserImpl.class));
    }

    @Override
    public List<? extends MotechUser> findByRole(String role) {
        if (role == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("find_by_role").key(role).includeDocs(true);
        return db.queryView(viewQuery, MotechUserImpl.class);
    }

    @Override
    public void add(MotechUser user) {
        if (findByUserName(user.getUserName()) != null) {
            return;
        } else if (findUserByEmail(user.getEmail()) != null) {
            throw new EmailExistsException("User with email " + user.getEmail() + " already exists");
        }

        super.add((MotechUserImpl) user);
    }

    @Override
    public void addOpenIdUser(MotechUser user) {
        if (findUserByOpenId(user.getOpenId()) != null) {
            return;
        }
        super.add((MotechUserImpl) user);
    }

    @Override
    public void update(MotechUser motechUser) {
        String email = motechUser.getEmail();
        MotechUser otherWithSameEmail = findUserByEmail(email);

        if (otherWithSameEmail != null && !otherWithSameEmail.getUserName().equals(motechUser.getUserName())) {
            throw new EmailExistsException("User with email " + email + " already exists");
        }

        super.update((MotechUserImpl) motechUser);
    }

    @Override
    public void remove(MotechUser motechUser) {
        super.remove((MotechUserImpl) motechUser);
    }

    @Override
    public List<MotechUser> getUsers() {
        List<MotechUser> users = new ArrayList<MotechUser>(getAll());
        List<MotechUser> noOpenIdUsers = new ArrayList<MotechUser>();
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
        return password.equals(user.getPassword());
    }

    @Override
    public List<MotechUser> getOpenIdUsers() {
        List<MotechUser> users = new ArrayList<MotechUser>(getAll());
        List<MotechUser> openIdUsers = new ArrayList<MotechUser>();
        for (MotechUser user : users) {
            if (!user.getOpenId().isEmpty()) {
                openIdUsers.add(user);
            }
        }
        return openIdUsers;
    }
}
