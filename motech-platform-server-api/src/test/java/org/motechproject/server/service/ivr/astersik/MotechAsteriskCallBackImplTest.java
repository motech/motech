package org.motechproject.server.service.ivr.astersik;

import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.LiveException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 *  TODO - develop proper tests
 */
public class MotechAsteriskCallBackImplTest {

    private MotechAsteriskCallBackImpl motechAsteriskCallBack;

    private AsteriskChannel asteriskChannel = mock(AsteriskChannel.class);

    @Before
    public void setup() {
        motechAsteriskCallBack = new MotechAsteriskCallBackImpl();
    }

    @Test
    public void testOnDialing() throws Exception {
        motechAsteriskCallBack.onDialing(asteriskChannel);
    }

    @Test
    public void testOnSuccess() throws Exception {
        motechAsteriskCallBack.onSuccess(asteriskChannel);
    }

    @Test
    public void testOnNoAnswer() throws Exception {
        motechAsteriskCallBack.onNoAnswer(asteriskChannel);
    }

    @Test
    public void testOnBusy() throws Exception {
        motechAsteriskCallBack.onBusy(asteriskChannel);
    }

    @Test
    public void testOnFailure() throws Exception {
        motechAsteriskCallBack.onFailure(new LiveException("error") {
        });
    }
}
