package org.motechproject.mds.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;

import javax.jdo.listener.InstanceLifecycleEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HistoryListenerTest extends BaseListenerTest {

    private HistoryListener historyListener;

    @Mock
    private HistoryService historyService;

    @Mock
    private InstanceLifecycleEvent lifecycleEvent;

    @Mock
    private MotechDataService dataService;

    private Object instance = new Object();

    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        when(getApplicationContext().getBean(HistoryService.class)).thenReturn(historyService);
        when(lifecycleEvent.getSource()).thenReturn(instance);

        final String beanName = Object.class.getName() + "DataService";
        when(getApplicationContext().containsBean(beanName)).thenReturn(true);
        when(getApplicationContext().getBean(beanName)).thenReturn(dataService);
        when(dataService.recordHistory()).thenReturn(true);

        historyListener = setUpListener(HistoryListener.class);
    }

    @Test
    public void shouldCreateHistoryForEntitiesPostStore() {
        historyListener.postStore(lifecycleEvent);
        verify(historyService).record(instance);
    }

    @Test
    public void shouldNotCreateHistoryForEntitiesPreStore() {
        historyListener.preStore(lifecycleEvent);
        verify(historyService, never()).record(any());
    }
}
