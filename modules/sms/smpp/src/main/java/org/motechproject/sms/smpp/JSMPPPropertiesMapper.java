package org.motechproject.sms.smpp;

import org.smslib.smpp.Address;
import org.smslib.smpp.BindAttributes;

import java.util.Properties;

import static org.motechproject.sms.smpp.constants.SmppProperties.BINDTYPE;
import static org.motechproject.sms.smpp.constants.SmppProperties.BIND_NPI;
import static org.motechproject.sms.smpp.constants.SmppProperties.BIND_TON;
import static org.motechproject.sms.smpp.constants.SmppProperties.DESTINATION_NPI;
import static org.motechproject.sms.smpp.constants.SmppProperties.DESTINATION_TON;
import static org.motechproject.sms.smpp.constants.SmppProperties.HOST;
import static org.motechproject.sms.smpp.constants.SmppProperties.PASSWORD;
import static org.motechproject.sms.smpp.constants.SmppProperties.PORT;
import static org.motechproject.sms.smpp.constants.SmppProperties.SOURCE_NPI;
import static org.motechproject.sms.smpp.constants.SmppProperties.SOURCE_TON;
import static org.motechproject.sms.smpp.constants.SmppProperties.SYSTEM_ID;

public class JSMPPPropertiesMapper {

    public static final int DEFAULT_PORT = 2772;
    public static final String DEFAULT_HOST = "localhost";
    private Properties smppProperties;

    public JSMPPPropertiesMapper(Properties smppProperties) {
        this.smppProperties = smppProperties;
    }

    public Address getSourceAddress() {
        if (null != smppProperties.getProperty(SOURCE_TON)) {
            return new Address(Address.TypeOfNumber.valueOf(Byte.valueOf(smppProperties.getProperty(SOURCE_TON))),
                    Address.NumberingPlanIndicator.valueOf(Byte.valueOf(smppProperties.getProperty(SOURCE_NPI))));
        }
        return new Address();
    }

    public Address getDestinationAddress() {
        if (null != smppProperties.getProperty(DESTINATION_TON)) {
            return new Address(Address.TypeOfNumber.valueOf(Byte.valueOf(smppProperties.getProperty(DESTINATION_TON))),
                    Address.NumberingPlanIndicator.valueOf(Byte.valueOf(smppProperties.getProperty(DESTINATION_NPI))));
        }
        return new Address();
    }

    public BindAttributes getBindAttributes() {
        if (null != smppProperties.getProperty(BINDTYPE)) {
            return new BindAttributes(smppProperties.getProperty(SYSTEM_ID),
                    smppProperties.getProperty(PASSWORD),
                    null,
                    BindAttributes.BindType.valueOf(smppProperties.getProperty(BINDTYPE)),
                    getBindAddress());
        }
        return new BindAttributes("", "", "", BindAttributes.BindType.TRANSCEIVER);
    }

    private Address getBindAddress() {
        if (null != smppProperties.getProperty(BIND_TON)) {
            return new Address(Address.TypeOfNumber.valueOf(Byte.valueOf(smppProperties.getProperty(BIND_TON))),
                    Address.NumberingPlanIndicator.valueOf(Byte.valueOf(smppProperties.getProperty(BIND_NPI))));
        }
        return new Address();
    }

    public String getHost() {
        return smppProperties.getProperty(HOST) != null? smppProperties.getProperty(HOST) : DEFAULT_HOST;
    }

    public int getPort() {
        return smppProperties.getProperty(PORT) != null? Integer.parseInt(smppProperties.getProperty(PORT)) : DEFAULT_PORT;
    }
}
