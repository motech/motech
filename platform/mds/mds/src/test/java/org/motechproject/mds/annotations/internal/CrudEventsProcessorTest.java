package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.TrackingDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CrudEventsProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Mock
    private TrackingDto initialTrackingDto;

    private CrudEventsProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new CrudEventsProcessor();
    }

    @Test
    public void shouldSetEntityCrudEvents() {
        processor.setClazz(Sample.class);
        processor.setTrackingDto(initialTrackingDto);
        processor.execute(bundle);

        verify(initialTrackingDto).setAllowCreateEvent(true);
    }

    @Test
    public void shouldSetAllEntityCrudEventsByDefault() {
        processor.setClazz(RelatedSample.class);
        processor.setTrackingDto(initialTrackingDto);
        processor.execute(bundle);

        verify(initialTrackingDto).setAllowCreateEvent(true);
        verify(initialTrackingDto).setAllowUpdateEvent(true);
        verify(initialTrackingDto).setAllowDeleteEvent(true);
    }

    @Test
    public void shouldNotSetCrudEventsForNoneValue() {
        processor.setClazz(AnotherSample.class);
        processor.setTrackingDto(initialTrackingDto);
        processor.execute(bundle);

        verifyNoMoreInteractions(initialTrackingDto);
    }
}
