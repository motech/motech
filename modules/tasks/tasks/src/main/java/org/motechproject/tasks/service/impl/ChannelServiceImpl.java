package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.channel.builder.ChannelBuilder;
import org.motechproject.tasks.contract.json.ActionEventRequestDeserializer;
import org.motechproject.tasks.exception.ValidationException;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TriggerEventService;
import org.motechproject.tasks.validation.ChannelValidator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.Query;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.tasks.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.constants.EventSubjects.CHANNEL_DEREGISTER_SUBJECT;
import static org.motechproject.tasks.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;

/**
 * A {@link ChannelService}, used to manage CRUD operations for a {@link Channel}.
 */
@Service("channelService")
public class ChannelServiceImpl implements ChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelServiceImpl.class);

    private static Map<Type, Object> typeAdapters = new HashMap<>();

    private TriggerEventService triggerEventService;
    private ChannelsDataService channelsDataService;
    private MotechJsonReader motechJsonReader;
    private EventRelay eventRelay;
    private BundleContext bundleContext;

    static {
        typeAdapters.put(ActionEventRequest.class, new ActionEventRequestDeserializer());
    }

    @Autowired
    public ChannelServiceImpl(TriggerEventService triggerEventService, ChannelsDataService channelsDataService, EventRelay eventRelay) {
        this.triggerEventService = triggerEventService;
        this.channelsDataService = channelsDataService;
        this.eventRelay = eventRelay;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    @Transactional
    public void registerChannel(ChannelRequest channelRequest) {
        LOGGER.info("Registering channel: {}", channelRequest.getModuleName());
        addOrUpdate(ChannelBuilder.fromChannelRequest(channelRequest).build());
    }

    @Override
    @Transactional
    public void unregisterChannel(String moduleName) {
        LOGGER.info("Unregistering Channel: {}", moduleName);
        delete(moduleName);
    }

    @Override
    @Transactional
    public void registerChannel(final InputStream stream, String moduleName, String moduleVersion) {
        LOGGER.info("Registering channel: {}", moduleName);

        Type type = new TypeToken<ChannelRequest>() {
        }.getType();
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(stream, writer);
            ChannelRequest channelRequest = (ChannelRequest) motechJsonReader.readFromString(writer.toString(), type, typeAdapters);
            channelRequest.setModuleName(moduleName);
            channelRequest.setModuleVersion(moduleVersion);

            registerChannel(channelRequest);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public synchronized void addOrUpdate(final Channel channel) {
        Set<TaskError> errors = ChannelValidator.validate(channel);

        if (!isEmpty(errors)) {
            throw new ValidationException(ChannelValidator.CHANNEL, TaskError.toDtos(errors));
        }

        final Channel existingChannel = getChannel(channel.getModuleName());

        if (existingChannel != null && !existingChannel.equals(channel)) {
            LOGGER.debug("Updating channel {}", channel.getDisplayName());
            existingChannel.setActionTaskEvents(channel.getActionTaskEvents());
            existingChannel.setTriggerTaskEvents(channel.getTriggerTaskEvents());
            existingChannel.setDescription(channel.getDescription());
            existingChannel.setDisplayName(channel.getDisplayName());
            existingChannel.setModuleName(channel.getModuleName());
            existingChannel.setModuleVersion(channel.getModuleVersion());

            channelsDataService.update(existingChannel);
            sendChannelUpdatedEvent(channel);
        } else if (existingChannel == null) {
            LOGGER.debug("Creating channel {}", channel.getDisplayName());
            channelsDataService.create(channel);
        }

        LOGGER.info(String.format("Saved channel: %s", channel.getDisplayName()));
    }

    @Override
    @Transactional
    public synchronized void delete(final String moduleName) {

        final Channel existingChannel = getChannel(moduleName);

        if (existingChannel != null) {
            LOGGER.debug("Deleting channel {}", moduleName);
            channelsDataService.delete(existingChannel);
            sendChannelDeleteEvent(moduleName);
        } else if (existingChannel == null) {
            LOGGER.debug("Channel doesn't exists {}", moduleName);
        }
    }

    @Override
    @Transactional
    public List<Channel> getAllChannels() {
        List<Channel> channels = channelsDataService.executeQuery(new QueryExecution<List<Channel>>() {
            @Override
            public List<Channel> execute(Query query, InstanceSecurityRestriction restriction) {
                List<String> param = WebBundleUtil.getSymbolicNames(bundleContext);

                query.setFilter("param.contains(moduleName)");
                query.declareParameters(String.format("%s param", List.class.getName()));

                return (List<Channel>) query.execute(param);
            }
        });

        for (Channel channel : channels) {
            if (triggerEventService.providesDynamicTriggers(channel.getModuleName()) ||
                    !channel.getTriggerTaskEvents().isEmpty()) {
                channel.setProvidesTriggers(true);
            }
        }

        return channels;
    }

    @Override
    @Transactional
    public Channel getChannel(final String moduleName) {
        List<String> symbolicNames = WebBundleUtil.getSymbolicNames(bundleContext);

        return symbolicNames.contains(moduleName)
                ? channelsDataService.findByModuleName(moduleName)
                : null;
    }

    @Override
    @Transactional
    public boolean channelExists(String moduleName) {
        return channelsDataService.countFindByModuleName(moduleName) > 0;
    }

    @Autowired(required = false)
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private void sendChannelUpdatedEvent(Channel channel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(CHANNEL_MODULE_NAME, channel.getModuleName());

        eventRelay.sendEventMessage(new MotechEvent(CHANNEL_UPDATE_SUBJECT, parameters));
    }

    private void sendChannelDeleteEvent(String moduleName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(CHANNEL_MODULE_NAME, moduleName);

        eventRelay.sendEventMessage(new MotechEvent(CHANNEL_DEREGISTER_SUBJECT, parameters));
    }

}
