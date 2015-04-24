package org.motechproject.mdsmigration.java;

public class V33__MOTECH1359 extends AbstractMDSMigration { // NO CHECKSTYLE Bad format of member name

    @Override
    public String getMigrationImplClassName() {
        return "org.motechproject.mds.dbmigration.java." + this.getClass().getSimpleName();
    }
}
