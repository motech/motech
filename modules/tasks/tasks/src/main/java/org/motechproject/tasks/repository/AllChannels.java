package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_moduleName", map = "function(doc) { if(doc.type === 'Channel') emit(doc.moduleName); }")
public class AllChannels extends MotechBaseRepository<Channel> {

    @Autowired
    public AllChannels(@Qualifier("taskDbConnector") final CouchDbConnector connector) {
        super(Channel.class, connector);
    }

    public void addOrUpdate(Channel channel) {
        Channel existingChannel = byModuleName(channel.getModuleName());

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

    public Channel byModuleName(final String moduleName) {
        List<Channel> channels = queryView("by_moduleName", moduleName);
        return channels.isEmpty() ? null : channels.get(0);
    }

}
