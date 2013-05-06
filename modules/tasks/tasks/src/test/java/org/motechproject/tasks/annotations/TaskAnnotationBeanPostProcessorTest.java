package org.motechproject.tasks.annotations;

import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnnotationUtils.class)
public class TaskAnnotationBeanPostProcessorTest {
    private static final String CHANNEL_NAME = "channelName";
    private static final String MODULE_NAME = "moduleName";
    private static final String MODULE_VERSION = "0.19";

    private static final String ACTION_DISPLAY_NAME = "taskAction";

    private static final String EXTERNAL_KEY = "externalId";
    private static final String EXTERNAL_DISPLAY_NAME = "ExternalId";

    private static final String MOTECH_KEY = "motechId";
    private static final String MOTECH_DISPLAY_NAME = "MotechId";

    private static final String METHOD_NAME = "action";

    interface TestAction {
        void action(String externalId, Integer motechId, String message);
    }

    @Controller
    @TaskChannel(channelName = CHANNEL_NAME, moduleName = MODULE_NAME, moduleVersion = MODULE_VERSION)
    class TestActionWithParam implements TestAction {

        @RequestMapping
        @TaskAction(displayName = ACTION_DISPLAY_NAME)
        @Override
        public void action(@TaskActionParam(key = EXTERNAL_KEY, displayName = EXTERNAL_DISPLAY_NAME) @PathVariable String externalId,
                           @TaskActionParam(key = MOTECH_KEY, displayName = MOTECH_DISPLAY_NAME, type = ParameterType.INTEGER) @PathVariable Integer motechId,
                           String message) {

        }

    }

    @Controller
    @TaskChannel(channelName = CHANNEL_NAME, moduleName = MODULE_NAME, moduleVersion = MODULE_VERSION)
    class TestActionWithoutParam implements Serializable, TestAction {
        private static final long serialVersionUID = 957560471491276865L;

        @RequestMapping
        @TaskAction(displayName = ACTION_DISPLAY_NAME)
        @Override
        public void action(@PathVariable String externalId, Integer motechId, String message) {

        }

    }

    @Mock
    private ApplicationContext context;

    @Mock
    private TaskChannel taskChannel;

    @Mock
    private TaskAction taskAction;

    @Mock
    private ChannelService channelService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference serviceReference;

    private TaskAnnotationBeanPostProcessor processor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(bundleContext.getServiceReference(TestAction.class.getName())).thenReturn(serviceReference);

