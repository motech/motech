package org.motechproject.tasks.annotations;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;
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

public class TaskAnnotationBeanPostProcessor implements BeanPostProcessor {
    private BundleContext bundleContext;
    private ChannelService channelService;

    public TaskAnnotationBeanPostProcessor(BundleContext bundleContext, ChannelService channelService) {
        this.bundleContext = bundleContext;
        this.channelService = channelService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        final Class<?> targetClass = getTargetClass(bean);
        final TaskChannel taskChannel = targetClass.getAnnotation(TaskChannel.class);

        if (taskChannel != null) {
            doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {

                @Override
                public void doWith(Method method) throws IllegalAccessException {
                    Method targetMethod = findMethod(targetClass, method.getName(), method.getParameterTypes());

                    if (targetMethod != null) {
                        TaskAction taskAction = targetMethod.getAnnotation(TaskAction.class);

                        if (taskAction != null) {
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

    public void processAnnotations(ApplicationContext applicationContext) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            postProcessAfterInitialization(applicationContext.getBean(beanName), beanName);
        }
    }

    private void addActionTaskEvent(Channel channel, String serviceInterface, Method method, TaskAction taskAction) {
        ActionEvent action = getAction(channel, serviceInterface, method);
        boolean foundAction = action != null;

        if (!foundAction) {
            action = new ActionEvent();
        }

        action.setDisplayName(taskAction.displayName());
        action.setServiceInterface(serviceInterface);
        action.setServiceMethod(method.getName());
        action.setActionParameters(getActionParams(method));

        if (!foundAction) {
            channel.addActionTaskEvent(action);
        }

        channelService.addOrUpdate(channel);
    }

    private Channel getChannel(TaskChannel taskChannel) {
        String displayName = taskChannel.channelName();
        String moduleName = taskChannel.moduleName();
        String moduleVersion = taskChannel.moduleVersion();

        Channel channel = channelService.getChannel(moduleName);

        if (channel == null) {
            channel = new Channel(displayName, moduleName, moduleVersion);
        }

        return channel;
    }

    private ActionEvent getAction(Channel channel, String serviceInterface, Method method) {
        TaskActionInformation info = new TaskActionInformation(channel.getDisplayName(), channel.getDisplayName(),
                channel.getModuleName(), channel.getModuleVersion(), serviceInterface, method.getName());
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
                    TaskActionParam param = (TaskActionParam) annotation;

                    parameters.add(new ActionParameter(param.displayName(), param.key(), param.type(), order));
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
