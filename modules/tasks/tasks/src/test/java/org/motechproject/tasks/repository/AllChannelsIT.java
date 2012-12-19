package org.motechproject.tasks.repository;

import com.google.gson.reflect.TypeToken;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
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
    public void shouldAddAndUpdateChannels() {
        List<Channel> channels = loadChannels();

        allChannels.addOrUpdate(channels.get(0));
        allChannels.addOrUpdate(channels.get(1));

        assertEquals(2, allChannels.getAll().size());

        allChannels.addOrUpdate(channels.get(1));

        assertEquals(2, allChannels.getAll().size());

        markForDeletion(allChannels.getAll());
    }

    @Test
    public void shouldFindChannelByChannelInfo() throws Exception {
        List<Channel> channels = loadChannels();

        allChannels.addOrUpdate(channels.get(0));
        allChannels.addOrUpdate(channels.get(1));

        List<Channel> channelList = allChannels.getAll();

        assertEquals(2, channelList.size());

        Channel channel = channelList.get(0);
        Channel actual = allChannels.byChannelInfo(channel.getDisplayName(), channel.getModuleName(), channel.getModuleVersion());

        assertEquals(channel, actual);

        markForDeletion(channelList);
    }

    private List<Channel> loadChannels() {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream messageCampaignChannelStream = classLoader.getResourceAsStream("message-campaign-test-channel.json");
        InputStream pillReminderChannelStream = classLoader.getResourceAsStream("pillreminder-test-channel.json");

        Type type = new TypeToken<Channel>() {
        }.getType();
        Channel messageCampaignChannel = (Channel) motechJsonReader.readFromStream(messageCampaignChannelStream, type);
        Channel pillReminderChannel = (Channel) motechJsonReader.readFromStream(pillReminderChannelStream, type);

        return Arrays.asList(messageCampaignChannel, pillReminderChannel);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
