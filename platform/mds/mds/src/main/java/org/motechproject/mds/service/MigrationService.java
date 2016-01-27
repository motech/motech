package org.motechproject.mds.service;

import org.osgi.framework.Bundle;

import java.io.IOException;

/**
 * This interface provides method for finding flyway migrations within bundle.
 * Default search location is db/migration.
 *
 * @see org.motechproject.mds.jdo.SchemaGenerator
 * @see org.motechproject.mds.domain.MigrationMapping
 */
public interface MigrationService {

    /**
     * Finds migration files in the given bundle and copy them to the
     * .motech/migration directory. This method also updates migration mapping.
     *
     * @param bundle the bundle to process.
     * @throws IOException if an I/O error occurs while copying migration files.
     */
    void processBundle(Bundle bundle) throws IOException;

}
