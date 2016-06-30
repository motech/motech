package org.motechproject.tasks.annotations.processor;

import org.motechproject.tasks.annotations.TaskAction;
import org.motechproject.tasks.annotations.TaskActionParam;
import org.motechproject.tasks.annotations.TaskChannel;
import org.motechproject.tasks.annotations.TaskPostActionParam;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.domain.mds.channel.builder.ActionParameterBuilder;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * Factory class which is looking for classes with {@link TaskChannel} annotation to add them to
 * the channel definition as channel action.
 *
 * @see TaskAction
 * @see TaskActionParam
 * @see TaskChannel
 * @since 0.19
 */
public class TaskAnnotationBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAnnotationBeanPostProcessor.class);

    private BundleContext bundleContext;
    private ChannelService channelService;

    /**
     * Class constructor.
     *
     * @param bundleContext  the bundle context, not null
     * @param channelService  the channel service, not null
     */
    public TaskAnnotationBeanPostProcessor(BundleContext bundleContext,
                                           ChannelService channelService) {
        this.bundleContext = bundleContext;
        this.channelService = channelService;
    }

    /**
     * Searches through the given application context and processes annotations used by task module.
     *
     * @param applicationContext  the context of the application, not null
     */
    public void processAnnotations(ApplicationContext applicationContext) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            postProcessAfterInitialization(applicationContext.getBean(beanName), beanName);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (bean == null) {
            return null;
        }

        final Class<?> targetClass = getTargetClass(bean);
        final TaskChannel taskChannel = targetClass.getAnnotation(TaskChannel.class);

        if (taskChannel != null) {
            LOGGER.debug("The @TaskChannel annotation was found in {}", targetClass.getName());
            doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {

                @Override
                public void doWith(Method method) throws IllegalAccessException {
                    Method targetMethod = findMethod(
                            targetClass, method.getName(), method.getParameterTypes()
                    );

                    if (targetMethod != null) {
                        TaskAction taskAction = targetMethod.getAnnotation(TaskAction.class);

                        if (taskAction != null) {
                            LOGGER.debug("The @TaskAction annotation was found in method: {}", targetMethod.getName());
                            String serviceInterface = getServiceInterface(targetClass);
                            Channel channel = getChannel(taskChannel);

                            addActionTaskEvent(channel, serviceInterface, targetMethod, taskAction);
                        }
                    }
                }
            });
        }

        return bean;
    }

    private void addActionTaskEvent(Channel channel, String serviceInterface, Method method,
                                    TaskAction taskAction) {
        ActionEvent action = getAction(channel, serviceInterface, method, taskAction.displayName());
        boolean foundAction = action != null;

        if (!foundAction) {
            action = new ActionEventBuilder().build();
        }

        action.setDisplayName(taskAction.displayName());
        action.setServiceInterface(serviceInterface);
        action.setServiceMethod(method.getName());
        action.setActionParameters(getActionParams(method));
        action.setPostActionParameters(getPostActionParams(method));

        if (!foundAction) {
            channel.addActionTaskEvent(action);
            LOGGER.debug("Action task event: {} added to channel: {}", action.getName(), channel.getDisplayName());
        }

        channelService.addOrUpdate(channel);
    }

    private Channel getChannel(TaskChannel taskChannel) {
        String displayName = taskChannel.channelName();
        String moduleName = taskChannel.moduleName();
        String moduleVersion = taskChannel.moduleVersion();

        Channel channel = channelService.getChannel(moduleName);

        if (channel == null) {
            LOGGER.debug("Creating new channel: {}  for module: {}", displayName, moduleName);
            channel = new Channel(displayName, moduleName, moduleVersion);
        } else {
            LOGGER.debug("Channel: {}  for module: {} was retrieved", displayName, moduleName);
        }

        return channel;
    }

    private ActionEvent getAction(Channel channel, String serviceInterface, Method method, String actionName) {
        TaskActionInformation info = new TaskActionInformation(method.getName(), actionName,
                channel.getDisplayName(), channel.getModuleName(), channel.getModuleVersion(),
                serviceInterface, method.getName());
        ActionEvent actionEvent = null;

        for (ActionEvent action : channel.getActionTaskEvents()) {
            if (action.accept(info)) {
                actionEvent = action;
                break;
            }
        }

        return actionEvent;
    }

    private SortedSet<ActionParameter> getActionParams(Method method) {
        SortedSet<ActionParameter> parameters = new TreeSet<>();
        int order = 0;

        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof TaskActionParam) {
                    LOGGER.debug("The @TaskActionParam annotation was found in parameters from method: {}", method.getName());
                    TaskActionParam param = (TaskActionParam) annotation;
                    LOGGER.debug("Task action parameter: {} added", param.displayName());

                    parameters.add(new ActionParameterBuilder().setDisplayName(param.displayName()).setKey(param.key())
                            .setType(param.type()).setOrder(order).setRequired(param.required()).build());
                    ++order;
                }
            }
        }

        return parameters.isEmpty() ? null : parameters;
    }

    private SortedSet<ActionParameter> getPostActionParams(Method method) {
        SortedSet<ActionParameter> parameters = new TreeSet<>();
        int order = 0;

        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof TaskPostActionParam) {
                    LOGGER.debug("The @TaskPostActionParam annotation was found in parameters from method: {}", method.getName());
                    TaskPostActionParam param = (TaskPostActionParam) annotation;
                    LOGGER.debug("Task action parameter: {} added", param.displayName());
                    parameters.add(new ActionParameterBuilder().setDisplayName(param.displayName()).setKey(param.key())
                            .setType(param.type()).setOrder(order).setRequired(param.required()).build());
                    ++order;
                }
            }
        }

        return parameters.isEmpty() ? null : parameters;
    }

    private String getServiceInterface(Class<?> targetClass) {
        Class<?> targetInterface = null;

        for (Class<?> inter : targetClass.getInterfaces()) {
            if (bundleContext.getServiceReference(inter.getName()) != null) {
                targetInterface = inter;
                break;
            }
        }

        return targetInterface == null ? null : targetInterface.getName();
    }
}
