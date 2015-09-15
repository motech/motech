package org.motechproject.mds.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.EntitySchemaBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.util.Constants.MetadataKeys.ENUM_CLASS_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;
import static org.motechproject.mds.util.Constants.Settings.ALLOW_MULTIPLE_SELECTIONS;
import static org.motechproject.mds.util.Constants.Util.FALSE;
import static org.motechproject.mds.util.Constants.Util.TRUE;

@RunWith(MockitoJUnitRunner.class)
public class InstancesWriterTest {

    public static final String ENTITY = "test.TestEntity";
    public static final String PROPERTY = "property";
    public static final String NULL_PROPERTY_JSON = "{\"refId\":1,\"property\":null}";

    @Mock
    private ExportContext exportContext;

    @Mock
    private MotechDataService dataService;

    @Before
    public void setUp() throws Exception {
        when(exportContext.getDataService(eq(ENTITY))).thenReturn(dataService);
    }

    @Test
    public void shouldWritePlainIntegerProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.integer").done().build();
        Integer integer = 13;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, integer), "{\"refId\":1,\"property\":13}");
    }

    @Test
    public void shouldWritePlainDecimalProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.decimal").done().build();
        Double decimal = 13.45;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, decimal), "{\"refId\":1,\"property\":13.45}");
    }

    @Test
    public void shouldWritePlainBooleanProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.boolean").done().build();
        Boolean bool = true;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, bool), "{\"refId\":1,\"property\":true}");
    }

    @Test
    public void shouldWriteMapProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.map").done().build();
        Map<String, String> map = new HashMap<>();
        map.put("keyOne", "valOne");
        map.put("keyTwo", "valTwo");
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, map), "{\"refId\":1,\"property\":{\"keyOne\":\"valOne\",\"keyTwo\":\"valTwo\"}}");
    }

    @Test
    public void shouldWriteNullForNullMapProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.map").done().build();
        Map<String, String> map = null;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, map), NULL_PROPERTY_JSON);
    }

    @Test
    public void shouldWriteSingleValueComboboxProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.combobox")
                .setting(ALLOW_MULTIPLE_SELECTIONS, FALSE)
                .metadata(ENUM_CLASS_NAME, "org.motechproject.mds.json.InstanceWriterTest.ABC")
                .done()
                .build();
        ABC combobox = ABC.A;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, combobox), "{\"refId\":1,\"property\":\"A\"}");
    }

    @Test
    public void shouldWriteNullForNullSingleValueComboboxProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.combobox")
                .setting(ALLOW_MULTIPLE_SELECTIONS, FALSE)
                .metadata(ENUM_CLASS_NAME, "org.motechproject.mds.json.InstanceWriterTest.ABC")
                .done()
                .build();
        ABC combobox = null;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, combobox), NULL_PROPERTY_JSON);
    }

    @Test
    public void shouldWriteMultiValueComboboxProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.combobox")
                .setting(ALLOW_MULTIPLE_SELECTIONS, TRUE)
                .metadata(ENUM_CLASS_NAME, ABC.class.getName())
                .done()
                .build();
        List<ABC> combobox = asList(ABC.A, ABC.C);
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, combobox), "{\"refId\":1,\"property\":[\"A\", \"C\"]}");
    }

    @Test
    public void shouldWriteNullForNullMultiValueComboboxProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.combobox")
                .setting(ALLOW_MULTIPLE_SELECTIONS, TRUE)
                .metadata(ENUM_CLASS_NAME, ABC.class.getName())
                .done()
                .build();
        List<ABC> combobox = null;
        assertInstanceWrittenCorrectly(entity, new EntityWithProperty<>(1L, combobox), NULL_PROPERTY_JSON);
    }

    @Test
    public void shouldWriteOneRefIdRelationshipProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.relationship.oneToOne")
                .metadata(RELATED_CLASS, EntityWithProperty.class.getName())
                .metadata(RELATED_FIELD, PROPERTY)
                .done()
                .build();
        EntityWithProperty<EntityWithProperty> instance = new EntityWithProperty<>(1L, null);
        EntityWithProperty<EntityWithProperty> relatedInstance = new EntityWithProperty<>(42L, null);
        instance.setProperty(relatedInstance);
        relatedInstance.setProperty(instance);
        assertInstanceWrittenCorrectly(entity, instance, "{\"refId\":1,\"property\":42}");
    }

    @Test
    public void shouldWriteNullForNullOneRefIdRelationshipProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.relationship.oneToOne")
                .metadata(RELATED_CLASS, EntityWithProperty.class.getName())
                .metadata(RELATED_FIELD, PROPERTY)
                .done()
                .build();
        EntityWithProperty<EntityWithProperty> instance = new EntityWithProperty<>(1L, null);
        assertInstanceWrittenCorrectly(entity, instance, NULL_PROPERTY_JSON);
    }

    @Test
    public void shouldWriteManyRefIdRelationshipProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.relationship.oneToMany")
                .metadata(RELATED_CLASS, EntityWithProperty.class.getName())
                .metadata(RELATED_FIELD, PROPERTY)
                .done()
                .build();
        EntityWithProperty<ArrayList<EntityWithProperty>> instance = new EntityWithProperty<>(1L, new ArrayList<EntityWithProperty>());
        EntityWithProperty<EntityWithProperty> relatedInstanceOne = new EntityWithProperty<EntityWithProperty>(42L, instance);
        EntityWithProperty<EntityWithProperty> relatedInstanceTwo = new EntityWithProperty<EntityWithProperty>(57L, instance);
        instance.getProperty().add(relatedInstanceOne);
        instance.getProperty().add(relatedInstanceTwo);
        assertInstanceWrittenCorrectly(entity, instance, "{\"refId\":1,\"property\":[42,57]}");
    }

    @Test
    public void shouldWriteNullForNullManyRefIdRelationshipProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY)
                .field(PROPERTY, "mds.field.relationship.oneToMany")
                .metadata(RELATED_CLASS, EntityWithProperty.class.getName())
                .metadata(RELATED_FIELD, PROPERTY)
                .done()
                .build();
        EntityWithProperty<ArrayList<EntityWithProperty>> instance = new EntityWithProperty<>(1L, null);
        assertInstanceWrittenCorrectly(entity, instance, NULL_PROPERTY_JSON);
    }

    @Test
    public void shouldWriteBlobProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.blob").done().build();
        byte[] blob = "This is blob!".getBytes();
        EntityWithProperty<byte[]> instance = new EntityWithProperty<>(1L, blob);
        when(dataService.getDetachedField(eq(1L), eq(PROPERTY))).thenReturn(blob);

        assertInstanceWrittenCorrectly(entity, instance, "{\"refId\":1,\"property\":\"VGhpcyBpcyBibG9iIQ==\"}");
    }

    @Test
    public void shouldWriteNullForNullBlobProperty() throws Exception {
        Entity entity = EntitySchemaBuilder.eude(ENTITY).field(PROPERTY, "mds.field.blob").done().build();
        byte[] blob = null;
        EntityWithProperty<byte[]> instance = new EntityWithProperty<>(1L, blob);
        when(dataService.getDetachedField(eq(1L), eq(PROPERTY))).thenReturn(blob);

        assertInstanceWrittenCorrectly(entity, instance, NULL_PROPERTY_JSON);
    }

    private void assertInstanceWrittenCorrectly(Entity entity, Object instance, String expectedString) throws IOException {

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        InstancesWriter instancesWriter = new InstancesWriter(jsonWriter, entity, exportContext);

        instancesWriter.writeInstance(instance);
        jsonWriter.flush();

        JsonParser parser = new JsonParser();
        JsonElement actual = parser.parse(stringWriter.toString());
        JsonElement expected = parser.parse(expectedString);

        Assert.assertEquals(expected, actual);
    }

    public static class EntityWithProperty<P> {
        private Long id;
        private P property;

        public EntityWithProperty(Long id, P property) {
            this.id = id;
            this.property = property;
        }

        public Long getId() {
            return id;
        }

        public P getProperty() {
            return property;
        }

        public void setProperty(P property) {
            this.property = property;
        }
    }

    public static enum ABC { A, B, C }
}
