package org.motechproject.ivr.kookoo.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.action.event.BaseEventAction;
import org.motechproject.server.service.ivr.IVREvent;

import java.util.Map;

public class Actions {
    private Map<String, BaseEventAction> map;

    public Actions(Map<String, BaseEventAction> map) {
        this.map = map;
    }

    public BaseEventAction findFor(IVREvent event) {
        String key = StringUtils.lowerCase(event.key());
        return map.get(key);
    }
}