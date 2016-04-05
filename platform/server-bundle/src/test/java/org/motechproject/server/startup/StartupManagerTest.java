package org.motechproject.server.startup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.config.domain.LoginMode;
import org.motechproject.config.domain.MotechSettings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StartupManagerTest {

    @Mock
    ConfigurationService configurationService;

    @Mock
    private EventAdmin eventAdmin;

    @Mock
    MotechSettings motechSettings;

    @InjectMocks
    StartupManager startupManager = new StartupManager();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testNoSettings() {
        when(motechSettings.getLoginMode()).thenReturn(LoginMode.REPOSITORY);

        startupManager.startup();

        assertTrue(startupManager.isConfigRequired());

        assertFalse(startupManager.canLaunchBundles());

        verify(eventAdmin, never()).postEvent(any(Event.class));
        verify(eventAdmin, never()).sendEvent(any(Event.class));
    }

    @Test
    public void shouldSetPlatformStateToNeedBootstrapIfNoBootstrapConfigFound() {
        when(configurationService.loadBootstrapConfig()).thenReturn(null);

        startupManager.startup();

        assertTrue(startupManager.isBootstrapConfigRequired());
    }

    @Test
    public void shouldNotTryToLoadDbSettingsIfConfigurationFilesAreStillRequired() {
        BootstrapConfig bootstrap = mock(BootstrapConfig.class);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrap);
        when(configurationService.requiresConfigurationFiles()).thenReturn(true);
        startupManager.startup();
        assertFalse(startupManager.isBootstrapConfigRequired());
        assertTrue(startupManager.isConfigRequired());
        verify(configurationService, never()).getPlatformSettings();
        verify(configurationService).requiresConfigurationFiles();
    }
}
