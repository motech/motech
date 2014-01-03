package org.motechproject.server.web.helper;

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
import java.util.List;
import java.util.Map;

import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITHOUT_SUBMENU;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITH_SUBMENU;

/**
 * Helper class for building the modules menu view(left-hand side nav). Modules to display are retrieved
 * from the {@link UIFrameworkService}. Filters entries based on user permissions.
 *
 * @see UIFrameworkService
 */
@Component
public class MenuBuilder {

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

        for (ModuleRegistrationData moduleRegistrationData : getModulesWithSubMenu(username)) {
            ModuleMenuSection menuSection = getModuleMenuSection(username, moduleRegistrationData);
            if (!menuSection.getLinks().isEmpty()) {
                moduleMenu.addMenuSection(menuSection);
            }
        }
        moduleMenu.addMenuSection(serverModulesMenuSection(username));
        return moduleMenu;
    }

    private ModuleMenuSection getModuleMenuSection(String username, ModuleRegistrationData moduleRegistrationData) {
        String moduleName = moduleRegistrationData.getModuleName();
        ModuleMenuSection menuSection = new ModuleMenuSection(moduleName, moduleRegistrationData.isNeedsAttention());

        for (Map.Entry<String, SubmenuInfo> submenuEntry : moduleRegistrationData.getSubMenu().entrySet()) {
            SubmenuInfo submenuInfo = submenuEntry.getValue();

            if (isSubMenuLinkAccessibleByCurrentUser(username, submenuInfo)) {
                String name = submenuEntry.getKey();

                ModuleMenuLink link = new ModuleMenuLink(name, moduleName, submenuInfo.getUrl(),
                        submenuInfo.isNeedsAttention());

                menuSection.addLink(link);
            }

        }
        return menuSection;
    }

    private ModuleMenuSection serverModulesMenuSection(String username) {
        ModuleMenuSection modulesSection = new ModuleMenuSection("server.modules", false);

        for (ModuleRegistrationData moduleRegistrationData : getModulesWithoutSubMenu(username)) {
            String moduleName = moduleRegistrationData.getModuleName();
            boolean needsAttention = moduleRegistrationData.isNeedsAttention();

            // these menu items don't make use of urls, the name is sufficient
            ModuleMenuLink link = new ModuleMenuLink(moduleName, moduleName, "", needsAttention);

            modulesSection.addLink(link);
        }

        return modulesSection;
    }

    private List<ModuleRegistrationData> getModulesWithSubMenu(String userName) {
        return filterPermittedModules(
                userName,
                uiFrameworkService.getRegisteredModules().get(MODULES_WITH_SUBMENU)
        );
    }

    private List<ModuleRegistrationData> getModulesWithoutSubMenu(String username) {
        return filterPermittedModules(
                username,
                uiFrameworkService.getRegisteredModules().get(MODULES_WITHOUT_SUBMENU)
        );
    }

    private List<ModuleRegistrationData> filterPermittedModules(String userName, Collection<ModuleRegistrationData> modules) {
        List<ModuleRegistrationData> allowedModules = new ArrayList<>();

        if (modules != null) {
            for (ModuleRegistrationData module : modules) {
                String requiredPermissionForAccess = module.getRoleForAccess();

                if (requiredPermissionForAccess != null) {
                    if (checkUserPermission(userService.getRoles(userName), requiredPermissionForAccess)) {
                        allowedModules.add(module);
                    }
                } else {
                    allowedModules.add(module);
                }
            }
        }

        return allowedModules;
    }

    private boolean isSubMenuLinkAccessibleByCurrentUser(String userName, SubmenuInfo submenuInfo) {
        String roleForAccess = submenuInfo.getRoleForAccess();
        return roleForAccess == null || "Admin Mode".equals(userName) ||
                checkUserPermission(userService.getRoles(userName), roleForAccess);
    }

    private boolean checkUserPermission(List<String> roles, String requiredPermission) {
        for (String userRole : roles) {
            RoleDto role = roleService.getRole(userRole);
            if (role != null && role.getPermissionNames() != null
                    && role.getPermissionNames().contains(requiredPermission)) {
                return true;
            }
        }

        return false;
    }
}
