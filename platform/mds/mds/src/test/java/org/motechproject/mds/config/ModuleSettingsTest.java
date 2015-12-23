package org.motechproject.mds.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_BUNDLE_RESTART;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_DELETE_MODE;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_EMPTY_TRASH;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_GRID_SIZE;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_TIME_UNIT;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_TIME_VALUE;

public class ModuleSettingsTest {
    private ModuleSettings settings = new ModuleSettings();

    @Test
    public void shouldContainsDefaultValues() throws Exception {
        assertDefaultValues();
    }

    @Test
    public void shouldContainsDefinedValues() throws Exception {
        settings.setDeleteMode(DeleteMode.DELETE);
        settings.setEmptyTrash(true);
        settings.setTimeValue(10);
        settings.setTimeUnit(TimeUnit.WEEKS);
        settings.setGridSize(100);
        settings.setRestartModuleAfterTimeout(true);

        assertValues(DeleteMode.DELETE, true, 10, TimeUnit.WEEKS, 100, true);
    }

    @Test
    public void shouldSetDefaultValuesIfStringParameterIsIncorrect() throws Exception {
        settings.setDeleteMode(null);
        settings.setEmptyTrash(null);
        settings.setTimeValue(null);
        settings.setTimeUnit(null);
        settings.setGridSize(null);
        settings.setRestartModuleAfterTimeout(null);

        assertDefaultValues();

        settings.setTimeValue(-1);
        assertEquals(DEFAULT_TIME_VALUE, settings.getTimeValue());
    }

    private void assertDefaultValues() {
        assertValues(
                DEFAULT_DELETE_MODE, DEFAULT_EMPTY_TRASH, DEFAULT_TIME_VALUE, DEFAULT_TIME_UNIT, DEFAULT_GRID_SIZE, DEFAULT_BUNDLE_RESTART
        );
    }

    private void assertValues(DeleteMode deleteMode, boolean emptyTrash, Integer timeValue,
                              TimeUnit timeUnit, Integer gridSize, boolean restartBundle) {
        assertEquals(deleteMode, settings.getDeleteMode());
        assertEquals(emptyTrash, settings.isEmptyTrash());
        assertEquals(timeValue, settings.getTimeValue());
        assertEquals(timeUnit, settings.getTimeUnit());
        assertEquals(gridSize, settings.getGridSize());
        assertEquals(restartBundle, settings.isRestartModuleAfterTimeout());
    }
}
