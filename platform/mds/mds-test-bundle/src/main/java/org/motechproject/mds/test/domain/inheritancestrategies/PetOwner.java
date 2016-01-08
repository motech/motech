package org.motechproject.mds.test.domain.inheritancestrategies;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.test.domain.inheritancestrategies.Person;
import org.motechproject.mds.test.domain.inheritancestrategies.Pet;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class PetOwner extends Person {

    @Field
    @Persistent(mappedBy = "petOwner")
    private List<Pet> pets;

    public PetOwner(int age, List<Pet> pets) {
        super(age);
        this.pets = pets;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
