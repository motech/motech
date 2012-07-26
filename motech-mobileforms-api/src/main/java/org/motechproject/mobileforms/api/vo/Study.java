package org.motechproject.mobileforms.api.vo;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.group.Group;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class Study {
    private String name;
    private List<FormBean> forms = new ArrayList<FormBean>();

    public Study() {
    }

    public Study(String name) {
        this.name = name;
    }

    public Study(String name, List<FormBean> forms) {
        this(name);
        this.forms = forms;
    }

    public String name() {
        return name;
    }

    public List<FormBean> forms() {
        return forms;
    }

    public void addForm(FormBean form) {
        forms.add(form);
    }

    public List<FormBeanGroup> groupedForms() {

        List<FormBean> formsThatCanNotBeGrouped = filter(having(on(FormBean.class).groupId(), equalTo(null)), forms);
        List<FormBean> formsThatCanBeGrouped = filter(having(on(FormBean.class).groupId(), not(equalTo(null))), forms);

        final List<FormBeanGroup> formGroups = convert(formsThatCanNotBeGrouped, new Converter<FormBean, FormBeanGroup>() {
            @Override
            public FormBeanGroup convert(FormBean form) {
                return new FormBeanGroup(Arrays.asList(form));
            }
        });

        formGroups.addAll(convert(group(formsThatCanBeGrouped, by(on(FormBean.class).groupId())).subgroups(), new Converter<Group<FormBean>, FormBeanGroup>() {
            @Override
            public FormBeanGroup convert(Group<FormBean> group) {
                return new FormBeanGroup(group.findAll());
            }
        }));
        return formGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Study)) {
            return false;
        }

        Study study = (Study) o;

        if (forms != null ? !forms.equals(study.forms) : study.forms != null) {
            return false;
        }

        if (name != null ? !name.equals(study.name) : study.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (forms != null ? forms.hashCode() : 0);
        return result;
    }
}
