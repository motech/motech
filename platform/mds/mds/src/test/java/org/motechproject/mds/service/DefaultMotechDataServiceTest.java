package org.motechproject.mds.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RecordRelation;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.testutil.records.RelatedRecord;
import org.motechproject.mds.testutil.records.history.Record__History;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.SecurityMode;
import org.springframework.context.ApplicationContext;

import java.util.Date;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMotechDataServiceTest {

    private static final long INSTANCE_ID = 77;
    private static final long HISTORY_ID = 40;
    private static final long SCHEMA_VERSION = 7;

    private static final Date DATE = new Date();
    private static final String STR_VAL = "strVal";

    @InjectMocks
    private DefaultMotechDataService<Record> dataService = new DefaultMotechDataService<Record>() {};

    @Mock
    private MotechDataRepository<Record> repository;

    @Mock
    private HistoryService historyService;

    @Mock
    private Entity entity;

    @Mock
    private AllEntities allEntities;

    @Mock
    private MotechDataService<RelatedRecord> relatedDataService;

    @Mock
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        when(repository.getClassType()).thenReturn(Record.class);
        when(allEntities.retrieveByClassName(Record.class.getName())).thenReturn(entity);
        when(entity.getSecurityMode()).thenReturn(SecurityMode.EVERYONE);
        when(entity.getEntityVersion()).thenReturn(SCHEMA_VERSION);
        when(applicationContext.containsBean(RelatedRecord.class.getName() + "DataService")).
                thenReturn(true);
        when(applicationContext.getBean(RelatedRecord.class.getName() + "DataService")).
                thenReturn(relatedDataService);

        dataService.init();
    }

    @Test
    public void shouldRevertToHistoricalVersions() {
        setUpRecords();

        dataService.revertToPreviousVersion(INSTANCE_ID, HISTORY_ID);

        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        verify(repository).update(captor.capture());
        Record updatedValue = captor.getValue();

        assertEquals(Long.valueOf(INSTANCE_ID), updatedValue.getId());
        assertEquals(STR_VAL, updatedValue.getValue());
        assertEquals(DATE, updatedValue.getDate());
        assertEquals(asList(STR_VAL + 1, STR_VAL + 2),
                extract(updatedValue.getRelatedValues(), on(RelatedRecord.class).getName()));
        assertEquals(asList(11, 12),
                extract(updatedValue.getRelatedValues(), on(RelatedRecord.class).getVal()));
        assertEquals(asList(1l, 2l),
                extract(updatedValue.getRelatedValues(), on(RelatedRecord.class).getId()));
        assertNotNull(updatedValue.getRelatedSingleValue());
        assertEquals(STR_VAL + 4, updatedValue.getRelatedSingleValue().getName());
        assertEquals(Integer.valueOf(14), updatedValue.getRelatedSingleValue().getVal());
        assertEquals(Long.valueOf(4), updatedValue.getRelatedSingleValue().getId());
    }

    private void setUpRecords() {
        Record record = new Record();
        record.setDate(new DateTime(DATE).plusDays(21).toDate());
        record.setValue("toDiscard");
        record.setId(INSTANCE_ID);

        Record__History historyRecord = new Record__History();
        historyRecord.setId(INSTANCE_ID);
        historyRecord.setValue(STR_VAL);
        historyRecord.setDate(DATE);
        historyRecord.setRecord__HistorySchemaVersion(SCHEMA_VERSION);

        // 1 : M

        RelatedRecord relatedRecord1 = new RelatedRecord(STR_VAL + 1, 11, 1l);
        RelatedRecord relatedRecord2 = new RelatedRecord(STR_VAL + 2, 12, 2l);
        RecordRelation recordRelation1 = new RecordRelation(1l, RelatedRecord.class.getName());
        RecordRelation recordRelation2 = new RecordRelation(2l, RelatedRecord.class.getName());
        // this related record will be ignored since it no longer exists
        RecordRelation recordRelation3 = new RecordRelation(3l, RelatedRecord.class.getName());

        historyRecord.setRelatedValues(asList(recordRelation1, recordRelation2, recordRelation3));

        when(relatedDataService.findById(1l)).thenReturn(relatedRecord1);
        when(relatedDataService.findById(2l)).thenReturn(relatedRecord2);

        // 1 : 1

        RelatedRecord singleRelatedRecord = new RelatedRecord(STR_VAL + 4, 14, 4l);
        RecordRelation singleRecordRelation = new RecordRelation(4l, RelatedRecord.class.getName());

        historyRecord.setRelatedSingleValue(singleRecordRelation);

        when(relatedDataService.findById(4l)).thenReturn(singleRelatedRecord);

        when(repository.retrieve(Constants.Util.ID_FIELD_NAME, INSTANCE_ID))
                .thenReturn(record);
        when(historyService.getSingleHistoryInstance(record, HISTORY_ID))
                .thenReturn(historyRecord);
    }
}
