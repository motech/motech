package org.motechproject.mds.performance.service.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.joda.time.DateTime;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.entity.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.performance.service.MdsDummyDataGenerator;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.Util.GENERATED_FIELD_NAMES;

/**
 * The <code>MdsDummyDataGenerator</code> class, allows developers and testers to
 * create any amount of dummy entities and instances in MDS.
 */
public class MdsDummyDataGeneratorImpl implements MdsDummyDataGenerator {

    private EntityService entityService;
    private JarGeneratorService jarGeneratorService;
    private BundleContext bundleContext;

    private static final Random RAND = new Random(System.currentTimeMillis());

    // Each generated entity will be named using this prefix concatenated with a number:
    // (GENERATED_ENTITIES_PREFIX)(NEXT_NUMBER)  eg. Entity0, Entity1, Entity2 ...
    private String entityPrefix = "Entity";

    // Each generated field will be named using this prefix concatenated with a number:
    // (GENERATED_ENTITIES_PREFIX)(NEXT_NUMBER)  eg. field0, field1, field2 ...
    private String fieldPrefix = "field";

    // Each generated lookup will be named using this prefix concatenated with a number:
    // (GENERATED_ENTITIES_PREFIX)(NEXT_NUMBER)  eg. Lookup0, Lookup1, Lookup2 ...
    private String lookupPrefix = "Lookup";

    public MdsDummyDataGeneratorImpl(EntityService entityService, JarGeneratorService jarGeneratorService, BundleContext bundleContext) {
        this.entityService = entityService;
        this.jarGeneratorService = jarGeneratorService;
        this.bundleContext = bundleContext;
    }

    @Override
    public void generateDummyEntities(int number, boolean regenerateBundle) throws IOException {
        generateDummyEntities(number, 0, 0, regenerateBundle);
    }

    @Override
    public void generateDummyEntities(int numberOfEntities, int fieldsPerEntity, int lookupsPerEntity, boolean regenerateBundle) throws IOException {
        for (int i = 0; i < numberOfEntities; i++) {
            prepareDummyEntity(i, fieldsPerEntity, lookupsPerEntity);
        }

        if (regenerateBundle) {
            SchemaHolder schemaHolder = entityService.getSchema();
            jarGeneratorService.regenerateMdsDataBundle(schemaHolder);
        }
    }

