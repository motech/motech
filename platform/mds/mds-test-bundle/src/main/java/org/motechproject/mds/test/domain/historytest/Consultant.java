package org.motechproject.mds.test.domain.historytest;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity(recordHistory = true)
public class Consultant extends MdsEntity {

    @Field
    private String name;

    @Field
    private Set<Company> companies;

    public Consultant() {
    }

    public Consultant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }

    public void addCompany(Company company) {
        if (getCompanies() == null) {
            setCompanies(new HashSet<Company>());
        }
        getCompanies().add(company);
    }

    public void removeCompany(String name) {
        Iterator<Company> it = getCompanies().iterator();
        while (it.hasNext()) {
            Company company = it.next();
            if (name.equals(company.getName())) {
                it.remove();
            }
        }
    }
}
