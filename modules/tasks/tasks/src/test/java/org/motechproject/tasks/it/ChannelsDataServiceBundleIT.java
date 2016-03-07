package org.motechproject.tasks.it;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.builder.ChannelBuilder;
import org.motechproject.tasks.contract.json.ActionEventRequestDeserializer;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ChannelsDataServiceBundleIT extends BasePaxIT {

    private static final String MDS_ENTITIES_BUNDLE = "org.motechproject.motech-platform-dataservices-entities";

    @Inject
    private ChannelsDataService channelsDataService;

    @Inject
    private BundleContext bundleContext;

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Test
    public void shouldFindChannelByChannelInfo() throws Exception {
        List<Channel> channels = loadChannels();

        channelsDataService.create(channels.get(0));
        channelsDataService.create(channels.get(1));

        List<Channel> channelList = channelsDataService.retrieveAll();
        // ignore the channels that register with the test bundle and with MDS
        removeOwnAndMDSChannels(channelList);

        assertEquals(channels, channelList);

        Channel channel = channelList.get(0);
        Channel actual = channelsDataService.findByModuleName(channel.getModuleName());

        assertEquals(channel, actual);

        channel = channelList.get(1);
        actual = channelsDataService.findByModuleName(channel.getModuleName());

        assertEquals(channel, actual);
    }

    private List<Channel> loadChannels() throws IOException {
        Type type = new TypeToken<ChannelRequest>() {}.getType();

        HashMap<Type, Object> typeAdapters = new HashMap<>();
        typeAdapters.put(ActionEventRequest.class, new ActionEventRequestDeserializer());

        List<StringWriter> writers = new ArrayList<>(2);

        for (String json : Arrays.asList("/message-campaign-test-channel.json", "/pillreminder-test-channel.json")) {
            try (InputStream stream = getClass().getResourceAsStream(json)) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer);

                writers.add(writer);
            }
        }

        List<Channel> channelRequests = new ArrayList<>(2);

        for (StringWriter writer : writers) {
            ChannelRequest channelRequest = (ChannelRequest) motechJsonReader.readFromString(writer.toString(), type, typeAdapters);
            channelRequest.setModuleName(channelRequest.getDisplayName());
            channelRequest.setModuleVersion("1.0");
            channelRequests.add(ChannelBuilder.fromChannelRequest(channelRequest).build());
        }

        return channelRequests;
    }

    private void removeOwnAndMDSChannels(List<Channel> channels) {
        Iterator<Channel> it = channels.iterator();
        while (it.hasNext()) {
            Channel channel = it.next();
            if (StringUtils.equals(bundleContext.getBundle().getSymbolicName(), channel.getModuleName()) ||
                    StringUtils.equals(MDS_ENTITIES_BUNDLE, channel.getModuleName())) {
                it.remove();
            }
        }
    }
}
