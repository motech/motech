package org.motechproject.mds.json;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.ImportExportBlueprint;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.osgi.framework.BundleContext;

/**
 * The <code>ExportContext</code> class holds all data needed in export process. It also provides methods
 * that must be executed in a proper context.
 *
 * @see org.motechproject.mds.domain.ImportExportBlueprint
 */
public class ExportContext {

    private ImportExportBlueprint blueprint;
    private BundleContext bundleContext;
    private AllEntities allEntities;

    public ExportContext(ImportExportBlueprint blueprint, BundleContext bundleContext, AllEntities allEntities) {
        this.blueprint = blueprint;
        this.bundleContext = bundleContext;
        this.allEntities = allEntities;
    }

    public ImportExportBlueprint getBlueprint() {
        return blueprint;
    }

    public Entity getEntity(String entityClassName) {
        return allEntities.retrieveByClassName(entityClassName);
    }

    public MotechDataService getDataService(String entityClassName) {
        return DataServiceHelper.getDataService(bundleContext, entityClassName);
    }
}
