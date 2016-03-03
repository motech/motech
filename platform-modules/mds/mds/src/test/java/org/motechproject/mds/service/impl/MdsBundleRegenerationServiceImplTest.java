package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.osgi.service.event.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.service.MdsBundleRegenerationService.REGENERATE_MDS_DATA_BUNDLE;
import static org.motechproject.mds.service.MdsBundleRegenerationService.REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT;

@RunWith(MockitoJUnitRunner.class)
public class MdsBundleRegenerationServiceImplTest {

    public static final String PUPPIES_MODULE = "puppies-module";
    public static final String KITTIES_MODULE = "kitties-module";
    public static final String[] MODULES = new String[]{PUPPIES_MODULE, KITTIES_MODULE};
    public static final String MODULE_NAMES_PARAM = "module_names";

    @Mock
    private OsgiEventProxy osgiEventProxy;

    @Mock
    private JarGeneratorService jarGeneratorService;

    @Mock
    private EntityService entityService;

    @Mock
    private SchemaHolder schemaHolder;

    @Mock
    private MdsBundleRegenerationServiceImpl mdsBundleRegenerationServiceOnOtherInstance;

    @InjectMocks
    private MdsBundleRegenerationServiceImpl mdsBundleRegenerationService = new MdsBundleRegenerationServiceImpl();

    @Before
    public void setUp() {
        when(entityService.getSchema()).thenReturn(schemaHolder);
    }

    @Test
    public void shouldBroadcastRegenerateMdsDataBundleEventAndRegenerateTheBundle() {
        mdsBundleRegenerationService.regenerateMdsDataBundle();
        verify(jarGeneratorService).regenerateMdsDataBundle(schemaHolder);
        verify(osgiEventProxy).broadcastEvent(eq(REGENERATE_MDS_DATA_BUNDLE), argThat(new ParamsMatcher(null)), eq(true));
    }

    @Test
    public void shouldHandleRegenerateMdsDataBundle() {
        Event event = new Event(REGENERATE_MDS_DATA_BUNDLE, new HashMap<>());
        mdsBundleRegenerationService.handleEvent(event);
        verify(jarGeneratorService).regenerateMdsDataBundle(schemaHolder);
    }

    @Test
    public void shouldCallRegenerateMdsDataBundleAfterDdeEnhancement() {
        mdsBundleRegenerationService.regenerateMdsDataBundleAfterDdeEnhancement(MODULES);
        verify(jarGeneratorService).regenerateMdsDataBundleAfterDdeEnhancement(eq(schemaHolder),
                eq(PUPPIES_MODULE), eq(KITTIES_MODULE));
    }

    @Test
    public void shouldBroadcastRegenerateMdsDataBundleAfterDdeEnhancementEvent() {
        mdsBundleRegenerationService.regenerateMdsDataBundleAfterDdeEnhancement(MODULES);
        verify(osgiEventProxy).broadcastEvent(eq(REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT),
                argThat(new ParamsMatcher(MODULES)), eq(true));
    }

    @Test
    public void shouldHandleRegenerateMdsDataBundleAfterDdeEnhancement() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MODULE_NAMES_PARAM, MODULES);
        Event event = new Event(REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT, parameters);
        mdsBundleRegenerationService.handleEvent(event);
        verify(jarGeneratorService).regenerateMdsDataBundleAfterDdeEnhancement(eq(schemaHolder),
                eq(PUPPIES_MODULE), eq(KITTIES_MODULE));
    }

    @Test
    public void shouldIgnoreEventFromItself() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        doNothing().when(osgiEventProxy).broadcastEvent(anyString(), captor.capture(), eq(true));
        mdsBundleRegenerationService.regenerateMdsDataBundle();
        verify(jarGeneratorService).regenerateMdsDataBundle(schemaHolder);
        mdsBundleRegenerationService.handleEvent(new Event(REGENERATE_MDS_DATA_BUNDLE, captor.getValue()));
        verify(jarGeneratorService).regenerateMdsDataBundle(schemaHolder);
    }

    private class ParamsMatcher extends ArgumentMatcher<Map<String, Object>> {

        private final String[] expectedModules;

        public ParamsMatcher(String[] expectedModules) {
            this.expectedModules = expectedModules;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof Map) {
                Map<String, Object> params = (Map<String, Object>) argument;
                if (expectedModules == null) {
                    return !params.containsKey(MODULE_NAMES_PARAM);
                } else {
                    return Arrays.equals(expectedModules, (String[]) params.get(MODULE_NAMES_PARAM));
                }
            } else {
                return false;
            }
        }
    }
}