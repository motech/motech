package org.motechproject.openmrs.services;

import org.motechproject.mrs.services.MRSIdentifierAdaptor;
import org.motechproject.openmrs.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

public class OpenMRSIdentifierAdaptor implements MRSIdentifierAdaptor {
    private Context context;
    private String generatorClass;

    public OpenMRSIdentifierAdaptor(String generatorClass, Context context) {
        this.generatorClass = generatorClass;
        this.context = context;
    }

    @Override
    public Object getGeneratorService() {
        try {
            return context.getService(Class.forName(generatorClass));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
