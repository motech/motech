package org.motechproject.mrs.web;

import org.motechproject.mrs.util.MrsImplementationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@Controller
@RequestMapping("impl")
public class ImplementationController {

    @Autowired
    private MrsImplementationManager mrsImplementationDataProvider;

    @RequestMapping(value = "/adapters", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getAvailableAdapters() {
        return mrsImplementationDataProvider.getAvailableAdapters();
    }

    @RequestMapping(value = "/adapters/default", method = RequestMethod.GET)
    @ResponseBody public String getDefaultAdapterName() {
        return mrsImplementationDataProvider.getCurrentImplName();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/adapters", method = RequestMethod.POST)
    public void setActiveAdapter(@RequestBody String selectedItem) {
        mrsImplementationDataProvider.setCurrentImplName(selectedItem);
    }
}
