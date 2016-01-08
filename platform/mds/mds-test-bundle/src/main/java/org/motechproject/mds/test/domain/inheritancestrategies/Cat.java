package org.motechproject.mds.test.domain.inheritancestrategies;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class Cat extends Pet {

    @Field
    private int livesLeft;

    public Cat(PetOwner petOwner, int livesLeft) {
        super(petOwner);
        this.livesLeft = livesLeft;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
    }
}
