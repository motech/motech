package org.motechproject.mds.config;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_DELETE_MODE;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_EMPTY_TRASH;
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

        assertValues(DeleteMode.DELETE, true, 10, TimeUnit.WEEKS);
    }

    @Test
    public void shouldSetDefaultValuesIfStringParameterIsIncorrect() throws Exception {
        settings.setDeleteMode((String) null);
        settings.setEmptyTrash((String) null);
        settings.setTimeValue((String) null);
        settings.setTimeUnit((String) null);

        assertDefaultValues();

        settings.setDeleteMode("  ");
        settings.setEmptyTrash("   ");
        settings.setTimeValue("   ");
        settings.setTimeUnit("   ");

        assertDefaultValues();

        settings.setDeleteMode("del");
        settings.setEmptyTrash("t");
        settings.setTimeValue("alb");
        settings.setTimeUnit("w");

        assertDefaultValues();

        settings.setTimeValue(-1);
        assertEquals(DEFAULT_TIME_VALUE, settings.getTimeValue());
    }

    @Test
    public void shouldCreateAppropriateJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.valueToTree(settings);
        assertValues(
                json.get("deleteMode").asText(), json.get("emptyTrash").asText(),
                json.get("timeValue").asText(), json.get("timeUnit").asText()
        );

        ModuleSettings fromJSON = mapper.readValue(json, ModuleSettings.class);
        assertValues(
                fromJSON.getDeleteMode(), fromJSON.isEmptyTrash(),
                fromJSON.getTimeValue(), fromJSON.getTimeUnit()
        );
    }

    private void assertDefaultValues() {
        assertValues(
                DEFAULT_DELETE_MODE, DEFAULT_EMPTY_TRASH, DEFAULT_TIME_VALUE, DEFAULT_TIME_UNIT
        );
    }

    private void assertValues(String deleteMode, String emptyTrash, String timeValue,
                              String timeUnit) {
        assertValues(
                DeleteMode.fromString(deleteMode), Boolean.parseBoolean(emptyTrash),
                Integer.parseInt(timeValue), TimeUnit.fromString(timeUnit)
        );
    }

    private void assertValues(DeleteMode deleteMode, boolean emptyTrash, Integer timeValue,
                              TimeUnit timeUnit) {
        assertEquals(deleteMode, settings.getDeleteMode());
        assertEquals(emptyTrash, settings.isEmptyTrash());
        assertEquals(timeValue, settings.getTimeValue());
        assertEquals(timeUnit, settings.getTimeUnit());
    }
}
