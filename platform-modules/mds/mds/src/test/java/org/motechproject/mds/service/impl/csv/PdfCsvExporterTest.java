package org.motechproject.mds.service.impl.csv;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.EntityInfoReader;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.records.Record2;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PdfCsvExporterTest {

    private static final String ENTITY_CLASSNAME = Record2.class.getName();
    private static final String DATA_SERVICE_CLASSNAME = "org.motechproject.mds.test.service.CsvEntityService";
    private static final long ENTITY_ID = 7543;

    private static final DateTime NOW = DateUtil.now();

    @InjectMocks
    private PdfCsvExporter pdfCsvExporter = new PdfCsvExporter();

    @Mock
    private MotechDataService<Record2> dataService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference serviceReference;

    @Mock
    private EntityInfoReader entityInfoReader;

    @Mock
    private EntityInfo entityInfo;

    @Mock
    private AdvancedSettingsDto advancedSettingsDto;

    @Mock
    private BrowsingSettingsDto browsingSettingsDto;

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        MotechClassPool.registerServiceInterface(ENTITY_CLASSNAME, DATA_SERVICE_CLASSNAME);

        when(entityInfoReader.getEntityInfo(ENTITY_CLASSNAME)).thenReturn(entityInfo);
        when(entityInfoReader.getEntityInfo(ENTITY_ID)).thenReturn(entityInfo);

        when(bundleContext.getServiceReference(DATA_SERVICE_CLASSNAME)).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(dataService);

        CsvTestHelper.mockRecord2Fields(entityInfo, advancedSettingsDto, browsingSettingsDto);
    }

    // Uncomment the writePdfToFile() calls to write a temp file with the pdf content
    // TODO: find a better way of verifying PDF content

    @Test
    public void shouldExportPdfNoDataByClassName() {
        long result = pdfCsvExporter.exportPdf(ENTITY_CLASSNAME, output);

        assertEquals(0, result);
        assertNotSame(0, output.size());

        // writePdfToFile();
    }

    @Test
    public void shouldExportPdfNoDataById() {
        long result = pdfCsvExporter.exportPdf(ENTITY_ID, output);

        assertEquals(0, result);
        assertNotSame(0, output.size());

        // writePdfToFile();
    }

    @Test
    public void shouldExportPdfByClassName() {
        setUpTestData();

        long result = pdfCsvExporter.exportPdf(ENTITY_CLASSNAME, output);

        assertEquals(2, result);
        assertNotSame(0, output.size());

        // writePdfToFile();
    }

    @Test
    public void shouldExportPdfById() {
        setUpTestData();

        long result = pdfCsvExporter.exportPdf(ENTITY_ID, output);

        assertEquals(2, result);
        assertNotSame(0, output.size());

        // writePdfToFile();
    }

    @Test
    public void shouldExportPdfWithOneEmptyColumn() {
        setUpTestData(false);

        long result = pdfCsvExporter.exportPdf(ENTITY_ID, output, null, null, singletonList("Value Disp"), null);

        assertEquals(1, result);
        assertNotSame(0, output.size());

        // writePdfToFile();
    }

    private void setUpTestData() {
        setUpTestData(true);
    }

    private void setUpTestData(boolean exportSecondRecord) {
        List<Record2> instances = new ArrayList<>();

        Record2 instance1 = new Record2();
        instance1.setCreationDate(NOW);
        instance1.setModificationDate(NOW.plusMinutes(10));
        instance1.setCreator("motech");
        instance1.setOwner("motech");
        instance1.setDate(NOW.toDate());
        instance1.setDateIgnoredByRest(NOW.plusHours(1).toDate());
        instance1.setValue(null);
        instances.add(instance1);

        if (exportSecondRecord) {
            Record2 instance2 = new Record2();
            instance2.setCreationDate(NOW);
            instance2.setModificationDate(NOW.plusMinutes(20));
            instance2.setCreator("motech2");
            instance2.setOwner("motech2");
            instance2.setDate(NOW.toDate());
            instance2.setDateIgnoredByRest(NOW.plusHours(2).toDate());
            instance2.setValue("SomeVal2");
            instances.add(instance2);
        }

        when(dataService.retrieveAll(null)).thenReturn(instances);
    }

    private void writePdfToFile() {
        try {
            File file = File.createTempFile("pdftest", ".pdf");
            FileUtils.writeByteArrayToFile(file, output.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Unable to write pdf to a file", e);
        }
    }
}
