package org.motechproject.mds.jdo;

import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.util.ClassName;

import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchemaGeneratorTest {

    @Mock
    private JDOPersistenceManagerFactory pmf;

    @Mock
    private PersistenceNucleusContext nucleusContext;

    @Mock(extraInterfaces = SchemaAwareStoreManager.class)
    private StoreManager storeManager;

    private SchemaGenerator schemaGenerator;

    @Before
    public void setUp() {
        schemaGenerator = new SchemaGenerator(pmf);
    }

    @Test
    public void shouldGenerateSchema() {
        when(pmf.getNucleusContext()).thenReturn(nucleusContext);
        when(nucleusContext.getStoreManager()).thenReturn(storeManager);

        schemaGenerator.generateSchema();

        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        verify((SchemaAwareStoreManager) storeManager).createSchemaForClasses(captor.capture(), eq(new Properties()));

        Set set = captor.getValue();
        assertNotNull(set);
        assertEquals(8, set.size());
        assertTrue(set.contains("org.motechproject.test.EntityClass"));
        assertTrue(set.contains("org.motechproject.test.AnotherClass"));
        assertTrue(set.contains("org.motechproject.test.EntityWithoutHistoryClass"));
        assertTrue(set.contains(ClassName.getHistoryClassName("org.motechproject.test.EntityClass")));
        assertTrue(set.contains(ClassName.getHistoryClassName("org.motechproject.test.AnotherClass")));
        assertFalse(set.contains(ClassName.getHistoryClassName("org.motechproject.test.EntityWithoutHistoryClass")));
        assertTrue(set.contains(ClassName.getTrashClassName("org.motechproject.test.EntityClass")));
        assertTrue(set.contains(ClassName.getTrashClassName("org.motechproject.test.AnotherClass")));
        assertTrue(set.contains(ClassName.getTrashClassName("org.motechproject.test.EntityWithoutHistoryClass")));
    }
}
