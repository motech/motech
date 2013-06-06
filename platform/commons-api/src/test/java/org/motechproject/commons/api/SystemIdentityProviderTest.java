package org.motechproject.commons.api;

import org.junit.Test;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SystemIdentityProviderTest {

    @Test
    public void shouldReturnNonBlankId() {
        String identity = new SystemIdentityProvider().getIdentity();
        assertThat(isNotBlank(identity), is(true));
    }
}
