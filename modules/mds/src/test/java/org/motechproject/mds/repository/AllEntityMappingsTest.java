package org.motechproject.mds.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.builder.EntityBuilder.PACKAGE;

@RunWith(MockitoJUnitRunner.class)
public class AllEntityMappingsTest {
    private static final String SIMPLE_NAME = "Sample";
    private static final String CLASS_NAME = String.format("%s.%s", PACKAGE, SIMPLE_NAME);

    @Mock
    private PersistenceManagerFactory pmf;

    @Mock
    private PersistenceManager pm;

    @Mock
    private Query query;

    @Captor
    private ArgumentCaptor<EntityMapping> entityMappingArgumentCaptor;

    private AllEntityMappings allEntityMappings;

    @Before
    public void setUp() throws Exception {
        allEntityMappings = new AllEntityMappings();
        allEntityMappings.setPersistenceManagerFactory(pmf);

        when(pmf.getPersistenceManager()).thenReturn(pm);
    }

    @Test
    public void shouldSaveEntity() throws Exception {
        allEntityMappings.save(CLASS_NAME);

        verify(pm).makePersistent(entityMappingArgumentCaptor.capture());

        EntityMapping entityMapping = entityMappingArgumentCaptor.getValue();

        assertNotNull(entityMapping);
        assertEquals(CLASS_NAME, entityMapping.getClassName());
        assertNull(entityMapping.getModule());
        assertNull(entityMapping.getNamespace());
    }

    @Test
    public void shouldFindExistingEntity() throws Exception {
        EntityMapping mapping = new EntityMapping();
        mapping.setClassName(CLASS_NAME);

        List<EntityMapping> mappings = new ArrayList<>();
        mappings.add(mapping);

        when(pm.newQuery(EntityMapping.class)).thenReturn(query);
        when(query.execute(CLASS_NAME)).thenReturn(mappings);

        assertTrue(allEntityMappings.containsEntity(SIMPLE_NAME));

        verify(query).setFilter("className == name");
        verify(query).declareParameters("java.lang.String name");
        verify(query).execute(CLASS_NAME);
    }

    @Test
    public void shouldNotFindExistingEntity() throws Exception {
        when(pm.newQuery(EntityMapping.class)).thenReturn(query);
        when(query.execute(CLASS_NAME)).thenReturn(new ArrayList<EntityMapping>());

        assertFalse(allEntityMappings.containsEntity(SIMPLE_NAME));

        verify(query).setFilter("className == name");
        verify(query).declareParameters("java.lang.String name");
        verify(query).execute(CLASS_NAME);
    }

    @Test
    public void shouldRemoveEntityById() throws Exception {
        EntityMapping mapping = new EntityMapping();
        Long entityId = 1L;

        when(pm.newQuery(EntityMapping.class)).thenReturn(query);
        when(query.execute(entityId)).thenReturn(mapping);

        allEntityMappings.delete(entityId);

        verify(query).setFilter("entityId == id");
        verify(query).declareParameters("java.lang.Long entityId");
        verify(query).execute(entityId);
        verify(pm).deletePersistent(mapping);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotRemoveEntityIfNotExists() throws Exception {
        Long entityId = 1L;

        when(pm.newQuery(EntityMapping.class)).thenReturn(query);
        when(query.execute(entityId)).thenReturn(null);

        allEntityMappings.delete(entityId);
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldNotRemoveEntityIfReadOnly() throws Exception {
        EntityMapping mapping = new EntityMapping();
        mapping.setModule("TestModule");
        Long entityId = 1L;

        when(pm.newQuery(EntityMapping.class)).thenReturn(query);
        when(query.execute(entityId)).thenReturn(mapping);

        allEntityMappings.delete(entityId);
    }
}
