package org.motechproject.mds.jdo;

import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.annotations.AnnotationManager;
import org.datanucleus.metadata.annotations.AnnotationObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.testutil.records.JustPc;
import org.motechproject.mds.testutil.records.PcAndEntity;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.util.ClassName;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsJdoAnnotationReaderTest {

    @Mock
    private MetaDataManager metaDataManager;

    @Mock
    private AnnotationManager annotationManager;

    private MdsJdoAnnotationReader mdsJdoAnnotationReader;

    @Before
    public void setUp() {
        when(metaDataManager.getAnnotationManager()).thenReturn(annotationManager);

        mdsJdoAnnotationReader = new MdsJdoAnnotationReader(metaDataManager);
    }

    @Test
    public void shouldAddEntityPackageAsSupported() {
        assertArrayEquals(new String[]{"javax.jdo", "org.datanucleus", ClassName.getPackage(Entity.class.getName())},
                mdsJdoAnnotationReader.getSupportedAnnotationPackages());
    }

    @Test
    public void shouldRecognizeRegularPCAnnotation() {
        AnnotationObject result = mdsJdoAnnotationReader.isClassPersistable(JustPc.class);

        assertEquals(PersistenceCapable.class.getName(), result.getName());
        assertNotNull(result.getNameValueMap());
        assertEquals("false", result.getNameValueMap().get("cacheable"));
        assertEquals("true", result.getNameValueMap().get("detachable"));
    }

    @Test
    public void shouldRecognizeEntityAnnotation() {
        AnnotationObject result = mdsJdoAnnotationReader.isClassPersistable(Record.class);

        assertEquals(PersistenceCapable.class.getName(), result.getName());
        assertNotNull(result.getNameValueMap());
        assertEquals(IdentityType.DATASTORE, result.getNameValueMap().get("identityType"));
        assertEquals("true", result.getNameValueMap().get("detachable"));
    }

    @Test
    public void shouldPrioritizePersistenceCapableAnnotationValuesOverEntityDefaults() {
        AnnotationObject result = mdsJdoAnnotationReader.isClassPersistable(PcAndEntity.class);

        assertEquals(PersistenceCapable.class.getName(), result.getName());
        assertNotNull(result.getNameValueMap());
        assertEquals(IdentityType.APPLICATION, result.getNameValueMap().get("identityType"));
        assertEquals("false", result.getNameValueMap().get("detachable"));
        assertEquals("testCatalog", result.getNameValueMap().get("catalog"));
    }
}
