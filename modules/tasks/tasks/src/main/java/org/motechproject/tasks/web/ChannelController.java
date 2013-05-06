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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Controller
public class ChannelController {
    private ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(value = "channel", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getAllChannels() {
        return channelService.getAllChannels();
    }

    @RequestMapping(value = "channel/icon", method = RequestMethod.GET)
    public void getChannelIcon(@RequestParam String moduleName, HttpServletResponse response) throws IOException {
        if (isNotEmpty(moduleName)) {
            BundleIcon bundleIcon = channelService.getChannelIcon(moduleName);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentLength(bundleIcon.getContentLength());
            response.setContentType(bundleIcon.getMime());

            response.getOutputStream().write(bundleIcon.getIcon());
        }
    }
}
