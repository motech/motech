package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.json.ActionEventDeserializer;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.validation.ChannelValidator;
import org.motechproject.tasks.validation.ValidationResult;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.server.api.BundleIcon.ICON_LOCATIONS;

@Service("channelService")
public class ChannelServiceImpl implements ChannelService {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelServiceImpl.class);

    private static final String DEFAULT_ICON = "/webapp/img/iconTaskChannel.png";

    private static Map<Type, Object> typeAdapters = new HashMap<>();

    private AllChannels allChannels;
    private MotechJsonReader motechJsonReader;
    private ResourceLoader resourceLoader;

    private BundleContext bundleContext;

    static {
        typeAdapters.put(ActionEvent.class, new ActionEventDeserializer());
    }

    @Autowired
    public ChannelServiceImpl(AllChannels allChannels, ResourceLoader resourceLoader) {
        this.allChannels = allChannels;
        this.resourceLoader = resourceLoader;

        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public void registerChannel(final InputStream stream) {
        Type type = new TypeToken<Channel>() {}.getType();
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(stream, writer);
            Channel channel = (Channel) motechJsonReader.readFromString(writer.toString(), type, typeAdapters);

            addOrUpdate(channel);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void addOrUpdate(Channel channel) {
        ValidationResult result = ChannelValidator.validate(channel);

        if (!result.isValid()) {
            throw new ValidationException(ChannelValidator.CHANNEL, result.getTaskErrors());
        }

        allChannels.addOrUpdate(channel);
        LOG.info(String.format("Saved channel: %s", channel.getDisplayName()));
    }

    @Override
    public List<Channel> getAllChannels() {
        return allChannels.getAll();
    }

    @Override
    public Channel getChannel(final String moduleName) {
        return allChannels.byModuleName(moduleName);
    }

    @Override
    public BundleIcon getChannelIcon(String moduleName) throws IOException {
        Bundle bundle = getModule(moduleName);
        BundleIcon bundleIcon = null;
        URL iconURL;

        if (bundle != null) {
            for (String iconLocation : ICON_LOCATIONS) {
                iconURL = bundle.getResource(iconLocation);

                if (iconURL != null) {
                    bundleIcon = loadBundleIcon(iconURL);
                    break;
                }
            }
        }

        if (bundleIcon == null) {
            iconURL = resourceLoader.getResource(DEFAULT_ICON).getURL();
            bundleIcon = loadBundleIcon(iconURL);
        }

        return bundleIcon;
    }

    @Autowired(required = false)
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private Bundle getModule(String moduleName) {
        if (bundleContext == null) {
            throw new IllegalArgumentException("Bundle context not set");
        }

        Bundle bundle = null;

        for (Bundle b : bundleContext.getBundles()) {
            if (b.getSymbolicName().equalsIgnoreCase(String.format("org.motechproject.%s", moduleName))) {
                bundle = b;
                break;
            }
        }

        if (bundle == null) {
            LOG.warn(String.format("Module with moduleName: %s not found", moduleName));
        }

        return bundle;
    }

    private BundleIcon loadBundleIcon(URL iconURL) {
        InputStream is = null;

        try {
            URLConnection urlConn = iconURL.openConnection();
            is = urlConn.getInputStream();

            String mime = urlConn.getContentType();
            byte[] image = IOUtils.toByteArray(is);

            return new BundleIcon(image, mime);
        } catch (IOException e) {
            throw new MotechException("Error loading icon", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}
