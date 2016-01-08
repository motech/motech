package org.motechproject.mds.test.domain.inheritancestrategies;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class Goldfish extends Fish {

    @Field
    private int wishesLeft;

    public Goldfish(PetOwner petOwner, int length, int wishesLeft) {
        super(petOwner, length);
        this.wishesLeft = wishesLeft;
    }

    public int getWishesLeft() {
        return wishesLeft;
    }

    public void setWishesLeft(int wishesLeft) {
        this.wishesLeft = wishesLeft;
    }
}
