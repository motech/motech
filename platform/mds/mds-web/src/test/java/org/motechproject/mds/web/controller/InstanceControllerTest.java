package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.impl.EntityServiceImpl;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.service.impl.InstanceServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class InstanceControllerTest {

    @Mock
    private EntityServiceImpl entityService;

    @Mock
    private InstanceServiceImpl instanceService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private InstanceController instanceController = new InstanceController();

    @Before
    public void setUp() {
        initMocks(this);
        when(entityService.getEntity(anyLong())).thenReturn(new EntityDto());
        when(instanceService.getEntityRecords(anyLong())).thenReturn(getTestEntityRecords());
    }

    @Test
    public void shouldExportTestEntitiesAsCsv() throws Exception {
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        instanceController.exportEntityInstances(1L, response);

        assertEquals(getTestEntityRecordsAsCsv(), writer.toString());
    }

    private List<EntityRecord> getTestEntityRecords() {
        List<EntityRecord> records = new ArrayList<EntityRecord>(Arrays.asList(
                new EntityRecord(1L, 1L, new ArrayList<FieldRecord>(Arrays.asList(
                        new FieldRecord("Field1", "Field1", "ValueA1", new TypeDto()),
                        new FieldRecord("Field2", "Field2", "ValueA2", new TypeDto()),
                        new FieldRecord("Field3", "Field3", "ValueA3", new TypeDto())
                ))),
                new EntityRecord(1L, 1L, new ArrayList<FieldRecord>(Arrays.asList(
                        new FieldRecord("Field1", "Field1", "ValueB1", new TypeDto()),
                        new FieldRecord("Field2", "Field2", "ValueB2", new TypeDto()),
                        new FieldRecord("Field3", "Field3", "ValueB3", new TypeDto())
                ))),
                new EntityRecord(1L, 1L, new ArrayList<FieldRecord>(Arrays.asList(
                        new FieldRecord("Field1", "Field1", "ValueC1", new TypeDto()),
                        new FieldRecord("Field2", "Field2", "ValueC2", new TypeDto()),
                        new FieldRecord("Field3", "Field3", "ValueC3", new TypeDto())
                )))
        ));
        return records;
    }

    private String getTestEntityRecordsAsCsv() {
        return "Field1,Field2,Field3\r\n" +
                "ValueA1,ValueA2,ValueA3\r\n" +
                "ValueB1,ValueB2,ValueB3\r\n" +
                "ValueC1,ValueC2,ValueC3\r\n";
    }
}
