package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.service.EntityService;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestOperationsProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Spy
    private RestOptionsDto restOptionsDto = new RestOptionsDto();

    @Mock
    private EntityService entityService;

    @Mock
    private AdvancedSettingsDto advancedSettingsDto;

    @Mock
    private EntityDto entity;

    @Captor
    private ArgumentCaptor<RestOptionsDto> restOptionsDtoCaptor;

    private RestOperationsProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new RestOperationsProcessor();
        processor.setEntityService(entityService);
    }

    @Test
    public void shouldSetEntityRestOperations() {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getRestOptions()).thenReturn(restOptionsDto);

        processor.setClazz(Sample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateRestOptions(eq(1L), restOptionsDtoCaptor.capture());
        RestOptionsDto restOptions = restOptionsDtoCaptor.getValue();

        assertEquals(restOptions.isCreate(), false);
        assertEquals(restOptions.isRead(), false);
        assertEquals(restOptions.isUpdate(), false);
        assertEquals(restOptions.isDelete(), true);
    }

    @Test
    public void shouldSetAllEntityRestOperations() {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getRestOptions()).thenReturn(restOptionsDto);

        processor.setClazz(RelatedSample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateRestOptions(eq(1L), restOptionsDtoCaptor.capture());
        RestOptionsDto restOptions = restOptionsDtoCaptor.getValue();

        assertEquals(restOptions.isCreate(), true);
        assertEquals(restOptions.isRead(), true);
        assertEquals(restOptions.isUpdate(), true);
        assertEquals(restOptions.isDelete(), true);
    }

    @Test
    public void shouldNotSetRestOperationsForMissingValue() {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getRestOptions()).thenReturn(restOptionsDto);

        processor.setClazz(AnotherSample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateRestOptions(eq(1L), restOptionsDtoCaptor.capture());
        RestOptionsDto restOptions = restOptionsDtoCaptor.getValue();

        assertEquals(restOptions.isCreate(), false);
        assertEquals(restOptions.isRead(), false);
        assertEquals(restOptions.isUpdate(), false);
        assertEquals(restOptions.isDelete(), false);
    }
}
