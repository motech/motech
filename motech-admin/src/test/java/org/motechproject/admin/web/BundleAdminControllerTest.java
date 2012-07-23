package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.bundles.BundleIcon;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.web.controller.BundleAdminController;
import org.motechproject.server.osgi.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    BundleIcon bundleIcon;

    @Mock
    HttpServletResponse response;

    @Mock
    ServletOutputStream outputStream;

    @Mock
    MultipartFile bundleFile;

    @Mock
    BundleException bundleException;

    @Mock
    StatusMessageService statusMessageService;

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

        BundleInformation result = controller.uploadBundle(bundleFile, "on");

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).installBundle(bundleFile, true);

        result = controller.uploadBundle(bundleFile, null);

        assertEquals(bundleInformation, result);
        verify(moduleAdminService).installBundle(bundleFile, false);
    }

    @Test
    public void testUninstallBundle() throws BundleException {
        controller.uninstallBundle(BUNDLE_ID);

        verify(moduleAdminService).uninstallBundle(BUNDLE_ID);
    }

    @Test
    public void testGetBundleIcon() throws IOException {
        final byte[] icon =  new byte[] { 1, 2, 3 };
        final String mime = "image/gif";

        when(moduleAdminService.getBundleIcon(BUNDLE_ID)).thenReturn(bundleIcon);
        when(bundleIcon.getIcon()).thenReturn(icon);
        when(bundleIcon.getMime()).thenReturn(mime);
        when(bundleIcon.getContentLength()).thenReturn(icon.length);
        when(response.getOutputStream()).thenReturn(outputStream);

        controller.getBundleIcon(BUNDLE_ID, response);

        verify(moduleAdminService).getBundleIcon(BUNDLE_ID);
        verify(response).setContentType(mime);
        verify(response).setContentLength(icon.length);
        verify(outputStream).write(icon);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testBundleException() {
        String msg = "error message";
        when(bundleException.getMessage()).thenReturn(msg);

        controller.handleBundleException(bundleException);
        verify(statusMessageService).error(msg);
    }
}
