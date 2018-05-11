package org.motechproject.admin.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.internal.service.ModuleAdminService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.commons.api.MotechException;
import org.motechproject.admin.bundles.BundleInformation;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * The Spring controller responsible for operations on bundles (modules). It allows
 * starting/stopping/installing/removing/restarting of bundles in the system. It is responsible
 * for handling the "Manage Modules" view on the Admin UI.
 */
@Controller
@Api(value="BundleAdminController", description = "The Spring controller responsible for operations on bundles (modules). It allows\n" +
        "starting/stopping/installing/removing/restarting of bundles in the system. It is responsible\n" +
        "for handling the \"Manage Modules\" view on the Admin UI")
public class BundleAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleAdminController.class);
    private static final String ADMIN_MODULE_NAME = "admin";

    @Autowired
    private ModuleAdminService moduleAdminService;

    @Autowired
    private StatusMessageService statusMessageService;

    /**
     * Retrieves a list of bundles in the system. Bundles are represented by {@link BundleInformation} objects.
     * Only non-platform (don't start with motech-platform), non-3rd party (must import at least one org.motechproject.* package) are
     * returned. The framework and the admin module bundle are omitted.
     * @return a list of module bundles
     */
    @RequestMapping(value = "/bundles", method = RequestMethod.GET)
    @ApiOperation(value="Retrieves a list of bundles in the system. Bundles are represented by {@link BundleInformation} objects.\n" +
            "Only non-platform (don't start with motech-platform), non-3rd party (must import at least one org.motechproject.* package) are\n" +
            "returned. The framework and the admin module bundle are omitted")
    @ResponseBody
    public List<BundleInformation> getBundles() {
        return moduleAdminService.getBundles();
    }

    /**
     * Retrieves information about the bundle with the given bundle ID.
     * This bundle does not have to be a MOTECH module.
     * The information is returned in the form of {@link BundleInformation}
     * @param bundleId the id of the bundle for which the information will be retrieved
     * @return the information about the bundle
     */
    @RequestMapping(value = "/bundles/{bundleId}", method = RequestMethod.GET)
    @ApiOperation(value="Retrieves information about the bundle with the given bundle ID.\n" +
            "This bundle does not have to be a MOTECH module.\n" +
            "The information is returned in the form of {@link BundleInformation}")
    @ResponseBody
    public BundleInformation getBundle(@PathVariable long bundleId) {
        return moduleAdminService.getBundleInfo(bundleId);
    }

    /**
     * Retrieves detailed information about the bundle with the given bundle ID.
     * This bundle does not have to be a MOTECH module.
     * The information is returned in the form of {@link org.motechproject.admin.bundles.ExtendedBundleInformation}
     * @param bundleId the id of the bundle for which the information will be retrieved
     * @return the detailed information about the bundle
     */
    @RequestMapping(value = "/bundles/{bundleId}/detail")
    @ApiOperation(value="Retrieves detailed information about the bundle with the given bundle ID.\n" +
            "This bundle does not have to be a MOTECH module.")
    @ResponseBody
    public ExtendedBundleInformation getBundleDetails(@PathVariable long bundleId) {
        return moduleAdminService.getBundleDetails(bundleId);
    }

    /**
     * Starts the bundle with the given bundle ID.
     * @param bundleId the ID of the bundle to start
     * @return information about the bundle started
     * @throws BundleException if starting the bundle failed
     */
    @RequestMapping(value = "/bundles/{bundleId}/start", method = RequestMethod.POST)
    @ApiOperation(value="Starts the bundle with the given bundle ID")
    @ResponseBody
    public BundleInformation startBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.startBundle(bundleId);
    }

    /**
     * Stops the bundle with the given bundle ID.
     * @param bundleId the ID of the bundle to stop
     * @return information about the stopped bundle
     * @throws BundleException if stopping the bundle failed
     */
    @RequestMapping(value = "/bundles/{bundleId}/stop", method = RequestMethod.POST)
    @ApiOperation(value="Stops the bundle with the given bundle ID")
    @ResponseBody
    public BundleInformation stopBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.stopBundle(bundleId);
    }

    /**
     * Restarts the bundle with the given bundle ID. Synonymous to doing a stop followed by a start.
     * @param bundleId the ID of the bundle to restart
     * @return information about the restarted bundle
     * @throws BundleException if stopping the bundle failed
     */
    @RequestMapping(value = "/bundles/{bundleId}/restart", method = RequestMethod.POST)
    @ApiOperation(value="Restarts the bundle with the given bundle ID. Synonymous to doing a stop followed by a start")
    @ResponseBody
    public BundleInformation restartBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.restartBundle(bundleId);
    }

    /**
     * Uninstalls the bundle with the given bundle ID from the OSGi framework.
     * If the bundle is a MOTECH module, its configuration will not be removed.
     * @param bundleId the ID of the bundle to remove from the framework
     * @throws BundleException if there were problems uninstalling the bundle
     * @see #uninstallBundleWithConfig(long) for the version that removes configuration
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/bundles/{bundleId}/uninstall", method = RequestMethod.POST)
    @ApiOperation(value="Uninstalls the bundle with the given bundle ID from the OSGi framework.\n" +
            "If the bundle is a MOTECH module, its configuration will not be removed.")
    public void uninstallBundle(@PathVariable long bundleId) throws BundleException {
        moduleAdminService.uninstallBundle(bundleId, false);
        LOGGER.info("Bundle [{}] removed successfully");
    }

    /**
     * Uninstalls the bundle with the given bundle ID from the OSGi framework.
     * If the bundle is a MOTECH module, its configuration will be removed.
     * @param bundleId the ID of the bundle to remove from the framework
     * @throws BundleException if there were problems uninstalling the bundle
     * @see #uninstallBundle(long) for the version that leaves configuration intact
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/bundles/{bundleId}/uninstallconfig", method = RequestMethod.POST)
    @ApiOperation(value="Uninstalls the bundle with the given bundle ID from the OSGi framework.\n" +
            "If the bundle is a MOTECH module, its configuration will be removed.")
    public void uninstallBundleWithConfig(@PathVariable long bundleId) throws BundleException {
        moduleAdminService.uninstallBundle(bundleId, true);
        LOGGER.info("Bundle [{}] removed successfully");
    }

    /**
     * Handles a request for installing a bundle in the OSGi framework.
     * This can either be a file upload or a request to install from the Nexus repository.
     * @param moduleSource the source from which the module will be installed. If it equals {@code File}, then this
     *                     request will be treated as bundle file upload.
     * @param moduleId the id of the module to be installed from Nexus (only used in Nexus install)
     * @param file the file from which to install the new module (only used in upload install)
     * @param startBundle true if the bundle should be started after installation
     * @return information about the newly installed bundle
     */
    @RequestMapping(value = "/bundles/upload", method = RequestMethod.POST)
    @ApiOperation(value="Handles a request for installing a bundle in the OSGi framework.\n" +
            "This can either be a file upload or a request to install from the Nexus repository.")
    @ResponseBody
    public BundleInformation uploadBundle(@RequestParam String moduleSource,
                                          @RequestParam(required = false) String moduleId,
                                          @RequestParam(required = false) MultipartFile file,
                                          @RequestParam(required = false) String startBundle) {
        boolean start = (StringUtils.isNotBlank(startBundle) && "on".equals(startBundle));
        if ("File".equals(moduleSource)) {
            if (file == null) {
                throw new IllegalArgumentException("No file passed in the request - invalid upload");
            }
            return moduleAdminService.installBundle(file, start);
        } else {
            if (isBlank(moduleId)) {
                throw new MotechException("No module selected.");
            }
            return moduleAdminService.installBundleFromRepository(moduleId, start);
        }
    }

    /**
     * The exception handler for this controller. Writes exception stacktrace to the output. Spring will
     * call this for exceptions coming from controller methods.
     * @param response HttpServletResponse used for writing the stacktrace
     * @param ex the exception being handled
     * @throws IOException if there were problems writing the stacktrace to the response
     */
    @ExceptionHandler(Exception.class)
    public void handleBundleException(HttpServletRequest request, HttpServletResponse response, Exception ex)
            throws IOException {
        Throwable rootEx = (ex.getCause() == null ? ex : ex.getCause());

        String msg = (StringUtils.isNotBlank(rootEx.getMessage())) ? rootEx.getMessage() : rootEx.toString();
        statusMessageService.error(msg, ADMIN_MODULE_NAME);

        LOGGER.error("Error when processing request: {}", request.getPathInfo(), ex);

        if (ex instanceof AccessDeniedException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        try (Writer writer = response.getWriter()) {
            writer.write(ExceptionUtils.getStackTrace(ex));
        }
    }
}
