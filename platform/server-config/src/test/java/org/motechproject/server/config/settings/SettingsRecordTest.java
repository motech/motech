package org.motechproject.server.config.settings;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.server.config.domain.SettingsRecord;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SettingsRecordTest {

    @Test
    public void shouldReturnUrlWithHttpProtocolIfProtocolNotPresentInitially() {
        SettingsRecord record = new SettingsRecord();
        record.setServerUrl("some.url");
        assertThat(record.getServerUrl(), Is.is("http://some.url"));
    }


    @Test
    public void blankUrlShouldBeReturnedAsBlank() {
        SettingsRecord recordWithNullUrl = new SettingsRecord();
        assertThat(recordWithNullUrl.getServerUrl(), nullValue());

        SettingsRecord recordWithEmptyUrl = new SettingsRecord();
        recordWithEmptyUrl.setServerUrl("  ");
        assertThat(recordWithEmptyUrl.getServerUrl(), Is.is(EMPTY));
    }


    @Test
    public void urlWithProtocolShouldBeReturnedWithoutModification() {
        SettingsRecord settingsRecord = new SettingsRecord();
        settingsRecord.setServerUrl("https://some.other.url");
        assertThat(settingsRecord.getServerUrl(), Is.is("https://some.other.url"));
    }


}
