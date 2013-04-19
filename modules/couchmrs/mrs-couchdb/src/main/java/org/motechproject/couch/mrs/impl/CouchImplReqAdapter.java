package org.motechproject.couch.mrs.impl;

import org.motechproject.mrs.services.MRSImplReqAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CouchImplReqAdapter implements MRSImplReqAdapter{

    public List<String> getRequired(){
        List<String> required = new ArrayList<>();

        required.add("motechId");
        required.add("firstName");
        required.add("lastName");

        return required;
    }

}
