package org.motechproject.security.annotations;

import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.service.MotechPermissionService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.remove;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * A {@link BeanPostProcessor} used by Motech to load permissions from modules. Given a module context, it looks for
 * {@link PreAuthorize} and {@link PostAuthorize} annotations. These annotations are then parsed using an
 * {@link ExpressionParser}. The permission names are deduced from {@code hasRole} and {@code hasAnyRole} in the
 * annotation value. The names of permissions are then saved using the {@link MotechPermissionService}. The bundle
 * name used to construct the permission is retrieved from the application context.
 */
public class SecurityAnnotationBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAnnotationBeanPostProcessor.class);

    private MotechPermissionService permissionService;

    private ExpressionParser annotationParser = new DefaultMethodSecurityExpressionHandler().getExpressionParser();

    private String currentBundleName = "";

    public SecurityAnnotationBeanPostProcessor(MotechPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void processAnnotations(ApplicationContext applicationContext) {
        LOGGER.info("Searching for security annotations in: {}", applicationContext.getDisplayName());
        currentBundleName = getBundleName(applicationContext);

        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            postProcessAfterInitialization(bean, beanName);
        }

        LOGGER.info("Searched for security annotations in: {}", applicationContext.getDisplayName());
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        LOGGER.info("Searching for security annotations in: {}", beanName);

        doWithMethods(bean.getClass(), new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalAccessException {
                Method methodOfOriginalClassIfProxied = findMethod(getTargetClass(bean), method.getName(), method.getParameterTypes());

                if (methodOfOriginalClassIfProxied != null) {
                    PreAuthorize preAuthorize = findAnnotation(methodOfOriginalClassIfProxied, PreAuthorize.class);
                    PostAuthorize postAuthorize = findAnnotation(methodOfOriginalClassIfProxied, PostAuthorize.class);

                    List<String> annotations = new ArrayList<>(2);
                    List<String> permissions = new ArrayList<>();

                    if (preAuthorize != null) {
                        annotations.add(preAuthorize.value());
                    }

                    if (postAuthorize != null) {
                        annotations.add(postAuthorize.value());
                    }

                    for (String annotation : annotations) {
                        SpelExpression expression = (SpelExpression) annotationParser.parseExpression(annotation);
                        permissions.addAll(findPermissions(expression.getAST()));
                    }

                    addRoleAndPermissions(permissions);
                }
            }
        });

        LOGGER.info("Searched for security annotations in: {}", beanName);

        return bean;
    }

    private List<String> findPermissions(SpelNode node) {
        List<String> list = new ArrayList<>(node.getChildCount());

        if (startsWithIgnoreCase(node.toStringAST(), "hasRole") || startsWithIgnoreCase(node.toStringAST(), "hasAnyRole")) {
            for (int i = 0; i < node.getChildCount(); ++i) {
                list.add(remove(node.getChild(i).toStringAST(), '\''));
            }
        } else {
            for (int i = 0; i < node.getChildCount(); ++i) {
                list.addAll(findPermissions(node.getChild(i)));
            }
        }

        LOGGER.debug("Found permissions: {}", list);

        return list;
    }

    private void addRoleAndPermissions(List<String> permissions) {
        if (!permissions.isEmpty() && permissionService != null) {
            for (String permission : permissions) {
                permissionService.addPermission(new PermissionDto(permission, currentBundleName));
            }
        }
    }

    private String getBundleName(ApplicationContext applicationContext) {
        BundleContext bundleContext = applicationContext.getBean(BundleContext.class);
        return (bundleContext == null) ? "" : bundleContext.getBundle().getSymbolicName();
    }
}
