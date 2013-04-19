package org.motechproject.openmrs.ws.impl;

import org.motechproject.mrs.services.MRSImplReqAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("implReqAdapter")
public class MRSImplReqAdapterImpl implements MRSImplReqAdapter{

    public List<String> getRequired(){
        List<String> required = new ArrayList<>();

        required.add("motechId");
        required.add("firstName");
        required.add("lastName");
        required.add("gender");
        required.add("facilityId");

        return required;
    }

}