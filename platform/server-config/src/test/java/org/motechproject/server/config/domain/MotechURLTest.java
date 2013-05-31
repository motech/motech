package org.motechproject.server.config.domain;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class MotechURLTest {

    @Test
    public void shouldReturnUrlWithHttpProtocolIfProtocolNotPresentInitially() {
        MotechURL record = new MotechURL("some.url");
        assertThat(record.toString(), Is.is("http://some.url"));
    }

    @Test
    public void shouldRetainTheProtocolIfPresent() {
        MotechURL record = new MotechURL("xyz://some.url");
        assertThat(record.toString(), Is.is("xyz://some.url"));
    }


    @Test
    public void blankUrlShouldBeReturnedAsBlank() {
        MotechURL nullUrl = new MotechURL(null);
        assertThat(nullUrl.toString(), nullValue());

        MotechURL emptyUrl = new MotechURL("  ");
        assertThat(emptyUrl.toString(), Is.is(EMPTY));
    }


    @Test
    public void urlShouldBeTrimmed() {
        MotechURL url = new MotechURL(" https://some.other.url ");
        assertThat(url.toString(), Is.is("https://some.other.url"));
    }

    @Test
    public void shouldReturnHostFromURL() {
        assertThat(new MotechURL("https://motech.org/some/path").getHost(), Is.is("motech.org"));
        assertThat(new MotechURL("https://motechorg/").getHost(), Is.is("motechorg"));
        assertThat(new MotechURL("https://google.co.uk").getHost(), Is.is("google.co.uk"));
        assertThat(new MotechURL("").getHost(), IsNull.nullValue());
        assertThat(new MotechURL(null).getHost(), IsNull.nullValue());
    }
}
