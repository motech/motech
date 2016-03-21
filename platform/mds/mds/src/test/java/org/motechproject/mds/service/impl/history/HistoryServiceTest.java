package org.motechproject.mds.service.impl.history;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.repository.internal.AllEntities;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.testutil.records.history.Record__History;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MDSClassLoader.class, TransactionSynchronizationManager.class})
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

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MotechDataService dataService;

    @Captor
    private ArgumentCaptor<Record__History> recordHistoryCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @InjectMocks
    private HistoryService historyService = new HistoryServiceImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        doReturn(Record__History.class).when(classLoader).loadClass(Record__History.class.getName());
        doReturn(reference).when(bundleContext).getServiceReference(anyString());

        doReturn(manager).when(factory).getPersistenceManager();
        doReturn(query).when(manager).newQuery(Record__History.class);
        doReturn(bundle).when(bundleContext).getBundle();
        doReturn(bundleWiring).when(bundle).adapt(BundleWiring.class);
        doReturn(classLoader).when(bundleWiring).getClassLoader();

        PowerMockito.mockStatic(TransactionSynchronizationManager.class);
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
        doReturn(Long.class).when(idType).getTypeClass();

        Field valueField = mock(Field.class);
        doReturn("value").when(valueField).getName();

        Type valueType = mock(Type.class);
        doReturn(valueType).when(valueField).getType();
        doReturn(String.class.getName()).when(valueType).getTypeClassName();
        doReturn(String.class).when(valueType).getTypeClass();

        Field dateField = mock(Field.class);
        doReturn("date").when(dateField).getName();

        Type dateType = mock(Type.class);
        doReturn(dateType).when(dateField).getType();
        doReturn(Date.class.getName()).when(dateType).getTypeClassName();
        doReturn(Date.class).when(dateType).getTypeClass();

        doReturn(17L).when(entity).getEntityVersion();
        doReturn(true).when(entity).isRecordHistory();
        doReturn(Arrays.asList(idField, valueField, dateField)).when(entity).getFields();

        doReturn(null).when(query).execute(anyLong());

        final String serviceName = ClassName.getServiceName(Record.class.getName());
        doReturn(true).when(applicationContext).containsBean(serviceName);
        doReturn(dataService).when(applicationContext).getBean(serviceName);
        doReturn(4L).when(dataService).getSchemaVersion();

        Record instance = new Record();
        historyService.record(instance);

        verify(manager).makePersistent(recordHistoryCaptor.capture());

        Record__History history = recordHistoryCaptor.getValue();

        assertEquals(instance.getId(), history.getRecord__HistoryCurrentVersion());
        assertEquals(Long.valueOf(4), history.getRecord__HistorySchemaVersion());
        assertEquals(instance.getValue(), history.getValue());
        assertEquals(instance.getDate(), history.getDate());
    }
}
