package org.motechproject.server.config.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Properties;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ConfigFileSettingsTest {

    @Test
    public void shouldReturnUrlWithHttpProtocolIfProtocolNotPresentInitially() {
        Properties motechSettings = new Properties();
        motechSettings.put(MotechSettings.SERVER_URL,"some.url");
        ConfigFileSettings settings = new ConfigFileSettings(motechSettings, new Properties());
        assertThat(settings.getServerUrl(), Is.is("http://some.url"));
    }

    @Test
    public void shouldReturnLoginMode() {
        Properties motechSettings = new Properties();
        motechSettings.put(MotechSettings.LOGINMODE, LoginMode.OPEN_ID.getName());
        ConfigFileSettings settings = new ConfigFileSettings(motechSettings, new Properties());
        assertThat(settings.getLoginMode(), Is.is(LoginMode.OPEN_ID));
    }

    @Test
    public void blankUrlShouldBeReturnedAsBlank() {
        ConfigFileSettings settingsWithNullUrl = new ConfigFileSettings();
        assertThat(settingsWithNullUrl.getServerUrl(), nullValue());

        Properties motechSettings = new Properties();
        motechSettings.put(MotechSettings.SERVER_URL,"  ");

        ConfigFileSettings settingsWithEmptyUrl = new ConfigFileSettings(motechSettings,new Properties());
        assertThat(settingsWithEmptyUrl.getServerUrl(), Is.is(EMPTY));
    }


    @Test
    public void urlWithProtocolShouldBeReturnedWithoutModification() {
        Properties motechSettings = new Properties();
        motechSettings.put(MotechSettings.SERVER_URL,"ftp://some.other.url");

        ConfigFileSettings settingsRecord = new ConfigFileSettings(motechSettings,new Properties());
        assertThat(settingsRecord.getServerUrl(), Is.is("ftp://some.other.url"));
    }


}
