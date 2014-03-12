package org.motechproject.mds.builder;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.impl.internal.EntityServiceImpl;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MdsDataProviderBuilderTest {

    private MDSDataProviderBuilder mdsDataProviderBuilder = new MDSDataProviderBuilder();

    private List<EntityDto> entityList = new LinkedList<>();
    private List<LookupDto> lookupList = new LinkedList<>();
    private List<FieldDto> fieldList = new LinkedList<>();

    @Mock
    private EntityService entityService = new EntityServiceImpl();


    private VelocityEngine velocityEngine = new VelocityEngine();

    @Before
    public void setUp() {
        when(entityService.getEntitiesWithLookups()).thenReturn(entityList);
        when(entityService.getEntityLookups(Long.valueOf("1"))).thenReturn(lookupList);
        when(entityService.getEntityFields(Long.valueOf("1"))).thenReturn(fieldList);
        mdsDataProviderBuilder.setEntityService(entityService);

        velocityEngine.addProperty("resource.loader", "classpath");
        velocityEngine.addProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        mdsDataProviderBuilder.setVelocityEngine(velocityEngine);
    }

    @Test
    public void shouldGenerateEmptyJson() {
        String generatedJson = mdsDataProviderBuilder.generateDataProvider();
        assertEquals(generatedJson, "");
    }

    @Test
    public void shouldGenerateJson() {
        String json = "{\n" +
                "    \"name\": \"data-services\",\n" +
                "    \"objects\": [         {\n" +
                "            \"displayName\": \"TestEntity\",\n" +
                "            \"type\": \"TestEntity\",\n" +
                "            \"lookupFields\": [                 {\n" +
                "                    \"displayName\": \"TestLookupName\",\n" +
                "                    \"fields\": [\n" +
                "                         \"TestFieldName\"                      ]\n" +
                "                }              ],\n" +
                "            \"fields\": [\n" +
                "                                {\n" +
                "                    \"displayName\": \"TestFieldDisplayName\",\n" +
                "                    \"fieldKey\": \"TestFieldName\"\n" +
                "                }              ]\n" +
                "        }      ]\n" +
                "}\n";

        EntityDto entity = new EntityDto();
        entity.setId(Long.valueOf("1"));
        entity.setName("TestEntity");

        FieldDto field = new FieldDto();
        FieldBasicDto fieldBasicDto = new FieldBasicDto();
        fieldBasicDto.setName("TestFieldName");
        fieldBasicDto.setDisplayName("TestFieldDisplayName");
        field.setBasic(fieldBasicDto);
        fieldList.add(field);

        LookupDto lookup = new LookupDto();
        lookup.setLookupName("TestLookupName");
        List<String> fieldNames = new LinkedList<>();
        fieldNames.add("TestFieldName");
        lookup.setFieldNames(fieldNames);
        lookupList.add(lookup);
        entityList.add(entity);

        String generatedJson = mdsDataProviderBuilder.generateDataProvider();

        assertEquals(generatedJson, json);
    }
}
