package org.motechproject.mds.test.domain.inheritancestrategies;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Fish extends Pet {

    @Field
    private int length;

    public Fish(PetOwner petOwner, int length) {
        super(petOwner);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
