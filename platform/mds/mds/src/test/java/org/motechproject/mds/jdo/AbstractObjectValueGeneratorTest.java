package org.motechproject.mds.jdo;

import org.junit.Test;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.util.PropertyUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractObjectValueGeneratorTest<T> {
    protected abstract AbstractObjectValueGenerator<T> getGenerator();

    protected abstract T getExpectedValue(boolean isNull);

    protected abstract String getPropertyName();

    @Test
    public void shouldHaveCorrectPropertyName() {
        assertEquals(getPropertyName(), getGenerator().getPropertName());
    }

    @Test
    public void shouldReturnCorrectValueWhenObjectIsNull() {
        Object actual = getGenerator().generate(null, null, null);

        assertNotNull(actual);
        assertEquals(getExpectedValue(true), actual);
    }

    @Test
    public void shouldReturnCorrectValueWhenPropertyIsNotSet() {
        Object actual = getGenerator().generate(null, new Record(), null);

        assertNotNull(actual);
        assertEquals(getExpectedValue(true), actual);
    }

    @Test
    public void shouldReturnExistingValue() {
        Record src = new Record();
        PropertyUtil.safeSetProperty(src, getPropertyName(), getExpectedValue(false));

        Object expected = PropertyUtil.safeGetProperty(src, getPropertyName());
        Object actual = getGenerator().generate(null, src, null);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

}
