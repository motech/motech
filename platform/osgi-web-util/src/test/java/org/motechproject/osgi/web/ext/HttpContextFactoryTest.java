package org.motechproject.osgi.web.ext;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.osgi.service.http.HttpContext;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpContextFactoryTest {


    @Test
    public void shouldCreateFileSystemAwareUiHttpContext() {
        HttpContext httpContext = mock(HttpContext.class);
        ApplicationEnvironment applicationEnvironment = mock(ApplicationEnvironment.class);

        MockBundle bundle = new MockBundle("org.motechproject.com-sms-api-bundle");

        when(applicationEnvironment.isInDevelopmentMode()).thenReturn(true);
        when(applicationEnvironment.getModulePath(new BundleName("org.motechproject.com-sms-api-bundle"))).thenReturn("/Users/s/project/motech/modules/sms/src/main/resources");

        FileSystemAwareUIHttpContext fileSystemAwareUIHttpContext = (FileSystemAwareUIHttpContext) HttpContextFactory.getHttpContext(httpContext, bundle, applicationEnvironment);
        assertThat(fileSystemAwareUIHttpContext, IsNot.not(notNull()));

        assertThat(fileSystemAwareUIHttpContext.getResourceRootDirectoryPath(), Is.is("/Users/s/project/motech/modules/sms/src/main/resources"));
    }


}
