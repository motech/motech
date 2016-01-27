package org.motechproject.mds.repository;

import org.motechproject.mds.domain.MigrationMapping;
import org.springframework.stereotype.Repository;

/**
 * The <code>AllMigrationMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.MigrationMapping}.
 */
@Repository
public class AllMigrationMappings extends MotechDataRepository<MigrationMapping> {

    public AllMigrationMappings() {
        super(MigrationMapping.class);
    }

    public MigrationMapping retrieveByModuleAndMigrationVersion(String moduleName, Integer version) {
        return retrieve(new String[] {"moduleName", "moduleMigrationVersion"}, new Object[] {moduleName, version});
    }

}
