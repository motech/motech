package org.motechproject.mobileforms.api.validator;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;

public class TestFormBean extends FormBean {

    @Required
    @RegEx(pattern = "[A-Z,a-z, ]+")
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TestFormBean setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TestFormBean setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
