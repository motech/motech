package org.motechproject.mds.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * The <code>MigrationMapping</code> class contains information about flyway migrations
 * from modules(It maps module migration version to the flyway migration version).
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class MigrationMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Integer flywayMigrationVersion;

    @Persistent
    private Integer moduleMigrationVersion;

    @Persistent
    private String moduleName;


    public MigrationMapping() {
        this(null, null);
    }

    public MigrationMapping(String moduleName, Integer moduleMigrationVersion) {
        this.moduleName = moduleName;
        this.moduleMigrationVersion = moduleMigrationVersion;
    }

    public Integer getFlywayMigrationVersion() {
        return flywayMigrationVersion;
    }

    public void setFlywayMigrationVersion(Integer flywayMigrationVersion) {
        this.flywayMigrationVersion = flywayMigrationVersion;
    }

    public Integer getModuleMigrationVersion() {
        return moduleMigrationVersion;
    }

    public void setModuleMigrationVersion(Integer moduleMigrationVersion) {
        this.moduleMigrationVersion = moduleMigrationVersion;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

}
