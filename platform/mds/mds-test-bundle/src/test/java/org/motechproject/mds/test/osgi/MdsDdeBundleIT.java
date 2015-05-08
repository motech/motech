package org.motechproject.mds.test.osgi;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.test.domain.Author;
import org.motechproject.mds.test.domain.Boat;
import org.motechproject.mds.test.domain.Book;
import org.motechproject.mds.test.domain.Cat;
import org.motechproject.mds.test.domain.Dog;
import org.motechproject.mds.test.domain.Goldfish;
import org.motechproject.mds.test.domain.Motorcycle;
import org.motechproject.mds.test.domain.Pet;
import org.motechproject.mds.test.domain.PetOwner;
import org.motechproject.mds.test.domain.SubclassA;
import org.motechproject.mds.test.domain.SubclassB;
import org.motechproject.mds.test.domain.TestLookup;
import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.domain.Truck;
import org.motechproject.mds.test.domain.Vehicle;
import org.motechproject.mds.test.domain.VehicleOwner;
import org.motechproject.mds.test.service.AuthorDataService;
import org.motechproject.mds.test.service.BoatDataService;
import org.motechproject.mds.test.service.BookDataService;
import org.motechproject.mds.test.service.CatDataService;
import org.motechproject.mds.test.service.DogDataService;
import org.motechproject.mds.test.service.GoldfishDataService;
import org.motechproject.mds.test.service.MotorcycleDataService;
import org.motechproject.mds.test.service.PetOwnerDataService;
import org.motechproject.mds.test.service.SubclassADataService;
import org.motechproject.mds.test.service.SubclassBDataService;
import org.motechproject.mds.test.service.TestLookupService;
import org.motechproject.mds.test.service.TestMdsEntityService;
import org.motechproject.mds.test.service.TransactionTestService;
import org.motechproject.mds.test.service.TruckDataService;
import org.motechproject.mds.test.service.VehicleOwnerDataService;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import javax.inject.Inject;
import javax.jdo.JDOUserException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.motechproject.mds.event.CrudEventBuilder.createSubject;
import static org.motechproject.mds.util.ClassName.simplifiedModuleName;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_CLASS;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_NAME;
import static org.motechproject.mds.util.Constants.MDSEvents.MODULE_NAME;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsDdeBundleIT extends BasePaxIT {

    @Inject
    private TestMdsEntityService testMdsEntityService;

    @Inject
    private TestLookupService testLookupService;

    @Inject
    private BookDataService bookDataService;

    @Inject
    private AuthorDataService authorDataService;

    @Inject
    private SubclassADataService subclassADataService;

    @Inject
    private SubclassBDataService subclassBDataService;

    @Inject
    private EventListenerRegistryService registry;

    @Inject
    private TransactionTestService transactionTestService;

    @Inject
    private BoatDataService boatDataService;

    @Inject
    private MotorcycleDataService motorcycleDataService;

    @Inject
    private TruckDataService truckDataService;

    @Inject
    private CatDataService catDataService;

    @Inject
    private DogDataService dogDataService;

    @Inject
    private GoldfishDataService goldfishDataService;

    @Inject
    private PetOwnerDataService petOwnerDataService;

    @Inject
    private VehicleOwnerDataService vehicleOwnerDataService;

    private final Object waitLock = new Object();

    @Before
    public void setUp() throws Exception {
        setUpSecurityContext();
    }

    @After
    public void tearDown() {
        testMdsEntityService.deleteAll();
        testLookupService.deleteAll();
        bookDataService.deleteAll();
        authorDataService.deleteAll();
        subclassADataService.deleteAll();
        subclassBDataService.deleteAll();
        petOwnerDataService.deleteAll();
        vehicleOwnerDataService.deleteAll();
        boatDataService.deleteAll();
        motorcycleDataService.deleteAll();
        truckDataService.deleteAll();
        catDataService.deleteAll();
        dogDataService.deleteAll();
        goldfishDataService.deleteAll();
    }

    @Test
    public void testMdsTestBundleInstallsProperly() throws Exception {
        assertDefaultConstructorPresent();
        verifyDDE();
        verifyLookup();
    }

    @Test(expected = JDOUserException.class)
    public void testEnforcementOfRequiredField() {
        Book book = new Book();
        bookDataService.create(book);
    }

    @Test
    public void testJdoListeners() throws Exception {
        getLogger().info("Test JdoListeners");

        testMdsEntityService.create(new TestMdsEntity("TestChangeName"));

        List<TestMdsEntity> entities = testMdsEntityService.retrieveAll();

        assertEquals(1, entities.size());
        assertEquals("NameWasChanged", entities.get(0).getSomeString());

        subclassADataService.create(new SubclassA());
        subclassBDataService.create(new SubclassB());

        List<SubclassA> subclassesA = subclassADataService.retrieveAll();
        List<SubclassB> subclassesB = subclassBDataService.retrieveAll();

        assertEquals(1, subclassesA.size());
        assertEquals(1, subclassesB.size());
        assertEquals("StringWasChanged", subclassesA.get(0).getSuperClassString());
        assertEquals("StringWasChanged", subclassesB.get(0).getSuperClassString());
    }

    @Test
    public void testMdsCrudEvents() throws Exception {
        getLogger().info("Test MDS CRUD Events");

        final ArrayList<String> receivedEvents = new ArrayList<>();
        final Map<String, Object> params = new HashMap<>();

        String moduleName = "MOTECH Platform Data Services Test Bundle";
        String simplifiedModuleName = simplifiedModuleName(moduleName);
        String entityName = "TestMdsEntity";
        final String subject = createSubject(moduleName, null, entityName, CrudEventType.CREATE);

        registry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                receivedEvents.add(event.getSubject());
                params.putAll(event.getParameters());

                synchronized (waitLock) {
                    waitLock.notify();
                }
            }

            @Override
            public String getIdentifier() {
                return subject;
            }
        }, subject);

        wait2s();
        testMdsEntityService.create(new TestMdsEntity("string"));
        wait2s();

        assertEquals(1, receivedEvents.size());
        assertEquals(subject, receivedEvents.get(0));
        assertEquals(simplifiedModuleName, params.get(MODULE_NAME));
        assertEquals(entityName, params.get(ENTITY_NAME));
        assertEquals(TestMdsEntity.class.getName(), params.get(ENTITY_CLASS));

        testMdsEntityService.deleteAll();
    }

    @Test
    public void testManyToManyRelationship() {
        getLogger().info("Test Many to Many relationship");

        Author a1 = new Author("author1");
        Author a2 = new Author("author2");
        Author a3 = new Author("author3");

        Book b1 = new Book("book1");
        Book b2 = new Book("book2");
        Book b3 = new Book("book3");

        bookDataService.create(b1);
        bookDataService.create(b2);
        bookDataService.create(b3);

        authorDataService.create(a1);
        authorDataService.create(a2);
        authorDataService.create(a3);

        a1.getBooks().add(b1);
        a1.getBooks().add(b2);

        // author1 - book1, book2
        authorDataService.update(a1);

        b3 = bookDataService.findById(b3.getId());
        a1 = authorDataService.findById(a1.getId());

        a1.getBooks().add(b3);

        // author1 - book3 ( after this update it should be author1 - book1, book2, book3 )
        authorDataService.update(a1);

        a2 = authorDataService.findById(a2.getId());
        b2 = bookDataService.findById(b2.getId());

        a2.getBooks().add(b2);

        // author2 - book2
        authorDataService.update(a2);

        a3 = authorDataService.findById(a3.getId());
        b2 = bookDataService.findById(b2.getId());
        b3 = bookDataService.findById(b3.getId());

        a3.getBooks().add(b2);
        a3.getBooks().add(b3);

        // author3 - book2, book3
        authorDataService.update(a3);

        // Retrieve all objects to check if many to many works correctly
        a1 = authorDataService.findById(a1.getId());
        a2 = authorDataService.findById(a2.getId());
        a3 = authorDataService.findById(a3.getId());

        b1 = bookDataService.findById(b1.getId());
        b2 = bookDataService.findById(b2.getId());
        b3 = bookDataService.findById(b3.getId());

        // After all changes relation 'author - book' should look like :
        // author1 - book1, book2, book3
        List<String> bookTitles = extract(a1.getBooks(), on(Book.class).getTitle());
        Collections.sort(bookTitles);
        assertEquals(asList("book1", "book2", "book3"), bookTitles);
        // author2 - book2
        assertEquals(asList("book2"), extract(a2.getBooks(), on(Book.class).getTitle()));
        // author3 - book2, book3
        bookTitles = extract(a3.getBooks(), on(Book.class).getTitle());
        Collections.sort(bookTitles);
        assertEquals(asList("book2", "book3"), bookTitles);

        //and 'book - author' :
        // book1 - author1
        assertEquals(asList("author1"), extract(b1.getAuthors(), on(Author.class).getName()));
        // book2 - author1, author2, author3
        List<String> authorNames = extract(b2.getAuthors(), on(Author.class).getName());
        Collections.sort(authorNames);
        assertEquals(asList("author1", "author2", "author3"), authorNames);
        // book3 - author1, author3
        authorNames = extract(b3.getAuthors(), on(Author.class).getName());
        Collections.sort(authorNames);
        assertEquals(asList("author1", "author3"), authorNames);
    }

    @Test
    public void shouldAddBooksInTransaction() {
        transactionTestService.addTwoBooks();

        List<Book> allBooks = bookDataService.retrieveAll();
        assertEquals(asList("txBook1", "txBook2"), extract(allBooks, on(Book.class).getTitle()));
    }

    @Test
    public void shouldRollbackTransactions() {
        boolean exCaught = false;
        try {
            transactionTestService.addTwoBooksAndRollback();
        } catch (IllegalStateException e) {
            exCaught = true;
        }

        assertTrue("Exception that was supposed to rollback the transaction was not thrown from the service", exCaught);

        List<Book> allBooks = bookDataService.retrieveAll();
        assertNotNull(allBooks);
        assertTrue(allBooks.isEmpty());
    }

    @Test
    public void testBoatClass() {

        Boat boat = new Boat(1900, 120);

        boatDataService.create(boat);

        List<Boat> created = boatDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(boat.getYearOfProduction(), created.get(0).getYearOfProduction(), 0.1);
        assertEquals(boat.getMaxSpeed(), created.get(0).getMaxSpeed());

        Boat toUpdate = created.get(0);
        toUpdate.setMaxSpeed(130);

        boatDataService.update(toUpdate);

        List<Boat> updated = boatDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getYearOfProduction(), updated.get(0).getYearOfProduction(), 0.1);
        assertEquals(toUpdate.getMaxSpeed(), updated.get(0).getMaxSpeed());

        boatDataService.delete(updated.get(0));

        assertEquals(0, boatDataService.retrieveAll().size());
    }

    @Test
    public void testMotorcycleClass() {

        Motorcycle motorcycle = new Motorcycle(1900, "Some Producer");

        motorcycleDataService.create(motorcycle);

        List<Motorcycle> created = motorcycleDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(motorcycle.getYearOfProduction(), created.get(0).getYearOfProduction(), 0.1);
        assertEquals(motorcycle.getProducer(), created.get(0).getProducer());

        Motorcycle toUpdate = created.get(0);
        toUpdate.setProducer("Some other Producer");

        motorcycleDataService.update(toUpdate);

        List<Motorcycle> updated = motorcycleDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getYearOfProduction(), updated.get(0).getYearOfProduction(), 0.1);
        assertEquals(toUpdate.getProducer(), updated.get(0).getProducer());

        motorcycleDataService.delete(updated.get(0));

        assertEquals(0, motorcycleDataService.retrieveAll().size());
    }

    @Test
    public void testTruckClass() {

        Truck truck = new Truck(1900, 2.0, 1000);

        truckDataService.create(truck);

        List<Truck> created = truckDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(truck.getYearOfProduction(), created.get(0).getYearOfProduction(), 0.1);
        assertEquals(truck.getEngineCapacity(), created.get(0).getEngineCapacity(), 0);
        assertEquals(truck.getVolume(), created.get(0).getVolume(), 0);

        Truck toUpdate = created.get(0);
        toUpdate.setVolume(2000);

        truckDataService.update(toUpdate);

        List<Truck> updated = truckDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getYearOfProduction(), updated.get(0).getYearOfProduction(), 0.1);
        assertEquals(toUpdate.getEngineCapacity(), updated.get(0).getEngineCapacity(), 0);
        assertEquals(toUpdate.getVolume(), updated.get(0).getVolume(), 0);

        truckDataService.delete(updated.get(0));

        assertEquals(0, truckDataService.retrieveAll().size());
    }

    @Test
    public void testDogClass() {

        Dog dog = new Dog(null, 3);

        dogDataService.create(dog);

        List<Dog> created = dogDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(dog.getHiddenBones(), created.get(0).getHiddenBones());

        Dog toUpdate = created.get(0);
        toUpdate.setHiddenBones(5);

        dogDataService.update(toUpdate);

        List<Dog> updated = dogDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getHiddenBones(), updated.get(0).getHiddenBones());

        dogDataService.delete(updated.get(0));

        assertEquals(0, dogDataService.retrieveAll().size());
    }

    @Test
    public void testCatClass() {

        Cat cat = new Cat(null, 9);

        catDataService.create(cat);

        List<Cat> created = catDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(cat.getLivesLeft(), created.get(0).getLivesLeft());

        Cat toUpdate = created.get(0);
        toUpdate.setLivesLeft(8);

        catDataService.update(toUpdate);

        List<Cat> updated = catDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getLivesLeft(), updated.get(0).getLivesLeft());

        catDataService.delete(updated.get(0));

        assertEquals(0, catDataService.retrieveAll().size());
    }

    @Test
    public void testGoldfishClass() {

        Goldfish goldfish = new Goldfish(null, 500, 3);

        goldfishDataService.create(goldfish);

        List<Goldfish> created = goldfishDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(goldfish.getLength(), created.get(0).getLength());
        assertEquals(goldfish.getWishesLeft(), created.get(0).getWishesLeft());

        Goldfish toUpdate = created.get(0);
        toUpdate.setWishesLeft(2);

        goldfishDataService.update(toUpdate);

        List<Goldfish> updated = goldfishDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getLength(), updated.get(0).getLength());
        assertEquals(toUpdate.getWishesLeft(), updated.get(0).getWishesLeft());

        goldfishDataService.delete(updated.get(0));

        assertEquals(0, goldfishDataService.retrieveAll().size());
    }

    @Test
    public void testVehicleOwnerClass() {

        List<Vehicle> vehicles = new ArrayList<>();

        vehicles.add(new Boat(1992, 120));
        vehicles.add(new Truck(1993, 2.0, 5000));
        vehicles.add(new Motorcycle(1994, "Some yet other Producer"));

        VehicleOwner person = new VehicleOwner(40, vehicles);

        vehicleOwnerDataService.create(person);

        List<VehicleOwner> created = vehicleOwnerDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(person.getAge(), created.get(0).getAge());
        assertVehicleListEquals(person.getVehicles(), created.get(0).getVehicles());

        VehicleOwner toUpdate = created.get(0);
        toUpdate.setAge(16);

        vehicleOwnerDataService.update(toUpdate);

        List<VehicleOwner> updated = vehicleOwnerDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getAge(), updated.get(0).getAge());
        assertVehicleListEquals(toUpdate.getVehicles(), updated.get(0).getVehicles());

        vehicleOwnerDataService.delete(updated.get(0));

        assertEquals(0, vehicleOwnerDataService.retrieveAll().size());
    }

    @Test
    public void testPetOwnerClass() throws Exception {

        PetOwner person = new PetOwner(30, null);

        List<Pet> pets = new ArrayList<>();

        pets.add(new Cat(person, 9));
        pets.add(new Dog(person, 2));
        pets.add(new Goldfish(person, 10, 2));

        person.setPets(pets);

        petOwnerDataService.create(person);

        List<PetOwner> created = petOwnerDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(person.getAge(), created.get(0).getAge());
        assertPetListEquals(person.getPets(), created.get(0).getPets());

        PetOwner toUpdate = created.get(0);
        toUpdate.setAge(16);

        petOwnerDataService.update(toUpdate);

        List<PetOwner> updated = petOwnerDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getAge(), updated.get(0).getAge());
        assertPetListEquals(toUpdate.getPets(), updated.get(0).getPets());

        petOwnerDataService.delete(updated.get(0));

        assertEquals(0, petOwnerDataService.retrieveAll().size());
    }

    private void assertDefaultConstructorPresent() throws ClassNotFoundException {
        Class<?> clazz = MDSClassLoader.getInstance().loadClass(TestMdsEntity.class.getName());
        Constructor[] constructors = clazz.getConstructors();

        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return;
            }
        }

        fail("Default constructor has not been found for ".concat(clazz.getName()));
    }

    private void verifyDDE() {
        getLogger().info("Verify DDE");

        TestMdsEntity expected = new TestMdsEntity("name");
        testMdsEntityService.create(expected);

        List<TestMdsEntity> testMdsEntities = testMdsEntityService.retrieveAll();
        assertEquals(asList(expected), testMdsEntities);

        TestMdsEntity actual = testMdsEntities.get(0);

        assertEquals(actual.getModifiedBy(), "motech");
        assertEquals(actual.getCreator(),"motech");
        assertEquals(actual.getOwner(),"motech");
        assertNotNull(actual.getId());

        actual.setSomeString("newName");
        actual.setOwner("newOwner");
        DateTime modificationDate = actual.getModificationDate();
        testMdsEntityService.update(actual);

        testMdsEntities = testMdsEntityService.retrieveAll();
        assertEquals(asList(actual), testMdsEntities);

        assertEquals(testMdsEntities.get(0).getOwner(), "newOwner");
        //Actual modificationDate of instance should be after previous one
        assertTrue(modificationDate.isBefore(testMdsEntities.get(0).getModificationDate()));
    }

    private void verifyLookup() {
        getLogger().info("Verify Lookup");

        TestLookup field1 = new TestLookup("someString1", "superClassString1");
        TestLookup field2 = new TestLookup("someString2", "superClassString2");
        TestLookup field3 = new TestLookup("someString3", "superClassString3");

        testLookupService.create(field1);
        testLookupService.create(field2);
        testLookupService.create(field3);

        List<TestLookup> testLookupByInheritedFieldList = testLookupService.findByInheritedField("superClassString3");
        List<TestLookup> testLookupByAutoGeneratedFieldList = testLookupService.findByAutoGeneratedField("motech");

        assertEquals(asList(field3), testLookupByInheritedFieldList);
        assertEquals(asList(field1, field2, field3), testLookupByAutoGeneratedFieldList);
    }

    private void setUpSecurityContext() {
        getLogger().info("Setting up security context");

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        authentication.setAuthenticated(false);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private void wait2s() throws InterruptedException {
        synchronized (waitLock) {
            waitLock.wait(2000);
        }
    }

    private void assertPetListEquals(List<Pet> expected, List<Pet> actual) {

        if (expected.size() != actual.size()) {
            throw new AssertionError("Lists differ in size!");
        }

        for (int i = 0; i < expected.size(); i++) {
            if (!assertPetsEquals(expected.get(i), actual.get(i))) {
                throw new AssertionError("Objects are not equal!");
            }
        }
    }

    private void assertVehicleListEquals(List<Vehicle> expected, List<Vehicle> actual) {

        if (expected.size() != actual.size()) {
            throw new AssertionError("Lists differ in size!");
        }

        for (int i = 0; i < expected.size(); i++) {
            if (!assertVehicleEquals(expected.get(i), actual.get(i))) {
                throw new AssertionError("Objects are not equal!");
            }
        }
    }

    private boolean assertPetsEquals(Pet expected, Pet actual) {

        if (expected instanceof Cat && actual instanceof Cat) {
            return ((Cat) expected).getLivesLeft() == ((Cat) actual).getLivesLeft();
        } else if (expected instanceof Dog && actual instanceof Dog) {
            return ((Dog) expected).getHiddenBones() == ((Dog) actual).getHiddenBones();
        } else if (expected instanceof Goldfish && actual instanceof Goldfish) {
            return ((Goldfish) expected).getWishesLeft() == ((Goldfish) actual).getWishesLeft();
        } else {
            throw new AssertionError("Classes mismatch!");
        }
    }

    private boolean assertVehicleEquals(Vehicle expected, Vehicle actual) {

        if (expected.getYearOfProduction() != actual.getYearOfProduction()) {
            return false;
        }

        if (expected instanceof Boat && actual instanceof Boat) {
            return ((Boat) expected).getMaxSpeed() == ((Boat) actual).getMaxSpeed();
        } else if (expected instanceof Motorcycle && actual instanceof Motorcycle) {
            return ((Motorcycle) expected).getProducer().equals(((Motorcycle) actual).getProducer());
        } else if (expected instanceof Truck && actual instanceof Truck) {
            return ((Truck) expected).getVolume() == ((Truck) actual).getVolume() &&
                    ((Truck) expected).getEngineCapacity() == ((Truck) actual).getEngineCapacity();
        } else {
            throw new AssertionError("Classes mismatch!");
        }
    }
}
