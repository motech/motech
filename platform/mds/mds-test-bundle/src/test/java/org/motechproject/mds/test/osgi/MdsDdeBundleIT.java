package org.motechproject.mds.test.osgi;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.test.domain.Author;
import org.motechproject.mds.test.domain.Book;
import org.motechproject.mds.test.domain.TestLookup;
import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.service.AuthorDataService;
import org.motechproject.mds.test.service.BookDataService;
import org.motechproject.mds.test.service.TestMdsEntityService;
import org.motechproject.mds.test.service.TestLookupService;
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
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    }

    @Test
    public void testMdsTestBundleInstallsProperly() throws Exception {
        assertDefaultConstructorPresent();
        verifyDDE();
        verifyLookup();
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

        assertEquals(asList(field3),testLookupByInheritedFieldList);
        assertEquals(asList(field1, field2, field3),testLookupByAutoGeneratedFieldList);
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
}
