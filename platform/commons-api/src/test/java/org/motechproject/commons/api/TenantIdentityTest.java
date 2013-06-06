package org.motechproject.commons.api;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class TenantIdentityTest {

    @Test
    public void shouldMakeTenantIdLowerCase() {
        IdentityProvider identityProvider = mock(IdentityProvider.class);
        given(identityProvider.getIdentity()).willReturn("SomeIdenTiTY");

        TenantIdentity tenantIdentity = new TenantIdentity(identityProvider);
        String id = tenantIdentity.getId();
        assertThat(id, Is.is("someidentity"));
    }

}
