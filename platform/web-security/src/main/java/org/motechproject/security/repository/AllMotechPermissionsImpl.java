package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link AllMotechPermissions} interface.
 */
@Component
@Views(value = {
        @View(name = "all", map = "function(doc) { emit(doc._id, doc); }"),
        @View(name = "by_permissionName", map = "function(doc) { if (doc.type ==='MotechPermission') { emit(doc.permissionName, doc._id); }}")
})
public class AllMotechPermissionsImpl extends MotechBaseRepository<MotechPermissionImpl> implements AllMotechPermissions {

    @Autowired
    protected AllMotechPermissionsImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechPermissionImpl.class, db);
    }

    @Override
    public void add(MotechPermission permission) {
        if (findByPermissionName(permission.getPermissionName()) != null) {
            return;
        }
        super.add((MotechPermissionImpl) permission);
    }

    @Override
    public MotechPermission findByPermissionName(String permissionName) {
        if (permissionName == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_permissionName").key(permissionName).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechPermissionImpl.class));
    }

    @Override
    public List<MotechPermission> getPermissions() {
        return new ArrayList<MotechPermission>(getAll());
    }

    @Override
    public void delete(MotechPermission permission) {
        remove((MotechPermissionImpl) permission);
    }
}
