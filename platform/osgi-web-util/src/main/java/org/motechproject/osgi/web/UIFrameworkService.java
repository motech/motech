package org.motechproject.osgi.web;

import org.motechproject.osgi.web.util.ModuleRegistrations;
import org.osgi.framework.Bundle;

import java.util.Map;

/**
 * Service responsible for managing the user interface. Provides methods for registering/un-registering modules.
 * All modules are represented by {@link ModuleRegistrationData} objects, either registered directly through this
 * service or automatically by exposing it in their spring context. This service also allows manipulation of module
 * state, by marking given modules as requiring attention on the UI.
 */
public interface UIFrameworkService {

    /**
     * Registers a module in the UI system.
     * @param module a {@link org.motechproject.osgi.web.ModuleRegistrationData} representing the module to register
     */
    void registerModule(ModuleRegistrationData module);

    /**
     * Unregisters module from the UI system.
     * @param moduleName the name of the module to unregister
     */
    void unregisterModule(String moduleName);

    /**
     * Returns information about all modules registered with the UI system. The modules are grouped into
     * module with their own submenus (links to menus in the top menu), those without their submenus (they are all placed
     * in the modules section) and those without UI altogether (messages from them will be loaded though, for usage in tasks
     * and possibly other places).
     * @return all modules registered with the system
     */
    ModuleRegistrations getRegisteredModules();

    /**
     * Gets registration data for the module with the given name.
     * @param moduleName the name of the module for which the registration data should be retrieved
     * @return the registration data for the given module, or null if such a module is not registered
     */
    ModuleRegistrationData getModuleData(String moduleName);

    /**
     * Retrieves the module registration data for a given bundle. Since {@link org.motechproject.osgi.web.ModuleRegistrationData}
     * held by the service contain references to the bundles registering them, this will match relying on Bundle.equals().
     * @param bundle the bundle for which the registration data should be retrieved
     * @return the registration data for the given bundle (module)
     */
    ModuleRegistrationData getModuleDataByBundle(Bundle bundle);

    /**
     * Retrieves module registration data which registers the given AngularJS module.
     * @param angularModule the name of the Angular module
     * @return the registration for the module that registers the Angular module
     */
    ModuleRegistrationData getModuleDataByAngular(String angularModule);

    /**
     * Checks whether the module with a given name is registered in the UI system.
     * @param moduleName the name of the module
     * @return true if the module is registered, false otherwise
     */
    boolean isModuleRegistered(String moduleName);

    /**
     * Marks a module as requiring attention, by setting the {@link org.motechproject.osgi.web.ModuleRegistrationData#setNeedsAttention(boolean)}
     * flag and the critical message for the module using {@link org.motechproject.osgi.web.ModuleRegistrationData#setCriticalMessage(String)}.
     * This will mark the module as needing attention on the UI by display a red warning sign next to its link and displaying the message.
     * @param moduleName the name of the module to mark as requiring attention
     * @param message the message explaining the reason for this alert
     */
    void moduleNeedsAttention(String moduleName, String message);

    /**
     * Does the same as {@link #moduleNeedsAttention(String, String)}, with the difference that an entire submenu (the top level menu)
     * will be marked as requiring attention.
     * @param moduleName the name of the module to mark as requiring attention
     * @param submenu the name of the submenu to be marked as requiring attention
     * @param message the message explaining the reason for this alert
     */
    void moduleNeedsAttention(String moduleName, String submenu, String message);

    /**
     * Marks the module as no longer requiring attention, undoing the {@link #moduleNeedsAttention(String, String)} call.
     * The red marker will disappear from the link of that module.
     * @param moduleName the name of the module that is back to normal
     */
    void moduleBackToNormal(String moduleName);

    /**
     * Marks the module and submenu as no longer requiring attention, undoing the {@link #moduleNeedsAttention(String, String, String)} call.
     * The red marker will disappear from the link of that module and submenu.
     * @param moduleName the name of the module that is back to normal
     * @param submenu the name of the submenu that is back to normal
     */
    void moduleBackToNormal(String moduleName, String submenu);

    /**
     * Returns a map of links to Swagger rest specifications registered with the system. Keys are module names,
     * values are urls to specifications.
     * @return the map containing all registered rest doc urls as values, and module names as keys
     */
    Map<String, String> getRestDocLinks();
}
