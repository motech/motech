package org.motechproject.osgi.web.ext;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class BundleNameTest {

    @Test
    public void shouldUnderscoreBundleName() {
        String underscoredBundleName = new BundleName("org.motechproject.com-sms-api-bundle").underscore();
        assertThat(underscoredBundleName, Is.is("org_motechproject_com_sms_api_bundle"));
    }

    @Test
    public void shouldTestBundleNameEquality() {
        assertThat(new BundleName("bundle-a"), Is.is(new BundleName("bundle-a")));
        assertThat(new BundleName("bundle-a"), IsNot.not(new BundleName("bundle-b")));
    }

}
