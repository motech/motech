package org.motechproject.sms.smpp;

import org.smslib.smpp.Address;
import org.smslib.smpp.BindAttributes;

import java.util.Properties;

import static org.motechproject.sms.smpp.constants.SmppProperties.*;

public class JSMPPPropertiesMapper {

    private Properties smppProperties;

    public JSMPPPropertiesMapper(Properties smppProperties) {
        this.smppProperties = smppProperties;
    }

    public Address getSourceAddress() {
        if (null != smppProperties.getProperty(SOURCE_TON))
            return new Address(Address.TypeOfNumber.valueOf(Byte.valueOf(smppProperties.getProperty(SOURCE_TON))),
                    Address.NumberingPlanIndicator.valueOf(Byte.valueOf(smppProperties.getProperty(SOURCE_NPI))));
        return new Address();
    }

    public Address getDestinationAddress() {
        if (null != smppProperties.getProperty(DESTINATION_TON))
            return new Address(Address.TypeOfNumber.valueOf(Byte.valueOf(smppProperties.getProperty(DESTINATION_TON))),
                    Address.NumberingPlanIndicator.valueOf(Byte.valueOf(smppProperties.getProperty(DESTINATION_NPI))));
        return new Address();
    }

    public BindAttributes getBindAttributes() {
        if (null != smppProperties.getProperty(BINDTYPE))
            return new BindAttributes(smppProperties.getProperty(SYSTEM_ID),
                    smppProperties.getProperty(PASSWORD),
                    null,
                    BindAttributes.BindType.valueOf(smppProperties.getProperty(BINDTYPE)),
                    getBindAddress());

        return null;
    }

    private Address getBindAddress() {
        if (null != smppProperties.getProperty(BIND_TON))
            return new Address(Address.TypeOfNumber.valueOf(Byte.valueOf(smppProperties.getProperty(BIND_TON))),
                    Address.NumberingPlanIndicator.valueOf(Byte.valueOf(smppProperties.getProperty(BIND_NPI))));
        return new Address();
    }

    public String getHost() {
        return smppProperties.getProperty(HOST);
    }

    public int getPort() {
        return Integer.parseInt(smppProperties.getProperty(PORT));
    }
}
