package org.motechproject.commons.api.json;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.model.MotechProperties;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class MotechJsonReaderTest {
    private MotechJsonReader reader;

    @Before
    public void setUp() {
        reader = new MotechJsonReader();
    }

    @Test(expected = MotechException.class)
    public void shouldFailGracefullyWhenJsonFileIsNotFound(){
        reader.readFromFile("this-file-does-not-exist.json", String.class);
    }

    @Test
    public void shouldParseMotechProperties() {
        MotechProperties motechProperties = (MotechProperties) reader.readFromFile("/motech-properties.json", MotechProperties.class);

        assertThat(motechProperties.size(), IsEqual.equalTo(14));
        assertThat(motechProperties.get("case_name"), IsEqual.equalTo("Pankaja"));
        assertThat(motechProperties.get("family_number"), IsEqual.equalTo(""));
        assertThat(motechProperties.get("external_id"), IsNull.nullValue());
    }

    @Test
    public void shouldSkipMotechPropertyThatHasJsonObjectAsValue() {
        MotechProperties motechProperties = (MotechProperties) reader.readFromFile("/complex-motech-properties.json", MotechProperties.class);

        assertThat(motechProperties.size(), IsEqual.equalTo(5));
        assertThat(motechProperties, not(Matchers.<String, String>hasKey("edd")));
        assertThat(motechProperties, not(Matchers.<String,String>hasKey("next_due")));
        assertThat(motechProperties, not(Matchers.<String,String>hasKey("next_form")));
        assertThat(motechProperties, not(Matchers.<String,String>hasKey("partner_name")));
        assertThat(motechProperties, not(Matchers.<String,String>hasKey("village")));
    }
}