package org.motechproject.mds.test.osgi;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.test.domain.Actor;
import org.motechproject.mds.test.domain.Movie;
import org.motechproject.mds.test.domain.TestLookup;
import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.domain.TestSingleReturnLookup;
import org.motechproject.mds.test.domain.cascadedelete.City;
import org.motechproject.mds.test.domain.cascadedelete.Country;
import org.motechproject.mds.test.domain.editablelookups.Entry;
import org.motechproject.mds.test.domain.historytest.Address;
import org.motechproject.mds.test.domain.historytest.Company;
import org.motechproject.mds.test.domain.historytest.Computer;
import org.motechproject.mds.test.domain.historytest.Consultant;
import org.motechproject.mds.test.domain.historytest.House;
import org.motechproject.mds.test.domain.historytest.Network;
import org.motechproject.mds.test.domain.inheritancestrategies.Boat;
import org.motechproject.mds.test.domain.inheritancestrategies.Cat;
import org.motechproject.mds.test.domain.inheritancestrategies.Dog;
import org.motechproject.mds.test.domain.inheritancestrategies.Goldfish;
import org.motechproject.mds.test.domain.inheritancestrategies.Motorcycle;
import org.motechproject.mds.test.domain.inheritancestrategies.Pet;
import org.motechproject.mds.test.domain.inheritancestrategies.PetOwner;
import org.motechproject.mds.test.domain.inheritancestrategies.Truck;
import org.motechproject.mds.test.domain.inheritancestrategies.Vehicle;
import org.motechproject.mds.test.domain.inheritancestrategies.VehicleOwner;
import org.motechproject.mds.test.domain.instancelifecyclelistener.SubclassA;
import org.motechproject.mds.test.domain.instancelifecyclelistener.SubclassB;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.LogAttribute;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.LogParameters;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.LogStatus;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.MessageLog;
import org.motechproject.mds.test.domain.manytomany.Author;
import org.motechproject.mds.test.domain.manytomany.Book;
import org.motechproject.mds.test.domain.manytomany.Clinic;
import org.motechproject.mds.test.domain.manytomany.Patient;
import org.motechproject.mds.test.domain.manytomany.Product;
import org.motechproject.mds.test.domain.manytomany.Supplier;
import org.motechproject.mds.test.domain.optimisticlocking.SimpleClassWithVersioning;
import org.motechproject.mds.test.domain.optimisticlocking.TestMdsVersionedEntity;
import org.motechproject.mds.test.domain.relationshipswithhistory.District;
import org.motechproject.mds.test.domain.relationshipswithhistory.Language;
import org.motechproject.mds.test.domain.relationshipswithhistory.State;
import org.motechproject.mds.test.domain.revertFromTrash.HomeAddress;
import org.motechproject.mds.test.domain.setofenumandstring.Channel;
import org.motechproject.mds.test.domain.setofenumandstring.Message;
import org.motechproject.mds.test.domain.transactions.Department;
import org.motechproject.mds.test.domain.transactions.Employee;
import org.motechproject.mds.test.service.ActorDataService;
import org.motechproject.mds.test.service.MovieDataService;
import org.motechproject.mds.test.service.TestLookupService;
import org.motechproject.mds.test.service.TestMdsEntityService;
import org.motechproject.mds.test.service.revertFromTrash.RevertFromTrashService;
import org.motechproject.mds.test.service.TestSingleReturnLookupService;
import org.motechproject.mds.test.service.TransactionTestService;
import org.motechproject.mds.test.service.cascadedelete.CityDataService;
import org.motechproject.mds.test.service.cascadedelete.CountryDataService;
import org.motechproject.mds.test.service.editablelookups.EntryDataService;
import org.motechproject.mds.test.service.historytest.AddressDataService;
import org.motechproject.mds.test.service.historytest.CompanyDataService;
import org.motechproject.mds.test.service.historytest.ConsultantDataService;
import org.motechproject.mds.test.service.historytest.HouseDataService;
import org.motechproject.mds.test.service.historytest.NetworkDataService;
import org.motechproject.mds.test.service.inheritancestrategies.BoatDataService;
import org.motechproject.mds.test.service.inheritancestrategies.CatDataService;
import org.motechproject.mds.test.service.inheritancestrategies.DogDataService;
import org.motechproject.mds.test.service.inheritancestrategies.GoldfishDataService;
import org.motechproject.mds.test.service.inheritancestrategies.MotorcycleDataService;
import org.motechproject.mds.test.service.inheritancestrategies.PetOwnerDataService;
import org.motechproject.mds.test.service.inheritancestrategies.TruckDataService;
import org.motechproject.mds.test.service.inheritancestrategies.VehicleOwnerDataService;
import org.motechproject.mds.test.service.instancelifecyclelistener.SubclassADataService;
import org.motechproject.mds.test.service.instancelifecyclelistener.SubclassBDataService;
import org.motechproject.mds.test.service.lookupcomboboxrelation.MessageLogDataService;
import org.motechproject.mds.test.service.manytomany.AuthorDataService;
import org.motechproject.mds.test.service.manytomany.BookDataService;
import org.motechproject.mds.test.service.manytomany.ClinicDataService;
import org.motechproject.mds.test.service.manytomany.PatientDataService;
import org.motechproject.mds.test.service.manytomany.ProductDataService;
import org.motechproject.mds.test.service.manytomany.SupplierDataService;
import org.motechproject.mds.test.service.optimisticlocking.SimpleClassWithVersioningService;
import org.motechproject.mds.test.service.optimisticlocking.TestMdsVersionedEntityService;
import org.motechproject.mds.test.service.relationshipswithhistory.DistrictDataService;
import org.motechproject.mds.test.service.relationshipswithhistory.LanguageDataService;
import org.motechproject.mds.test.service.relationshipswithhistory.StateDataService;
import org.motechproject.mds.test.service.setofenumandstring.MessageDataService;
import org.motechproject.mds.test.service.transactions.DepartmentDataService;
import org.motechproject.mds.test.service.transactions.EmployeeDataService;
import org.motechproject.mds.test.service.transactions.OfficeService;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.StateManagerUtil;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.springframework.orm.jdo.JdoOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.jdo.JDOUserException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.motechproject.mds.event.CrudEventBuilder.createSubject;
import static org.motechproject.mds.util.ClassName.simplifiedModuleName;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_CLASS;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_NAME;
import static org.motechproject.mds.util.Constants.MDSEvents.MODULE_NAME;
import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;
import static org.motechproject.mds.util.PropertyUtil.safeGetProperty;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsDdeBundleIT extends BasePaxIT {

    private static final QueryParams ASC_ID = QueryParams.ascOrder(ID_FIELD_NAME);

    @Inject
    private TrashService trashService;

    @Inject
    private TestMdsEntityService testMdsEntityService;

    @Inject
    private TestLookupService testLookupService;

    @Inject
    private RevertFromTrashService revertFromTrashService;

    @Inject
    private BookDataService bookDataService;

    @Inject
    private AuthorDataService authorDataService;

    @Inject
    private SubclassADataService subclassADataService;

    @Inject
    private SubclassBDataService subclassBDataService;

    @Inject
    private PatientDataService patientDataService;

    @Inject
    private ClinicDataService clinicDataService;

    @Inject
    private HistoryService historyService;

    @Inject
    private DistrictDataService districtDataService;

    @Inject
    private LanguageDataService languageDataService;

    @Inject
    private StateDataService stateDataService;

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

    @Inject
    private CityDataService cityDataService;

    @Inject
    private CountryDataService countryDataService;

    @Inject
    private MessageDataService messageDataService;

    @Inject
    private MessageLogDataService messageLogDataService;

    @Inject
    private EntryDataService entryDataService;

    @Inject
    private EntityService entityService;

    @Inject
    private TestSingleReturnLookupService testSingleReturnLookupService;

    @Inject
    private OfficeService officeService;

    @Inject
    private DepartmentDataService departmentDataService;

    @Inject
    private EmployeeDataService employeeDataService;

    @Inject
    private MDSLookupService lookupService;

    @Inject
    private MovieDataService movieDataService;

    @Inject
    private ActorDataService actorDataService;

    @Inject
    private ProductDataService productDataService;

    @Inject
    private SupplierDataService supplierDataService;

    @Inject
    private TestMdsVersionedEntityService testMdsVersionedEntityService;

    @Inject
    private SimpleClassWithVersioningService simpleClassWithVersioningService;

    @Inject
    private NetworkDataService networkDataService;

    @Inject
    private HouseDataService houseDataService;

    @Inject
    private AddressDataService addressDataService;

    @Inject
    private CompanyDataService companyDataService;

    @Inject
    private ConsultantDataService consultantDataService;

    private final Object waitLock = new Object();

    @Before
    public void setUp() throws Exception {
        setUpSecurityContextForDefaultUser("mdsSchemaAccess");
        clearDB();
    }

    @After
    public void tearDown() {
        clearDB();
    }

    private void clearDB() {
        testMdsEntityService.deleteAll();
        testLookupService.deleteAll();
        revertFromTrashService.deleteAll();
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
        patientDataService.deleteAll();
        clinicDataService.deleteAll();
        stateDataService.deleteAll();
        districtDataService.deleteAll();
        languageDataService.deleteAll();
        cityDataService.deleteAll();
        countryDataService.deleteAll();
        testSingleReturnLookupService.deleteAll();
        departmentDataService.deleteAll();
        employeeDataService.deleteAll();
        messageLogDataService.deleteAll();
        messageDataService.deleteAll();
        testMdsVersionedEntityService.deleteAll();
        simpleClassWithVersioningService.deleteAll();
        consultantDataService.deleteAll();
        companyDataService.deleteAll();
        entryDataService.deleteAll();
        removeFromListManyToMany();
    }

    private void removeFromListManyToMany() {
        // we must delete relation to avoid sql integrity constraint violation
        for (Movie m : movieDataService.retrieveAll()) {
            m.setActors(new ArrayList<Actor>());
            movieDataService.update(m);
        }
        for (Actor a : actorDataService.retrieveAll()) {
            a.setMovies(new ArrayList<Movie>());
            actorDataService.update(a);
        }
        movieDataService.deleteAll();
        actorDataService.deleteAll();

        for (Supplier p : supplierDataService.retrieveAll()) {
            p.setProducts(new ArrayList<Product>());
            supplierDataService.update(p);
        }
        for (Product c : productDataService.retrieveAll()) {
            c.setSuppliers(new ArrayList<Supplier>());
            productDataService.update(c);
        }
        productDataService.deleteAll();
        supplierDataService.deleteAll();
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
    public void shouldUpdateRelationshipInTransaction() {
        // Create department and add 2 members
        Department department = new Department("Sales Department");
        Employee employee1 = new Employee("John D.");
        Employee employee2 = new Employee("Chris T.");

        Set<Employee> employees = new LinkedHashSet<>();
        employees.add(employee1);
        employees.add(employee2);

        department.setEmployees(employees);
        departmentDataService.create(department);

        // Create and add new employee
        Employee employee3 = employeeDataService.detachedCopy(employeeDataService.create(new Employee("Elliot R.")));
        employee1 = employeeDataService.detachedCopy(employeeDataService.findById(employee1.getId()));

        // Create a set of existing and new employee
        Set<Employee> otherEmployees = new LinkedHashSet<>();
        otherEmployees.add(employee1);
        otherEmployees.add(employee3);

        // Create a new department
        Department anotherDepartment = departmentDataService.create(new Department("Marketing Department"));

        // Update the department with its members in transaction
        officeService.saveEmployees(anotherDepartment.getId(), otherEmployees);

        //Then
        List<Department> departments = departmentDataService.retrieveAll();
        assertEquals(2, departments.size());

        // check the employees for the new department
        anotherDepartment = departmentDataService.findById(anotherDepartment.getId());
        assertEquals(2, anotherDepartment.getEmployees().size());

        department = departmentDataService.findById(department.getId());
        assertEquals(1, department.getEmployees().size());
    }

    @Test
    public void testSetOfEnumsAndSetOfStrings() {
        Message message = new Message("New Message", "Hello, this is a new message");
        message.getBroadcastChannels().add(Channel.EMAIL);
        message.getBroadcastChannels().add(Channel.FAX);
        message.getRecipients().addAll(Arrays.asList("Ana", "Katy", "Christina"));

        messageDataService.create(message);
        assertEquals(1, messageDataService.count());

        Message retrieved = messageDataService.getMessageBySubject("New Message");
        assertNotNull(retrieved);

        assertTrue(CollectionUtils.contains(retrieved.getBroadcastChannels().iterator(), Channel.EMAIL));
        assertTrue(CollectionUtils.contains(retrieved.getBroadcastChannels().iterator(), Channel.FAX));

        assertTrue(CollectionUtils.contains(retrieved.getRecipients().iterator(), "Ana"));
        assertTrue(CollectionUtils.contains(retrieved.getRecipients().iterator(), "Katy"));
        assertTrue(CollectionUtils.contains(retrieved.getRecipients().iterator(), "Christina"));
    }

    @Test
    public void testCascadeDelete() {
        City warsaw = new City("Warsaw");
        cityDataService.create(warsaw);

        City gdynia = new City("Gdynia");
        cityDataService.create(gdynia);

        Country poland = new Country("Poland");
        poland.getCities().add(warsaw);

        poland.setCapital(warsaw);

        countryDataService.create(poland);

        Country co1 = countryDataService.findById(poland.getId());
        assertNotNull(co1);

        City ci1 = cityDataService.findById(warsaw.getId());
        assertNotNull(ci1);

        countryDataService.delete(co1);
        co1 = countryDataService.findById(poland.getId());
        assertNull(co1);

        ci1 = cityDataService.findById(warsaw.getId());
        assertNull(ci1);
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
    public void testHistoryTrackingWithRelationships() {
        District district = new District();
        district.setName("district1");
        State state = new State();
        state.setName("state1");
        Language lang = new Language();
        lang.setName("eng");

        district = districtDataService.detachedCopy(districtDataService.create(district));
        state = stateDataService.detachedCopy(stateDataService.create(state));
        lang = languageDataService.detachedCopy(languageDataService.create(lang));

        state.getLanguages().add(lang);
        state.getDistricts().add(district);
        stateDataService.update(state);

        List audit = historyService.getHistoryForInstance(district, null);
        assertNotNull(audit);
        assertEquals(1, audit.size());

        State retrievedState = stateDataService.findByName("state1");
        retrievedState.getLanguages().clear();
        stateDataService.update(retrievedState);

        // check what happened for districts
        // Latest version should not be included in the history audit
        audit = historyService.getHistoryForInstance(district, null);
        assertNotNull(audit);
        assertEquals(1, audit.size());

        Object firstRevision = audit.get(0); //Initial revision - no relations
        assertEquals("district1", safeGetProperty(firstRevision, "name"));
        assertNull(safeGetProperty(firstRevision, "state"));
        assertNull(safeGetProperty(firstRevision, "language"));

        final State retrievedState2 = stateDataService.detachedCopy(stateDataService.findByName(state.getName()));
        retrievedState2.setDefaultDistrict(district);
        stateDataService.update(retrievedState2);

        retrievedState2.getLanguages().clear();
        stateDataService.update(retrievedState2);


        audit = historyService.getHistoryForInstance(retrievedState2, null);
        assertNotNull(audit);
        assertEquals(4, audit.size());

        Object lastRevision = audit.get(3);
        assertNotNull(safeGetProperty(lastRevision, "defaultDistrict"));
        assertNotNull(safeGetProperty(lastRevision, "districts"));
    }

    @Test
    public void testManyToManyRelationshipWithCustomTableAndColumnNames() {
        Patient patient = new Patient("patient1");
        Patient patient2 = new Patient("patient2");

        Clinic clinic = new Clinic("clinic1");
        Clinic clinic2 = new Clinic("clinic2");
        Clinic clinic3 = new Clinic("clinic3");

        patient = patientDataService.detachedCopy(patientDataService.create(patient));
        patient2 = patientDataService.detachedCopy(patientDataService.create(patient2));

        clinic = clinicDataService.detachedCopy(clinicDataService.create(clinic));
        clinic2 = clinicDataService.detachedCopy(clinicDataService.create(clinic2));
        clinic3 = clinicDataService.detachedCopy(clinicDataService.create(clinic3));

        patient.getClinics().add(clinic);
        patient.getClinics().add(clinic2);
        patientDataService.update(patient);

        // Test that the relationship was added
        Patient retrievedPatient = patientDataService.findByName("patient1");
        assertNotNull(retrievedPatient);
        assertEquals(2, retrievedPatient.getClinics().size());

        //Test that the backlink has been created (It's bi-directional relationship)
        Clinic retrievedClinic = clinicDataService.detachedCopy(clinicDataService.findByName("clinic1"));
        Clinic retrievedClinic2 = clinicDataService.detachedCopy(clinicDataService.findByName("clinic2"));
        assertNotNull(retrievedClinic);
        assertNotNull(retrievedClinic2);
        assertEquals(1, retrievedClinic.getPatients().size());
        assertEquals(1, retrievedClinic2.getPatients().size());

        // We use the clinic objects we retrieved, since they contain information about existing relationship
        patient2.getClinics().add(retrievedClinic);
        patient2.getClinics().add(retrievedClinic2);
        patient2.getClinics().add(clinic3);

        patientDataService.update(patient2);

        // Test that the relationship was added
        retrievedPatient = patientDataService.findByName("patient2");
        assertNotNull(retrievedPatient);
        assertEquals(3, retrievedPatient.getClinics().size());

        //Test that the backlink has been created (It's bi-directional relationship)
        Clinic retrievedClinic11 = clinicDataService.findByName("clinic1");
        Clinic retrievedClinic12 = clinicDataService.findByName("clinic2");
        Clinic retrievedClinic13 = clinicDataService.findByName("clinic3");

        assertNotNull(retrievedClinic11);
        assertNotNull(retrievedClinic12);
        assertNotNull(retrievedClinic13);

        //Previous relations should not get removed
        assertEquals(2, retrievedClinic11.getPatients().size());
        assertEquals(2, retrievedClinic12.getPatients().size());

        //This is a new relation
        assertEquals(1, retrievedClinic13.getPatients().size());
    }

    @Test
    public void testManyToManyRelationshipList() {
        getLogger().info("Test Many to Many List relationship");

        setUpActorMovieData();

        // Verify m1 -> a1
        Actor a = actorDataService.findByName("actor1");
        verifyActorMovie(a, "actor1", "movie1", 1, 1, 0, 0);

        Movie m = movieDataService.findByName("movie1");
        verifyMovieActor(m, "actor1", "movie1", 1, 1, 0, 0);

        // Verify m2 -> a2, a3
        a = actorDataService.findByName("actor2");
        verifyActorMovie(a, "actor2", "movie2", 1, 2, 0, 0);
        a = actorDataService.findByName("actor3");
        verifyActorMovie(a, "actor3", "movie2", 1, 2, 0, 1);

        m = movieDataService.findByName("movie2");
        verifyMovieActor(m, "actor2", "movie2", 1, 2, 0, 0);
        verifyMovieActor(m, "actor3", "movie2", 1, 2, 0, 1);

        // Verify m3, m4 -> a4
        a = actorDataService.findByName("actor4");
        verifyActorMovie(a, "actor4", "movie3", 2, 1, 0, 0);
        verifyActorMovie(a, "actor4", "movie4", 2, 1, 1, 0);

        m = movieDataService.findByName("movie3");
        verifyMovieActor(m, "actor4", "movie3", 2, 1, 0, 0);
        m = movieDataService.findByName("movie4");
        verifyMovieActor(m, "actor4", "movie4", 2, 1, 1, 0);

        // Delete m1 and a1
        m = movieDataService.detachedCopy(movieDataService.findByName("movie1"));
        m.getActors().remove(0);
        movieDataService.update(m);

        a = actorDataService.findByName("actor1");
        actorDataService.delete(a);

        assertEquals(4l, movieDataService.count());
        assertEquals(3l, actorDataService.count());

        movieDataService.delete(m);
        assertEquals(3l, movieDataService.count());

        // m2 -> a2, a3, a5
        Actor a5 = new Actor("actor5");
        a5 = actorDataService.detachedCopy(actorDataService.create(a5));

        m = movieDataService.detachedCopy(movieDataService.findByName("movie2"));
        m.getActors().add(0, a5);
        a5.getMovies().add(m);
        movieDataService.update(m);
        verifyMovieActor(m, "actor5", "movie2", 1, 3, 0, 0);

        // remove a5 from m2
        m.getActors().remove(0);
        movieDataService.update(m);
        m = movieDataService.findByName("movie2");
        verifyMovieActor(m, "actor2", "movie2", 1, 2, 0, 0);
    }

    @Test
    public void testManyToManyRelationshipListWithCustomTableNames() {
        getLogger().info("Test Many to Many List relationship with custom table names");

        Product p1 = new Product("product1");
        Product p2 = new Product("product2");
        Product p3 = new Product("product3");
        Product p4 = new Product("product4");
        Product p5 = new Product("product5");
        Product p6 = new Product("product6");
        Product p7 = new Product("product7");

        Supplier s1 = new Supplier("supplier1");
        Supplier s2 = new Supplier("supplier2");

        p1 = productDataService.detachedCopy(productDataService.create(p1));
        p2 = productDataService.detachedCopy(productDataService.create(p2));
        p3 = productDataService.detachedCopy(productDataService.create(p3));
        p4 = productDataService.detachedCopy(productDataService.create(p4));
        p5 = productDataService.detachedCopy(productDataService.create(p5));
        p6 = productDataService.detachedCopy(productDataService.create(p6));
        p7 = productDataService.detachedCopy(productDataService.create(p7));

        s1 = supplierDataService.detachedCopy(supplierDataService.create(s1));
        s2 = supplierDataService.detachedCopy(supplierDataService.create(s2));

        s1.getProducts().add(p1);
        s1.getProducts().add(p2);
        s1.getProducts().add(p3);
        s1.getProducts().add(p4);
        s1.getProducts().add(p5);

        p1.getSuppliers().add(s1);
        p2.getSuppliers().add(s1);
        p3.getSuppliers().add(s1);
        p4.getSuppliers().add(s1);
        p5.getSuppliers().add(s1);

        supplierDataService.update(s1);

        s2.getProducts().add(p4);
        s2.getProducts().add(p5);
        s2.getProducts().add(p6);
        s2.getProducts().add(p7);

        p4.getSuppliers().add(s2);
        p5.getSuppliers().add(s2);
        p6.getSuppliers().add(s2);
        p7.getSuppliers().add(s2);

        supplierDataService.update(s2);

        // verify s1 -> p1, p2, p3, p4, p5
        Supplier supplier = supplierDataService.findBySupplierName("supplier1");
        List<String> products = extract(supplier.getProducts(), on(Product.class).getName());
        assertEquals(asList("product1", "product2", "product3", "product4", "product5"), products);

        // verify s2 -> p4, p5, p6, p7
        supplier = supplierDataService.findBySupplierName("supplier2");
        products = extract(supplier.getProducts(), on(Product.class).getName());
        assertEquals(asList("product4", "product5", "product6", "product7"), products);
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

        b1 = bookDataService.detachedCopy(bookDataService.create(b1));
        b2 = bookDataService.detachedCopy(bookDataService.create(b2));
        b3 = bookDataService.detachedCopy(bookDataService.create(b3));

        a1 = authorDataService.detachedCopy(authorDataService.create(a1));
        a2 = authorDataService.detachedCopy(authorDataService.create(a2));
        a3 = authorDataService.detachedCopy(authorDataService.create(a3));

        a1.getBooks().add(b1);
        a1.getBooks().add(b2);

        // author1 - book1, book2
        a1 = authorDataService.detachedCopy(authorDataService.update(a1));
        b1 = bookDataService.detachedCopy(bookDataService.findById(b1.getId()));
        b2 = bookDataService.detachedCopy(bookDataService.findById(b2.getId()));

        a1.getBooks().add(b3);

        // author1 - book3 ( after this update it should be author1 - book1, book2, book3 )
        authorDataService.update(a1);
        b3 = bookDataService.detachedCopy(bookDataService.findById(b3.getId()));

        a2.getBooks().add(b2);

        // author2 - book2
        authorDataService.update(a2);
        b2 = bookDataService.detachedCopy(bookDataService.findById(b2.getId()));

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
        dog.setWeight(35.6f);

        dogDataService.create(dog);

        List<Dog> created = dogDataService.retrieveAll();

        assertEquals(1, created.size());
        assertEquals(dog.getHiddenBones(), created.get(0).getHiddenBones());
        assertEquals(dog.getWeight(), created.get(0).getWeight(), 0.01);

        Dog toUpdate = created.get(0);
        toUpdate.setHiddenBones(5);
        toUpdate.setWeight(24.2f);

        dogDataService.update(toUpdate);

        List<Dog> updated = dogDataService.retrieveAll();

        assertEquals(1, updated.size());
        assertEquals(toUpdate.getHiddenBones(), updated.get(0).getHiddenBones());
        assertEquals(toUpdate.getWeight(), updated.get(0).getWeight(), 0.01);

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

        List<PetOwner> created = petOwnerDataService.detachedCopyAll(petOwnerDataService.retrieveAll());

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

    @Test(expected = JDOUserException.class)
    public void testSingleReturnLookup() {

        TestSingleReturnLookup single1 = new TestSingleReturnLookup("sameField", "notSameField");
        TestSingleReturnLookup single2 = new TestSingleReturnLookup("sameField", "anotherField");

        testSingleReturnLookupService.create(single1);
        testSingleReturnLookupService.create(single2);

        // As 'findByFirstName' is single return lookup it should return only
        // one object or null, so it is expected to exception be thrown when
        // searching for 'sameField' which appears twice
        testSingleReturnLookupService.findByFirstFieldName("sameField");
    }

    @Test
    public void testDistrictLookupsOnRelationshipFields() {
        setUpDataForLookupsOnRelationshipFields();

        List<District> districts = districtDataService.findByLanguage("language_1");
        assertEquals(1, districts.size());
        assertEquals("district_1", districts.get(0).getName());

        districts = districtDataService.findByLanguage("language_2");
        assertEquals(1, districts.size());
        assertEquals("district_2", districts.get(0).getName());

        districts = districtDataService.findByLanguage("language_3_eng");
        assertEquals(1, districts.size());
        assertEquals("district_3", districts.get(0).getName());

        districts = districtDataService.findByNameAndLanguage("district_2", "language_2");
        assertEquals(1, districts.size());
        assertEquals("district_2", districts.get(0).getName());
        assertEquals("language_2", districts.get(0).getLanguage().getName());

        districts = districtDataService.findByNameAndLanguage("district_1", "language_4_eng");
        assertEquals(0, districts.size());

        districts = districtDataService.findByNameAndLanguageWithOperators("district_3", "eng");
        assertEquals(1, districts.size());
        assertEquals("district_3", districts.get(0).getName());
        assertEquals("language_3_eng", districts.get(0).getLanguage().getName());

        districts = districtDataService.findByNameAndLanguageWithOperators("district_2", "eng");
        assertEquals(0, districts.size());

        final Set<String> languageNames = new HashSet<>();
        languageNames.add("language_1");
        languageNames.add("language_2");

        districtDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                List<District> districts = districtDataService.findByNameLanguageAndState(languageNames, "state_1");
                assertEquals(1, districts.size());
                assertEquals("district_1", districts.get(0).getName());
                assertEquals("state_1", districts.get(0).getState().getName());
            }
        });

        districtDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                List<District> districts = districtDataService.findByNameLanguageAndState(languageNames, "state_2");
                assertEquals(1, districts.size());
                assertEquals("district_2", districts.get(0).getName());
                assertEquals("state_2", districts.get(0).getState().getName());
            }
        });

        districts = districtDataService.findByNameLanguageAndState(languageNames, "state_3");
        assertEquals(0, districts.size());
    }


    @Test
    public void testLanguageLookupsOnRelationshipFields() {
        setUpDataForLookupsOnRelationshipFields();

        List<Language> languages = languageDataService.findByDistrictName("district_1");
        assertEquals(1, languages.size());
        assertEquals("language_1", languages.get(0).getName());
        assertEquals("district_1", ((District)(languages.get(0).getDistricts().toArray()[0])).getName());

        languages = languageDataService.findByDistrictName("district_3");
        assertEquals(1, languages.size());
        assertEquals("language_3_eng", languages.get(0).getName());
        assertEquals("district_3", ((District)(languages.get(0).getDistricts().toArray()[0])).getName());

        languages = languageDataService.findByDistrictSerialNumber(123l);
        assertEquals(1, languages.size());
        assertEquals("language_1", languages.get(0).getName());
        assertEquals("district_1", ((District)(languages.get(0).getDistricts().toArray()[0])).getName());

        languages = languageDataService.findByDistrictSerialNumber(220l);
        assertEquals(1, languages.size());
        assertEquals("language_4_eng", languages.get(0).getName());
        assertEquals("district_4", ((District)(languages.get(0).getDistricts().toArray()[0])).getName());

        languages = languageDataService.findByDistrictNameAndSerialNumber("distr", new Range<Long>(100l, 240l));
        assertEquals(2, languages.size());
        assertEquals("language_1", languages.get(0).getName());
        assertEquals("district_1", ((District)(languages.get(0).getDistricts().toArray()[0])).getName());
        assertEquals("language_4_eng", languages.get(1).getName());
        assertEquals("district_4", ((District)(languages.get(1).getDistricts().toArray()[0])).getName());

        languages = languageDataService.findByDistrictSerialNumber(3111l);
        assertEquals(1, languages.size());
        assertEquals(2, languages.get(0).getDistricts().size());
        assertEquals("english", languages.get(0).getName());

        languages = languageDataService.findByDistrictNameAndSerialNumber("", new Range<Long>(3111l, 3599l));
        assertEquals(2, languages.size());
        assertEquals(2, languages.get(0).getDistricts().size());
        assertEquals(2, languages.get(1).getDistricts().size());
        assertEquals("english", languages.get(0).getName());
        assertEquals("french", languages.get(1).getName());
    }

    @Test
    public void testLookupsOnComboboxRelationshipFields() {
        setUpDataForLookupsOnComboboxRelationshipFields();

        List<MessageLog> messageLogs = messageLogDataService.findByPatameterStatus(LogStatus.PROCESSED);
        assertEquals(2, messageLogs.size());
        assertEquals("info_1_processed", messageLogs.get(0).getInfo());
        assertEquals("info_2_processed", messageLogs.get(1).getInfo());

        Set<LogStatus> statuses = new HashSet<>();
        statuses.add(LogStatus.TO_PROCESS);
        statuses.add(LogStatus.UNKNOWN);
        messageLogs = messageLogDataService.findByPatameterStatusSet(statuses);
        assertEquals(3, messageLogs.size());
        assertEquals("info_3_to_process", messageLogs.get(0).getInfo());
        assertEquals("info_4_to_process", messageLogs.get(1).getInfo());
        assertEquals("info_5_to_unknow", messageLogs.get(2).getInfo());
        assertEquals(LogStatus.UNKNOWN, messageLogs.get(2).getMainParameter().getParamStatus());

        messageLogs = messageLogDataService.findByPatametersStatus(LogStatus.PROCESSED);
        assertEquals(3, messageLogs.size());
        assertContainsEnumValue(messageLogs, LogStatus.PROCESSED);

        messageLogs = messageLogDataService.findByPatametersStatus(LogStatus.UNKNOWN);
        assertEquals(2, messageLogs.size());
        assertEquals("info_4_to_process", messageLogs.get(0).getInfo());
        assertEquals(LogStatus.UNKNOWN, messageLogs.get(0).getParameters().get(2).getParamStatus());
        assertEquals("info_5_to_unknow", messageLogs.get(1).getInfo());
        assertContainsEnumValue(messageLogs, LogStatus.UNKNOWN);

        messageLogs = messageLogDataService.findByPatameterValue("value_1");
        assertEquals(3, messageLogs.size());
        assertEquals("info_1_processed", messageLogs.get(0).getInfo());
        assertEquals("info_4_to_process", messageLogs.get(1).getInfo());
        assertEquals("info_5_to_unknow", messageLogs.get(2).getInfo());

        messageLogs = messageLogDataService.findByPatametersAttributes(LogAttribute.ATTRIBUTE_4);
        assertEquals(3, messageLogs.size());
        assertEquals("info_1_processed", messageLogs.get(0).getInfo());
        assertEquals("info_4_to_process", messageLogs.get(1).getInfo());
        assertEquals("info_5_to_unknow", messageLogs.get(2).getInfo());

        Set<LogAttribute> attributes = new HashSet<>();
        attributes.add(LogAttribute.ATTRIBUTE_3);
        attributes.add(LogAttribute.ATTRIBUTE_2);
        messageLogs = messageLogDataService.findByPatametersSetAttributes(attributes);
        assertEquals(3, messageLogs.size());
        assertEquals("info_2_processed", messageLogs.get(0).getInfo());
        assertEquals("info_3_to_process", messageLogs.get(1).getInfo());
        assertEquals("info_4_to_process", messageLogs.get(2).getInfo());
    }

    private void verifyActorMovie(Actor a, String actorName, String movieName, int expectedMovies, int expectedActors, int moviePosition, int actorPosition) {
        assertEquals(expectedMovies, a.getMovies().size());

        Movie m = a.getMovies().get(moviePosition);
        assertEquals(movieName, m.getName());

        assertEquals(expectedActors, m.getActors().size());
        assertEquals(actorName, m.getActors().get(actorPosition).getName());
    }

    private void verifyMovieActor(Movie m, String actorName, String movieName, int expectedMovies, int expectedActors, int moviePosition, int actorPosition) {
        assertEquals(expectedActors, m.getActors().size());

        Actor a = m.getActors().get(actorPosition);
        assertEquals(actorName, a.getName());

        assertEquals(expectedMovies, a.getMovies().size());
        assertEquals(movieName, a.getMovies().get(moviePosition).getName());
    }

    private void setUpActorMovieData() {
        Movie m1 = new Movie("movie1");
        Movie m2 = new Movie("movie2");
        Movie m3 = new Movie("movie3");
        Movie m4 = new Movie("movie4");

        Actor a1 = new Actor("actor1");
        Actor a2 = new Actor("actor2");
        Actor a3 = new Actor("actor3");
        Actor a4 = new Actor("actor4");

        m1 = movieDataService.detachedCopy(movieDataService.create(m1));
        m2 = movieDataService.detachedCopy(movieDataService.create(m2));
        m3 = movieDataService.detachedCopy(movieDataService.create(m3));
        m4 = movieDataService.detachedCopy(movieDataService.create(m4));

        a1 = actorDataService.detachedCopy(actorDataService.create(a1));
        a2 = actorDataService.detachedCopy(actorDataService.create(a2));
        a3 = actorDataService.detachedCopy(actorDataService.create(a3));
        a4 = actorDataService.detachedCopy(actorDataService.create(a4));

        // m1 - > a1
        m1.getActors().add(a1);
        a1.getMovies().add(m1);
        movieDataService.update(m1);

        // m2 - > a2, a3
        m2.getActors().add(a2);
        m2.getActors().add(a3);
        a2.getMovies().add(m2);
        a3.getMovies().add(m2);
        movieDataService.update(m2);

        //m3, m4 -> a4
        m3.getActors().add(a4);
        m4.getActors().add(a4);
        a4.getMovies().add(m3);
        a4.getMovies().add(m4);
        movieDataService.update(m4);
        movieDataService.update(m3);
    }

    private void setUpDataForLookupsOnRelationshipFields() {
        final District district1 = new District("district_1", 123l);
        final District district2 = new District("district_2", 842l);
        final District district3 = new District("district_3", 11l);
        final District district4 = new District("district_4", 220l);

        final Language lang1 = new Language();
        lang1.setName("language_1");
        final Language lang2 = new Language();
        lang2.setName("language_2");
        final Language lang3 = new Language();
        lang3.setName("language_3_eng");
        final Language lang4 = new Language();
        lang4.setName("language_4_eng");

        final State state1 = new State();
        state1.setName("state_1");
        final State state2 = new State();
        state2.setName("state_2");
        final State state3 = new State();
        state3.setName("state_3");
        final State state4 = new State();
        state4.setName("state_4");

        district1.setLanguage(lang1);
        district2.setLanguage(lang2);
        district3.setLanguage(lang3);
        district4.setLanguage(lang4);

        district1.setState(state1);
        district2.setState(state2);
        district3.setState(state3);
        district4.setState(state4);

        districtDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                districtDataService.create(district1);
                districtDataService.create(district2);
                districtDataService.create(district3);
                districtDataService.create(district4);
            }
        });

        final Language english = new Language();
        english.setName("english");
        final Language french = new Language();
        french.setName("french");

        final District districtEnglish1 = new District("districtEnglish_1", 3596l);
        final District districtEnglish2 = new District("districtEnglish_2", 3111l);
        final District districtEnglish3 = new District("districtEnglish_3", 3966l);
        final District districtEnglish4 = new District("districtEnglish_4", 3599l);

        final Set<District> disctricts1 = new HashSet<>();
        disctricts1.add(districtEnglish1);
        disctricts1.add(districtEnglish2);
        english.setDistricts(disctricts1);

        final Set<District> disctricts2 = new HashSet<>();
        disctricts2.add(districtEnglish3);
        disctricts2.add(districtEnglish4);
        french.setDistricts(disctricts2);

        languageDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                languageDataService.create(english);
                languageDataService.create(french);
            }
        });
    }

    private void assertContainsEnumValue(List<MessageLog> messageLogs, LogStatus value) {
        for (MessageLog messageLog : messageLogs) {
            Boolean contains = false;
            for (LogParameters logParameters : messageLog.getParameters()) {
                contains = contains || logParameters.getParamStatus().equals(value);
            }
            assertEquals(true, contains);
        }
    }

    private void setUpDataForLookupsOnComboboxRelationshipFields() {
        final MessageLog messageLog1 = new MessageLog("info_1_processed", LogStatus.PROCESSED);
        final MessageLog messageLog2 = new MessageLog("info_2_processed", LogStatus.PROCESSED);
        final MessageLog messageLog3 = new MessageLog("info_3_to_process", LogStatus.TO_PROCESS);
        final MessageLog messageLog4 = new MessageLog("info_4_to_process", LogStatus.TO_PROCESS);
        final MessageLog messageLog5 = new MessageLog("info_5_to_unknow", LogStatus.UNKNOWN);

        //MainParameter
        LogParameters logParameters1 = new LogParameters("param_1", LogStatus.PROCESSED, asList("value_1", "value_2"), asList(LogAttribute.ATTRIBUTE_1));
        LogParameters logParameters2 = new LogParameters("param_2", LogStatus.PROCESSED, asList("value_2", "value_3"), asList(LogAttribute.ATTRIBUTE_1, LogAttribute.ATTRIBUTE_2));
        LogParameters logParameters3 = new LogParameters("param_3", LogStatus.TO_PROCESS, asList("value_3", "value_4"), asList(LogAttribute.ATTRIBUTE_3));
        LogParameters logParameters4 = new LogParameters("param_4", LogStatus.TO_PROCESS, asList("value_1", "value_2"), asList(LogAttribute.ATTRIBUTE_4));
        LogParameters logParameters5 = new LogParameters("param_5", LogStatus.UNKNOWN, asList("value_1"), asList(LogAttribute.ATTRIBUTE_4, LogAttribute.ATTRIBUTE_3));

        //Parameters
        LogParameters logParameters6 = new LogParameters("param_6", LogStatus.PROCESSED, asList("value_2"), asList(LogAttribute.ATTRIBUTE_1, LogAttribute.ATTRIBUTE_4));
        LogParameters logParameters7 = new LogParameters("param_7", LogStatus.TO_PROCESS, asList("value_5", "value_5"), asList(LogAttribute.ATTRIBUTE_1));
        LogParameters logParameters8 = new LogParameters("param_8", LogStatus.TO_PROCESS, asList("value_1", "value_8"), asList(LogAttribute.ATTRIBUTE_2));
        LogParameters logParameters9 = new LogParameters("param_9", LogStatus.PROCESSED, asList("value_5"), asList(LogAttribute.ATTRIBUTE_2));
        LogParameters logParameters10 = new LogParameters("param_10", LogStatus.PROCESSED, asList("value_8", "value_2"), asList(LogAttribute.ATTRIBUTE_3));
        LogParameters logParameters11 = new LogParameters("param_11", LogStatus.TO_PROCESS, asList("value_1", "value_5"), asList(LogAttribute.ATTRIBUTE_3));
        LogParameters logParameters12 = new LogParameters("param_12", LogStatus.PROCESSED, asList("value_6", "value_8"), asList(LogAttribute.ATTRIBUTE_3));
        LogParameters logParameters13 = new LogParameters("param_13", LogStatus.TO_PROCESS, asList("value_5", "value_7"), asList(LogAttribute.ATTRIBUTE_4));
        LogParameters logParameters14 = new LogParameters("param_14", LogStatus.UNKNOWN, asList("value_1", "value_5"), asList(LogAttribute.ATTRIBUTE_4));
        LogParameters logParameters15 = new LogParameters("param_15", LogStatus.UNKNOWN, asList("value_8", "value_2"), asList(LogAttribute.ATTRIBUTE_4));

        messageLog1.setMainParameter(logParameters1);
        messageLog2.setMainParameter(logParameters2);
        messageLog3.setMainParameter(logParameters3);
        messageLog4.setMainParameter(logParameters4);
        messageLog5.setMainParameter(logParameters5);

        messageLog1.setParameters(asList(logParameters6));
        messageLog2.setParameters(asList(logParameters7, logParameters11));
        messageLog3.setParameters(asList(logParameters8, logParameters12));
        messageLog4.setParameters(asList(logParameters9, logParameters10, logParameters14));
        messageLog5.setParameters(asList(logParameters13, logParameters15));

        messageLogDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                messageLogDataService.create(messageLog1);
                messageLogDataService.create(messageLog2);
                messageLogDataService.create(messageLog3);
                messageLogDataService.create(messageLog4);
                messageLogDataService.create(messageLog5);
            }
        });
    }

    @Test
    public void shouldLoadLookupsFromFile() {

        Entry entryOne = new Entry();
        entryOne.setValue("someValue");
        entryDataService.create(entryOne);

        Entry entryTwo = new Entry();
        entryTwo.setValue("someValueTwo");
        entryDataService.create(entryTwo);

        EntityDto entity = entityService.getEntityByClassName("org.motechproject.mds.test.domain.editablelookups.Entry");

        List<LookupDto> lookups = entityService.getEntityLookups(entity.getId());

        assertEquals(1, lookups.size());
        assertEquals("Find by Value", lookups.get(0).getLookupName());

        Map<String, String> params = new HashMap<>();
        params.put("value", entryOne.getValue());

        List<Entry> lookupResult = lookupService.findMany(entity.getClassName(), lookups.get(0).getLookupName(), params);

        assertEquals(1, lookupResult.size());
        assertEquals(entryOne.getValue(), lookupResult.get(0).getValue());
    }

    @Test
    public void shouldAutomaticallyIncrementTheVersionInMdsVersionedEntityClass() throws InterruptedException {
        TestMdsVersionedEntity record = testMdsVersionedEntityService.create( new TestMdsVersionedEntity("value_1"));
        assertEquals(new Long(1l), record.getInstanceVersion());

        record.setStringField("value_2");
        record = testMdsVersionedEntityService.update(record);
        assertEquals(new Long(2l), record.getInstanceVersion());

        Thread simpleThread = new SimpleThread(testMdsVersionedEntityService, record.getId(), "stringField");
        simpleThread.run();
        simpleThread.join();

        record = testMdsVersionedEntityService.findById(record.getId());
        assertEquals(new Long(3l), record.getInstanceVersion());
    }

    @Test(expected = JdoOptimisticLockingFailureException.class)
    public void shouldUseInstanceVersioningFromMdsVersionedEntityClass() throws InterruptedException {
        TestMdsVersionedEntity record = testMdsVersionedEntityService.detachedCopy(testMdsVersionedEntityService.create( new TestMdsVersionedEntity("name")));
        record.setStringField("new_name");

        try {
            testMdsVersionedEntityService.update(record);
        } catch (Exception e) {
            getLogger().error("Cannot update record of {} class", TestMdsEntity.class.getName());
            fail();
        }

        Thread simpleThread = new SimpleThread(testMdsVersionedEntityService, record.getId(), "stringField");
        record = testMdsVersionedEntityService.detachedCopy(testMdsVersionedEntityService.findById(record.getId()));

        simpleThread.run();
        simpleThread.join();

        assertEquals("new_name", record.getStringField());

        record.setStringField("sample_name");
        record.setCreator("Somebody");

        //should throw exception
        testMdsVersionedEntityService.update(record);
    }

    @Test
    public void shouldAutomaticallyIncrementTheVersionInClassWithVersionAnnotation() throws InterruptedException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SimpleClassWithVersioning record = simpleClassWithVersioningService.create( new SimpleClassWithVersioning("value_1"));
        assertEquals(new Long(1l), record.getVersion());

        record.setStringField("value_2");
        record = simpleClassWithVersioningService.update(record);
        assertEquals(new Long(2l), record.getVersion());

        Thread simpleThread = new SimpleThread(simpleClassWithVersioningService, (Long) PropertyUtils.getProperty(record, "id"), "stringField");
        simpleThread.run();
        simpleThread.join();

        record = simpleClassWithVersioningService.findById((Long) PropertyUtils.getProperty(record, "id"));
        assertEquals(new Long(3l), record.getVersion());
    }

    @Test(expected = JdoOptimisticLockingFailureException.class)
    public void shouldUseInstanceVersioningFromClassWithVersionAnnotation() throws InterruptedException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SimpleClassWithVersioning record = simpleClassWithVersioningService.create( new SimpleClassWithVersioning("name"));
        SimpleClassWithVersioning recordFromDatabase = simpleClassWithVersioningService.findById((Long) PropertyUtils.getProperty(record, "id"));
        recordFromDatabase.setStringField("new_name");

        try {
            simpleClassWithVersioningService.update(recordFromDatabase);
        } catch (Exception e) {
            getLogger().error("Cannot update record of {} class", SimpleClassWithVersioning.class.getName());
            fail();
        }

        Thread simpleThread = new SimpleThread(simpleClassWithVersioningService, (Long) PropertyUtils.getProperty(record, "id"), "stringField");
        recordFromDatabase = simpleClassWithVersioningService.findById((Long) PropertyUtils.getProperty(record, "id"));

        simpleThread.run();
        simpleThread.join();

        assertEquals("new_name", recordFromDatabase.getStringField());

        recordFromDatabase.setStringField("sample_name");

        //should throw exception
        simpleClassWithVersioningService.update(recordFromDatabase);
    }

    //This test is for StateManagerUtil.class
    @Test(expected = JdoOptimisticLockingFailureException.class)
    public void shouldThrowOptimisticExceptionWithTransientObject() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SimpleClassWithVersioning instance = new SimpleClassWithVersioning("version_1");
        instance = simpleClassWithVersioningService.create(instance);

        SimpleClassWithVersioning recordFromDatabase1 = simpleClassWithVersioningService.findById((Long) PropertyUtils.getProperty(instance, "id"));
        SimpleClassWithVersioning recordFromDatabase2 = simpleClassWithVersioningService.findById((Long) PropertyUtils.getProperty(instance, "id"));

        Set<String> fieldsToUpdate = new HashSet<>();
        fieldsToUpdate.add("stringField");
        fieldsToUpdate.add("version");

        recordFromDatabase1.setStringField("version_2");
        try {
            simpleClassWithVersioningService.update(recordFromDatabase1);
        } catch (Exception e) {
            getLogger().error("Cannot update record of {} class", SimpleClassWithVersioning.class.getName());
            fail();
        }

        recordFromDatabase2.setStringField("version_3");
        simpleClassWithVersioningService.updateFromTransient(recordFromDatabase2, fieldsToUpdate);
    }

    //This test is for StateManagerUtil.class
    @Test(expected = JdoOptimisticLockingFailureException.class)
    public void shouldSetProperTransactionVersion() throws Exception {
        TestMdsVersionedEntity record1 = testMdsVersionedEntityService.create(new TestMdsVersionedEntity("value_1"));
        final Long id = (Long) PropertyUtils.getProperty(record1, "id");
        try {
            record1.setStringField("value_2");
            record1 = testMdsVersionedEntityService.update(record1);
            record1.setStringField("value_3");
            testMdsVersionedEntityService.update(record1);
            TestMdsVersionedEntity record1FromDb = testMdsVersionedEntityService.findById(id);
        } catch (Exception e) {
            getLogger().error("Cannot update record of {} class", TestMdsVersionedEntity.class.getName());
            fail();
        }

        testMdsVersionedEntityService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                TestMdsVersionedEntity instance = testMdsVersionedEntityService.findById(id);
                instance.setStringField("value_4");
                // we set older version
                instance.setInstanceVersion(2l);
                StateManagerUtil.setTransactionVersion(instance, 2l, "instanceVersion");
                //we should get exception after this update
                testMdsVersionedEntityService.update(instance);
            }
        });
    }

    private class SimpleThread extends Thread {

        private MotechDataService service;
        private long id;
        private String fieldName;

        public SimpleThread(MotechDataService service, long id, String fieldName) {
            super();
            this.service = service;
            this.id = id;
            this.fieldName = fieldName;
        }

        @Override
        public void run() {
            service.doInTransaction(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    Object testRecord = service.findById(id);
                    try {
                        //If this fail then version asserts in test will also fail
                        PropertyUtils.setProperty(testRecord, fieldName, "other_thread");
                        service.update(testRecord);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Test
    public void shouldCreateHistoryForOneToOneRelationships() {
        Address address = new Address();
        address.setStreet("Broadway");

        House house = new House();
        house.setName("A house");
        house.setHouseNumber((short)12);
        house.setAddress(address);
        house.setUuid(UUID.randomUUID());

        house = houseDataService.detachedCopy(houseDataService.update(house));
        
        //check whether uuid field is set
        assertNotNull(house.getUuid());

        final long firstAddressId = house.getAddress().getId();

        // no history at first
        List historyList = historyService.getHistoryForInstance(house, ASC_ID);
        assertNotNull(historyList);
        assertTrue(historyList.isEmpty());

        // change the name of the house
        house.setName("Second house");
        house = houseDataService.detachedCopy(houseDataService.update(house));

        //check whether uuid field is set
        assertNotNull(house.getUuid());
        
        // then change the address
        Address secondAddress = new Address();
        secondAddress.setStreet("Abbey Road");
        house.setAddress(secondAddress);
        house.setHouseNumber((short)87);
        house.setUuid(UUID.randomUUID());
        house = houseDataService.update(house);

        secondAddress = house.getAddress();
        final long secondAddressId = secondAddress.getId();
        assertNotSame(firstAddressId, secondAddressId);

        // update the second address, should not affect house history
        secondAddress.setStreet("The Abbey Road");
        addressDataService.update(secondAddress);

        // there were 2 changes
        historyList = historyService.getHistoryForInstance(house, ASC_ID);
        assertNotNull(historyList);
        assertEquals(2, historyList.size());

        // the first version - "A house" with first (Broadway) address
        verifyHouseRecord(historyList.get(0), "A house", firstAddressId);
        final long firstVersionId = (long) PropertyUtil.safeGetProperty(historyList.get(0), ID_FIELD_NAME);

        // the second version - "Second house", with the first address
        verifyHouseRecord(historyList.get(1), "Second house", firstAddressId);

        // no history entries for the first address
        historyList = historyService.getHistoryForInstance(address, ASC_ID);
        assertNotNull(historyList);
        assertEquals(0, historyList.size());

        // one history entry for the second address
        historyList = historyService.getHistoryForInstance(secondAddress, ASC_ID);
        assertNotNull(historyList);
        assertEquals(1, historyList.size());
        assertEquals("Abbey Road", safeGetProperty(historyList.get(0), "street"));

        // Revert the house to the first version

        final long houseId = house.getId();
        houseDataService.revertToHistoricalRevision(houseId, firstVersionId);

        // "A house" with the old broadway address
        house = houseDataService.findById(houseId);
        verifyHouse(house, "A house", "Broadway");

        // delete the house
        houseDataService.delete(house);
    }

    @Test
    public void shouldCreateHistoryForOneToManyRelationship() {
        // a network with two computers
        Computer deepBlue = new Computer("Deep Blue");
        Computer watson = new Computer("Watson");

        Network ibmNetwork = new Network("192.168.1.0/24", new ArrayList<>(asList(deepBlue, watson)));

        ibmNetwork = networkDataService.detachedCopy(networkDataService.create(ibmNetwork));
        final DateTime mdDt1 = ibmNetwork.getModificationDate();
        final DateTime creationDate = ibmNetwork.getCreationDate();

        final long deepBlueId = ibmNetwork.getComputers().get(0).getId();
        final long watsonId = ibmNetwork.getComputers().get(1).getId();

        // remove one computer from the network
        ibmNetwork.removeComputer("Watson");

        ibmNetwork = networkDataService.detachedCopy(networkDataService.update(ibmNetwork));
        final DateTime mdDt2 = ibmNetwork.getModificationDate();

        // add two new computers
        Computer deepThought = new Computer("Deep Thought");
        ibmNetwork.getComputers().add(deepThought);
        Computer deepFritz = new Computer("Deep Fritz");
        ibmNetwork.getComputers().add(deepFritz);

        ibmNetwork = networkDataService.detachedCopy(networkDataService.update(ibmNetwork));
        final DateTime mdDt3 = ibmNetwork.getModificationDate();

        final long deepFritzId = ibmNetwork.getComputerByName("Deep Fritz").getId();

        // change the name of the new computer
        deepThought = ibmNetwork.getComputerByName("Deep Thought");
        final long deepThoughtId = deepThought.getId();
        deepThought.setName("Deep Thought 2");
        ibmNetwork = networkDataService.update(ibmNetwork);

        // the network should have three history entries
        List historyList = historyService.getHistoryForInstance(ibmNetwork, QueryParams.ascOrder(ID_FIELD_NAME));
        assertNotNull(historyList);
        assertEquals(3, historyList.size());

        // first version with two computers
        verifyNetworkHistoryRecord(historyList.get(0), "192.168.1.0/24", asList(deepBlueId, watsonId),
                mdDt1, creationDate);
        final long firstVersionId = (long) PropertyUtil.safeGetProperty(historyList.get(0), ID_FIELD_NAME);

        // second version with just one computer
        verifyNetworkHistoryRecord(historyList.get(1), "192.168.1.0/24", singletonList(deepBlueId),
                mdDt2, creationDate);


        verifyNetworkHistoryRecord(historyList.get(2), "192.168.1.0/24", asList(deepBlueId, deepThoughtId, deepFritzId),
                mdDt3, creationDate);

        // deep blue has no history
        historyList = historyService.getHistoryForInstance(ibmNetwork.getComputerByName("Deep Blue"), ASC_ID);
        assertNotNull(historyList);
        assertTrue(historyList.isEmpty());

        // deep thought has one change
        historyList = historyService.getHistoryForInstance(ibmNetwork.getComputerByName("Deep Thought 2"), ASC_ID);
        assertNotNull(historyList);
        assertEquals(1, historyList.size());
        Object firstVersion = historyList.get(0);
        assertEquals("Deep Thought", safeGetProperty(firstVersion, "name"));

        // revert the network to its first version
        final long ibmNetworkId = ibmNetwork.getId();
        ibmNetwork = networkDataService.revertToHistoricalRevision(ibmNetworkId, firstVersionId);

        // first version with two computers
        assertEquals("192.168.1.0/24", ibmNetwork.getMask());
        assertEquals(2, ibmNetwork.getComputers().size());
        assertNotNull(ibmNetwork.getComputerByName("Deep Blue"));
        assertNotNull(ibmNetwork.getComputerByName("Watson"));
        assertEquals(creationDate, ibmNetwork.getCreationDate());
        // modification date should be new
        assertNotSame(mdDt1, ibmNetwork.getModificationDate());
    }

    @Test
    public void shouldCreateHistoryForManyToMany() {
        Company google = companyDataService.detachedCopy(companyDataService.create(new Company("Google")));
        Company microsoft = companyDataService.detachedCopy(companyDataService.create(new Company("Microsoft")));
        Company atari = companyDataService.detachedCopy(companyDataService.create(new Company("Atari")));

        Consultant jack = consultantDataService.detachedCopy(consultantDataService.create(new Consultant("Jack")));
        Consultant tom = consultantDataService.detachedCopy(consultantDataService.create(new Consultant("Tom")));
        Consultant mike = consultantDataService.detachedCopy(consultantDataService.create(new Consultant("Mike")));

        final DateTime jackDt1 = jack.getModificationDate();
        final DateTime googleDt1 = google.getModificationDate();

        // Jack is a consultant for Microsoft and Google
        // Tom is a consultant for Google and Atari
        // Mike is a consultant for Atari and Microsoft
        jack.addCompany(microsoft);
        jack.addCompany(google);
        microsoft.addConsultant(jack);
        google.addConsultant(jack);

        tom.addCompany(google);
        tom.addCompany(atari);
        google.addConsultant(tom);
        atari.addConsultant(tom);

        mike.addCompany(atari);
        mike.addCompany(microsoft);
        atari.addConsultant(mike);
        microsoft.addConsultant(mike);

        atari = companyDataService.update(atari);
        microsoft = companyDataService.update(microsoft);
        // explicit update to Google, but Jack employees are already persisted and connected
        // this will change the modification date and create a historical revision
        google = companyDataService.update(google);

        final DateTime jackDt2 = jack.getModificationDate();
        final DateTime googleDt2 = google.getModificationDate();

        final long jackId = jack.getId();
        final long tomId = tom.getId();
        final long googleId = google.getId();
        final long microsoftId = microsoft.getId();

        // Jack leaves Google
        jack.removeCompany("Google");
        google.removeConsultant("Jack");

        google = companyDataService.update(google);

        // Google has 3 historical revisions - first no employees, second while Jack was still there
        // third is the same, because we call update explicitly
        List historyList = historyService.getHistoryForInstance(google, ASC_ID);
        assertEquals(3, historyList.size());

        Object firstVersion = historyList.get(0);
        assertNotNull(firstVersion);
        assertEquals("Google", safeGetProperty(firstVersion, "name"));
        assertEquals(Collections.emptySet(), safeGetProperty(firstVersion, "consultants"));
        assertEquals(googleDt1, safeGetProperty(firstVersion, "modificationDate"));

        Object secondVersion = historyList.get(1);
        assertNotNull(secondVersion);
        assertEquals("Google", safeGetProperty(secondVersion, "name"));
        assertEquals(asSet(jackId, tomId), safeGetProperty(secondVersion, "consultants"));
        assertEquals(googleDt1, safeGetProperty(secondVersion, "modificationDate"));

        Object thirdVersion = historyList.get(2);
        assertNotNull(thirdVersion);
        assertEquals("Google", safeGetProperty(thirdVersion, "name"));
        assertEquals(asSet(jackId, tomId), safeGetProperty(thirdVersion, "consultants"));
        assertEquals(googleDt2, safeGetProperty(thirdVersion, "modificationDate"));

        // Jack ahs two entries - he was first created, then joined Google and Microsoft
        historyList = historyService.getHistoryForInstance(jack, ASC_ID);
        assertEquals(2, historyList.size());

        firstVersion = historyList.get(0);
        assertNotNull(firstVersion);
        assertEquals("Jack", safeGetProperty(firstVersion, "name"));
        assertEquals(Collections.emptySet(), safeGetProperty(firstVersion, "companies"));
        assertEquals(jackDt1, safeGetProperty(firstVersion, "modificationDate"));

        secondVersion = historyList.get(1);
        assertNotNull(secondVersion);
        assertEquals("Jack", safeGetProperty(secondVersion, "name"));
        assertEquals(asSet(googleId, microsoftId), safeGetProperty(secondVersion, "companies"));
        assertEquals(jackDt2, safeGetProperty(secondVersion, "modificationDate"));
    }

    @Test
    public void shouldRevertFromTrash() {
        HomeAddress homeAddress1 = revertFromTrashService.create(new HomeAddress("street1","city1"));
        HomeAddress homeAddress2 = revertFromTrashService.create(new HomeAddress("street2","city2"));

        revertFromTrashService.delete(homeAddress1);
        revertFromTrashService.delete(homeAddress2);

        revertFromTrashService.revertFromTrash(homeAddress1.getId());

        QueryParams q = new QueryParams(1,10);
        Collection instancesInTrash = trashService.getInstancesFromTrash(HomeAddress.class.getName(), q);
        Collection<HomeAddress> instancesOutsideTrash = revertFromTrashService.retrieveAll();

        assertEquals(1, instancesInTrash.size());
        assertEquals(1, instancesOutsideTrash.size());
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

        List<TestMdsEntity> testMdsEntities = testMdsEntityService.detachedCopyAll(testMdsEntityService.retrieveAll());
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

    private void verifyNetworkHistoryRecord(Object historyRevision, String mask, List<Long> computerIds,
                                            DateTime modficationDate, DateTime creationDate) {
        assertNotNull(historyRevision);
        assertEquals(mask, safeGetProperty(historyRevision, "mask"));
        assertEquals(computerIds, safeGetProperty(historyRevision, "computers"));
        assertEquals(modficationDate, safeGetProperty(historyRevision, "modificationDate"));
        assertEquals(creationDate, safeGetProperty(historyRevision, "creationDate"));
    }

    private void verifyHouseRecord(Object houseRecord, String houseName, Long addressId) {
        assertNotNull(houseRecord);
        assertEquals(houseName, safeGetProperty(houseRecord, "name"));
        assertEquals(addressId, safeGetProperty(houseRecord, "address"));
    }

    private void verifyHouse(House house, String houseName, String streetName) {
        assertNotNull(house);
        assertEquals(houseName, house.getName());
        assertNotNull(house.getAddress());
        assertEquals(streetName, house.getAddress().getStreet());
    }

    @SafeVarargs
    private final <T> Set<T> asSet(T... args) {
        return new HashSet<>(asList(args));
    }
}
