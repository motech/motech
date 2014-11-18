package org.motechproject.mds.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;

import javax.jdo.listener.InstanceLifecycleEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TrashListenerTest extends BaseListenerTest {

    private TrashListener trashListener;

    @Mock
    private TrashService trashService;

    @Mock
    private HistoryService historyService;


    @Mock
    private InstanceLifecycleEvent lifecycleEvent;

    private Object instance = new Object();

    @Mock
    private MotechDataService<Object> dataService;

    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        when(getApplicationContext().getBean(HistoryService.class)).thenReturn(historyService);
        when(getApplicationContext().getBean(TrashService.class)).thenReturn(trashService);
        when(lifecycleEvent.getSource()).thenReturn(instance);

        final String beanName = Object.class.getName() + "DataService";
        when(getApplicationContext().containsBean(beanName)).thenReturn(true);
        when(getApplicationContext().getBean(beanName)).thenReturn(dataService);
        when(dataService.getSchemaVersion()).thenReturn(2L);
        when(dataService.recordHistory()).thenReturn(true);

        trashListener = setUpListener(TrashListener.class);
    }

    @Test
    public void shouldNotDoAnythingPostDelete() {
        trashListener.postDelete(lifecycleEvent);
        verifyNoMoreInteractions(trashService, historyService, dataService);
    }

    @Test
    public void shouldMoveObjectsToTrashPreDelete() {
        when(trashService.isTrashMode()).thenReturn(true);

        trashListener.preDelete(lifecycleEvent);

        verify(trashService).moveToTrash(instance, 2L, true);
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void shouldDeleteObjectsHistoryIfInTrashInactivePreDelete() {
        when(trashService.isTrashMode()).thenReturn(false);

        trashListener.preDelete(lifecycleEvent);

        verify(historyService).remove(instance);
        verify(trashService).isTrashMode();
        verifyNoMoreInteractions(trashService);
    }
}
