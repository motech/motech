package org.motechproject.mds.web.util;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.web.util.mock.Car;
import org.motechproject.mds.web.util.mock.Driver;
import org.motechproject.mds.web.util.mock.Factory;
import org.motechproject.mds.web.util.mock.Location;
import org.motechproject.mds.web.util.mock.Manufacturer;
import org.motechproject.mds.web.util.mock.SafetyPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RelationshipDisplayUtilTest {

    private Car car;
    private RelationshipDisplayUtil displayUtil = new RelationshipDisplayUtil();

    @Before
    public void setUp() {
        car = prepareTestObject();
    }

    @Test
    public void shouldRemoveDeeperRelationshipsFromJavaObject() {
        Object actual = displayUtil.breakDeepRelationChainForDisplay(car.getManufacturer(), prepareFieldDefinitionsForManufacturer());
        Manufacturer parsed = (Manufacturer) actual;

        // Non-relationship field should be left untouched
        assertEquals(car.getManufacturer().getName(), parsed.getName());

        // Relationship fields should be set to null, no matter if it is single object or collection
        assertNull(parsed.getFactories());
        assertNull(parsed.getSafetyPolicy());
    }

    @Test
    public void shouldRemoveDeeperRelationshipFromCollectionOfJavaObjects() {
        List<Factory> factories = car.getManufacturer().getFactories();

        Object actual = displayUtil.breakDeepRelationChainForDisplay(factories, prepareFieldDefinitionsForFactory());
        List<Factory> parsed = (List<Factory>) actual;

        // The number of items in the passed collection should never change
        assertEquals(factories.size(), parsed.size());

        Factory parsed1 = parsed.get(0);
        Factory parsed2 = parsed.get(1);

        // Non-relationship field should be left untouched
        assertEquals(factories.get(0).getName(), parsed1.getName());
        assertEquals(factories.get(1).getName(), parsed2.getName());

        // Relationship fields should be set to null, no matter if it is single object or collection
        assertNull(parsed1.getLocation());
        assertNull(parsed2.getLocation());
    }

    private List<FieldDto> prepareFieldDefinitionsForManufacturer() {
        List<FieldDto> fields = new ArrayList<>();

        FieldDto name = new FieldDto("name", "Name", null);

        FieldDto factories = new FieldDto("factories", "Factories", null);
        factories.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATED_CLASS, "Factory"));

        FieldDto safetyPolicy = new FieldDto("safetyPolicy", "Safety policy", null);
        safetyPolicy.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATED_CLASS, "SafetyPolicy"));

        fields.add(name);
        fields.add(factories);
        fields.add(safetyPolicy);

        return fields;
    }

    private List<FieldDto> prepareFieldDefinitionsForFactory() {
        List<FieldDto> fields = new ArrayList<>();

        FieldDto name = new FieldDto("name", "Name", null);

        FieldDto location = new FieldDto("location", "Location", null);
        location.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATED_CLASS, "Location"));

        fields.add(name);
        fields.add(location);

        return fields;
    }

    private Car prepareTestObject() {
        Car car = new Car("Meriva");
        Manufacturer manufacturer = new Manufacturer("Opel");

        Location location = new Location("Eisenach", "99817");
        Location location2 = new Location("Luton", "LU6");

        SafetyPolicy policy = new SafetyPolicy(manufacturer, "DO read the manual before turning the engine on!");
        manufacturer.setSafetyPolicy(policy);

        Factory factory = new Factory("First Factory");
        factory.setLocation(location);
        Factory factory2 = new Factory("Second Factory");
        factory2.setLocation(location2);

        manufacturer.setFactories(Arrays.asList(factory, factory2));

        Driver driver = new Driver("Peeta M.", new DateTime(2095, 6, 27, 10, 35));

        car.setManufacturer(manufacturer);
        car.setDriver(driver);

        return car;
    }
}
