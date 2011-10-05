package org.motechproject.server.service.ivr;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IVRCallIdentifiers {

    public String getNew() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
