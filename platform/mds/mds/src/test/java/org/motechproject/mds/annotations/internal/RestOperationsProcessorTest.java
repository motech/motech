package org.motechproject.mds.annotations.internal;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.annotations.internal.samples.AnotherSample;
import org.motechproject.mds.annotations.internal.samples.RelatedSample;
import org.motechproject.mds.annotations.internal.samples.Sample;
import org.motechproject.mds.dto.RestOptionsDto;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RestOperationsProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    private RestOperationsProcessor processor;

    private RestOptionsDto restOptions;

    @Before
    public void setUp() throws Exception {
        processor = new RestOperationsProcessor();
        restOptions = new RestOptionsDto();
        processor.setRestOptions(restOptions);
    }

    @Test
    public void shouldSetEntityRestOperations() {
        processor.setClazz(Sample.class);
        processor.execute(bundle);

        assertEquals(restOptions.isCreate(), false);
        assertEquals(restOptions.isRead(), false);
        assertEquals(restOptions.isUpdate(), false);
        assertEquals(restOptions.isDelete(), true);
    }

    @Test
    public void shouldSetAllEntityRestOperations() {
        processor.setClazz(RelatedSample.class);
        processor.execute(bundle);

        assertEquals(restOptions.isCreate(), true);
        assertEquals(restOptions.isRead(), true);
        assertEquals(restOptions.isUpdate(), true);
        assertEquals(restOptions.isDelete(), true);
    }

    @Test
    public void shouldNotSetRestOperationsForMissingValue() {
        processor.setClazz(AnotherSample.class);
        processor.execute(bundle);

        assertEquals(restOptions.isCreate(), false);
        assertEquals(restOptions.isRead(), false);
        assertEquals(restOptions.isUpdate(), false);
        assertEquals(restOptions.isDelete(), false);
    }
}
