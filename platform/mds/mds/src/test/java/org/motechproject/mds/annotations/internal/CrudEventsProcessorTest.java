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
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.service.EntityService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrudEventsProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Spy
    private TrackingDto trackingDto = new TrackingDto();

    @Mock
    private EntityService entityService;

    @Mock
    private AdvancedSettingsDto advancedSettingsDto;

    @Mock
    private EntityDto entity;

    @Captor
    private ArgumentCaptor<TrackingDto> trackingDtoCaptor;

    private CrudEventsProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new CrudEventsProcessor();
        processor.setEntityService(entityService);
    }

    @Test
    public void shouldSetEntityCrudEvents() {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getTracking()).thenReturn(trackingDto);

        processor.setClazz(Sample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateTracking(eq(1L), trackingDtoCaptor.capture());
        TrackingDto tracking = trackingDtoCaptor.getValue();

        assertEquals(tracking.isAllowCreateEvent(), true);
        assertEquals(tracking.isAllowUpdateEvent(), false);
        assertEquals(tracking.isAllowDeleteEvent(), false);
    }

    @Test
    public void shouldSetAllEntityCrudEventsByDefault() {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getTracking()).thenReturn(trackingDto);

        processor.setClazz(RelatedSample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateTracking(eq(1L), trackingDtoCaptor.capture());
        TrackingDto tracking = trackingDtoCaptor.getValue();

        assertEquals(tracking.isAllowCreateEvent(), true);
        assertEquals(tracking.isAllowUpdateEvent(), true);
        assertEquals(tracking.isAllowDeleteEvent(), true);
    }

    @Test
    public void shouldNotSetCrudEventsForNoneValue() {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getTracking()).thenReturn(trackingDto);

        processor.setClazz(AnotherSample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateTracking(eq(1L), trackingDtoCaptor.capture());
        TrackingDto tracking = trackingDtoCaptor.getValue();

        assertEquals(tracking.isAllowCreateEvent(), false);
        assertEquals(tracking.isAllowUpdateEvent(), false);
        assertEquals(tracking.isAllowDeleteEvent(), false);
    }
}
