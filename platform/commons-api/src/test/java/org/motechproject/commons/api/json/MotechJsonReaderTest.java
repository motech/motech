package org.motechproject.commons.api.json;

import org.junit.Test;
import org.motechproject.commons.api.MotechException;

public class MotechJsonReaderTest {
    @Test(expected = MotechException.class)
    public void shouldFailGracefullyWhenJsonFileIsNotFound(){
        MotechJsonReader reader = new MotechJsonReader();
        reader.readFromFile("this-file-does-not-exist.json", String.class);
    }
}
