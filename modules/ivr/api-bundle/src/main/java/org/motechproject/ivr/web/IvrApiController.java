package org.motechproject.ivr.web;

import org.motechproject.ivr.domain.Call;
import org.motechproject.ivr.domain.Provider;
import org.motechproject.ivr.osgi.AllIvrBundles;
import org.motechproject.ivr.service.contract.CallRequest;
import org.motechproject.ivr.service.contract.IVRService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IvrApiController {

    @RequestMapping(value = "/providers")
    @ResponseBody
    public List<Provider> getAllIvrBundles() {
        List<Provider> providers = new ArrayList<>();
        for (String providerName : AllIvrBundles.instance().getAll()) {
            providers.add(new Provider(providerName));
        }
        return providers;
    }

    @RequestMapping(value = "/test-call", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void dial(@RequestBody Call call) {
        IVRService ivrService = AllIvrBundles.instance().getIvrService(call.getProvider());
        CallRequest callRequest = new CallRequest(call.getPhoneNumber(), null, null);
        ivrService.initiateCall(callRequest);
    }
}

