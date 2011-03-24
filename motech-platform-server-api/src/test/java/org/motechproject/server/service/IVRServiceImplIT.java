package org.motechproject.server.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.InitiateCallData;
import org.motechproject.server.service.ivr.IVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is an interactive test. In order to run that test please make sure that Asterisk, Voiceglue and VXML application
 * are properly configures, up and running. Your SIP phone configured to use the "SIP/1001" account and that account registered
 * in Asterisk.
 *
 * In order to run unit test
 * - start your soft phone
 * - run the initiateCallTest()
 * Your soft phone should start ringing. Answer the phone, you should hear a voice message specified in the voice XML document retrieved
 * from the URL specified as a value of the voiceXmlUrl property of the ivrServise bean. See testApplicationContext.xml.
 *
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testIVRAppContext.xml"})
public class IVRServiceImplIT {

    @Autowired
    private IVRService ivrService;

    @Test
    public void initiateCallTest() throws Exception{

        InitiateCallData initiateCallData = new InitiateCallData(1L, "SIP/1001", 5000, "http://10.0.1.29:8080/TamaIVR/reminder/doc");

        ivrService.initiateCall(initiateCallData);
    }


}
