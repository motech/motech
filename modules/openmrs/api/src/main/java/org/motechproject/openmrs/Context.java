package org.motechproject.openmrs;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.ModuleFactory;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Properties;

public class Context {
    private static Logger logger = Logger.getLogger(Context.class);
    private String url;
    private String user;
    private String password;
    private String openmrsUser;
    private String openmrsPassword;
    private String dataDir;
    private static final String ENABLE_HIBERNATE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";

    @Autowired
    private ResourceLoader resourceLoader;

    public Context(String url, String user, String password, String openmrsUser, String openmrsPassword, String dataDir) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.openmrsUser = openmrsUser;
        this.openmrsPassword = openmrsPassword;
        this.dataDir = dataDir;
    }

    public void initialize() throws InputRequiredException, DatabaseUpdateException, URISyntaxException, IOException {
        Resource resource = (StringUtils.isNotBlank(dataDir)) ?
                new FileSystemResource(dataDir) :
                resourceLoader.getResource("openmrs-data");

        if (resource != null) {
            String path = URLDecoder.decode(resource.getURL().getPath(), CharEncoding.UTF_8);
            logger.info(String.format("openmrs data folder is set to %s", path));

            Properties properties = new Properties();
            properties.setProperty(OpenmrsConstants.AUTO_UPDATE_DATABASE_RUNTIME_PROPERTY, String.valueOf(true));
            properties.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
            properties.setProperty(ENABLE_HIBERNATE_SECOND_LEVEL_CACHE, String.valueOf(false));

            logger.info(String.format("connecting to openmrs instance at %s", url));
            org.openmrs.api.context.Context.startup(url, user, password, properties);

            logger.info(String.format("loaded %d modules", ModuleFactory.getLoadedModules().size()));
        }
    }

    public PatientService getPatientService() {
        return org.openmrs.api.context.Context.getPatientService();
    }

    public PersonService getPersonService() {
        return org.openmrs.api.context.Context.getPersonService();
    }

    public UserService getUserService() {
        return org.openmrs.api.context.Context.getUserService();
    }

    public AdministrationService getAdministrationService() {
        return org.openmrs.api.context.Context.getAdministrationService();
    }

    public LocationService getLocationService() {
        return org.openmrs.api.context.Context.getLocationService();
    }

    public OpenmrsService getService(Class clazz) {
        return (OpenmrsService) org.openmrs.api.context.Context.getService(clazz);
    }

    public ObsService getObsService() {
        return org.openmrs.api.context.Context.getObsService();
    }

    public EncounterService getEncounterService() {
        return org.openmrs.api.context.Context.getEncounterService();
    }

    public ConceptService getConceptService() {
        return org.openmrs.api.context.Context.getConceptService();
    }
}
