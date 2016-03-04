package org.motechproject.mds.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.config.DeleteMode.DELETE;
import static org.motechproject.mds.config.DeleteMode.TRASH;
import static org.motechproject.mds.config.DeleteMode.UNKNOWN;
import static org.motechproject.mds.config.DeleteMode.fromString;

public class DeleteModeTest {

    @Test
    public void shouldConvertStringToAppopriateDeleteMode() throws Exception {
        assertMode(DELETE, "DELETE", "delete", "DeLeTe", "dElEtE", "delETE", "DELete");
        assertMode(TRASH, "TRASH", "trash", "TrAsH", "tRaSh", "traSH", "TRAsh");
        assertMode(UNKNOWN, "UNKNOWN", "unknown", "UnKnOwN", "uNkNoWn", "unkNOWN", "UNKnown");

        // for other vlues the UNKNOWN mode should be returned
        assertMode(UNKNOWN, "     ", "", null, "string", "some value", "del", "tra");
    }

    private void assertMode(DeleteMode mode, String... values) {
        for (String value : values) {
            assertEquals(mode, fromString(value));
        }
    }

}
