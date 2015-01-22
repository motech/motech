package org.motechproject.mds.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.EntityInfo;
import org.motechproject.mds.domain.FieldInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldInfo;

public class SwaggerGeneratorTest {

    private SwaggerGenerator swaggerGenerator = new SwaggerGenerator();

    @Before
    public void setUp() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("swagger.properties")) {
            Properties swaggerProperties = new Properties();
            swaggerProperties.load(in);
            swaggerGenerator.setSwaggerProperties(swaggerProperties);
        }
    }

    @Test
    public void shouldGenerateJson() {
        StringWriter stringWriter = new StringWriter();

        swaggerGenerator.generateDocumentation(stringWriter, entities());

        assertNotNull(stringWriter.toString());
    }

    private List<EntityInfo> entities() {
        List<EntityInfo> entities = new ArrayList<>();

        // Entity 1

        EntityInfo entity = new EntityInfo();
        entity.setClassName("org.example.TestEntity");
        entity.setEntityName("TestEntity");
        entity.setModule("example");
        entity.setNamespace("ns");

        entity.setRestCreateEnabled(true);
        entity.setRestReadEnabled(true);

        List<FieldInfo> fields = new ArrayList<>();
        fields.add(fieldInfo("str", String.class, true, true));
        fields.add(fieldInfo("integerField", Integer.class, true, true));
        fields.add(fieldInfo("longField", Long.class, false, true));
        fields.add(fieldInfo("timeField", Time.class, false, true));
        fields.add(fieldInfo("ignoredField", String.class, false, false));

        entity.setFieldsInfo(fields);

        entities.add(entity);

        // Entity 2

        entity = new EntityInfo();
        entity.setClassName("org.motechproject.ExampleEnt");
        entity.setNamespace("ExampleEnt");

        entity.setRestUpdateEnabled(true);
        entity.setRestDeleteEnabled(true);

        fields = new ArrayList<>();
        fields.add(fieldInfo("doubleField", Double.class, true, true));
        fields.add(fieldInfo("dateField", Date.class, false, true));
        fields.add(fieldInfo("dtField", DateTime.class, false, true));
        fields.add(fieldInfo("ldField", LocalDate.class, false, true));
        fields.add(fieldInfo("localeField", Locale.class, false, true));

        FieldInfo listField = fieldInfo("listField", List.class, true, true);
        listField.setAdditionalTypeInfo(FieldInfo.TypeInfo.ALLOWS_MULTIPLE_SELECTIONS);
        fields.add(listField);

        entity.setFieldsInfo(fields);

        entities.add(entity);

        return entities;
    }
}
