package org.motechproject.commons.api;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class TenantTest {


    @Test
    public void shouldCreateTenant() {
        assertNotNull(Tenant.current());
    }


    @Test
    public void shouldIndicateIfTenantHasQueueWithGivenName() {
        IdentityProvider identityProvider = mock(IdentityProvider.class);
        given(identityProvider.getIdentity()).willReturn("foo");
        TenantIdentity tenantIdentity = new TenantIdentity(identityProvider);
        Tenant foo = new Tenant(tenantIdentity);
        assertThat(foo.canHaveQueue("foo_queue"), Is.is(true));
        assertThat(foo.canHaveQueue(null), Is.is(false));
        assertThat(foo.canHaveQueue(""), Is.is(false));
        assertThat(foo.canHaveQueue("bar_queue"), Is.is(false));
    }

    @Test
    public void shouldReturnSuffixedId(){
        IdentityProvider identityProvider = mock(IdentityProvider.class);
        given(identityProvider.getIdentity()).willReturn("foo");
        TenantIdentity tenantIdentity = new TenantIdentity(identityProvider);
        Tenant tenant = new Tenant(tenantIdentity);
        String suffixedId  = tenant.getSuffixedId();
        assertThat(suffixedId,Is.is("foo_"));
    }
}
