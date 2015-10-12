package org.motechproject.mds.testJdoDiscriminator.domain;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Discr2Multiple
{
    public Discr2Multiple() {
        ds = new HashSet<Discr2>();
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
    private Set<Discr2> ds;

    public Set<Discr2> getDs() {
        return ds;
    }

    public void setDs(Set<Discr2> ds) {
        this.ds = ds;
    }
}
