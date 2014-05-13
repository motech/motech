package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Views(value = {
        @View(name = "all", map = "function(doc) { emit(doc._id, doc); }"),
        @View(name = "by_roleName", map = "function(doc) { if (doc.type ==='MotechRole') { emit(doc.roleName, doc._id); }}")
})
public class AllMotechRolesImpl extends MotechBaseRepository<MotechRoleImpl> implements AllMotechRoles {

    @Autowired
    protected AllMotechRolesImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechRoleImpl.class, db);
    }

    @Override
    public List<MotechRole> getRoles() {
        return new ArrayList<MotechRole>(getAll());
    }

    @Override
    public void add(MotechRole role) {
        if (findByRoleName(role.getRoleName()) != null) {
            return;
        }
        super.add((MotechRoleImpl) role);
    }

    @Override

    public MotechRole findByRoleName(String roleName) {
        if (roleName == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_roleName").key(roleName).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechRoleImpl.class));
    }

    @Override
    public void remove(MotechRole motechRole) {
        super.remove((MotechRoleImpl) motechRole);
    }

    @Override
    public void update(MotechRole motechRole) {
        super.update((MotechRoleImpl) motechRole);
    }


}
