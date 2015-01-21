package org.motechproject.mds.web.rest.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.web.rest.docs.RestEntry;
import org.motechproject.mds.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;
import static org.motechproject.mds.testutil.FieldTestHelper.requiredFieldDto;

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

        swaggerGenerator.generateDocumentation(stringWriter, restEntries());

        assertNotNull(stringWriter.toString());
    }

    private List<RestEntry> restEntries() {
        List<RestEntry> restEntries = new ArrayList<>();

        // Entity 1

        EntityDto entity = new EntityDto("org.example.TestEntity");
        entity.setName("TestEntity");
        entity.setModule("example");
        entity.setNamespace("ns");

        List<FieldDto> fields = new ArrayList<>();
        fields.add(requiredFieldDto(1L, "str", String.class));
        fields.add(requiredFieldDto(2L, "integerField", Integer.class));
        fields.add(requiredFieldDto(3L, "longField", Long.class));
        fields.add(fieldDto(4L, "timeField", Time.class));
        fields.add(fieldDto(5L, "ignoredField", String.class));

        RestOptionsDto restOptions = new RestOptionsDto(true, true, false, false, false);
        restOptions.setFieldIds(new ArrayList<Number>(asList(1, 2, 3, 4)));

        restEntries.add(new RestEntry(entity, fields, restOptions, null));

        // Entity 2

        entity = new EntityDto("org.motechproject.ExampleEnt");
        entity.setNamespace("ExampleEnt");

        fields = new ArrayList<>();
        fields.add(requiredFieldDto(6L, "doubleField", Double.class));
        fields.add(fieldDto(7L, "dateField", Date.class));
        fields.add(fieldDto(8L, "dtField", DateTime.class));
        fields.add(fieldDto(9L, "ldField", LocalDate.class));
        fields.add(fieldDto(10L, "localeField", Locale.class));

        FieldDto listField = requiredFieldDto(11L, "listField", List.class);
        listField.setSettings(asList(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, true)));
        fields.add(listField);

        restOptions = new RestOptionsDto(false, false, true, true, false);
        restOptions.setFieldIds(new ArrayList<Number>(Arrays.asList(6, 7, 8, 9, 10, 11)));

        restEntries.add(new RestEntry(entity, fields, restOptions, null));

        return restEntries;
    }
}
