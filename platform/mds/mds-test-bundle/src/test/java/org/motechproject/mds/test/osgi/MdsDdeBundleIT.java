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
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.test.domain.Author;
import org.motechproject.mds.test.domain.Book;
import org.motechproject.mds.test.domain.Clinic;
import org.motechproject.mds.test.domain.District;
import org.motechproject.mds.test.domain.Language;
import org.motechproject.mds.test.domain.Patient;
import org.motechproject.mds.test.domain.State;
import org.motechproject.mds.test.domain.TestLookup;
import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.domain.historytest.Address;
import org.motechproject.mds.test.domain.historytest.Company;
import org.motechproject.mds.test.domain.historytest.Computer;
import org.motechproject.mds.test.domain.historytest.Consultant;
import org.motechproject.mds.test.domain.historytest.House;
import org.motechproject.mds.test.domain.historytest.Network;
import org.motechproject.mds.test.service.AuthorDataService;
import org.motechproject.mds.test.service.BookDataService;
import org.motechproject.mds.test.service.ClinicDataService;
import org.motechproject.mds.test.service.DistrictDataService;
import org.motechproject.mds.test.service.LanguageDataService;
import org.motechproject.mds.test.service.PatientDataService;
import org.motechproject.mds.test.service.StateDataService;
import org.motechproject.mds.test.service.TestLookupService;
import org.motechproject.mds.test.service.TestMdsEntityService;
import org.motechproject.mds.test.service.TransactionTestService;
import org.motechproject.mds.test.service.historytest.AddressDataService;
import org.motechproject.mds.test.service.historytest.CompanyDataService;
import org.motechproject.mds.test.service.historytest.ConsultantDataService;
import org.motechproject.mds.test.service.historytest.HouseDataService;
import org.motechproject.mds.test.service.historytest.NetworkDataService;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import javax.inject.Inject;
import javax.jdo.JDOUserException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private TestMdsEntityService testMdsEntityService;

    @Inject
    private TestLookupService testLookupService;

    @Inject
    private BookDataService bookDataService;

    @Inject
    private AuthorDataService authorDataService;

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
        setUpSecurityContext();
    }

    @After
    public void tearDown() {
        testMdsEntityService.deleteAll();
        testLookupService.deleteAll();
        bookDataService.deleteAll();
        authorDataService.deleteAll();
        patientDataService.deleteAll();
        clinicDataService.deleteAll();
        stateDataService.deleteAll();
        districtDataService.deleteAll();
        languageDataService.deleteAll();
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
        final District district = new District();
        district.setName("district1");
        final State state = new State();
        state.setName("state1");
        final Language lang = new Language();
        lang.setName("eng");

        districtDataService.create(district);
        stateDataService.create(state);
        languageDataService.create(lang);

        stateDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                state.getLanguages().add(lang);
                state.getDistricts().add(district);
                stateDataService.update(state);
            }
        });

        List audit = historyService.getHistoryForInstance(district, null);
        assertNotNull(audit);
        assertEquals(1, audit.size());

        final State retrievedState = stateDataService.findByName(state.getName());

        stateDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                retrievedState.getLanguages().clear();
                stateDataService.update(retrievedState);
            }
        });

        // check what happened for districts
        // Latest version should not be included in the history audit
        audit = historyService.getHistoryForInstance(district, null);
        assertNotNull(audit);
        assertEquals(1, audit.size());

        Object firstRevision = audit.get(0); //Initial revision - no relations
        assertEquals("district1", safeGetProperty(firstRevision, "name"));
        assertNull(safeGetProperty(firstRevision, "state"));
        assertNull(safeGetProperty(firstRevision, "language"));

        final State retrievedState2 = stateDataService.findByName(state.getName());

        stateDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                retrievedState2.setDefaultDistrict(district);
                stateDataService.update(retrievedState2);
            }
        });

        stateDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                retrievedState2.getLanguages().clear();
                stateDataService.update(retrievedState2);
            }
        });

        audit = historyService.getHistoryForInstance(retrievedState2, null);
        assertNotNull(audit);
        assertEquals(4, audit.size());

        Object lastRevision = audit.get(3);
        assertNotNull(safeGetProperty(lastRevision, "defaultDistrict"));
        assertNotNull(safeGetProperty(lastRevision, "districts"));
    }

    @Test
    public void testManyToManyRelationshipWithCustomTableAndColumnNames() {
        final Patient patient = new Patient("patient1");
        final Patient patient2 = new Patient("patient2");

        final Clinic clinic = new Clinic("clinic1");
        final Clinic clinic2 = new Clinic("clinic2");
        final Clinic clinic3 = new Clinic("clinic3");

        patientDataService.create(patient);
        patientDataService.create(patient2);

        clinicDataService.create(clinic);
        clinicDataService.create(clinic2);
        clinicDataService.create(clinic3);

        patientDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                patient.getClinics().add(clinic);
                patient.getClinics().add(clinic2);
                patientDataService.update(patient);
            }
        });

        // Test that the relationship was added
        Patient retrievedPatient = patientDataService.findByName("patient1");
        assertNotNull(retrievedPatient);
        assertEquals(2, retrievedPatient.getClinics().size());

        //Test that the backlink has been created (It's bi-directional relationship)
        final Clinic retrievedClinic = clinicDataService.findByName("clinic1");
        final Clinic retrievedClinic2 = clinicDataService.findByName("clinic2");
        assertNotNull(retrievedClinic);
        assertNotNull(retrievedClinic2);
        assertEquals(1, retrievedClinic.getPatients().size());
        assertEquals(1, retrievedClinic2.getPatients().size());

        patientDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                // We use the clinic objects we retrieved, since they contain information about existing relationship
                patient2.getClinics().add(retrievedClinic);
                patient2.getClinics().add(retrievedClinic2);
                patient2.getClinics().add(clinic3);

                patientDataService.update(patient2);
            }
        });

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
    public void shouldCreateHistoryForOneToOneRelationships() {
        Address address = new Address();
        address.setStreet("Broadway");

        House house = new House();
        house.setName("A house");
        house.setAddress(address);

        house = houseDataService.create(house);

        final long firstAddressId = house.getAddress().getId();

        // no history at first
        List historyList = historyService.getHistoryForInstance(house, ASC_ID);
        assertNotNull(historyList);
        assertTrue(historyList.isEmpty());

        // change the name of the house
        house.setName("Second house");
        house = houseDataService.update(house);

        // then change the address
        Address secondAddress = new Address();
        secondAddress.setStreet("Abbey Road");
        house.setAddress(secondAddress);
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
        Object firstVersion = historyList.get(0);
        assertNotNull(firstVersion);
        assertEquals("A house", safeGetProperty(firstVersion, "name"));
        assertEquals(firstAddressId, safeGetProperty(firstVersion, "address"));

        // the second version - "Second house", with the first address
        Object secondVersion = historyList.get(1);
        assertNotNull(secondVersion);
        assertEquals("Second house", safeGetProperty(secondVersion, "name"));
        assertEquals(firstAddressId, safeGetProperty(secondVersion, "address"));

        // no history entries for the first address
        historyList = historyService.getHistoryForInstance(address, ASC_ID);
        assertNotNull(historyList);
        assertEquals(0, historyList.size());

        // one history entry for the second address
        historyList = historyService.getHistoryForInstance(secondAddress, ASC_ID);
        assertNotNull(historyList);
        assertEquals(1, historyList.size());
        assertEquals("Abbey Road", safeGetProperty(historyList.get(0), "street"));
    }

    @Test
    public void shouldCreateHistoryForOneToManyRelationship() {
        // a network with two computers
        Computer deepBlue = new Computer("Deep Blue");
        Computer watson = new Computer("Watson");

        Network ibmNetwork = new Network("192.168.1.0/24", new ArrayList<>(asList(deepBlue, watson)));

        ibmNetwork = networkDataService.create(ibmNetwork);
        final DateTime mdDt1 = ibmNetwork.getModificationDate();

        final long deepBlueId = ibmNetwork.getComputers().get(0).getId();
        final long watsonId = ibmNetwork.getComputers().get(1).getId();

        // remove one computer from the network
        ibmNetwork.removeComputer("Watson");

        ibmNetwork = networkDataService.update(ibmNetwork);
        final DateTime mdDt2 = ibmNetwork.getModificationDate();

        // add two new computers
        Computer deepThought = new Computer("Deep Thought");
        ibmNetwork.getComputers().add(deepThought);
        Computer deepFritz = new Computer("Deep Fritz");
        ibmNetwork.getComputers().add(deepFritz);

        ibmNetwork = networkDataService.update(ibmNetwork);
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
        verifyNetworkHistoryRecord(historyList.get(0), "192.168.1.0/24", asList(deepBlueId, watsonId), mdDt1);

        // second version with just one computer
        verifyNetworkHistoryRecord(historyList.get(1), "192.168.1.0/24", singletonList(deepBlueId), mdDt2);


        verifyNetworkHistoryRecord(historyList.get(2), "192.168.1.0/24", asList(deepBlueId, deepThoughtId, deepFritzId),
                mdDt3);

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
    }

    @Test
    public void shouldCreateHistoryForManyToMany() {
        Company google = new Company("Google");
        Company microsoft = new Company("Microsoft");
        Company atari = new Company("Atari");

        Consultant jack = consultantDataService.create(new Consultant("Jack"));
        Consultant tom = consultantDataService.create(new Consultant("Tom"));
        Consultant mike = consultantDataService.create(new Consultant("Mike"));

        final DateTime jackDt1 = jack.getModificationDate();

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

        companyDataService.create(atari);
        companyDataService.create(microsoft);
        companyDataService.create(google);

        final DateTime jackDt2 = jack.getModificationDate();
        final DateTime googleDt1 = google.getModificationDate();

        final long jackId = jack.getId();
        final long tomId = tom.getId();
        final long googleId = google.getId();
        final long microsoftId = microsoft.getId();

        // Jack leaves Google
        jack.removeCompany("Google");
        google.removeConsultant("Jack");

        google = companyDataService.update(google);

        // Google has 1 historical revision - while Jack was still there
        List historyList = historyService.getHistoryForInstance(google, ASC_ID);
        assertEquals(1, historyList.size());

        Object firstVersion = historyList.get(0);
        assertNotNull(firstVersion);
        assertEquals("Google", safeGetProperty(firstVersion, "name"));
        assertEquals(asSet(jackId, tomId), safeGetProperty(firstVersion, "consultants"));
        assertEquals(googleDt1, safeGetProperty(firstVersion, "modificationDate"));

        // Jack ahs two entries - he was first created, then joined Google and Microsoft
        historyList = historyService.getHistoryForInstance(jack, ASC_ID);
        assertEquals(2, historyList.size());

        firstVersion = historyList.get(0);
        assertNotNull(firstVersion);
        assertEquals("Jack", safeGetProperty(firstVersion, "name"));
        assertEquals(Collections.emptySet(), safeGetProperty(firstVersion, "companies"));
        assertEquals(jackDt1, safeGetProperty(firstVersion, "modificationDate"));

        Object secondVersion = historyList.get(1);
        assertNotNull(secondVersion);
        assertEquals("Jack", safeGetProperty(secondVersion, "name"));
        assertEquals(asSet(googleId, microsoftId), safeGetProperty(secondVersion, "companies"));
        assertEquals(jackDt2, safeGetProperty(secondVersion, "modificationDate"));

        // no history for Microsoft or Atari
        historyList = historyService.getHistoryForInstance(microsoft, ASC_ID);
        assertEquals(0, historyList.size());

        historyList = historyService.getHistoryForInstance(atari, ASC_ID);
        assertEquals(0, historyList.size());
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

        assertEquals(testMdsEntities.get(0).getOwner(),"newOwner");
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

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        authentication.setAuthenticated(false);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private void verifyNetworkHistoryRecord(Object historyRevision, String mask, List<Long> computerIds,
                                            DateTime modficationDate) {
        assertNotNull(historyRevision);
        assertEquals(mask, safeGetProperty(historyRevision, "mask"));
        assertEquals(computerIds, safeGetProperty(historyRevision, "computers"));
        assertEquals(modficationDate, safeGetProperty(historyRevision, "modificationDate"));
    }

    @SafeVarargs
    private final <T> Set<T> asSet(T... args) {
        return new HashSet<>(asList(args));
    }

    private void wait2s() throws InterruptedException {
        synchronized (waitLock) {
            waitLock.wait(2000);
        }
    }
}
