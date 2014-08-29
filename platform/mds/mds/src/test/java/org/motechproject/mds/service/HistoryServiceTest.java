package org.motechproject.mds.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.impl.history.BasePersistenceService;
import org.motechproject.mds.service.impl.history.HistoryServiceImpl;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.testutil.records.history.Record__History;
import org.motechproject.mds.testutil.records.history.Record__Trash;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MDSClassLoader.class)
public class HistoryServiceTest {

    @Mock
    private AllEntities allEntities;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private Bundle bundle;

    @Mock
    private BundleWiring bundleWiring;

    @Mock
    private ServiceReference reference;

    @Mock
    private PersistenceManagerFactory factory;

    @Mock
    private PersistenceManager manager;

    @Mock
    private Query query;

    @Captor
    private ArgumentCaptor<Record__History> recordHistoryCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    private HistoryService historyService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        historyService = new HistoryServiceImpl();
        ((HistoryServiceImpl) historyService).setPersistenceManagerFactory(factory);
        ((HistoryServiceImpl) historyService).setBundleContext(bundleContext);
        ((BasePersistenceService) historyService).setAllEntities(allEntities);

        doReturn(Record__History.class).when(classLoader).loadClass(Record__History.class.getName());
        doReturn(reference).when(bundleContext).getServiceReference(anyString());

        doReturn(manager).when(factory).getPersistenceManager();
        doReturn(query).when(manager).newQuery(Record__History.class);
        doReturn(bundle).when(bundleContext).getBundle();
        doReturn(bundleWiring).when(bundle).adapt(BundleWiring.class);
        doReturn(classLoader).when(bundleWiring).getClassLoader();
    }

    @Test
    public void shouldNotRecordIfClassNotFound() throws Exception {
        doReturn(null).when(classLoader).loadClass(anyString());

        historyService.record(null);
        verifyZeroInteractions(manager);

        historyService.record(new Object());
        verifyZeroInteractions(manager);
    }

    @Test
    public void shouldCreateNewRecord() throws Exception {
        Entity entity = mock(Entity.class);

        Field idField = mock(Field.class);
        doReturn("id").when(idField).getName();

        Type idType = mock(Type.class);
        doReturn(idType).when(idField).getType();
        doReturn(Long.class.getName()).when(idType).getTypeClassName();

        Field valueField = mock(Field.class);
        doReturn("value").when(valueField).getName();

        Type valueType = mock(Type.class);
        doReturn(valueType).when(valueField).getType();
        doReturn(String.class.getName()).when(valueType).getTypeClassName();

        Field dateField = mock(Field.class);
        doReturn("date").when(dateField).getName();

        Type dateType = mock(Type.class);
        doReturn(dateType).when(dateField).getType();
        doReturn(Date.class.getName()).when(dateType).getTypeClassName();

        doReturn(17L).when(entity).getEntityVersion();
        doReturn(Arrays.asList(idField, valueField, dateField)).when(entity).getFields();

        doReturn(null).when(query).execute(anyLong());
        doReturn(entity).when(allEntities).retrieveByClassName(anyString());

        Record instance = new Record();
        historyService.record(instance);

        verify(manager).makePersistent(recordHistoryCaptor.capture());

        Record__History history = recordHistoryCaptor.getValue();

        assertEquals(instance.getId(), history.getRecord__HistoryCurrentVersion());
        assertEquals(instance.getValue(), history.getValue());
        assertEquals(instance.getDate(), history.getDate());
    }

    @Test
    public void shouldNotRemoveIfClassNotFound() throws Exception {
        doReturn(null).when(classLoader).loadClass(anyString());

        historyService.remove(null);
        verifyZeroInteractions(manager);

        historyService.remove(new Object());
        verifyZeroInteractions(manager);
    }

    @Test
    public void shouldRemoveAllHistoryRecordsRelatedWithGivenInstance() throws Exception {
        historyService.remove(new Record());

        verify(factory).getPersistenceManager();
        verify(manager).newQuery(Record__History.class);
        verify(query).setFilter(stringCaptor.capture());
        verify(query).declareParameters(stringCaptor.capture());
        verify(query).deletePersistentAll(1L, false);

        List<String> values = stringCaptor.getAllValues();

        assertThat(values, hasItem("record__HistoryCurrentVersion == param0 && record__HistoryFromTrash == param1"));
        assertThat(values, hasItem("java.lang.Long param0, java.lang.Boolean param1"));
    }

    @Test
    public void shouldNotSetTrashFlagIfClassNotFound() throws Exception {
        doReturn(null).when(classLoader).loadClass(anyString());

        historyService.setTrashFlag(null, null, true);
        verifyZeroInteractions(manager);

        historyService.setTrashFlag(new Object(), null, true);
        verifyZeroInteractions(manager);
    }

    @Test
    public void shouldSetTrashFlag() throws Exception {
        Record__History value = new Record__History(1L, "value");
        assertFalse(value.getRecord__HistoryFromTrash());

        List<Record__History> collection = new ArrayList<>();
        collection.add(value);

        doReturn(collection).when(query).execute(1L, false);

        Record__Trash trash = new Record__Trash();
        historyService.setTrashFlag(new Record(), trash, true);

        assertTrue(value.getRecord__HistoryFromTrash());
        assertEquals(trash.getId(), value.getRecord__HistoryCurrentVersion());
        verify(manager).makePersistentAll(collection);
    }

}