        processor = new TaskAnnotationBeanPostProcessor(bundleContext, channelService);
    }

    @Test
    public void shouldNotRegisterChannelWhenClassNotContainsTaskChannelAnnotation() {
        processor.postProcessAfterInitialization(new Object(), null);

        verify(channelService, never()).addOrUpdate(any(Channel.class));
    }

    @Test
    public void shouldNotRegisterChannelWhenMethodNotContainsTaskActionAnnotation() throws Exception {
        PowerMockito.mockStatic(AnnotationUtils.class);
        PowerMockito.when(AnnotationUtils.findAnnotation(Object.class, TaskChannel.class)).thenReturn(taskChannel);

        processor.postProcessAfterInitialization(new Object(), null);

        verify(channelService, never()).addOrUpdate(any(Channel.class));
    }

    @Test
    public void shouldAddActionWithoutParams() throws Exception {
        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);

        processor.postProcessAfterInitialization(new TestActionWithoutParam(), null);

        verify(channelService).addOrUpdate(captor.capture());

        assertChannel(captor.getValue());
    }

    @Test
    public void shouldAddActionWithParams() throws Exception {
        Channel channel = new Channel(CHANNEL_NAME, MODULE_NAME, MODULE_VERSION);
        channel.addActionTaskEvent(new ActionEvent(ACTION_DISPLAY_NAME, ACTION_DISPLAY_NAME, "", null));
        channel.addActionTaskEvent(new ActionEvent(ACTION_DISPLAY_NAME, "", TestAction.class.getName(), "action", null));

        when(channelService.getChannel(MODULE_NAME)).thenReturn(channel);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);

        processor.postProcessAfterInitialization(new TestActionWithParam(), null);

        verify(channelService).addOrUpdate(captor.capture());

        Channel actualChannel = captor.getValue();

        assertNotNull(actualChannel);
        assertEquals(CHANNEL_NAME, actualChannel.getDisplayName());
        assertEquals(MODULE_NAME, actualChannel.getModuleName());
        assertEquals(MODULE_VERSION, actualChannel.getModuleVersion());

        assertNotNull(actualChannel.getActionTaskEvents());
        assertEquals(channel.getActionTaskEvents().size(), actualChannel.getActionTaskEvents().size());

        ActionEvent actualActionEvent = (ActionEvent) find(actualChannel.getActionTaskEvents(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof ActionEvent && ((ActionEvent) object).hasService();
            }
        });

        assertNotNull(actualActionEvent);

        assertEquals(ACTION_DISPLAY_NAME, actualActionEvent.getDisplayName());
        assertEquals(TestAction.class.getName(), actualActionEvent.getServiceInterface());
        assertEquals(METHOD_NAME, actualActionEvent.getServiceMethod());

        assertEquals(getExpectedActionParameters(), actualActionEvent.getActionParameters());
    }

    @Test
    public void shouldNotRegisterSameActionTwice() {
        Channel channel = new Channel(CHANNEL_NAME, MODULE_NAME, MODULE_VERSION);

        when(channelService.getChannel(MODULE_NAME)).thenReturn(channel);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        processor.postProcessAfterInitialization(new TestActionWithoutParam(), null);
        processor.postProcessAfterInitialization(new TestActionWithoutParam(), null);

        verify(channelService, times(2)).addOrUpdate(captor.capture());

        assertChannel(captor.getValue());
    }

    @Test
    public void shouldNotProcessWhenContextNotContainsBean() {
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{});

        processor.processAnnotations(context);

        verify(channelService, never()).addOrUpdate(any(Channel.class));
    }

    @Test
    public void shouldProcessForAllBeansInContext() {
        String beanName = "testActionWithoutParam";
        TestActionWithoutParam bean = new TestActionWithoutParam();

        when(context.getBeanDefinitionNames()).thenReturn(new String[]{beanName});
        when(context.getBean(beanName)).thenReturn(bean);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        processor.processAnnotations(context);

        verify(channelService).addOrUpdate(captor.capture());

        assertChannel(captor.getValue());
    }

    private SortedSet<ActionParameter> getExpectedActionParameters() {
        SortedSet<ActionParameter> set = new TreeSet<>();
        set.add(new ActionParameter(EXTERNAL_DISPLAY_NAME, EXTERNAL_KEY, 0));
        set.add(new ActionParameter(MOTECH_DISPLAY_NAME, MOTECH_KEY, ParameterType.INTEGER, 1));

        return set;
    }

    private void assertChannel(Channel actualChannel) {
        assertNotNull(actualChannel);
        assertEquals(CHANNEL_NAME, actualChannel.getDisplayName());
        assertEquals(MODULE_NAME, actualChannel.getModuleName());
        assertEquals(MODULE_VERSION, actualChannel.getModuleVersion());

        assertNotNull(actualChannel.getActionTaskEvents());
        assertEquals(1, actualChannel.getActionTaskEvents().size());

        ActionEvent actualActionEvent = actualChannel.getActionTaskEvents().get(0);

        assertEquals(ACTION_DISPLAY_NAME, actualActionEvent.getDisplayName());
        assertEquals(TestAction.class.getName(), actualActionEvent.getServiceInterface());
        assertEquals(METHOD_NAME, actualActionEvent.getServiceMethod());

        assertEquals(0, actualActionEvent.getActionParameters().size());
    }

}
