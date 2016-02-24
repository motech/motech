package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.server.ui.BundleIconService;
import org.motechproject.server.web.dto.BundleIcon;
import org.motechproject.server.web.helper.MenuBuilder;
import org.osgi.framework.BundleContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ModuleControllerTest {

    private static final long BUNDLE_ID = 1;
    private static final String ICON_MIME = "image/png";

    @InjectMocks
    ModuleController moduleController = new ModuleController();

    @Mock
    private UIFrameworkService uiFrameworkService;

    @Mock
    private LocaleService localeService;

    @Mock
    private BundleIconService bundleIconService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private MenuBuilder menuBuilder;

    @Mock
    HttpServletResponse response;

    @Mock
    ServletOutputStream outputStream;

    @Before
    public void setup()throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetBundleIcon() throws IOException {
        BundleIcon bundleIcon = new BundleIcon(new byte[] {1, 2, 3, 4}, ICON_MIME);

        when(bundleIconService.getBundleIconById(BUNDLE_ID, null)).thenReturn(bundleIcon);
        when(response.getOutputStream()).thenReturn(outputStream);

        moduleController.getBundleIcon(BUNDLE_ID, null, null, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentLength(bundleIcon.getContentLength());
        verify(response).setContentType(bundleIcon.getMime());
        verify(response).getOutputStream();

        verify(outputStream).write(bundleIcon.getIcon());
    }
}
