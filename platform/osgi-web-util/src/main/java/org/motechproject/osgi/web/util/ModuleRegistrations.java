package org.motechproject.osgi.web.util;

import org.motechproject.osgi.web.ModuleRegistrationData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents all modules registered with the system. Modules are grouped into 3 categories:
 * modules with sub-menu - modules that have multiple menu items, they get a link in the top level, the admin module is an example
 * modules without sub-menu - modules that are placed in the "Modules" section on the UI, the email module is an example
 * modules without UI - modules that don't have any UI, but register i18n files for example, pill-reminder is an example
 */
public class ModuleRegistrations {

    private Collection<ModuleRegistrationData> modulesWithSubMenu;
    private Collection<ModuleRegistrationData> modulesWithoutSubmenu;
    private Collection<ModuleRegistrationData> modulesWithoutUI;

    /**
     * Constructs a new instance, initializing all 3 groups as empty lists.
     */
    public ModuleRegistrations() {
        this(new ArrayList<ModuleRegistrationData>(), new ArrayList<ModuleRegistrationData>(),
                new ArrayList<ModuleRegistrationData>());
    }

    /**
     * Constructs a new instances with the 3 groups provided as collections.
     * @param modulesWithSubMenu modules with sub menus
     * @param modulesWithoutSubmenu modules without sub-menus
     * @param modulesWithoutUI modules without UI
     */
    public ModuleRegistrations(Collection<ModuleRegistrationData> modulesWithSubMenu,
                               Collection<ModuleRegistrationData> modulesWithoutSubmenu,
                               Collection<ModuleRegistrationData> modulesWithoutUI) {
        this.modulesWithSubMenu = modulesWithSubMenu;
        this.modulesWithoutSubmenu = modulesWithoutSubmenu;
        this.modulesWithoutUI = modulesWithoutUI;
    }

    /**
     * @return registered modules with sub-menus
     */
    public Collection<ModuleRegistrationData> getModulesWithSubMenu() {
        return modulesWithSubMenu;
    }

    /**
     * @param modulesWithSubMenu registered modules with sub-menus
     */
    public void setModulesWithSubMenu(Collection<ModuleRegistrationData> modulesWithSubMenu) {
        this.modulesWithSubMenu = modulesWithSubMenu;
    }

    /**
     * @return registered modules without sub-menus
     */
    public Collection<ModuleRegistrationData> getModulesWithoutSubmenu() {
        return modulesWithoutSubmenu;
    }

    /**
     * @param modulesWithoutSubmenu registered modules without sub-menus
     */
    public void setModulesWithoutSubmenu(Collection<ModuleRegistrationData> modulesWithoutSubmenu) {
        this.modulesWithoutSubmenu = modulesWithoutSubmenu;
    }

    /**
     * @return registered modules without UI
     */
    public Collection<ModuleRegistrationData> getModulesWithoutUI() {
        return modulesWithoutUI;
    }

    /**
     * @param modulesWithoutUI registered modules without UI
     */
    public void setModulesWithoutUI(Collection<ModuleRegistrationData> modulesWithoutUI) {
        this.modulesWithoutUI = modulesWithoutUI;
    }

    /**
     * Returns all registered modules from all the 3 groups as one list.
     * @return all registered modules
     */
    public List<ModuleRegistrationData> allRegistrations() {
        List<ModuleRegistrationData> allModules = new ArrayList<>();

        allModules.addAll(modulesWithoutSubmenu);
        allModules.addAll(modulesWithSubMenu);
        allModules.addAll(modulesWithoutUI);

        return allModules;
    }
}
