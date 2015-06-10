package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.web.service.InstanceService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class InstanceControllerTest {

    @Mock
    private InstanceService instanceService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private CsvImportExportService csvImportExportService;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private InstanceController instanceController = new InstanceController();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldExportTestEntitiesAsCsv() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        instanceController.exportEntityInstances(1L, response);

        verify(instanceService).verifyEntityAccess(1L);
        verify(csvImportExportService).exportCsv(1L, writer);
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition",
                "attachment; filename=Entity_1_instances.csv");
    }
}
