package org.motechproject.mds.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClassNameTest {

    @Test
    public void shouldTrimHistoryTrashClassNamesCorrectly() {
        assertEquals("org.mydomain.domain.TestClass",
                ClassName.trimTrashHistorySuffix("org.mydomain.domain.history.TestClass__History"));
        assertEquals("org.mydomain.domain.TestClass",
                ClassName.trimTrashHistorySuffix("org.mydomain.domain.history.TestClass__Trash"));
        assertEquals("org.motechproject.entities.GGGTtest",
                ClassName.trimTrashHistorySuffix("org.motechproject.entities.history.GGGTtest__History"));
        assertEquals("org.motechproject.entities.GGGTtest",
                ClassName.trimTrashHistorySuffix("org.motechproject.entities.history.GGGTtest__Trash"));
    }

    @Test
    public void shouldNotTrimRegularClassNames() {
        assertNull(ClassName.trimTrashHistorySuffix(null));
        assertEquals("", ClassName.trimTrashHistorySuffix(""));
        assertEquals("org.motechproject.domain.Test", ClassName.trimTrashHistorySuffix("org.motechproject.domain.Test"));
        assertEquals("org.motechproject.domain.history.TestHistory",
                ClassName.trimTrashHistorySuffix("org.motechproject.domain.history.TestHistory"));
        assertEquals("org.motechproject.domain.history.TestTrash",
                ClassName.trimTrashHistorySuffix("org.motechproject.domain.history.TestTrash"));
    }
}
