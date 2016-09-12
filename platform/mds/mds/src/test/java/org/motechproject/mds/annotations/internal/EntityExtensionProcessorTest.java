package org.motechproject.mds.annotations.internal;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.internal.samples.*;
import org.motechproject.mds.domain.ManyToOneRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.dto.*;
import org.motechproject.mds.exception.entity.EntityDoesNotExtendEntityException;
import org.motechproject.mds.exception.entity.EntityDoesNotExtendMDSEntityException;
import org.motechproject.mds.exception.entity.FieldExistInExtendedEntityException;
import org.motechproject.mds.testutil.MockBundle;
import org.motechproject.mds.util.SecurityMode;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class EntityExtensionProcessorTest extends MockBundle{

    @Spy
    private Bundle bundle = new org.eclipse.gemini.blueprint.mock.MockBundle();


    @Mock
    private SchemaHolder schemaHolder;


    @Mock
    private FieldProcessor fieldProcessor;

    @Mock
    private FieldProcessor fieldProcessorEx;

    @Mock
    private UIFilterableProcessor uiFilterableProcessor;

    @Mock
    private UIFilterableProcessor uiFilterableProcessorEx;

    @Mock
    private UIDisplayableProcessor uiDisplayableProcessor;

    @Mock
    private UIDisplayableProcessor uiDisplayableProcessorEx;

    @Mock
    private RestOperationsProcessor restOperationsProcessor;

    @Mock
    private RestOperationsProcessor restOperationsProcessorEx;

    @Mock
    private RestIgnoreProcessor restIgnoreProcessor;

    @Mock
    private RestIgnoreProcessor restIgnoreProcessorEx;

    @Mock
    private CrudEventsProcessor crudEventsProcessor;

    @Mock
    private CrudEventsProcessor crudEventsProcessorEx;

    @Mock
    private NonEditableProcessor nonEditableProcessor;

    @Mock
    private NonEditableProcessor nonEditableProcessorEx;

    @Mock
    private BundleWiring bundleWiring;

    @Mock
    private BundleWiring bundleWiringEx;

    @Mock
    private TreeSet<Bundle> affectedBundles;

    @Mock
    private EntityProcessorOutput entityProcessorOutput;

    @Captor
    private ArgumentCaptor<EntityDto> captor;

    @Captor
    private ArgumentCaptor<TrackingDto> trackingDtoCaptor;

    private EntityExtensionProcessor processor;

    private EntityProcessor entityProcessor;

    private FieldProcessor fieldProcessorForTest;

    EntityDto entity = new EntityDto(
        null, AnotherSample.class.getName(), "test", "mds", null, null, false,

        SecurityMode.EVERYONE, null, null, null, null, false, false, null);

    EntityDto readOnlyEntity = new EntityDto(
        null, ReadAccessSample.class.getName(), "foo", "mds", null, null, false,
        null, null, SecurityMode.EVERYONE, null, null, false, false, null);


    @Before
    public void setUp() throws Exception {
        processor = new EntityExtensionProcessor();
        processor.setFieldProcessor(fieldProcessorEx);
        processor.setUIFilterableProcessor(uiFilterableProcessorEx);
        processor.setUIDisplayableProcessor(uiDisplayableProcessorEx);
        processor.setRestOperationsProcessor(restOperationsProcessorEx);
        processor.setRestIgnoreProcessor(restIgnoreProcessorEx);
        processor.setCrudEventsProcessor(crudEventsProcessorEx);
        processor.setNonEditableProcessor(nonEditableProcessorEx);
        processor.setBundle(bundle);
        processor.setSchemaHolder(schemaHolder);
        processor.beforeExecution();
        processor.setAffectedBundles(affectedBundles);
        processor.setExtendedEntityProcessorOutput(entityProcessorOutput);

        entityProcessor = new EntityProcessor();
        entityProcessor.setFieldProcessor(fieldProcessor);
        entityProcessor.setUIFilterableProcessor(uiFilterableProcessor);
        entityProcessor.setUIDisplayableProcessor(uiDisplayableProcessor);
        entityProcessor.setRestOperationsProcessor(restOperationsProcessor);
        entityProcessor.setRestIgnoreProcessor(restIgnoreProcessor);
        entityProcessor.setCrudEventsProcessor(crudEventsProcessor);
        entityProcessor.setNonEditableProcessor(nonEditableProcessor);
        entityProcessor.setBundle(bundle);
        entityProcessor.setSchemaHolder(schemaHolder);
        entityProcessor.beforeExecution();

        fieldProcessorForTest = new FieldProcessor();
        fieldProcessorForTest.setBundle(bundle);
        fieldProcessorForTest.setSchemaHolder(schemaHolder);

        doReturn(TypeDto.STRING).when(schemaHolder).getType(String.class);
        doReturn(TypeDto.STRING).when(schemaHolder).getType(String.class.getName());
        doReturn(TypeDto.TIME).when(schemaHolder).getType(Time.class);
        doReturn(TypeDto.DATE).when(schemaHolder).getType(Date.class);
        doReturn(TypeDto.BOOLEAN).when(schemaHolder).getType(Boolean.class);
        doReturn(TypeDto.BOOLEAN).when(schemaHolder).getType(boolean.class);
        doReturn(TypeDto.DOUBLE).when(schemaHolder).getType(Double.class);
        doReturn(TypeDto.DOUBLE).when(schemaHolder).getType(Double.class.getName());
        doReturn(TypeDto.DOUBLE).when(schemaHolder).getType(double.class);
        doReturn(TypeDto.INTEGER).when(schemaHolder).getType(Integer.class.getName());
        doReturn(TypeDto.INTEGER).when(schemaHolder).getType(Integer.class);
        doReturn(TypeDto.INTEGER).when(schemaHolder).getType(int.class);
        doReturn(TypeDto.LONG).when(schemaHolder).getType(Long.class);
        doReturn(TypeDto.LONG).when(schemaHolder).getType(long.class);
        doReturn(TypeDto.COLLECTION).when(schemaHolder).getType(Collection.class);
        doReturn(TypeDto.ONE_TO_ONE_RELATIONSHIP).when(schemaHolder).getType(OneToOneRelationship.class);
        doReturn(TypeDto.ONE_TO_MANY_RELATIONSHIP).when(schemaHolder).getType(OneToManyRelationship.class);
        doReturn(TypeDto.MANY_TO_ONE_RELATIONSHIP).when(schemaHolder).getType(ManyToOneRelationship.class);

        when(schemaHolder.getType(Long.class)).thenReturn(TypeDto.LONG);
        when(schemaHolder.getType(String.class)).thenReturn(TypeDto.STRING);
        when(schemaHolder.getType(DateTime.class)).thenReturn(TypeDto.DATETIME);

        when(schemaHolder.getEntityByClassName(AnotherSample.class.getName())).thenReturn(entity);

        when(schemaHolder.getEntityByClassName(ReadAccessSample.class.getName())).thenReturn(readOnlyEntity);

        when(schemaHolder.getAdvancedSettings(anyString())).thenReturn(new AdvancedSettingsDto());

        setUpMockBundle();
    }

    @Test
    public void shouldReturnCorrectAnnotation() throws Exception {
        assertEquals(EntityExtension.class, processor.getAnnotationType());
    }

    @Test
    public void shouldReturnCorrectElementList() throws Exception {
        File file = computeTestDataRoot(getClass());
        String location = file.toURI().toURL().toString();

        doReturn(location).when(bundle).getLocation();
        doReturn(Sample.class).when(bundle).loadClass(Sample.class.getName());
        doReturn(bundleWiring).when(bundle).adapt(BundleWiring.class);

        Set<? extends AnnotatedElement> actual = processor.getElementsToProcess();

        assertEquals(4, actual.size());
        assertContainsClass(actual, FieldOrverridingSample.class.getName());
        assertContainsClass(actual, ExtendedSample.class.getName());
    }

    @Test
    public void shouldProcessClassWithEntityExtensionAnnotation() throws Exception {

        when(restOperationsProcessor.getProcessingResult()).thenReturn(new RestOptionsDto());
        when(crudEventsProcessor.getProcessingResult()).thenReturn(new TrackingDto());

        entityProcessor.process(Sample.class);
        List<EntityProcessorOutput> entityProcessorOutput = entityProcessor.getProcessingResult();

        List<MDSProcessorOutput> outputs = new ArrayList<>();

        MDSProcessorOutput mdsProcessorOutput = new MDSProcessorOutput(entityProcessorOutput, null, bundle);
        outputs.add(mdsProcessorOutput);

        processor.setEntitiesProcessingResult(outputs);
        processor.process(ExtendedSample.class);


        verify(fieldProcessorEx).setClazz(ExtendedSample.class);
        verify(fieldProcessorEx).setEntity(any(EntityDto.class));
        verify(fieldProcessorEx).execute(bundle, schemaHolder);

        verify(uiFilterableProcessorEx).setClazz(ExtendedSample.class);
        verify(uiFilterableProcessorEx).execute(bundle, schemaHolder);

        verify(uiDisplayableProcessorEx).setClazz(ExtendedSample.class);
        verify(uiDisplayableProcessorEx).execute(bundle, schemaHolder);

        verify(nonEditableProcessorEx).setClazz(ExtendedSample.class);
        verify(nonEditableProcessorEx).execute(bundle, schemaHolder);
    }

    @Test
    public void shouldOverrideRecordHistoryFlag() {
        when(restOperationsProcessor.getProcessingResult()).thenReturn(new RestOptionsDto());
        when(crudEventsProcessor.getProcessingResult()).thenReturn(new TrackingDto());

        entityProcessor.process(AnotherSample.class);

        List<EntityProcessorOutput> entityProcessorOutputs = entityProcessor.getProcessingResult();
        List<MDSProcessorOutput> outputs = new ArrayList<>();
        MDSProcessorOutput mdsProcessorOutput = new MDSProcessorOutput(entityProcessorOutputs, null, bundle);
        outputs.add(mdsProcessorOutput);

        verify(crudEventsProcessor).setClazz(AnotherSample.class);
        verify(crudEventsProcessor).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessor).execute(bundle, schemaHolder);

        TrackingDto trackingDto = trackingDtoCaptor.getValue();

        assertFalse(trackingDto.isRecordHistory());


        processor.setEntitiesProcessingResult(outputs);
        processor.process(AnotherExtendedSample.class);

        verify(crudEventsProcessorEx).setClazz(AnotherExtendedSample.class);
        verify(crudEventsProcessorEx).setTrackingDto(trackingDtoCaptor.capture());
        verify(crudEventsProcessorEx).execute(bundle, schemaHolder);

        trackingDto = trackingDtoCaptor.getValue();
        assertTrue(trackingDto.isRecordHistory());
    }

    @Test (expected = EntityDoesNotExtendMDSEntityException.class)
    public void shouldFailWhenClassExtendsNotMDSEntityClass() {
        entityProcessor.process(Sample.class);

        List<EntityProcessorOutput> entityProcessorOutputs = entityProcessor.getProcessingResult();
        List<MDSProcessorOutput> outputs = new ArrayList<>();
        MDSProcessorOutput mdsProcessorOutput = new MDSProcessorOutput(entityProcessorOutputs, null, bundle);
        outputs.add(mdsProcessorOutput);

        processor.setEntitiesProcessingResult(outputs);
        processor.process(AnotherExtendedSample.class);
    }

    @Test
    public void shouldNotProcessClassWithoutEntityExtensionAnnotation() throws Exception {
        processor.process(Object.class);

        verifyZeroInteractions(schemaHolder, fieldProcessorEx);
    }

    @Test(expected = EntityDoesNotExtendEntityException.class)
    public void shouldFailWhenClassDoesNotExtednsAnyClass(){
        processor.process(NotExtendingSample.class);
    }

    @Test(expected = FieldExistInExtendedEntityException.class)
    public void shouldFaileWhenTryToOverrideField(){
        when(restOperationsProcessor.getProcessingResult()).thenReturn(new RestOptionsDto());
        when(crudEventsProcessor.getProcessingResult()).thenReturn(new TrackingDto());

        processor.setFieldProcessor(fieldProcessorForTest);

        entityProcessor.setFieldProcessor(fieldProcessorForTest);
        entityProcessor.process(Sample.class);

        List<EntityProcessorOutput> entityProcessorOutputs = entityProcessor.getProcessingResult();
        List<MDSProcessorOutput> outputs = new ArrayList<>();
        MDSProcessorOutput mdsProcessorOutput = new MDSProcessorOutput(entityProcessorOutputs, null, bundle);
        outputs.add(mdsProcessorOutput);

        processor.setEntitiesProcessingResult(outputs);
        processor.process(FieldOrverridingSample.class);
    }

    @Override
    protected Map<String, Class> getMappingsForLoader() {
        Map<String, Class> mappings = new LinkedHashMap<>();
        mappings.put(Sample.class.getName(), Sample.class);

        return mappings;
    }

    @Override
    protected Class getTestClass() {
        return getClass();
    }

    @Override
    protected Bundle getMockBundle() {
        return bundle;
    }

    private void assertContainsClass(Set<? extends AnnotatedElement> classes, String className) {
        for (AnnotatedElement element : classes) {
            Class<?> clazz = (Class<?>) element;
            if (clazz.getName().equals(className)) {
                return;
            }
        }
        fail("Class not found in list: " + className);
    }
}
