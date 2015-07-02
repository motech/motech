package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.service.InstanceService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    public void shouldExportInstancesFromTableAsCsv() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        GridSettings gridSettings = new GridSettings();
        gridSettings.setSortColumn("sortColumn");
        gridSettings.setLookup("lookup");
        gridSettings.setFields("{}");

        instanceController.exportEntityInstances(1L, gridSettings, "table", response);

        verify(instanceService).verifyEntityAccess(1L);
        verify(csvImportExportService).exportCsv(eq(1L), eq("lookup"), any(QueryParams.class), any(List.class), any(Map.class), eq(writer));
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition",
                "attachment; filename=Entity_1_instances.csv");
    }

    @Test
    public void shouldExportAllInstancesAsCsv() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        instanceController.exportEntityInstances(1L, new GridSettings(), "all", response);

        verify(instanceService).verifyEntityAccess(1L);
        verify(csvImportExportService).exportCsv(1L, writer);
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition",
                "attachment; filename=Entity_1_instances.csv");
    }
}
