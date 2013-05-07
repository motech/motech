package org.motechproject.commcare.web;

import org.motechproject.commcare.domain.CommcareModule;
import org.motechproject.commcare.domain.FormSchema;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller that handles the incoming full form feed from CommCareHQ.
 */
@Controller
public class StubModulesController {

    @RequestMapping(value = "/modules")
    @ResponseBody
    public List<CommcareModule> all() {
        return dummyFormHierarchy();
    }

    private List<CommcareModule> dummyFormHierarchy() {
        List<CommcareModule> modules = new ArrayList<>();

        CommcareModule module1 = new CommcareModule();
        module1.setName("mod1");

        List<FormSchema> formSchemas1 = new ArrayList<>();
        FormSchema formSchema1 = new FormSchema();
        formSchema1.setName("form1");
        formSchemas1.add(formSchema1);

        FormSchema formSchema2 = new FormSchema();
        formSchema2.setName("forma2");
        formSchemas1.add(formSchema2);

        module1.setFormSchemas(formSchemas1);
        modules.add(module1);

        CommcareModule module2 = new CommcareModule();
        module2.setName("mod2");

        List<FormSchema> formSchemas2 = new ArrayList<>();
        FormSchema formSchema3 = new FormSchema();
        formSchema3.setName("formic3");
        formSchemas2.add(formSchema3);

        module2.setFormSchemas(formSchemas2);
        modules.add(module2);

        return modules;
    }
}
