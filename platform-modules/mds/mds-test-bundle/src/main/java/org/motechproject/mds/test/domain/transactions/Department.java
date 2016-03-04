package org.motechproject.mds.test.domain.transactions;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity(tableName = "department", recordHistory = true)
@Unique(name = "UNIQUE_CONFIGURATION_IDX", members = { "name" })
public class Department {

    private Long id;

    @Field(required = true)
    private String name;

    @Field
    @Persistent(mappedBy = "department")
    private Set<Employee> employees = new LinkedHashSet<>();

    public Department(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Department other = (Department) o;
        return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}