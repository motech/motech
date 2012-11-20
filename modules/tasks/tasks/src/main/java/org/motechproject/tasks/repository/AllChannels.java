package org.motechproject.tasks.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_channelInfo", map = "function(doc) { if(doc.type === 'Channel') emit([doc.displayName, doc.moduleName, doc.moduleVersion]); }")
public class AllChannels extends MotechBaseRepository<Channel> {

    @Autowired
    public AllChannels(final CouchDbConnector connector) {
        super(Channel.class, connector);
    }

    public void addOrUpdate(Channel channel) {
        Channel existingChannel = byChannelInfo(channel.getDisplayName(), channel.getModuleName(), channel.getModuleVersion());

        if (existingChannel == null) {
            add(channel);
        } else {
            existingChannel.setActionTaskEvents(channel.getActionTaskEvents());
            existingChannel.setTriggerTaskEvents(channel.getTriggerTaskEvents());
            existingChannel.setDescription(channel.getDescription());
            existingChannel.setDisplayName(channel.getDisplayName());
            existingChannel.setModuleName(channel.getModuleName());
            existingChannel.setModuleVersion(channel.getModuleVersion());

            update(existingChannel);
        }
    }

    public Channel byChannelInfo(final String displayName, final String moduleName, final String moduleVersion) {
        List<Channel> channels = queryView("by_channelInfo", ComplexKey.of(displayName, moduleName, moduleVersion));
        return channels.isEmpty() ? null : channels.get(0);
    }

}
