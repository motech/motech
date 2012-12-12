package org.motechproject.security.annotations;

import org.apache.commons.lang.WordUtils;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
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
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.findMethod;

public class SecurityAnnotationBeanPostProcessor implements BeanPostProcessor {
    private MotechPermissionService permissionService;
    private MotechRoleService roleService;
    private String moduleName = "unknown";
    private ExpressionParser annotationParser = new DefaultMethodSecurityExpressionHandler().getExpressionParser();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        doWithMethods(bean.getClass(), new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
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

                    if (!permissions.isEmpty() && permissionService != null) {
                        for (String permission : permissions) {
                            permissionService.addPermission(new PermissionDto(permission, moduleName));
                        }

                        if (roleService != null) {
                            String role = WordUtils.capitalize(String.format("%s bundle", moduleName));
                            RoleDto dto = roleService.getRole(role);

                            if (dto == null) {
                                dto = new RoleDto(role, permissions);

                                roleService.createRole(dto);
                            } else {
                                for (String permission : permissions) {
                                    if (!dto.getPermissionNames().contains(permission)) {
                                        dto.getPermissionNames().add(permission);
                                    }
                                }

                                roleService.updateRole(dto);
                            }
                        }
                    }
                }
            }
        });

        return bean;
    }

    @Autowired(required = false)
    public void setPermissionService(MotechPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired(required = false)
    public void setRoleService(MotechRoleService roleService) {
        this.roleService = roleService;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

        return list;
    }
}