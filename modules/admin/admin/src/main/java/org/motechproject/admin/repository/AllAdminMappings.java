package org.motechproject.admin.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.admin.domain.AdminMapping;
import org.motechproject.dao.MotechBaseRepository;

import java.util.List;

public class AllAdminMappings extends MotechBaseRepository<AdminMapping> {

    public AllAdminMappings(CouchDbConnector connector) {
        super(AdminMapping.class, connector);
    }

    @View(name = "by_bundleName", map = "function(doc) { if(doc.type === 'AdminMapping') emit(doc.bundleName); }")
    public AdminMapping byBundleName(String bundleName) {
        List<AdminMapping> result = queryView("by_bundleName", bundleName);
        return (result.isEmpty() ? null : result.get(0));
    }

    public void addOrUpdate(AdminMapping mapping) {
        AdminMapping existingMapping = byBundleName(mapping.getBundleName());
        if (existingMapping == null) {
            add(mapping);
        } else {
            existingMapping.setBundleName(mapping.getBundleName());
            existingMapping.setDestination(mapping.getDestination());
            update(existingMapping);
        }
    }

    public void removeByBundleName(String bundleName) {
        AdminMapping mapping = byBundleName(bundleName);
        if (mapping != null) {
            remove(mapping);
        }
    }
}