    @Override
    public void generateDummyInstances(Long entityId, int numberOfInstances)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        generateDummyInstances(entityId, numberOfInstances, 0, 0);
    }

    @Override
    public void generateDummyInstances(Long entityId, int numberOfInstances,
                                       int numberOfHistoricalRevisions, int numberOfTrashInstances)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        MotechDataService service = getService(entitiesBundle.getBundleContext(), getEntityClassName(entityId));

        makeInstances(service, entityId, numberOfInstances, numberOfHistoricalRevisions, numberOfTrashInstances);
    }

    @Override
    public Object makeDummyInstance(Long entityId) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        String className = entityService.getEntity(entityId).getClassName();
        Class objectClass = MDSClassLoader.getInstance().loadClass(className);
        Object obj = objectClass.newInstance();

        for (FieldDto fieldDto : entityService.getEntityFields(entityId)) {
            String fieldName = fieldDto.getBasic().getName();

            if (!ArrayUtils.contains(GENERATED_FIELD_NAMES, fieldName)) {
                PropertyUtil.safeSetProperty(obj, fieldName, randomValueFor(fieldDto.getType()));
            }
        }

        return obj;
    }

    /*
     * NOTE: Only removes the records, but table schema stays in DB.
     * You might be required to clear that manually.
     */
    @Override
    public void clearEntities() {
        for (EntityDto entity : entityService.listEntities()) {
            if (!entity.isDDE()) {
                entityService.deleteEntity(entity.getId());
            }
        }
    }

    private void prepareDummyEntity(int number, int fieldsPerEntity, int lookupsPerEntity) throws IOException {
        EntityDto entityDto = new EntityDto(Long.valueOf(number), entityPrefix.concat(String.valueOf(number)));
        entityDto = entityService.createEntity(entityDto);

        List<FieldDto> fields = new ArrayList<>();

        for (int i = 0; i < fieldsPerEntity; i++) {
            TypeDto type = pickRandomFieldType();
            fields.add(new FieldDto(null, entityDto.getId(),
                    type,
                    new FieldBasicDto(fieldPrefix.concat(String.valueOf(i)),
                            fieldPrefix.concat(String.valueOf(i))),
                    false, null, null, settingsFor(type), null));
        }

        entityService.addFields(entityDto, fields);

        List<LookupDto> lookups = new ArrayList<>();

        for (int i = 0; i < lookupsPerEntity; i++) {
            List<LookupFieldDto> lookupFields = new ArrayList<>();
            List<FieldDto> entityFields = entityService.getFields(entityDto.getId());

            int amountOfFields = RAND.nextInt(entityFields.size());

            for (int j = 0; j < amountOfFields; j++) {
                lookupFields.add(new LookupFieldDto(null, entityFields.get(j).getBasic().getName(), LookupFieldType.VALUE));
            }
            lookups.add(new LookupDto(lookupPrefix.concat(String.valueOf(i)), RAND.nextBoolean(), RAND.nextBoolean(), lookupFields, false));
        }

        entityService.addLookups(entityDto.getId(), lookups);
    }

    private void makeInstances(MotechDataService service, Long entityId,
                               int numberOfInstances, int numberOfHistoricalRevisions, int numberOfTrashInstances)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        EntityDto entityDto = entityService.getEntity(entityId);

        for (int i = 0; i < numberOfInstances; i++) {
            Object obj = makeDummyInstance(entityDto.getId());
            service.create(obj);

            for (int j = 0; j < numberOfHistoricalRevisions; j++) {
                service.update(obj);
            }
        }

        for (int i = 0; i < numberOfTrashInstances; i++) {
            Object obj = makeDummyInstance(entityDto.getId());
            obj = service.create(obj);
            service.delete(obj);
        }
    }

    private String getEntityClassName(Long entityId) {
        EntityDto entityDto = entityService.getEntity(entityId);
        return entityDto == null ? null : entityDto.getClassName();
    }

    private List<SettingDto> settingsFor(TypeDto type) {
        LinkedList settings = new LinkedList();
        if (TypeDto.DOUBLE.equals(type)) {
            settings.add(new SettingDto("precision", 9));
        }

        return settings;
    }

    private Object randomValueFor(TypeDto type) {
        if (TypeDto.BOOLEAN.equals(type)) {
            return RAND.nextBoolean();
        } else if (TypeDto.INTEGER.equals(type)) {
            return RAND.nextInt();
        } else if (TypeDto.LONG.equals(type)) {
            return RAND.nextLong();
        } else if (TypeDto.DOUBLE.equals(type)) {
            return RAND.nextDouble();
        } else if (TypeDto.STRING.equals(type)) {
            return makeRandomStringValue();
        } else if (TypeDto.DATETIME.equals(type)) {
            return makeRandomDateTime();
        }

        return null;
    }

    private TypeDto pickRandomFieldType() {
        int randomType = RAND.nextInt(6);
        switch (randomType) {
            case 0:
                return TypeDto.BOOLEAN;
            case 1:
                return TypeDto.INTEGER;
            case 2:
                return TypeDto.LONG;
            case 3:
                return TypeDto.DOUBLE;
            case 4:
                return TypeDto.STRING;
            case 5:
                return TypeDto.DATETIME;
        }
        return null;
    }

    private DateTime makeRandomDateTime() {
        return new DateTime(RAND.nextInt(Integer.MAX_VALUE));
    }

    private String makeRandomStringValue() {
        int chars = 1 + RAND.nextInt(150);
        return RandomStringUtils.randomAscii(chars);
    }

    @Override
    public MotechDataService getService(BundleContext bundleContext, String className) {
        String interfaceName = MotechClassPool.getInterfaceName(className);
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        if (ref == null) {
            throw new ServiceNotFoundException(interfaceName);
        }

        return (MotechDataService) bundleContext.getService(ref);
    }

    @Override
    public String getEntityPrefix() {
        return entityPrefix;
    }

    @Override
    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    @Override
    public String getLookupPrefix() {
        return lookupPrefix;
    }

    @Override
    public void setLookupPrefix(String lookupPrefix) {
        this.lookupPrefix = lookupPrefix;
    }

    @Override
    public String getFieldPrefix() {
        return fieldPrefix;
    }

    @Override
    public void setFieldPrefix(String fieldPrefix) {
        this.fieldPrefix = fieldPrefix;
    }
}
