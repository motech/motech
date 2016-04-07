package org.motechproject.commons.api;

import org.springframework.stereotype.Service;


/**
 * Timer service implementation.
 */
@Service("timerService")
public class TimerServiceImpl implements TimerService {

    @Override
    public Timer createTimer() {
        return new Timer();
    }

}
