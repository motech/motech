package org.motechproject.mds.service.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.service.JarGeneratorService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MdsBundleRegenerationServiceImplTest {

    public static final String PUPPIES_MODULE = "puppies-module";
    public static final String KITTIES_MODULE = "kitties-module";
    public static final String[] MODULES = new String[]{PUPPIES_MODULE, KITTIES_MODULE};
    public static final String REGENERATE_EVENT = "org.motechproject.mds.regenerate_mds_data_bundle";
    public static final String REGENERATE_DDE_EVENT = "org.motechproject.mds.regenerate_mds_data_bundle_after_dde_enhancement";
    public static final String MODULE_NAMES_PARAM = "module_names";

    @Mock
    private EventRelay eventRelay;

    @Mock
    private JarGeneratorService jarGeneratorService;

    @Mock
    private MdsBundleRegenerationServiceImpl mdsBundleRegenerationServiceOnOtherInstance;

    private MdsBundleRegenerationServiceImpl mdsBundleRegenerationService;

    @Before
    public void setUp() throws Exception {
        mdsBundleRegenerationService = new MdsBundleRegenerationServiceImpl();
        mdsBundleRegenerationService.setEventRelay(eventRelay);
        mdsBundleRegenerationService.setJarGeneratorService(jarGeneratorService);
    }

    @Test
    public void shouldCallRegenerateMdsDataBundle() {
        mdsBundleRegenerationService.regenerateMdsDataBundle();
        verify(jarGeneratorService, times(1)).regenerateMdsDataBundle();
    }

    @Test
    public void shouldBroadcastRegenerateMdsDataBundleEvent() {
        mdsBundleRegenerationService.regenerateMdsDataBundle();
        verify(eventRelay, times(1)).broadcastEventMessage(argThat(new MotechEventArgumentMatcher(REGENERATE_EVENT)));
    }

    @Test
    public void shouldHandleRegenerateMdsDataBundle() {
        MotechEvent event = new MotechEvent(REGENERATE_EVENT);
        mdsBundleRegenerationService.handleMdsDataBundleRegeneration(event);
        verify(jarGeneratorService, times(1)).regenerateMdsDataBundle();
    }

    @Test
    public void shouldCallRegenerateMdsDataBundleAfterDdeEnhancement() {
        mdsBundleRegenerationService.regenerateMdsDataBundleAfterDdeEnhancement(MODULES);
        verify(jarGeneratorService, times(1)).regenerateMdsDataBundleAfterDdeEnhancement(eq(PUPPIES_MODULE), eq(KITTIES_MODULE));
    }

    @Test
    public void shouldBroadcastRegenerateMdsDataBundleAfterDdeEnhancementEvent() {
        mdsBundleRegenerationService.regenerateMdsDataBundleAfterDdeEnhancement(MODULES);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MODULE_NAMES_PARAM, MODULES);
        verify(eventRelay, times(1)).broadcastEventMessage(argThat(
                new MotechEventArgumentMatcher(REGENERATE_DDE_EVENT, parameters)));
    }

    @Test
    public void shouldHandleRegenerateMdsDataBundleAfterDdeEnhancement() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MODULE_NAMES_PARAM, MODULES);
        MotechEvent event = new MotechEvent(REGENERATE_DDE_EVENT, parameters);
        mdsBundleRegenerationService.handleMdsDataBundleRegenerationAfterDdeEnhancement(event);
        verify(jarGeneratorService, times(1)).regenerateMdsDataBundleAfterDdeEnhancement(eq(PUPPIES_MODULE), eq(KITTIES_MODULE));
    }

    @Test
    public void shouldIgnoreEventFromItself() {
        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        doNothing().when(eventRelay).broadcastEventMessage(captor.capture());
        mdsBundleRegenerationService.regenerateMdsDataBundle();
        verify(jarGeneratorService, times(1)).regenerateMdsDataBundle();
        mdsBundleRegenerationService.handleMdsDataBundleRegeneration(captor.getValue());
        verify(jarGeneratorService, times(1)).regenerateMdsDataBundle();
    }

    private class MotechEventArgumentMatcher extends ArgumentMatcher<MotechEvent> {

        private final String subject;
        private final Map<String, Object> parameters;

        private MotechEventArgumentMatcher(String subject) {
            this(subject, new HashMap<String, Object>());
        }

        private MotechEventArgumentMatcher(String subject, Map<String, Object> parameters) {
            this.subject = subject;
            this.parameters = parameters;
        }

        @Override
        public boolean matches(Object o) {
            return o instanceof MotechEvent &&
                    StringUtils.equals(((MotechEvent) o).getSubject(), subject) &&
                    containsAll(((MotechEvent) o).getParameters(), parameters);
        }
    }

    private boolean containsAll(Map<String, Object> map, Map<String, Object> entries) {
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            if (!Objects.equals(map.get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}