package org.motechproject.osgi.web.ext;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.service.http.HttpContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationEnvironment.class)
public class HttpContextFactoryTest {

    @Test
    public void shouldCreateFileSystemAwareUiHttpContext() {
        HttpContext httpContext = mock(HttpContext.class);
        PowerMockito.mockStatic(ApplicationEnvironment.class);

        MockBundle bundle = new MockBundle("org.motechproject.com-sms-api-bundle");

        when(ApplicationEnvironment.isInDevelopmentMode()).thenReturn(true);
        when(ApplicationEnvironment.getModulePath(new BundleName("org.motechproject.com-sms-api-bundle"))).thenReturn("/Users/s/project/motech/modules/sms/src/main/resources");

        FileSystemAwareUIHttpContext fileSystemAwareUIHttpContext = (FileSystemAwareUIHttpContext) HttpContextFactory.getHttpContext(httpContext, bundle);
        assertThat(fileSystemAwareUIHttpContext, IsNot.not(notNull()));

        assertThat(fileSystemAwareUIHttpContext.getResourceRootDirectoryPath(), Is.is("/Users/s/project/motech/modules/sms/src/main/resources"));
    }

}
