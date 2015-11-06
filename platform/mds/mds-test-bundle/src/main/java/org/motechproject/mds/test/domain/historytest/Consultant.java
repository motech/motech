package org.motechproject.mds.test.domain.historytest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Unique;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity(recordHistory = true)
public class Consultant {

    @Field
    private Long id;

    @Field
    private DateTime modificationDate;

    @Field
    @Unique
    private String name;

    @Field
    private Set<Company> companies = new HashSet<>();

    public Consultant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Consultant)) {
            return false;
        } else {
            Consultant that = (Consultant) o;
            return Objects.equals(id, that.id) && StringUtils.equals(name, that.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
