package org.motechproject.openmrs;


import org.apache.log4j.Logger;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import static java.lang.String.format;
import static org.openmrs.api.context.Context.*;
import static org.openmrs.util.OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY;
import static org.openmrs.util.OpenmrsConstants.AUTO_UPDATE_DATABASE_RUNTIME_PROPERTY;

public class Context {
    Logger logger = Logger.getLogger(Context.class);
    private String url;
    private String user;
    private String password;
    private String openmrsUser;
    private String openmrsPassword;

    public Context(String url, String user, String password, String openmrsUser, String openmrsPassword) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.openmrsUser = openmrsUser;
        this.openmrsPassword = openmrsPassword;
    }

    public void initialize() throws InputRequiredException, DatabaseUpdateException, IOException, URISyntaxException {
        logger.warn(format("connecting to openmrs instance at %s", url));
        Properties properties = new Properties();
        properties.setProperty(AUTO_UPDATE_DATABASE_RUNTIME_PROPERTY, String.valueOf(true));
        String path = getClass().getClassLoader().getResource("openmrs-data").toURI().getPath();
        logger.warn(format("openmrs data folder is  set to %s", path));
        properties.setProperty(APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        startup(url, user, password, properties);
        openSession();
        authenticate(openmrsUser, openmrsPassword);
    }

    public ServiceContext getServiceContext(){
        return ServiceContext.getInstance();
    }
}
