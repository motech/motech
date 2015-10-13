package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Discr1Start
{
    public Discr1Start() {
        this.onetoones = new HashSet<Discr1OneToOne>();
        this.multiples = new HashSet<Discr1Multiple>();
    }
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Field
    private Set<Discr1OneToOne> onetoones;

    public Set<Discr1OneToOne> getOnetoones() {
        return onetoones;
    }

    public void setOnetoones(Set<Discr1OneToOne> onetoones) {
        this.onetoones = onetoones;
    }

    @Field
    private Set<Discr1Multiple> multiples;

    public Set<Discr1Multiple> getMultiples() {
        return multiples;
    }

    public void setMultiples(Set<Discr1Multiple> multiples) {
        this.multiples = multiples;
    }
}
