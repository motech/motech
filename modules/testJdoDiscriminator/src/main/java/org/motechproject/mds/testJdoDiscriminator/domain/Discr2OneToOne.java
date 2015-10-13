package org.motechproject.mds.testJdoDiscriminator.domain;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Discr2OneToOne
{
    public Discr2OneToOne(Discr2 d) {
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
    private Discr2 d;

    public Discr2 getD() {
        return d;
    }

    public void setD(Discr2 d) { this.d = d; }
}
