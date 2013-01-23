package org.motechproject.tasks.repository;

import com.google.gson.reflect.TypeToken;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllTaskDataProvidersIT extends SpringIntegrationTest {

    @Autowired
    private AllTaskDataProviders allTaskDataProviders;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Test
    public void shouldAddDataProvider() {
        TaskDataProvider expected = loadDataProvider();

        allTaskDataProviders.addOrUpdate(expected);

        TaskDataProvider actual = allTaskDataProviders.byName("MRS");

        assertEquals(expected, actual);

        markForDeletion(actual);
    }

    private TaskDataProvider loadDataProvider() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream mrsDataProviderStream = classLoader.getResourceAsStream("mrs-test-data-provider.json");
        Type type = new TypeToken<TaskDataProvider>() { }.getType();

        return (TaskDataProvider) motechJsonReader.readFromStream(mrsDataProviderStream, type);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
