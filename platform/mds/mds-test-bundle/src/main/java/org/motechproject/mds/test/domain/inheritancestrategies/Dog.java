package org.motechproject.mds.test.domain.inheritancestrategies;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class Dog extends Pet {

    @Field
    private int hiddenBones;
    
    @Field
    private float weight;

    public Dog(PetOwner petOwner, int hiddenBones) {
        super(petOwner);
        this.hiddenBones = hiddenBones;
    }

    public int getHiddenBones() {
        return hiddenBones;
    }

    public void setHiddenBones(int hiddenBones) {
        this.hiddenBones = hiddenBones;
    }
    
    public float getWeight() {
        return weight;
    }
    
    public void setWeight(float weight) {
        this.weight = weight;
    }
}
