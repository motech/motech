package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMotechPermissionsCouchdbImpl extends MotechBaseRepository<MotechPermissionCouchdbImpl> implements AllMotechPermissions {

    @Autowired
    protected AllMotechPermissionsCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechPermissionCouchdbImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void add(MotechPermission permission) {
        if (findByPermissionName(permission.getPermissionName()) != null) { return; }
        super.add((MotechPermissionCouchdbImpl) permission);
    }

    @Override
    @View(name = "by_permissionName", map = "function(doc) { if (doc.type ==='MotechPermission') { emit(doc.permissionName, doc._id); }}")
    public MotechPermission findByPermissionName(String permissionName) {
        if (permissionName == null) { return null; }
        ViewQuery viewQuery = createQuery("by_permissionName").key(permissionName).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechPermissionCouchdbImpl.class));    }

    @Override
    public List<MotechPermission> getPermissions() {
        return new ArrayList<MotechPermission>(getAll());
    }
}
