package org.motechproject.ivr;

import org.junit.Test;
import org.motechproject.ivr.service.contract.CallRequest;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestInitiateCallData {

    private static final String CALLBACK_URL = "http://10.0.1.29:8080/m/module/ar/vxml/ar?r=1";

    @Test
    public void TestConstructor() {
        String phone = "1001";
        int timeOut = Integer.MAX_VALUE;

        CallRequest callRequest = new CallRequest(phone, timeOut, CALLBACK_URL);

        assertEquals(phone, callRequest.getPhone());
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestConstructorNullPhone() {
        String phone = null;
        int timeOut = Integer.MAX_VALUE;

        CallRequest callRequest = new CallRequest(phone, timeOut, CALLBACK_URL);
    }
}
