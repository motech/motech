package org.motechproject.mds.test.osgi;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.service.TestMdsEntityService;
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
import java.util.List;

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

    @Before
    public void setUp() throws Exception {
        setUpSecurityContext();
    }

    @After
    public void tearDown() {
        testMdsEntityService.deleteAll();
    }

    @Test
    public void testMdsTestBundleInstallsProperly() throws Exception {
        assertDefaultConstructorPresent();
        verifyDDE();
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
