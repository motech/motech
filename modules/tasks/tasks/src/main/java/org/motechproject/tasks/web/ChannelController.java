package org.motechproject.tasks.web;

import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for managing channels.
 */
@Controller
public class ChannelController {

    private ChannelService channelService;

    /**
     * Controller constructor.
     *
     * @param channelService  the channel service, not null
     */
    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * Returns the list of all channels.
     *
     * @return  the list of all channels
     */
    @RequestMapping(value = "channel", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getAllChannels() {
        return channelService.getAllChannels();
    }

    /**
     * Returns the channels icon for the module with given name.
     *
     * @param moduleName  the name of the module
     * @param response  the HTTP response
     * @throws IOException  when there were problems while accessing the icon
     */
    @RequestMapping(value = "channel/icon", method = RequestMethod.GET)
    public void getChannelIcon(@RequestParam String moduleName, HttpServletResponse response) throws IOException {
        BundleIcon bundleIcon = channelService.getChannelIcon(moduleName);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(bundleIcon.getContentLength());
        response.setContentType(bundleIcon.getMime());

        response.getOutputStream().write(bundleIcon.getIcon());
    }
}
