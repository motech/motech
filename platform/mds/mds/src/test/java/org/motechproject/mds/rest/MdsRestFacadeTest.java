package org.motechproject.mds.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.util.Order;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsRestFacadeTest {

    @Mock
    private AllEntities allEntities;

    @Mock
    private Entity entity;

    @Mock
    private MotechDataService<Record> dataService;

    @Mock
    private RestOptions restOptions;

    @Mock
    private RestOptionsDto restOptionsDto;

    @InjectMocks
    private MdsRestFacadeImpl<Record> mdsRestFacade = new MdsRestFacadeImpl<>();

    @Before
    public void setUp() {
        when(dataService.getClassType()).thenReturn(Record.class);
        when(allEntities.retrieveByClassName(Record.class.getName())).thenReturn(entity);
        when(entity.getRestOptions()).thenReturn(restOptions);
        when(restOptions.toDto()).thenReturn(restOptionsDto);
        mdsRestFacade.init();
    }

    @Test
    public void shouldDoReadOperations() {
        setUpCrudAccess(false, true, false, false);
        Record record = mock(Record.class);
        when(dataService.retrieveAll(any(QueryParams.class)))
                .thenReturn(asList(record));

        List<Record> result = mdsRestFacade.get(new QueryParams(5, 20,
                new Order("value", Order.Direction.DESC)));

        assertEquals(asList(record), result);

        ArgumentCaptor<QueryParams> captor = ArgumentCaptor.forClass(QueryParams.class);
        verify(dataService).retrieveAll(captor.capture());

        assertNotNull(captor.getValue());
        assertEquals(Integer.valueOf(5), captor.getValue().getPage());
        assertEquals(Integer.valueOf(20), captor.getValue().getPageSize());
        assertNotNull(captor.getValue().getOrder());
        assertEquals("value", captor.getValue().getOrder().getField());
        assertEquals(Order.Direction.DESC, captor.getValue().getOrder().getDirection());
    }

    @Test
    public void shouldDoCreateOperation() {
        setUpCrudAccess(true, false, false, false);
        Record record = mock(Record.class);

        mdsRestFacade.create(record);

        verify(dataService).create(record);
    }

    @Test(expected = RestOperationNotSupportedException.class)
    public void shouldThrowExceptionForUnsupportedCreate() {
        setUpCrudAccess(false, true, true, true);
        mdsRestFacade.create(mock(Record.class));
    }

    @Test(expected = RestOperationNotSupportedException.class)
    public void shouldThrowExceptionForUnsupportedRead() {
        setUpCrudAccess(true, false, true, true);
        mdsRestFacade.get(new QueryParams(1, 10));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfRestNotSupportedByEntity() {
        when(entity.getRestOptions()).thenReturn(null);
        mdsRestFacade.init();
    }

    private void setUpCrudAccess(boolean allowCreate, boolean allowRead,
                                 boolean allowUpdate, boolean allowDelete) {
        when(restOptionsDto.isCreate()).thenReturn(allowCreate);
        when(restOptionsDto.isRead()).thenReturn(allowRead);
        when(restOptionsDto.isUpdate()).thenReturn(allowUpdate);
        when(restOptionsDto.isDelete()).thenReturn(allowDelete);
    }
}
