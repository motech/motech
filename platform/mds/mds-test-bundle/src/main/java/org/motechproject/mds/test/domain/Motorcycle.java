package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class Motorcycle extends Vehicle {

    @Field
    private String producer;

    public Motorcycle(int yearOfProduction, String producer) {
        super(yearOfProduction);
        this.producer = producer;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }
}
