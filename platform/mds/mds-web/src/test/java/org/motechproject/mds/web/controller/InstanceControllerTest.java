package org.motechproject.mds.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.domain.BasicEntityRecord;
import org.motechproject.mds.web.domain.BasicFieldRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.commons.api.Records;
import org.motechproject.mds.web.domain.RelationshipsUpdate;
import org.motechproject.mds.web.service.InstanceService;
import org.motechproject.testing.utils.rest.RestTestUtil;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
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

    private MockMvc controller;

    @Before
    public void setUp() {
        controller = MockMvcBuilders.standaloneSetup(instanceController).build();
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
        gridSettings.setSelectedFields(asList("id", "date"));
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

    @Test
    public void shouldRetrieveRelatedFieldValues() throws Exception {
        Records<BasicEntityRecord> records = new Records<>(2, 5, 7, recordsList());

        when(instanceService.getRelatedFieldValue(eq(1L), eq(6L), eq("relField"), any(RelationshipsUpdate.class), any(QueryParams.class)))
                .thenReturn(records);

        controller.perform(post("/instances/1/instance/6/relField?rows=5&page=2&sortColumn=age&sortDirection=desc"))
            .andExpect(status().isOk()).andExpect(content().type(RestTestUtil.JSON_UTF8))
            .andExpect(content().string(new ObjectMapper().writeValueAsString(records)));

        ArgumentCaptor<QueryParams> captor = ArgumentCaptor.forClass(QueryParams.class);
        verify(instanceService).getRelatedFieldValue(eq(1L), eq(6L), eq("relField"), any(RelationshipsUpdate.class), captor.capture());
        QueryParams queryParams = captor.getValue();

        // check query params
        assertNotNull(queryParams);
        assertEquals(Integer.valueOf(5), queryParams.getPageSize());
        assertEquals(Integer.valueOf(2), queryParams.getPage());
        assertNotNull(queryParams.getOrderList());
        assertEquals(2, queryParams.getOrderList().size());
        assertEquals("age", queryParams.getOrderList().get(0).getField());
        assertEquals(Order.Direction.DESC, queryParams.getOrderList().get(0).getDirection());
        assertEquals(Constants.Util.ID_FIELD_NAME, queryParams.getOrderList().get(1).getField());
        assertEquals(Order.Direction.ASC, queryParams.getOrderList().get(1).getDirection());
    }

    private List<BasicEntityRecord> recordsList() {
        List<BasicEntityRecord> records = new ArrayList<>();

        records.add(testRecordAsEntityRecord("n1", 22, 1));
        records.add(testRecordAsEntityRecord("test", 7, 2));

        return records;
    }

    private BasicEntityRecord testRecordAsEntityRecord(String name, int val, long id) {
        BasicFieldRecord nameField = new FieldRecord("name", name, TypeDto.STRING);
        BasicFieldRecord valField = new FieldRecord("val", val, TypeDto.INTEGER);

        return new BasicEntityRecord(id, asList(nameField, valField));
    }
}
