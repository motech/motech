package org.motechproject.admin.web.controller;

import org.motechproject.admin.bundles.BundleIcon;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.server.osgi.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class BundleAdminController {

    @Autowired
    private ModuleAdminService moduleAdminService;

    @RequestMapping(value = "/bundles", method = RequestMethod.GET)
    public @ResponseBody List<BundleInformation> getBundles() {
        return moduleAdminService.getBundles();
    }

    @RequestMapping(value = "/bundles/{bundleId}", method = RequestMethod.GET)
    public @ResponseBody BundleInformation getBundle(@PathVariable long bundleId) {
        return moduleAdminService.getBundleInfo(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/start", method = RequestMethod.POST)
    public @ResponseBody BundleInformation startBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.startBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/stop", method = RequestMethod.POST)
    public @ResponseBody BundleInformation stopBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.stopBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/restart", method = RequestMethod.POST)
    public @ResponseBody BundleInformation restartBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.restartBundle(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/bundles/{bundleId}/uninstall", method = RequestMethod.POST)
    public void uninstallBundle(@PathVariable long bundleId) throws BundleException {
        moduleAdminService.uninstallBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/upload", method = RequestMethod.POST)
    public @ResponseBody BundleInformation uploadBundle(@RequestParam MultipartFile bundleFile) {
        return moduleAdminService.installBundle(bundleFile);
    }

    @RequestMapping(value = "/bundles/{bundleId}/icon", method = RequestMethod.GET)
    public void getBundleIcon(@PathVariable long bundleId, HttpServletResponse response) throws IOException {
        BundleIcon bundleIcon = moduleAdminService.getBundleIcon(bundleId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(bundleIcon.getContentLength());
        response.setContentType(bundleIcon.getMime());

        response.getOutputStream().write(bundleIcon.getIcon());
    }
}
