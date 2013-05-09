package org.motechproject.tasks.repository;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.json.ActionEventDeserializer;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllChannelsIT extends SpringIntegrationTest {

    @Autowired
    private AllChannels allChannels;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Test
    public void shouldAddAndUpdateChannels() throws IOException {
        List<Channel> channels = loadChannels();

        allChannels.addOrUpdate(channels.get(0));
        allChannels.addOrUpdate(channels.get(1));

        assertEquals(channels, allChannels.getAll());

        allChannels.addOrUpdate(channels.get(1));

        assertEquals(channels, allChannels.getAll());

        markForDeletion(allChannels.getAll());
    }

    @Test
    public void shouldFindChannelByChannelInfo() throws Exception {
        List<Channel> channels = loadChannels();

        allChannels.addOrUpdate(channels.get(0));
        allChannels.addOrUpdate(channels.get(1));

        List<Channel> channelList = allChannels.getAll();

        assertEquals(channels, channelList);

        Channel channel = channelList.get(0);
        Channel actual = allChannels.byModuleName(channel.getModuleName());

        assertEquals(channel, actual);

        channel = channelList.get(1);
        actual = allChannels.byModuleName(channel.getModuleName());

        assertEquals(channel, actual);

        markForDeletion(allChannels.getAll());
    }

    private List<Channel> loadChannels() throws IOException {
        Type type = new TypeToken<Channel>() { }.getType();

        HashMap<Type, Object> typeAdapters = new HashMap<>();
        typeAdapters.put(ActionEvent.class, new ActionEventDeserializer());

        List<StringWriter> writers = new ArrayList<>(2);

        for (String json : Arrays.asList("/message-campaign-test-channel.json", "/pillreminder-test-channel.json")) {
            try (InputStream stream = getClass().getResourceAsStream(json)) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer);

                writers.add(writer);
            }
        }

        List<Channel> channels = new ArrayList<>(2);

        for (StringWriter writer : writers) {
            channels.add((Channel) motechJsonReader.readFromString(writer.toString(), type, typeAdapters));
        }

        return channels;
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
