package org.motechproject.mds.testJdoDiscriminator.domain;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Discr2Start
{
    public Discr2Start() {
        this.onetoones = new HashSet<Discr2OneToOne>();
        this.multiples = new HashSet<Discr2Multiple>();
    }
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Field
    private Set<Discr2OneToOne> onetoones;

    public Set<Discr2OneToOne> getOnetoones() {
        return onetoones;
    }

    public void setOnetoones(Set<Discr2OneToOne> onetoones) {
        this.onetoones = onetoones;
    }

    @Field
    private Set<Discr2Multiple> multiples;

    public Set<Discr2Multiple> getMultiples() {
        return multiples;
    }

    public void setMultiples(Set<Discr2Multiple> multiples) {
        this.multiples = multiples;
    }
}
