package org.motechproject.mds.query;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class PropertyTest {

    protected abstract Property getProperty();

    protected abstract int getIdx();

    protected abstract String expectedFilter();

    protected abstract String expectedDeclareParameter();

    protected abstract Collection expectedUnwrap();

    protected boolean ignoresNull() {
        return true;
    }

    @Test
    public void shouldGenerateAppropriateFilter() throws Exception {
        assertEquals(expectedFilter(), getProperty().asFilter(getIdx()));
    }

    @Test
    public void shouldGenerateAppropriateDeclareParameter() throws Exception {
        assertEquals(expectedDeclareParameter(), getProperty().asDeclareParameter(getIdx()));
    }

    @Test
    public void shouldUnwrap() throws Exception {
        assertEquals(expectedUnwrap(), getProperty().unwrap());
    }

    @Test
    public void shouldHandleNullValues() throws Exception {
        Property property = Mockito.spy(getProperty());
        Mockito.doReturn(null).when(property).getValue();

        if (ignoresNull()) {
            assertNull(property.asFilter(getIdx()));
            assertNull(property.asDeclareParameter(getIdx()));
            assertNull(property.unwrap());
        } else {
            assertEquals(expectedFilter(), property.asFilter(getIdx()));
            assertEquals(expectedDeclareParameter(), property.asDeclareParameter(getIdx()));
            assertNotNull(property.unwrap());
            assertTrue(property.unwrap().size() == 1);
            assertNull(property.unwrap().iterator().next());
        }
    }

}
