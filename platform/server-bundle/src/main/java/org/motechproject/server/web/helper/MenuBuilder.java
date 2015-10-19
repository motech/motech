package org.motechproject.server.web.helper;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.SubmenuInfo;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.dto.ModuleMenu;
import org.motechproject.server.web.dto.ModuleMenuLink;
import org.motechproject.server.web.dto.ModuleMenuSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Helper class for building the modules menu view(left-hand side nav). Modules to display are retrieved
 * from the {@link UIFrameworkService}. Filters entries based on user permissions.
 *
 * @see UIFrameworkService
 */
@Component
public class MenuBuilder {

    private static final List<String> REST_API_PERMISSION = Collections.singletonList("viewRestApi");

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @Autowired
    private MotechUserService userService;

    @Autowired
    private MotechRoleService roleService;

    /**
     * Builds the menu for the given user. Modules are retrieved from {@code UIFrameworkService} and filtered
     * based on permissions.
     *
     * @param username username of the user for which the menu should be built.
     * @return the built menu object.
     */
    public ModuleMenu buildMenu(String username) {
        ModuleMenu moduleMenu = new ModuleMenu();

        List<String> userRoles = userService.getRoles(username);

        for (ModuleRegistrationData moduleRegistrationData : getModulesWithSubMenu(userRoles)) {
            ModuleMenuSection menuSection = getModuleMenuSection(username, userRoles, moduleRegistrationData);
            if (!menuSection.getLinks().isEmpty()) {
                moduleMenu.addMenuSection(menuSection);
            }
        }

        moduleMenu.addMenuSection(serverModulesMenuSection(userRoles));

        // we add a separate API on the top for REST API documentation

        if (checkUserPermission(userRoles, REST_API_PERMISSION)) {
            ModuleMenuSection restSection = restDocumentationMenu();
            if (CollectionUtils.isNotEmpty(restSection.getLinks())) {
                moduleMenu.addMenuSection(restSection);
            }
        }

        return moduleMenu;
    }

    private ModuleMenuSection restDocumentationMenu() {
        ModuleMenuSection section = new ModuleMenuSection("server.rest.documentation", false);
        // each module registering rest docs gets a rest documentation link in the REST API menu
        for (String moduleName : uiFrameworkService.getRestDocLinks().keySet()) {
            section.addLink(new ModuleMenuLink(moduleName, "rest-docs", "/rest-docs/" + moduleName, false, null));
        }
        return section;
    }

    private ModuleMenuSection getModuleMenuSection(String username, List<String> userRoles,
                                                   ModuleRegistrationData moduleRegistrationData) {
        String moduleName = moduleRegistrationData.getModuleName();
        ModuleMenuSection menuSection = new ModuleMenuSection(moduleName, moduleRegistrationData.isNeedsAttention());

        menuSection.setModuleDocsUrl(moduleRegistrationData.getDocumentationUrl());
        for (Map.Entry<String, SubmenuInfo> submenuEntry : moduleRegistrationData.getSubMenu().entrySet()) {
            SubmenuInfo submenuInfo = submenuEntry.getValue();

            if (isSubMenuLinkAccessibleByCurrentUser(username, userRoles, submenuInfo)) {
                String name = submenuEntry.getKey();
                String angularName = getAngularModuleName(moduleRegistrationData);

                ModuleMenuLink link = new ModuleMenuLink(name, angularName, submenuInfo.getUrl(),
                        submenuInfo.isNeedsAttention(), null);

                menuSection.addLink(link);
            }

        }
        return menuSection;
    }

    private ModuleMenuSection serverModulesMenuSection(List<String> userRoles) {
        ModuleMenuSection modulesSection = new ModuleMenuSection("server.modules", false);

        for (ModuleRegistrationData moduleRegistrationData : getModulesWithoutSubMenu(userRoles)) {
            String name = moduleRegistrationData.getModuleName();
            String angularName = getAngularModuleName(moduleRegistrationData);
            boolean needsAttention = moduleRegistrationData.isNeedsAttention();

            // these menu items don't make use of urls, the name is sufficient
            ModuleMenuLink link = new ModuleMenuLink(name, angularName, moduleRegistrationData.determineDefaultTab(), needsAttention,
                    moduleRegistrationData.getDocumentationUrl());

            modulesSection.addLink(link);
        }

        return modulesSection;
    }

    private List<ModuleRegistrationData> getModulesWithSubMenu(List<String> userRoles) {
        return filterPermittedModules(
                userRoles,
                uiFrameworkService.getRegisteredModules().getModulesWithSubMenu()
        );
    }

    private List<ModuleRegistrationData> getModulesWithoutSubMenu(List<String> userRoles) {
        return filterPermittedModules(
                userRoles,
                uiFrameworkService.getRegisteredModules().getModulesWithoutSubmenu()
        );
    }

    private List<ModuleRegistrationData> filterPermittedModules(List<String> userRoles,
                                                                Collection<ModuleRegistrationData> modules) {
        List<ModuleRegistrationData> allowedModules = new ArrayList<>();

        if (modules != null) {
            for (ModuleRegistrationData module : modules) {
                List<String> requiredPermissionForAccess = module.getRoleForAccess();

                if (!requiredPermissionForAccess.isEmpty()) {
                    if (checkUserPermission(userRoles, requiredPermissionForAccess)) {
                        allowedModules.add(module);
                    }
                } else {
                    allowedModules.add(module);
                }
            }
        }

        return allowedModules;
    }

    private boolean isSubMenuLinkAccessibleByCurrentUser(String userName, List<String> userRoles,
                                                         SubmenuInfo submenuInfo) {
        List<String> roleForAccess = submenuInfo.getRoleForAccess();
        return roleForAccess.isEmpty() || "Admin Mode".equals(userName) ||
                checkUserPermission(userRoles, roleForAccess);
    }

    private boolean checkUserPermission(List<String> roles, List<String> requiredPermission) {
        for (String userRole : roles) {
            RoleDto role = roleService.getRole(userRole);
            for (String permission : requiredPermission) {
                if (role != null && role.getPermissionNames() != null
                        && role.getPermissionNames().contains(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    private String getAngularModuleName(ModuleRegistrationData data) {
        List<String> angularModules = data.getAngularModules();
        return isEmpty(angularModules) ? data.getModuleName() : angularModules.get(0);
    }
}
