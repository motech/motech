package org.motechproject.admin.bundles;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BundleDirectoryManagerTest {

    private static final String FILENAME = "bundleDirManagerTest.jar";
    private static final String CONTENT = "Test content!";

    private static String tmpDir;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Bundle bundle;

    private BundleDirectoryManager bundleDirManager = new BundleDirectoryManager();

    @BeforeClass
    public static void setUpClass() throws IOException {
        File tmpFile = File.createTempFile("testmppdir", "txt");
        tmpDir = tmpFile.getParent();
        tmpFile.delete();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bundleDirManager.setBundleDir(tmpDir);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(new File(tmpDir, FILENAME));
    }

    @Test
    public void testSaveBundleFile() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn(FILENAME);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(CONTENT.getBytes()));

        bundleDirManager.saveBundleFile(multipartFile);

        verify(multipartFile).getInputStream();
        assertEquals(CONTENT, FileUtils.readFileToString(new File(tmpDir, FILENAME)));
    }

    @Test
    public void testRemoveBundle() throws IOException {
        final File bundleFile = new File(tmpDir, FILENAME);
        final String location = "file:" + bundleFile.getAbsolutePath();
        FileUtils.writeByteArrayToFile(bundleFile, CONTENT.getBytes());
        when(bundle.getLocation()).thenReturn(location);

        bundleDirManager.removeBundle(bundle);

        assertFalse(bundleFile.exists());
    }
}
