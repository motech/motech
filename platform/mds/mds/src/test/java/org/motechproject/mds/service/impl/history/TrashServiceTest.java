package org.motechproject.mds.service.impl.history;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.config.TimeUnit;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.repository.internal.AllEntities;
import org.motechproject.mds.service.MdsSchedulerService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.testutil.records.history.Record__Trash;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MDSClassLoader.class, QueryExecutor.class})
public class TrashServiceTest {

    @Mock
    private MdsSchedulerService schedulerService;

    @Mock
    private SettingsService settingsService;

    @Mock
    private AllEntities allEntities;

    @Mock
    private PersistenceManagerFactory factory;

    @Mock
    private PersistenceManager manager;

    @Mock
    private Query query;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    @Mock
    private BundleWiring bundleWiring;

    @Mock
    Entity entity;

    @Captor
    private ArgumentCaptor<Record__Trash> trashCaptor;

    private TrashService trashService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        trashService = new TrashServiceImpl();
        ((TrashServiceImpl) trashService).setSettingsService(settingsService);
        ((TrashServiceImpl) trashService).setMdsSchedulerService(schedulerService);
        ((TrashServiceImpl) trashService).setPersistenceManagerFactory(factory);
        ((TrashServiceImpl) trashService).setBundleContext(bundleContext);

        doReturn(manager).when(factory).getPersistenceManager();
        doReturn(query).when(manager).newQuery(Record.class);
        doReturn(bundle).when(bundleContext).getBundle();
        doReturn(bundleWiring).when(bundle).adapt(BundleWiring.class);
        doReturn(classLoader).when(bundleWiring).getClassLoader();
    }

    @Test
    public void shouldReturnCorrectBoolForTrashMode() {
        doReturn(DeleteMode.DELETE).when(settingsService).getDeleteMode();
        assertFalse(trashService.isTrashMode());

        doReturn(DeleteMode.TRASH).when(settingsService).getDeleteMode();
        assertTrue(trashService.isTrashMode());
    }

    @Test
    public void shouldMoveObjectToTrash() throws Exception {
        doReturn(Record__Trash.class).when(classLoader).loadClass(Record__Trash.class.getName());

        Entity entity = mock(Entity.class);
        Field field = mock(Field.class);
        Type type = mock(Type.class);

        doReturn(17L).when(entity).getEntityVersion();
        doReturn(field).when(entity).getField("id");
        doReturn(field).when(entity).getField("value");

        doReturn(type).when(field).getType();
        doReturn(false).when(type).isRelationship();

        doReturn(entity).when(allEntities).retrieveByClassName(anyString());

        Record instance = new Record();
        trashService.moveToTrash(instance, 1L);

        verify(manager).makePersistent(trashCaptor.capture());

        Record__Trash trash = trashCaptor.getValue();
        assertEquals(instance.getValue(), trash.getValue());
    }

    @Test
    public void shouldFindTrashEntityById() throws Exception {
        doReturn(Record__Trash.class).when(classLoader).loadClass("org.test.history.TestEntity__Trash");
        doReturn(query).when(manager).newQuery(Record__Trash.class);

        Entity entity = mock(Entity.class);
        Field field = mock(Field.class);
        Type type = mock(Type.class);

        doReturn(17L).when(entity).getEntityVersion();
        doReturn("org.test.TestEntity").when(entity).getClassName();
        doReturn(field).when(entity).getField("id");
        doReturn(field).when(entity).getField("value");

        doReturn(type).when(field).getType();
        doReturn(false).when(type).isRelationship();

        doReturn(entity).when(allEntities).retrieveById(1L);
        doReturn(new Record__Trash()).when(query).execute(anyLong());

        Object trash = trashService.findTrashById(1L, "org.test.TestEntity");

        assertNotNull(trash);
    }

    @Test
    public void shouldMoveObjectFromTrash() {
        Record instance = new Record();
        Record__Trash trash = new Record__Trash();
        doReturn(entity).when(allEntities).retrieveByClassName(anyString());
        doReturn(true).when(entity).isRecordHistory();

        trashService.removeFromTrash(trash);

        verify(manager).deletePersistent(trashCaptor.capture());

        Record__Trash captor = trashCaptor.getValue();
        assertEquals(captor.getValue(), trash.getValue());
    }

    @Test
    public void shouldNotScheduleJobIfNotTrashMode() throws Exception {
        doReturn(DeleteMode.DELETE).when(settingsService).getDeleteMode();

        trashService.scheduleEmptyTrashJob();

        verify(schedulerService).unscheduleRepeatingJob();
        verify(schedulerService, never()).scheduleRepeatingJob(anyLong());
    }

    @Test
    public void shouldNotScheduleJobIfEmptyTrashPropertyIsNotSet() throws Exception {
        doReturn(DeleteMode.TRASH).when(settingsService).getDeleteMode();
        doReturn(false).when(settingsService).isEmptyTrash();

        trashService.scheduleEmptyTrashJob();

        verify(schedulerService).unscheduleRepeatingJob();
        verify(schedulerService, never()).scheduleRepeatingJob(anyLong());
    }

    @Test
    public void shouldScheduleJob() throws Exception {
        DateTime start = DateTime.now();

        fakeNow(start);

        try {
            doReturn(DeleteMode.TRASH).when(settingsService).getDeleteMode();
            doReturn(true).when(settingsService).isEmptyTrash();
            doReturn(2).when(settingsService).getTimeValue();
            doReturn(TimeUnit.HOURS).when(settingsService).getTimeUnit();

            trashService.scheduleEmptyTrashJob();

            verify(schedulerService).unscheduleRepeatingJob();
            verify(schedulerService).scheduleRepeatingJob(anyLong());
        } finally {
            stopFakingTime();
        }
    }
}
