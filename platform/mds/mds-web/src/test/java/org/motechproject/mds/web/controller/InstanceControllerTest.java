package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.service.InstanceService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    public void shouldExportInstancesWithAllRecordsAsCsv() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        ArgumentCaptor<QueryParams> captor = ArgumentCaptor.forClass(QueryParams.class);

        GridSettings gridSettings = new GridSettings();
        gridSettings.setLookup("lookup");

        instanceController.exportEntityInstances(1L, gridSettings, "all", "csv", response);

        verify(instanceService).verifyEntityAccess(1L);
        verify(csvImportExportService).exportCsv(eq(1L), eq(writer), eq("lookup"), captor.capture(), any(List.class), any(Map.class));
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition",
                "attachment; filename=Entity_1_instances.csv");

        assertNull(captor.getValue().getPageSize());
        assertTrue(captor.getValue().isOrderSet());
        assertEquals(1, captor.getValue().getOrderList().size());
        assertEquals(Constants.Util.ID_FIELD_NAME, captor.getValue().getOrderList().get(0).getField());
        assertEquals(Order.Direction.ASC, captor.getValue().getOrderList().get(0).getDirection());
    }

    @Test
    public void shouldExportInstancesWithAdditionalOptionsAsCsv() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        ArgumentCaptor<QueryParams> queryParamsCaptor = ArgumentCaptor.forClass(QueryParams.class);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);

        GridSettings gridSettings = new GridSettings();
        gridSettings.setSortColumn("sortColumn");
        gridSettings.setSortDirection("asc");
        gridSettings.setSelectedFields(Arrays.asList("id", "date"));
        gridSettings.setLookup("lookup");

        instanceController.exportEntityInstances(1L, gridSettings, "50", "csv", response);

        verify(instanceService).verifyEntityAccess(1L);
        verify(csvImportExportService).exportCsv(eq(1L), eq(writer), eq("lookup"), queryParamsCaptor.capture(), listCaptor.capture(), any(Map.class));
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition",
                "attachment; filename=Entity_1_instances.csv");

        QueryParams captorValue = queryParamsCaptor.getValue();
        assertEquals(Order.Direction.ASC, captorValue.getOrderList().get(0).getDirection());
        assertEquals("sortColumn", captorValue.getOrderList().get(0).getField());
        assertEquals(Integer.valueOf(1), captorValue.getPage());
        assertEquals(Integer.valueOf(50), captorValue.getPageSize());

        assertEquals(2, listCaptor.getValue().size());
        assertTrue(listCaptor.getValue().contains("id"));
        assertTrue(listCaptor.getValue().contains("date"));
    }
}
