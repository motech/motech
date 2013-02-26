package org.motechproject.admin.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.server.api.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

@Controller
public class BundleAdminController {

    @Autowired
    private ModuleAdminService moduleAdminService;

    @Autowired
    private StatusMessageService statusMessageService;

    @RequestMapping(value = "/bundles", method = RequestMethod.GET)
    @ResponseBody
    public List<BundleInformation> getBundles() {
        return moduleAdminService.getBundles();
    }

    @RequestMapping(value = "/bundles/{bundleId}", method = RequestMethod.GET)
    @ResponseBody
    public BundleInformation getBundle(@PathVariable long bundleId) {
        return moduleAdminService.getBundleInfo(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/detail")
    @ResponseBody
    public ExtendedBundleInformation getBundleDetails(@PathVariable long bundleId) {
        return moduleAdminService.getBundleDetails(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/start", method = RequestMethod.POST)
    @ResponseBody
    public BundleInformation startBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.startBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/stop", method = RequestMethod.POST)
    @ResponseBody
    public BundleInformation stopBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.stopBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/restart", method = RequestMethod.POST)
    @ResponseBody
    public BundleInformation restartBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.restartBundle(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/bundles/{bundleId}/uninstall", method = RequestMethod.POST)
    public void uninstallBundle(@PathVariable long bundleId) throws BundleException {
        moduleAdminService.uninstallBundle(bundleId);
        statusMessageService.ok("{bundles.uninstall.success}");
    }

    @RequestMapping(value = "/bundles/upload", method = RequestMethod.POST)
    @ResponseBody
    public BundleInformation uploadBundle(@RequestParam String moduleSource,
                                          @RequestParam(required = false) String moduleId,
                                          @RequestParam(required = false) MultipartFile bundleFile,
                                          @RequestParam(required = false) String startBundle) {
        boolean start = (StringUtils.isBlank(startBundle) ? false : "on".equals(startBundle));
        if ("File".equals(moduleSource)) {
            return moduleAdminService.installBundle(bundleFile, start);
        } else {
            if (isBlank(moduleId)) {
                throw new MotechException("No module selected.");
            }
            return moduleAdminService.installFromRepository(moduleId, start);
        }
    }

    @RequestMapping(value = "/bundles/{bundleId}/icon", method = RequestMethod.GET)
    public void getBundleIcon(@PathVariable long bundleId, HttpServletResponse response) throws IOException {
        BundleIcon bundleIcon = moduleAdminService.getBundleIcon(bundleId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(bundleIcon.getContentLength());
        response.setContentType(bundleIcon.getMime());

        response.getOutputStream().write(bundleIcon.getIcon());
    }

    @ExceptionHandler(Exception.class)
    public void handleBundleException(HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Throwable rootEx = (ex.getCause() == null ? ex : ex.getCause());

        String msg = (StringUtils.isNotBlank(rootEx.getMessage())) ? rootEx.getMessage() : rootEx.toString();
        statusMessageService.error(msg);

        try (Writer writer = response.getWriter()) {
            writer.write(ExceptionUtils.getStackTrace(ex));
        }

    }
}
