package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class VehicleOwner extends Person {

    @Field
    private List<Vehicle> vehicles;

    public VehicleOwner(int age, List<Vehicle> vehicles) {
        super(age);
        this.vehicles = vehicles;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
