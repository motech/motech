package org.motechproject.admin.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class TenantTest {


    @Test
    public void shouldCreateTenant() {
        assertNotNull(Tenant.current());
    }


    @Test
    public void shouldIndicateIfTenantHasQueueWithGivenName() {
        Tenant foo = new Tenant("foo");
        assertThat(foo.canHaveQueue("foo_queue"), Is.is(true));
        assertThat(foo.canHaveQueue(null), Is.is(false));
        assertThat(foo.canHaveQueue(""), Is.is(false));
        assertThat(foo.canHaveQueue("bar_queue"), Is.is(false));
    }

}
