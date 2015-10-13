package org.motechproject.mds.test.secondary.osgi;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.test.domain.differentbundles.Animal;
import org.motechproject.mds.test.domain.differentbundles.EntityB;
import org.motechproject.mds.test.domain.differentbundles.EntityC;
import org.motechproject.mds.test.domain.differentbundles.Priority;
import org.motechproject.mds.test.domain.differentbundles.type.MessageStatus;
import org.motechproject.mds.test.secondary.domain.CallStatus;
import org.motechproject.mds.test.domain.mapdeserialisation.EntityWithStringObjectMap;
import org.motechproject.mds.test.secondary.domain.DeserializationTestClass;
import org.motechproject.mds.test.secondary.domain.EntityA;
import org.motechproject.mds.test.secondary.domain.MessageRecord;
import org.motechproject.mds.test.secondary.service.EntityADataService;
import org.motechproject.mds.test.secondary.service.MessageRecordDataService;
import org.motechproject.mds.test.service.differentbundles.EntityBDataService;
import org.motechproject.mds.test.service.differentbundles.EntityCDataService;
import org.motechproject.mds.test.secondary.osgi.MdsSecondBundleIT;
import org.motechproject.mds.test.service.instancelifecyclelistener.JdoListenerTestService;
import org.motechproject.mds.test.service.mapdeserialisation.EntityWithStringObjectMapDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsSecondBundleIT extends BasePaxIT {

    public static final String A_NAME = "a";
    public static final String B_NAME = "b";
    public static final String C_NAME = "c";

    public static final String A_UPDATED_NAME = "a_updated";
    public static final String B_UPDATED_NAME = "b_updated";
    public static final String C_UPDATED_NAME = "c_updated";

    // Unused. We inject it to make sure that the service is ready before starting tests
    @Inject
    private JdoListenerTestService jdoListenerTestService;

    @Inject
    private EntityWithStringObjectMapDataService entityWithStringObjectMapDataService;

    @Inject
    private EntityADataService entityADataService;

    @Inject
    private EntityBDataService entityBDataService;

    @Inject
    private EntityCDataService entityCDataService;

    @Inject
    private MessageRecordDataService messageRecordDataService;

    @After
    public void tearDown() {
        entityADataService.deleteAll();
        entityBDataService.deleteAll();
        entityCDataService.deleteAll();
        messageRecordDataService.deleteAll();
        entityWithStringObjectMapDataService.deleteAll();
    }

    @Test
    public void testCrossBundleEnums() {
        MessageRecord record = new MessageRecord("John", CallStatus.createFrom(MessageStatus.FINISHED), "Hello!");
        MessageRecord record2 = new MessageRecord("Amy", CallStatus.createFrom(MessageStatus.PENDING), "Good morning!");

        messageRecordDataService.create(record);
        messageRecordDataService.create(record2);

        List<MessageRecord> foundRecords = messageRecordDataService.findByAuthor("John");
        assertEquals(1, foundRecords.size());
        assertEquals(foundRecords.get(0).getCallStatus(), CallStatus.FINISHED);
    }

    @Test
    public void testEntitiesEnhancement() {
        assertEntityEnhanced(EntityA.class);
        assertEntityEnhanced(EntityB.class);
        assertEntityEnhanced(EntityC.class);
    }

    @Test
    public void testCrossBundleRelationshipCreate() {
        EntityA a = new EntityA();
        EntityB b = new EntityB();
        EntityC c = new EntityC();

        setEntitiesRelations(a, b, c);
        setEntitiesFields(a, b, c, A_NAME, B_NAME, Priority.LOW, C_NAME, Animal.CAT);

        entityADataService.create(a);

        a = entityADataService.findById(a.getId());
        b = entityBDataService.findById(b.getId());
        c = entityCDataService.findById(c.getId());

        assertEntitiesFields(a, b, c, A_NAME, B_NAME, Priority.LOW, C_NAME, Animal.CAT);

        setEntitiesFields(a, b, c, A_UPDATED_NAME, B_UPDATED_NAME, Priority.HIGH, C_UPDATED_NAME, Animal.DUCK);

        setEntitiesRelations(a, b, c);
        entityADataService.update(a);

        a = entityADataService.findById(a.getId());
        b = entityBDataService.findById(b.getId());
        c = entityCDataService.findById(c.getId());

        assertEntitiesFields(a, b, c, A_UPDATED_NAME, B_UPDATED_NAME, Priority.HIGH, C_UPDATED_NAME, Animal.DUCK);
    }

    @Test
    public void deserialisationTest() throws InterruptedException {
        EntityWithStringObjectMap instance = new EntityWithStringObjectMap();
        Map<String, Object> params = new HashMap<>();
        DeserializationTestClass element = new DeserializationTestClass();

        element.setName("sampleName");
        element.setNumber(123l);
        DateTime time = new DateTime();
        element.setSomeDate(time);
        params.put("instance", element);

        instance.setParams(params);

        entityWithStringObjectMapDataService.create(instance);
        List<EntityWithStringObjectMap> instances = entityWithStringObjectMapDataService.retrieveAll();
        assertNotNull(instances);
        assertEquals(1, instances.size());

        instance = instances.get(0);
        assertNotNull(instance);
        assertEquals(1, instance.getParams().size());
        DeserializationTestClass deserializedElement = (DeserializationTestClass) instance.getParams().get("instance");
        assertEquals("sampleName", deserializedElement.getName());
        assertEquals((Long) 123l, deserializedElement.getNumber());
        assertEquals(time.getMillis(), deserializedElement.getSomeDate().getMillis());
    }

    private void setEntitiesFields(EntityA a, EntityB b, EntityC c,
                                   String aName, String bName, Priority bPriority, String cName, Animal cAnimal) {
        a.setName(aName);
        b.setName(bName);
        b.setPriority(bPriority);
        c.setName(cName);
        c.setAnimal(cAnimal);
    }

    private void setEntitiesRelations(EntityA a, EntityB b, EntityC c) {
        a.setEntityB(b);
        b.setEntityC(c);
    }

    private void assertEntitiesFields(EntityA a, EntityB b, EntityC c,
                                      String aName, String bName, Priority bPriority, String cName, Animal cAnimal) {
        assertEquals(aName, a.getName());
        assertNotNull(a.getEntityB());
        assertEquals(bName, a.getEntityB().getName());
        assertEquals(bPriority, a.getEntityB().getPriority());

        assertEquals(bName, b.getName());
        assertEquals(bPriority, b.getPriority());
        assertNotNull(b.getEntityC());
        assertEquals(cName, b.getEntityC().getName());
        assertEquals(cAnimal, b.getEntityC().getAnimal());

        assertEquals(cName, c.getName());
        assertEquals(cAnimal, c.getAnimal());
    }

    private void assertEntityEnhanced(Class<?> clazz) {
        for (String field : Constants.Util.GENERATED_FIELD_NAMES) {
            assertTrue(hasField(clazz, field));
        }
    }

    private boolean hasField(Class<?> clazz, String field) {
        try {
            clazz.getDeclaredField(field);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
