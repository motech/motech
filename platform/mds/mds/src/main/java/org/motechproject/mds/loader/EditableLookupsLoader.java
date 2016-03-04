package org.motechproject.mds.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.internal.MDSProcessorOutput;
import org.motechproject.mds.dto.JsonLookupDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.exception.loader.LookupsJsonReadException;
import org.motechproject.mds.exception.loader.MalformedLookupsJsonException;
import org.motechproject.mds.lookup.EntityLookups;
import org.motechproject.mds.service.JsonLookupService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for loading editable
 */
@Component
public class EditableLookupsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditableLookupsLoader.class);

    private static final Gson GSON = new GsonBuilder().create();

    private static final String MDS_LOOKUPS_JSON = "mds-lookups.json";
    private static final String LOOKUP_ADDED = "Added \"{}\" lookup for \"{}\" entity";
    private static final String INVALID_CLASS_NAME = "Entity \"{}\" does not originate from the \"{}\" bundle. " +
                                                     "Related lookups won't be added.";
    private static final String EMPTY_JSON = "\"mds-lookups.json\" file is empty in module \"{}\".";
    private static final String NO_JSON = "No \"mds-lookups.json\" resource file found for \"{}\" bundle.";

    private JsonLookupService jsonLookupService;

    public void addEditableLookups(MDSProcessorOutput output, Bundle bundle) {
        List<EntityLookups> entitiesLookups = loadEntitiesLookups(bundle);
        addEditableEntitiesLookups(output, bundle, entitiesLookups);
    }

    private void addEditableEntitiesLookups(MDSProcessorOutput output, Bundle bundle,
                                            List<EntityLookups> entitiesLookups) {

        for (EntityLookups entityLookups : entitiesLookups) {

            String entityClassName = entityLookups.getEntityClassName();

            if (output.getEntityProcessorOutputByClassName(entityClassName) != null) {
                addEditableEntityLookups(output, entityLookups);
            } else {
                LOGGER.error(INVALID_CLASS_NAME, entityClassName, bundle.getSymbolicName());
            }
        }
    }

    private void addEditableEntityLookups(MDSProcessorOutput output, EntityLookups entityLookups) {

        String entityClassName = entityLookups.getEntityClassName();
        List<LookupDto> lookupsToAdd = new ArrayList<>();

        for (LookupDto lookup : entityLookups.getLookups()) {
            if (!jsonLookupService.exists(entityClassName, lookup.getLookupName())) {

                lookupsToAdd.add(lookup);

                JsonLookupDto jsonLookup = new JsonLookupDto(entityClassName, lookup.getLookupName());
                jsonLookupService.createJsonLookup(jsonLookup);

                LOGGER.debug(LOOKUP_ADDED, lookup.getLookupName(), entityClassName);
            }
        }

        List<LookupDto> lookups = getLookups(output, entityClassName);

        if (lookupsToAdd.size() > 0) {

            if (lookups == null) {
                lookups = new ArrayList<>();
                output.getLookupProcessorOutputs().put(entityClassName, lookups);
            }

            lookups.addAll(lookupsToAdd);
        }
    }

    private List<EntityLookups> loadEntitiesLookups(Bundle bundle) {

        URL lookupsResource = getLookupsResource(bundle);

        if (lookupsResource == null) {
            return new ArrayList<>();
        }

        try (InputStream stream = lookupsResource.openStream()) {

            String lookupsJson = toLookupsJson(bundle, stream);

            List<EntityLookups> entitiesLookups = new ArrayList<>();

            if (!StringUtils.isBlank(lookupsJson)) {
                entitiesLookups.addAll(Arrays.asList(GSON.fromJson(lookupsJson, EntityLookups[].class)));
            }

            return entitiesLookups;

        } catch (JsonSyntaxException e) {
            throw new MalformedLookupsJsonException(bundle.getSymbolicName(), e);
        } catch (IOException e) {
            throw new LookupsJsonReadException(bundle.getSymbolicName(), e);
        }
    }

    private String toLookupsJson(Bundle bundle, InputStream stream) throws IOException {

        String lookupsJson = IOUtils.toString(stream);

        if (StringUtils.isBlank(lookupsJson)) {
            LOGGER.warn(EMPTY_JSON, bundle);
        }

        return lookupsJson;
    }

    private URL getLookupsResource(Bundle bundle) {

        URL resource = bundle.getResource(MDS_LOOKUPS_JSON);

        if (resource == null) {
            LOGGER.debug(NO_JSON, bundle);
        }

        return resource;
    }

    private List<LookupDto> getLookups(MDSProcessorOutput output, String entityClassName) {
        return output.getLookupProcessorOutputs().get(entityClassName);
    }

    @Autowired
    public EditableLookupsLoader(JsonLookupService jsonLookupService) {
        this.jsonLookupService = jsonLookupService;
    }
}
