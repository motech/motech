package org.motechproject.server.bootstrap;

import org.motechproject.server.util.MessageBrokerPingUtil;
import org.springframework.stereotype.Service;

/**
 * Created by atish on 13/7/15.
 */
@Service("messageBrokerPingService")
public class MessageBrokerPingServiceImpl implements MessageBrokerPingService {

    @Override
    public boolean pingBroker(String queueUrl) {
        return MessageBrokerPingUtil.getInstance().test(queueUrl);
    }
}
