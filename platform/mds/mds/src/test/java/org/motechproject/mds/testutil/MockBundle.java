package org.motechproject.mds.testutil;

import org.osgi.framework.Bundle;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

public abstract class MockBundle {

    protected abstract Map<String, Class> getMappingsForLoader();

    protected abstract Class getTestClass();

    protected abstract Bundle getMockBundle();

    protected void setUpMockBundle() throws MalformedURLException, ClassNotFoundException {
        File file = computeTestDataRoot(getClass());
        String location = file.toURI().toURL().toString();
        doReturn(location).when(getMockBundle()).getLocation();

        for(Map.Entry entry : getMappingsForLoader().entrySet()) {
            doReturn(entry.getValue()).when(getMockBundle()).loadClass((String) entry.getKey());
        }
    }

    private File computeTestDataRoot(Class anyTestClass) {
        String clsUri = anyTestClass.getName().replace('.', '/') + ".class";
        URL url = anyTestClass.getClassLoader().getResource(clsUri);
        String clsPath = url.getPath();

        return new File(clsPath).getParentFile();
    }
}
