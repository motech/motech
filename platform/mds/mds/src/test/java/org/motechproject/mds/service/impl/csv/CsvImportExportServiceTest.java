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
import org.motechproject.mds.exception.csv.CsvImportException;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
import org.motechproject.mds.service.DefaultCsvImportCustomizer;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.Constants;
import org.motechproject.server.osgi.event.OsgiEventProxy;

import java.io.OutputStream;
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
    private PdfCsvExporter pdfCsvExporter;

    @Mock
    private OsgiEventProxy osgiEventProxy;

    @Mock
    private Reader reader;

    @Mock
    private Writer writer;

    @Mock
    private OutputStream outputStream;

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
        CsvImportResults importResults = new CsvImportResults(entityDto, NEW_IDS, UPDATED_IDS, null);
        when(csvImporterExporter.importCsv(ENTITY_CLASS_NAME, reader, false)).thenReturn(importResults);

        csvImportExportService.importCsv(ENTITY_CLASS_NAME, reader, FILE_NAME, false);

        verify(csvImporterExporter).importCsv(ENTITY_CLASS_NAME, reader, false);
        verifyImportSuccessEvent();
    }

    @Test
    public void shouldImportInstancesById() {
        CsvImportResults importResults = new CsvImportResults(entityDto, NEW_IDS, UPDATED_IDS, null);
        when(csvImporterExporter.importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class), eq(false), eq(false)))
                .thenReturn(importResults);

        csvImportExportService.importCsv(ENTITY_ID, reader, FILE_NAME, false, false);

        verify(csvImporterExporter).importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class), eq(false), eq(false));
        verifyImportSuccessEvent();
    }

    @Test
    public void shouldClearTableAndImportInstancesById() {
        CsvImportResults importResults = new CsvImportResults(entityDto, NEW_IDS, UPDATED_IDS, null);
        when(csvImporterExporter.importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class), eq(false), eq(true)))
                .thenReturn(importResults);

        csvImportExportService.importCsv(ENTITY_ID, reader, FILE_NAME, false, true);

        verify(csvImporterExporter).importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class), eq(false), eq(true));
        verifyImportSuccessEvent();
    }

    @Test
    public void shouldThrowImportFailureExceptionWhenImportingById() {
        when(csvImporterExporter.importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class), eq(false), eq(false)))
                .thenThrow(new CsvImportException(FAILURE_EX_MSG));
        when(entityService.getEntity(ENTITY_ID)).thenReturn(entityDto);

        boolean thrown = false;
        try {
            csvImportExportService.importCsv(ENTITY_ID, reader, FILE_NAME, false, false);
        } catch (CsvImportException e) {
            thrown = true;
        }
        assertTrue("CSV Import exception was not propagated", thrown);

        verify(csvImporterExporter).importCsv(eq(ENTITY_ID), eq(reader), any(DefaultCsvImportCustomizer.class), eq(false), eq(false));
        verifyImportFailureEvent();
    }

    @Test
    public void shouldThrowImportFailureExceptionWhenImportingByClassName() {
        when(csvImporterExporter.importCsv(ENTITY_CLASS_NAME, reader, false)).thenThrow(new CsvImportException(FAILURE_EX_MSG));
        when(entityService.getEntityByClassName(ENTITY_CLASS_NAME)).thenReturn(entityDto);

        boolean thrown = false;
        try {
            csvImportExportService.importCsv(ENTITY_CLASS_NAME, reader, FILE_NAME, false);
        } catch (CsvImportException e) {
            thrown = true;
        }
        assertTrue("CSV Import exception was not propagated", thrown);

        verify(csvImporterExporter).importCsv(ENTITY_CLASS_NAME, reader, false);
        verifyImportFailureEvent();
    }

    @Test
    public void ShouldExportCsvById() {
        when(csvImporterExporter.exportCsv(ENTITY_ID, writer)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_ID, writer));
        verify(csvImporterExporter).exportCsv(ENTITY_ID, writer);
    }

    @Test
    public void ShouldExportCsvWithACustomizer() {
        CsvExportCustomizer exportCustomizer = new DefaultCsvExportCustomizer();
        when(csvImporterExporter.exportCsv(eq(ENTITY_ID), eq(writer), any(CsvExportCustomizer.class)))
                .thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_ID, writer, exportCustomizer));
        verify(csvImporterExporter).exportCsv(ENTITY_ID, writer, exportCustomizer);
    }

    @Test
    public void ShouldExportCsvWithACustomizerByClassName() {
        CsvExportCustomizer exportCustomizer = new DefaultCsvExportCustomizer();
        when(csvImporterExporter.exportCsv(eq(ENTITY_CLASS_NAME), eq(writer), any(CsvExportCustomizer.class)))
                .thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_CLASS_NAME, writer, exportCustomizer));
        verify(csvImporterExporter).exportCsv(ENTITY_CLASS_NAME, writer, exportCustomizer);
    }


    @Test
    public void ShouldExportCsvByClassNameId() {
        when(csvImporterExporter.exportCsv(ENTITY_CLASS_NAME, writer)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_CLASS_NAME, writer));
        verify(csvImporterExporter).exportCsv(ENTITY_CLASS_NAME, writer);
    }

    @Test
    public void ShouldExportCsvWithParameters() {
        when(csvImporterExporter.exportCsv(ENTITY_ID, writer, "lookup", null, null, null)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_ID, writer, "lookup", null, null, null));
        verify(csvImporterExporter).exportCsv(ENTITY_ID, writer, "lookup", null, null, null);
    }

    @Test
    public void ShouldExportCsvWithParametersByClassName() {
        when(csvImporterExporter.exportCsv(ENTITY_CLASS_NAME, writer, "lookup", null, null, null)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportCsv(ENTITY_CLASS_NAME, writer, "lookup", null, null, null));
        verify(csvImporterExporter).exportCsv(ENTITY_CLASS_NAME, writer, "lookup", null, null, null);
    }

    @Test
    public void ShouldExportPdfById() {
        when(pdfCsvExporter.exportPdf(ENTITY_ID, outputStream)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportPdf(ENTITY_ID, outputStream));
        verify(pdfCsvExporter).exportPdf(ENTITY_ID, outputStream);
    }

    @Test
    public void ShouldExportPdfWithACustomizer() {
        CsvExportCustomizer exportCustomizer = new DefaultCsvExportCustomizer();
        when(pdfCsvExporter.exportPdf(eq(ENTITY_ID), eq(outputStream), any(CsvExportCustomizer.class)))
                .thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportPdf(ENTITY_ID, outputStream, exportCustomizer));
        verify(pdfCsvExporter).exportPdf(ENTITY_ID, outputStream, exportCustomizer);
    }

    @Test
    public void ShouldExportPdfWithACustomizerByClassName() {
        CsvExportCustomizer exportCustomizer = new DefaultCsvExportCustomizer();
        when(pdfCsvExporter.exportPdf(eq(ENTITY_CLASS_NAME), eq(outputStream), any(CsvExportCustomizer.class)))
                .thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportPdf(ENTITY_CLASS_NAME, outputStream, exportCustomizer));
        verify(pdfCsvExporter).exportPdf(ENTITY_CLASS_NAME, outputStream, exportCustomizer);
    }


    @Test
    public void ShouldExportPdfByClassNameId() {
        when(pdfCsvExporter.exportPdf(ENTITY_CLASS_NAME, outputStream)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportPdf(ENTITY_CLASS_NAME, outputStream));
        verify(pdfCsvExporter).exportPdf(ENTITY_CLASS_NAME, outputStream);
    }

    @Test
    public void ShouldExportPdfWithParameters() {
        when(pdfCsvExporter.exportPdf(ENTITY_ID, outputStream, "lookup", null, null, null)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportPdf(ENTITY_ID, outputStream, "lookup", null, null, null));
        verify(pdfCsvExporter).exportPdf(ENTITY_ID, outputStream, "lookup", null, null, null);
    }

    @Test
    public void ShouldExportPdfWithParametersByClassName() {
        when(pdfCsvExporter.exportPdf(ENTITY_CLASS_NAME, outputStream, "lookup", null, null, null)).thenReturn(EXPORTED_COUNT);
        assertEquals(EXPORTED_COUNT, csvImportExportService.exportPdf(ENTITY_CLASS_NAME, outputStream, "lookup", null, null, null));
        verify(pdfCsvExporter).exportPdf(ENTITY_CLASS_NAME, outputStream, "lookup", null, null, null);
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
