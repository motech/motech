package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Discr1OneToOne
{
    public Discr1OneToOne(Discr1 d) {
        this.d = d;
    }

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Field
    private Discr1 d;

    public Discr1 getD() {
        return d;
    }

    public void setD(Discr1 d) { this.d = d; }
}
