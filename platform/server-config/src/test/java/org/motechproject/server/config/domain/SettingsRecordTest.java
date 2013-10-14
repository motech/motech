package org.motechproject.server.config.domain;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SettingsRecordTest {

    @Test
    public void shouldReturnUrlWithHttpProtocolIfProtocolNotPresentInitially() {
        SettingsRecord record = new SettingsRecord();
        record.setServerUrl("some.url");
        assertThat(record.getServerUrl(), is("http://some.url"));
    }


    @Test
    public void blankUrlShouldBeReturnedAsBlank() {
        SettingsRecord recordWithNullUrl = new SettingsRecord();
        assertThat(recordWithNullUrl.getServerUrl(), nullValue());

        SettingsRecord recordWithEmptyUrl = new SettingsRecord();
        recordWithEmptyUrl.setServerUrl("  ");
        assertThat(recordWithEmptyUrl.getServerUrl(), is(EMPTY));
    }


    @Test
    public void urlWithProtocolShouldBeReturnedWithoutModification() {
        SettingsRecord settingsRecord = new SettingsRecord();
        settingsRecord.setServerUrl("https://some.other.url");
        assertThat(settingsRecord.getServerUrl(), is("https://some.other.url"));
    }

    @Test
    public void shouldReturnLoginModeByStringSetter() {
        SettingsRecord settingsRecord = new SettingsRecord();
        settingsRecord.setLoginModeValue(LoginMode.REPOSITORY.getName());
        assertThat(settingsRecord.getLoginMode(), is(LoginMode.REPOSITORY));
        assertThat(settingsRecord.getLoginModeValue(), is(LoginMode.REPOSITORY.getName()));
    }

    @Test
    public void shouldReturnLoginModeEmptyForNull() {
        SettingsRecord settingsRecord = new SettingsRecord();
        assertNull(settingsRecord.getLoginModeValue());
    }
}
