package org.motechproject.mds.service.impl.csv;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.DefaultCsvImportCustomizer;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.Constants;
import org.motechproject.server.osgi.event.OsgiEventProxy;

import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CsvImportExportServiceTest {

    private static final String ENTITY_CLASS_NAME = "org.example.Something";
    private static final String ENTITY_NAME = "Something";
    private static final String ENTITY_NS = "some-namespace";
    private static final String ENTITY_MODULE = "exampleModule";
    private static final long ENTITY_ID = 4;

    private static final List<Long> UPDATED_IDS = Arrays.asList(1L, 2L, 3L);
    private static final List<Long> NEW_IDS = Arrays.asList(4L, 5L, 6L, 7L);
    private static final String FAILURE_EX_MSG = "error when importing";
    private static final long EXPORTED_COUNT = 76514;
    private static final String FILE_NAME = "myfile.csv";

    private static final String EXPECTED_BASE_SUBJECT = "mds.crud.examplemodule.some-namespace.Something.";
    private static final String EXPECTED_SUCCESS_SUBJECT = EXPECTED_BASE_SUBJECT + "csv-import.success";
    private static final String EXPECTED_FAILURE_SUBJECT = EXPECTED_BASE_SUBJECT + "csv-import.failure";

    @InjectMocks
    private CsvImportExportService csvImportExportService = new CsvImportExportServiceImpl();

    @Mock
    private CsvImporterExporter csvImporterExporter;

    @Mock
    private OsgiEventProxy osgiEventProxy;

    @Mock
    private Reader reader;

    @Mock
    private Writer writer;

    @Mock
    private EntityDto entityDto;

    @Mock
    private EntityService entityService;

    @Before
    public void setUp() {
        when(entityDto.getClassName()).thenReturn(ENTITY_CLASS_NAME);
        when(entityDto.getName()).thenReturn(ENTITY_NAME);
        when(entityDto.getNamespace()).thenReturn(ENTITY_NS);
        when(entityDto.getModule()).thenReturn(ENTITY_MODULE);
    }

    @Test
    public void shouldImportInstancesByClassName() {
        CsvImportResults importResults = new CsvImportResults(entityDto, NEW_IDS, UPDATED_IDS);
        when(csvImporterExporter.importCsv(ENTITY_CLASS_NAME, reader)).thenReturn(importResults);

        csvImportExportService.importCsv(ENTITY_CLASS_NAME, reader, FILE_NAME);

        verify(csvImporterExporter).importCsv(ENTITY_CLASS_NAME, reader);
        verifyImportSuccessEvent();
    }

    @Test
    public void shouldImportInstancesById() {
        CsvImportResults importResults = new CsvImportResults(entityDto, NEW_IDS, UPDATED_IDS);
        when(csvImporterExporter.importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class)))
                .thenReturn(importResults);

        csvImportExportService.importCsv(ENTITY_ID, reader, FILE_NAME);

        verify(csvImporterExporter).importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class));
        verifyImportSuccessEvent();
    }

    @Test
    public void shouldThrowImportFailureExceptionWhenImportingById() {
        when(csvImporterExporter.importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class)))
                .thenThrow(new CsvImportException(FAILURE_EX_MSG));
        when(entityService.getEntity(ENTITY_ID)).thenReturn(entityDto);

        boolean thrown = false;
        try {
            csvImportExportService.importCsv(ENTITY_ID, reader, FILE_NAME);
        } catch (CsvImportException e) {
            thrown = true;
        }
        assertTrue("CSV Import exception was not propagated", thrown);

        verify(csvImporterExporter).importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class));
        verifyImportFailureEvent();
    }

    @Test
    public void shouldThrowImportFailureExceptionWhenImportingByClassName() {
        when(csvImporterExporter.importCsv(ENTITY_CLASS_NAME, reader)).thenThrow(new CsvImportException(FAILURE_EX_MSG));
        when(entityService.getEntityByClassName(ENTITY_CLASS_NAME)).thenReturn(entityDto);

        boolean thrown = false;
        try {
            csvImportExportService.importCsv(ENTITY_CLASS_NAME, reader, FILE_NAME);
        } catch (CsvImportException e) {
            thrown = true;
        }
        assertTrue("CSV Import exception was not propagated", thrown);

        verify(csvImporterExporter).importCsv(ENTITY_CLASS_NAME, reader);
        verifyImportFailureEvent();
    }

    @Test
    public void shouldExportById() {
        when(csvImporterExporter.exportCsv(ENTITY_ID, writer)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_ID, writer));
        verify(csvImporterExporter).exportCsv(ENTITY_ID, writer);
    }

    @Test
    public void shouldExportByClassNameId() {
        when(csvImporterExporter.exportCsv(ENTITY_CLASS_NAME, writer)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_CLASS_NAME, writer));
        verify(csvImporterExporter).exportCsv(ENTITY_CLASS_NAME, writer);
    }

    private void verifyImportSuccessEvent() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(osgiEventProxy).sendEvent(eq(EXPECTED_SUCCESS_SUBJECT), captor.capture());

        Map<String, Object> params = captor.getValue();

        assertNotNull(params);
        assertEquals(UPDATED_IDS, params.get(Constants.MDSEvents.CSV_IMPORT_UPDATED_IDS));
        assertEquals(NEW_IDS, params.get(Constants.MDSEvents.CSV_IMPORT_CREATED_IDS));
        assertEquals(3, params.get(Constants.MDSEvents.CSV_IMPORT_UPDATED_COUNT));
        assertEquals(4, params.get(Constants.MDSEvents.CSV_IMPORT_CREATED_COUNT));
        assertEquals(7, params.get(Constants.MDSEvents.CSV_IMPORT_TOTAL_COUNT));
        assertEquals(FILE_NAME, params.get(Constants.MDSEvents.CSV_IMPORT_FILENAME));
        assertEntityParams(params);
    }

    private void verifyImportFailureEvent() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(osgiEventProxy).sendEvent(eq(EXPECTED_FAILURE_SUBJECT), captor.capture());

        Map<String, Object> params = captor.getValue();

        assertNotNull(params);
        assertEquals(FILE_NAME, params.get(Constants.MDSEvents.CSV_IMPORT_FILENAME));
        assertEquals(FAILURE_EX_MSG, params.get(Constants.MDSEvents.CSV_IMPORT_FAILURE_MSG));
        assertNotNull(params.get(Constants.MDSEvents.CSV_IMPORT_FAILURE_STACKTRACE));
        assertEntityParams(params);
    }

    private void assertEntityParams(Map<String, Object> params) {
        assertEquals(ENTITY_CLASS_NAME, params.get(Constants.MDSEvents.ENTITY_CLASS));
        assertEquals(ENTITY_NAME, params.get(Constants.MDSEvents.ENTITY_NAME));
        assertEquals("examplemodule", params.get(Constants.MDSEvents.MODULE_NAME));
        assertEquals(ENTITY_NS, params.get(Constants.MDSEvents.NAMESPACE));
    }
}
