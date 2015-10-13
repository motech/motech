package org.motechproject.mds.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.mds.it.reposistory.AllEntitiesContextIT;
import org.motechproject.mds.it.reposistory.AllEntityDraftsContextIT;
import org.motechproject.mds.it.reposistory.AllTypeSettingsContextIT;
import org.motechproject.mds.it.reposistory.AllTypesContextIT;
import org.motechproject.mds.it.reposistory.ComboboxValueRepositoryContextIT;
import org.motechproject.mds.it.service.EntityServiceContextIT;
import org.motechproject.mds.it.service.HistoryServiceContextIT;
import org.motechproject.mds.it.service.JarGeneratorServiceContextIT;
import org.motechproject.mds.it.service.TypeServiceImplContextIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({FilterContextIT.class, AutoGenerationContextIT.class, AllEntitiesContextIT.class,
        AllEntityDraftsContextIT.class, AllTypeSettingsContextIT.class, AllTypesContextIT.class,
        EntityServiceContextIT.class, HistoryServiceContextIT.class, TypeServiceImplContextIT.class,
        JarGeneratorServiceContextIT.class, ComboboxValueRepositoryContextIT.class})
public class MdsContextIntegrationTests {
}
