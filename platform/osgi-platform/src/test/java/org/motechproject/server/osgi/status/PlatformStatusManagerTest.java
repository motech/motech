package org.motechproject.server.osgi.status;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlatformStatusManagerTest {

    private static final String OSGI_WEB_UTILS = "org.motechproject.motech-platform-osgi-web-utils";
    private static final String COMMONS_SQL = "org.motechproject.motech-platform-commons-sql";
    private static final String CONFIG_CORE = "org.motechproject.motech-platform-commons-config-core";
    private static final String EVENT = "org.motechproject.motech-platform-event";
    private static final String MDS = "org.motechproject.motech-platform-dataservices";
    private static final String MDS_ENTITIES = "org.motechproject.motech-platform-dataservices-entities";
    private static final String SERVER_CONFIG = "org.motechproject.motech-platform-server-config";
    private static final String EMAIL = "org.motechproject.motech-platform-email";
    private static final String MOTECH_TASKS = "org.motechproject.motech-tasks";

    private PlatformStatusManagerImpl platformStatusManager = new PlatformStatusManagerImpl();

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void shouldReturnEmptyStatusAtFirst() {
        PlatformStatus status = platformStatusManager.getCurrentStatus();

        assertNotNull(status);
        assertEmpty(status.getStartedBundles());
        assertEmpty(status.getBundleErrorsByBundle());
        assertEmpty(status.getContextErrorsByBundle());
    }

    @Test
    public void shouldUpdateStatusBasedOnOSGiEventsAndBundleErrors() {
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(OSGI_WEB_UTILS));
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(COMMONS_SQL));
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(CONFIG_CORE));
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(EVENT));
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(MDS));
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(MDS_ENTITIES));

        PlatformStatus status = platformStatusManager.getCurrentStatus();

        assertNotNull(status);
        assertEmpty(status.getBundleErrorsByBundle());
        assertEmpty(status.getContextErrorsByBundle());
        assertEquals(asList(OSGI_WEB_UTILS, COMMONS_SQL, CONFIG_CORE, EVENT, MDS, MDS_ENTITIES),
                status.getStartedBundles());

        platformStatusManager.onOsgiApplicationEvent(ctxClosedEvent(COMMONS_SQL));
        platformStatusManager.onOsgiApplicationEvent(ctxFailedEvent(MDS, "mds error"));
        platformStatusManager.onOsgiApplicationEvent(ctxFailedEvent(SERVER_CONFIG, "server config error"));
        platformStatusManager.registerBundleError(OSGI_WEB_UTILS, "osgi web util error");
        platformStatusManager.registerBundleError(MOTECH_TASKS, "tasks error");
        // should still keep adding to started bundles despite the error
        platformStatusManager.onOsgiApplicationEvent(ctxRefreshedEvent(EMAIL));

        status = platformStatusManager.getCurrentStatus();

        assertNotNull(status);
        // remove failed / closed
        assertEquals(asList(CONFIG_CORE, EVENT, MDS_ENTITIES, EMAIL), status.getStartedBundles());
        assertErrorMap(status.getBundleErrorsByBundle(), asList(OSGI_WEB_UTILS, MOTECH_TASKS),
                asList("osgi web util error", "tasks error"));
        assertErrorMap(status.getContextErrorsByBundle(), asList(MDS, SERVER_CONFIG),
                asList("mds error", "server config error"));
    }

    public OsgiBundleContextRefreshedEvent ctxRefreshedEvent(String symbolicName) {
        return new OsgiBundleContextRefreshedEvent(applicationContext, mockBundle(symbolicName));
    }

    public OsgiBundleContextClosedEvent ctxClosedEvent(String symbolicName) {
        return new OsgiBundleContextClosedEvent(applicationContext, mockBundle(symbolicName));
    }

    public OsgiBundleContextFailedEvent ctxFailedEvent(String symbolicName, String errorMsg) {
        return new OsgiBundleContextFailedEvent(applicationContext, mockBundle(symbolicName), new RuntimeException(errorMsg));
    }

    private Bundle mockBundle(String symbolicName) {
        Bundle bundle = mock(Bundle.class);
        when(bundle.getSymbolicName()).thenReturn(symbolicName);
        return bundle;
    }

    private void assertEmpty(Collection collection) {
        assertNotNull(collection);
        assertTrue(collection.isEmpty());
    }

    private void assertEmpty(Map map) {
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    private void assertErrorMap(Map<String, String> map, List<String> keys, List<String> values) {
        assertNotNull(map);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String expectedValue = values.get(i);
            String actualValue = map.get(key);

            assertEquals("Wrong error for " + key, expectedValue, actualValue);
        }
    }
}
