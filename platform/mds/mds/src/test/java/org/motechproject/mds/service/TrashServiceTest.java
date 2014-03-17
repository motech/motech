package org.motechproject.mds.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.MotechEvent;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.SettingsWrapper;
import org.motechproject.mds.config.TimeUnit;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.service.impl.TrashServiceImpl;
import org.motechproject.mds.util.QueryUtil;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.testing.utils.BaseUnitTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.motechproject.mds.util.Constants.Config.EMPTY_TRASH_EVENT;
import static org.motechproject.mds.util.Constants.Config.EMPTY_TRASH_JOB_ID;
import static org.motechproject.scheduler.MotechSchedulerService.JOB_ID_KEY;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MDSClassLoader.class, QueryUtil.class})
public class TrashServiceTest extends BaseUnitTest {

    @Mock
    private MotechSchedulerService schedulerService;

    @Mock
    private SettingsWrapper settingsWrapper;

    @Mock
    private HistoryService historyService;

    @Mock
    private EntityService entityService;

    @Mock
    private PersistenceManagerFactory factory;

    @Mock
    private PersistenceManager manager;

    @Mock
    private Query query;

    @Mock
    private MDSClassLoader classLoader;

    @Captor
    private ArgumentCaptor<RepeatingSchedulableJob> jobCaptor;

    @Captor
    private ArgumentCaptor<Record__Trash> trashCaptor;

    private TrashService trashService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        trashService = new TrashServiceImpl();
        ((TrashServiceImpl) trashService).setEntityService(entityService);
        ((TrashServiceImpl) trashService).setHistoryService(historyService);
        ((TrashServiceImpl) trashService).setSettingsWrapper(settingsWrapper);
        ((TrashServiceImpl) trashService).setSchedulerService(schedulerService);
        ((TrashServiceImpl) trashService).setPersistenceManagerFactory(factory);

        doReturn(manager).when(factory).getPersistenceManager();
        doReturn(query).when(manager).newQuery(Record.class);

        PowerMockito.mockStatic(MDSClassLoader.class);
        PowerMockito.when(MDSClassLoader.getInstance()).thenReturn(classLoader);

        Object[] objects = {Long.valueOf("1")};

        PowerMockito.mockStatic(QueryUtil.class);
        PowerMockito.when(QueryUtil.executeWithArray(query, objects, null)).thenReturn(new Record__Trash());
    }

    @Test
    public void shouldReturnCorrectBoolForTrashMode() {
        doReturn(DeleteMode.DELETE).when(settingsWrapper).getDeleteMode();
        assertFalse(trashService.isTrashMode());

        doReturn(DeleteMode.TRASH).when(settingsWrapper).getDeleteMode();
        assertTrue(trashService.isTrashMode());
    }

    @Test
    public void shouldMoveObjectToTrash() throws Exception {
        doReturn(Record__Trash.class).when(classLoader).loadClass(Record__Trash.class.getName());

        Record instance = new Record();
        trashService.moveToTrash(instance, 1L);

        verify(manager).makePersistent(trashCaptor.capture());

        Record__Trash trash = trashCaptor.getValue();
        assertEquals(instance.getValue(), trash.getValue());
    }

    @Test
    public void shouldFindTrashEntityById() throws Exception {
        doReturn(Record__Trash.class).when(classLoader).loadClass("TestEntity__Trash");
        doReturn(query).when(manager).newQuery(Record__Trash.class);

        EntityDto entity = new EntityDto();
        entity.setClassName("TestEntity");

        doReturn(entity).when(entityService).getEntity(Long.valueOf("1"));

        Object trash = trashService.findTrashById(Long.valueOf("1"), Long.valueOf("1"));

        assertNotNull(trash);
    }

    @Test
    public void shouldMoveObjectFromTrash() {
        Record instance = new Record();
        Record__Trash trash = new Record__Trash();
        trashService.moveFromTrash(instance, trash);

        verify(manager).deletePersistent(trashCaptor.capture());

        Record__Trash captor = trashCaptor.getValue();
        assertEquals(captor.getValue(), trash.getValue());
    }

    @Test
    public void shouldNotScheduleJobIfNotTrashMode() throws Exception {
        doReturn(DeleteMode.DELETE).when(settingsWrapper).getDeleteMode();

        trashService.scheduleEmptyTrashEvent(null);

        verify(schedulerService).safeUnscheduleRepeatingJob(EMPTY_TRASH_EVENT, EMPTY_TRASH_JOB_ID);
        verify(schedulerService, never()).scheduleRepeatingJob(any(RepeatingSchedulableJob.class));
    }

    @Test
    public void shouldNotScheduleJobIfEmptyTrashPropertyIsNotSet() throws Exception {
        doReturn(DeleteMode.TRASH).when(settingsWrapper).getDeleteMode();
        doReturn(false).when(settingsWrapper).isEmptyTrash();

        trashService.scheduleEmptyTrashEvent(null);

        verify(schedulerService).safeUnscheduleRepeatingJob(EMPTY_TRASH_EVENT, EMPTY_TRASH_JOB_ID);
        verify(schedulerService, never()).scheduleRepeatingJob(any(RepeatingSchedulableJob.class));
    }

    @Test
    public void shouldScheduleJob() throws Exception {
        DateTime start = DateTime.now();

        mockCurrentDate(start);

        doReturn(DeleteMode.TRASH).when(settingsWrapper).getDeleteMode();
        doReturn(true).when(settingsWrapper).isEmptyTrash();
        doReturn(2).when(settingsWrapper).getTimeValue();
        doReturn(TimeUnit.HOURS).when(settingsWrapper).getTimeUnit();

        trashService.scheduleEmptyTrashEvent(null);

        verify(schedulerService).safeUnscheduleRepeatingJob(EMPTY_TRASH_EVENT, EMPTY_TRASH_JOB_ID);
        verify(schedulerService).scheduleRepeatingJob(jobCaptor.capture());

        RepeatingSchedulableJob job = jobCaptor.getValue();
        assertNull(job.getEndTime());
        assertNull(job.getRepeatCount());
        assertEquals(start.toDate(), job.getStartTime());
        assertEquals(7200000L, job.getRepeatIntervalInMilliSeconds().longValue());

        MotechEvent event = job.getMotechEvent();
        assertEquals(EMPTY_TRASH_EVENT, event.getSubject());
        assertEquals(EMPTY_TRASH_JOB_ID, event.getParameters().get(JOB_ID_KEY));
    }

    public static final class Record {
        private Long id = 1L;
        private String value = "value";

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static final class Record__Trash {
        private Long id = 1L;
        private String value = "value";

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
