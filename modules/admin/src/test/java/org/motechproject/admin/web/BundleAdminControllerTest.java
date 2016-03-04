package org.motechproject.admin.web;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.internal.service.ModuleAdminService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.web.controller.BundleAdminController;
import org.motechproject.admin.bundles.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BundleAdminControllerTest {

    private static final long BUNDLE_ID = 1;

    @InjectMocks
    BundleAdminController controller = new BundleAdminController();

    @Mock
    ModuleAdminService moduleAdminService;

    @Mock
    List<BundleInformation> bundles;

    @Mock
    BundleInformation bundleInformation;

    @Mock
    HttpServletResponse response;

    @Mock
    MultipartFile bundleFile;

    @Mock
    StatusMessageService statusMessageService;

    @Mock
    PrintWriter writer;

    @Mock
    HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testBundleList() {
        when(moduleAdminService.getBundles()).thenReturn(bundles);

        List<BundleInformation> result = controller.getBundles();

        assertEquals(bundles, result);
        verify(moduleAdminService).getBundles();
    }

    @Test
    public void testGetBundleInfo() {
        when(moduleAdminService.getBundleInfo(BUNDLE_ID)).thenReturn(bundleInformation);

        BundleInformation result = controller.getBundle(BUNDLE_ID);

        assertEquals(bundleInformation,  result);
        verify(moduleAdminService).getBundleInfo(BUNDLE_ID);
    }

    @Test
    public void testStopBundle() throws BundleException {
        when(moduleAdminService.stopBundle(BUNDLE_ID)).thenReturn(bundleInformation);

        BundleInformation result = controller.stopBundle(BUNDLE_ID);

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).stopBundle(BUNDLE_ID);
    }

    @Test
    public void testStartBundle() throws BundleException {
        when(moduleAdminService.startBundle(BUNDLE_ID)).thenReturn(bundleInformation);

        BundleInformation result = controller.startBundle(BUNDLE_ID);

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).startBundle(BUNDLE_ID);
    }

    @Test
    public void testRestartBundle() throws BundleException {
        when(moduleAdminService.restartBundle(BUNDLE_ID)).thenReturn(bundleInformation);

        BundleInformation result = controller.restartBundle(BUNDLE_ID);

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).restartBundle(BUNDLE_ID);
    }

    @Test
    public void testUploadBundle() throws BundleException {
        when(moduleAdminService.installBundle(bundleFile, true)).thenReturn(bundleInformation);
        when(moduleAdminService.installBundle(bundleFile, false)).thenReturn(bundleInformation);

        BundleInformation result = controller.uploadBundle("File", null, bundleFile, "on");

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).installBundle(bundleFile, true);

        result = controller.uploadBundle("File", null, bundleFile, null);

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).installBundle(bundleFile, false);
    }

    @Test
    public void testUninstallBundle() throws BundleException {
        controller.uninstallBundle(BUNDLE_ID);

        verify(moduleAdminService).uninstallBundle(BUNDLE_ID, false);
    }

    @Test
    public void testBundleException() throws IOException {
        Exception ex = new Exception("testMessage");

        String msg = ex.getMessage();
        String exMsg = ExceptionUtils.getStackTrace(ex);

        when(response.getWriter()).thenReturn(writer);

        controller.handleBundleException(request, response, ex);

        verify(statusMessageService).error(msg, "admin");
        verify(writer).write(exMsg);
    }
}