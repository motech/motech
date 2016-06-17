package org.motechproject.mdsmigration.java;

public class V47__MOTECH1269 extends AbstractMDSMigration { // NO CHECKSTYLE Bad format of member name

    @Override
    public String getMigrationImplClassName() {
        return "org.motechproject.mds.dbmigration.java." + this.getClass().getSimpleName();
    }
}
