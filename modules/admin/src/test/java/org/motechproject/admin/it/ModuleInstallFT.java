package org.motechproject.admin.it;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.HttpStatus;
import org.motechproject.server.commons.PlatformCommons;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ModuleInstallFT extends BasePaxIT {

    private static final String HOST = "localhost";
    private static final int PORT = TestContext.getJettyPort();

    @Inject
    private BundleContext bundleContext;

    @Inject
    private PlatformCommons platformCommons;

    @BeforeClass
    public static void beforeClass() throws Exception {
        createAdminUser();
        login();
    }

    @Test
    public void testUploadBundleFromRepository() throws IOException, InterruptedException {
        uploadBundle("Repository", "org.motechproject:cms-lite:" + platformCommons.getMotechVersion(), null,
                "on","org.motechproject.cms-lite");
    }

    @Test
    public void testUploadAtomClientBundleFromRepository() throws IOException, InterruptedException {
        uploadBundle("Repository", "org.motechproject:atom-client:" + platformCommons.getMotechVersion(), null,
                "on","org.motechproject.atom-client");
    }

    @Test
    public void testUploadBundleFromFile() throws IOException, InterruptedException {
        File file = new File("target/test-bundle/motech-upload-test-bundle.jar");

        uploadBundle("File", null, file, "on", "motech-upload-test-bundle");
    }

    private void uploadBundle(String moduleSource, String moduleId, File bundleFile,
                              String startBundle, String bundleSymbolicName) throws IOException, InterruptedException {
        String uri = String.format("http://%s:%d/admin/api/bundles/upload/", HOST, PORT);

        HttpPost httpPost = new HttpPost(uri);
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        Charset chars = Charset.forName("UTF-8");
        entity.setCharset(chars);
        entity.addTextBody("moduleSource", moduleSource, ContentType.MULTIPART_FORM_DATA);
        switch (moduleSource) {
            case "Repository":
                entity.addTextBody("moduleId", moduleId, ContentType.MULTIPART_FORM_DATA);
                break;
            case "File":
                assertNotNull(bundleFile);
                entity.addBinaryBody("bundleFile", bundleFile);
                break;
            default:
                fail("Wrong module source.");
                break;
        }

        entity.addTextBody("startBundle", startBundle, ContentType.MULTIPART_FORM_DATA);
        httpPost.setEntity(entity.build());
        HttpResponse response = getHttpClient().execute(httpPost);
        EntityUtils.consume(response.getEntity());
        assertEquals(HttpStatus.ORDINAL_200_OK, response.getStatusLine().getStatusCode());

        Bundle uploadedBundle = getBundleFromBundlesArray(bundleSymbolicName);
        assertNotNull(uploadedBundle);
        assertEquals(Bundle.ACTIVE, uploadedBundle.getState());
    }

    private Bundle getBundleFromBundlesArray(String bundleSymbolicName) {
        Bundle[] bundles = bundleContext.getBundles();
        for(Bundle bundle : bundles) {
            if (StringUtils.equals(bundle.getSymbolicName(), bundleSymbolicName)) {
                return bundle;
            }
        }
        return null;
    }
}
