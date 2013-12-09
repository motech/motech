package org.motechproject.config.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.config.domain.ModulePropertiesRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllModulePropertiesTest {

    @Mock
    CouchDbConnector db;

    private AllModuleProperties allModuleProperties;

    @Before
    public void setUp() {
        initMocks(this);
        allModuleProperties = new AllModuleProperties(db);
    }

    @Test
    public void shouldBulkAddOrUpdate() {
        final List<ModulePropertiesRecord> records = asList(new ModulePropertiesRecord());
        allModuleProperties.bulkAddOrUpdate(records);
        verify(db).executeBulk(records);
    }

    @Test
    public void shouldNotBulkAddOrUpdate() {
        final List<ModulePropertiesRecord> records = Collections.emptyList();
        allModuleProperties.bulkAddOrUpdate(records);
        verify(db, never()).executeBulk((Collection<?>) any());
    }

    @Test
    public void shouldBulkDelete() {
        final ModulePropertiesRecord record = new ModulePropertiesRecord();
        record.setId("1");
        final List<ModulePropertiesRecord> records = asList(record);
        allModuleProperties.bulkDelete(records);
        verify(db).executeBulk(Arrays.asList(BulkDeleteDocument.of(record)));
    }

    @Test
    public void shouldNotBulkDelete() {
        final List<ModulePropertiesRecord> records = Collections.emptyList();
        allModuleProperties.bulkDelete(records);
        verify(db, never()).executeBulk((Collection<?>) any());
    }
}
