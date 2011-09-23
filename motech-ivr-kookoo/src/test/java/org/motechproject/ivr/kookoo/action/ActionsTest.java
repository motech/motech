package org.motechproject.ivr.kookoo.action;

import org.junit.Test;
import org.motechproject.ivr.kookoo.action.event.BaseEventAction;
import org.motechproject.ivr.kookoo.action.event.HangupEventAction;
import org.motechproject.ivr.kookoo.action.event.NewCallEventAction;
import org.motechproject.server.service.ivr.IVREvent;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ActionsTest {
    @Test
    public void shouldReturnActionBasedOnKey() {
        Map<String, BaseEventAction> map = new HashMap<String, BaseEventAction>();
        map.put("newcall", new NewCallEventAction(null, null, null, null, null, null));
        map.put("hangup", new HangupEventAction());

        Actions actions = new Actions(map);

        assertTrue(actions.findFor(IVREvent.NEW_CALL).getClass().equals(NewCallEventAction.class));
        assertTrue(actions.findFor(IVREvent.HANGUP).getClass().equals(HangupEventAction.class));
    }
}
