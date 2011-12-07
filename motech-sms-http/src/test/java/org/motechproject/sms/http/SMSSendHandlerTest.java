package org.motechproject.sms.http;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.EventKeys;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class SMSSendHandlerTest {

    private SMSSendHandler handler;

    @Before
    public void setUp(){
      handler = new SMSSendHandler();
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SMSSendHandler.class.getDeclaredMethod("handle", null);
        assertTrue(handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{ EventKeys.SEND_SMS }, annotation.subjects());
    }
    
    @Test
    public void should(){
    
    }    
}
