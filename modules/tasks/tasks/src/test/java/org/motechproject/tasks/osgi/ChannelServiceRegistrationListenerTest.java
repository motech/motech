package org.motechproject.tasks.osgi;

import org.junit.Test;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ChannelServiceRegistrationListenerTest {

    @Test
    public void shouldDeRegisterAllChannelsWhenListenerStarts() {
        BundleContext bundleContext = mock(BundleContext.class);
        ChannelServiceRegistrationListener listener = new ChannelServiceRegistrationListener(bundleContext);
        ChannelService channelService = mock(ChannelService.class);

        listener.registered(channelService, null);

        verify(channelService).deregisterAllChannels();
    }

}
