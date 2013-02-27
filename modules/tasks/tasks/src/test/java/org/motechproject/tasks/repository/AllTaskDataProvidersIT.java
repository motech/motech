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
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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
        List<TaskDataProvider> expected = new ArrayList<>();
        expected.add(loadDataProvider());

        allTaskDataProviders.addOrUpdate(expected.get(0));

        List<TaskDataProvider> providers = allTaskDataProviders.getAll();

        assertEquals(expected, providers);

        TaskDataProvider actual = providers.get(0);
        actual.getObjects().remove(2);

        allTaskDataProviders.addOrUpdate(actual);

        providers = allTaskDataProviders.getAll();

        assertEquals(asList(actual), providers);

        markForDeletion(allTaskDataProviders.getAll());
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
