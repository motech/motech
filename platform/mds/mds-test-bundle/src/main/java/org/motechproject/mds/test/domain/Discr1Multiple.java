package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Discr1Multiple
{
    public Discr1Multiple() {
        ds = new HashSet<Discr1>();
    }
    // required for DiscStartService?
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Field
    private Set<Discr1> ds;

    public Set<Discr1> getDs() {
        return ds;
    }

    public void setDs(Set<Discr1> ds) {
        this.ds = ds;
    }
}
