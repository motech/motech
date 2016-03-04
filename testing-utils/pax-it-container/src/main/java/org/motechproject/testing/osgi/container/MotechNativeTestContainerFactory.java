package org.motechproject.testing.osgi.container;

import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.TestContainerFactory;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * This is the TestContainerFactory which will be used by PaxExam to create the
 * {@link org.motechproject.testing.osgi.container.MotechNativeTestContainer} which will run OSGi integration tests.
 * Tests willing to use the {@link org.motechproject.testing.osgi.container.MotechNativeTestContainer} must use the
 * {@link org.ops4j.pax.exam.ExamFactory} annotation which will point to this class.
 */
public class MotechNativeTestContainerFactory implements TestContainerFactory {

    @Override
    public TestContainer[] create(ExamSystem system) {
        // we use ServiceLoader to load the OSGi Framework Factory
        List<TestContainer> containers = new ArrayList<>();
        Iterator<FrameworkFactory> factories = ServiceLoader.load(FrameworkFactory.class)
                .iterator();
        boolean factoryFound = false;

        while (factories.hasNext()) {
            try {
                containers.add(new MotechNativeTestContainer(system, factories.next()));
                factoryFound = true;
            } catch (IOException e) {
                throw new TestContainerException("Problem initializing container.", e);
            }
        }

        if (!factoryFound) {
            throw new TestContainerException(
                    "No service org.osgi.framework.launch.FrameworkFactory found in META-INF/services on classpath");
        }

        return containers.toArray(new TestContainer[containers.size()]);
    }
}
