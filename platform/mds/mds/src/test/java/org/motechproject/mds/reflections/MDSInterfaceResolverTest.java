package org.motechproject.mds.reflections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.motechproject.mds.annotations.internal.samples.Sample;
import org.motechproject.mds.builder.SampleWithLookups;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.testutil.MockBundle;
import org.osgi.framework.Bundle;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareForTest({MotechClassPool.class})
@RunWith(PowerMockRunner.class)
public class MDSInterfaceResolverTest extends MockBundle {

    @Spy
    Bundle bundle = new org.eclipse.gemini.blueprint.mock.MockBundle();

    @Before
    public void setUp() throws MalformedURLException, ClassNotFoundException {
        setUpMockBundle();
    }

    @Test
    public void shouldDiscoverMotechDataServiceInPackage() throws MalformedURLException, ClassNotFoundException {
        mockStatic(MotechClassPool.class);
        MDSInterfaceResolver.processMDSInterfaces(bundle);

        //We verify that the interface resolver registers our entity class with matching
        //MDS interface in the MotechClassPool

        verifyStatic();
        //TestMDService defines Sample as its entity class and it should be properly resolved
        MotechClassPool.registerServiceInterface("org.motechproject.mds.annotations.internal.samples.Sample",
                "org.motechproject.mds.reflections.test.TestMDService");

        verifyStatic();
        //ComplicatedService defines SampleWithLookups as its entity class and it should be properly resolved
        MotechClassPool.registerServiceInterface("org.motechproject.mds.builder.SampleWithLookups",
                "org.motechproject.mds.reflections.test.ComplicatedService");

    }

    @Override
    protected Map<String, Class> getMappingsForLoader() {
        Map mappings = new LinkedHashMap<>();
        mappings.put(Sample.class.getName(), Sample.class);
        mappings.put(SampleWithLookups.class.getName(), SampleWithLookups.class);

        return mappings;
    }

    @Override
    protected Class getTestClass() {
        return getClass();
    }

    @Override
    protected Bundle getMockBundle() {
        return bundle;
    }
}
