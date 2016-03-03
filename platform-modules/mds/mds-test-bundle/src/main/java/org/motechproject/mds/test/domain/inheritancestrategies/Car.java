package org.motechproject.mds.test.domain.inheritancestrategies;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Car extends Vehicle {

    @Field
    private double engineCapacity;

    public Car(int yearOfProduction, double engineCapacity) {
        super(yearOfProduction);
        this.engineCapacity = engineCapacity;
    }

    public double getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(double engineCapacity) {
        this.engineCapacity = engineCapacity;
    }
}
