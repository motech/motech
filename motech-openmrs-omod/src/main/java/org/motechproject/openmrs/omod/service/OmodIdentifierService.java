package org.motechproject.openmrs.omod.service;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.List;

public class OmodIdentifierService {

    public String getIdFor(String generatorName, String patientIdTypeName, String user, String password) {

        Context.openSession();
        Context.authenticate(user, password);

        IdentifierSourceService idSourceService = Context.getService(IdentifierSourceService.class);
        PatientService patientService = Context.getService(PatientService.class);

        String newId = StringUtils.EMPTY;
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByName(patientIdTypeName);
        List<IdentifierSource> idSources = idSourceService.getAllIdentifierSources(false);

        for (IdentifierSource idSource : idSources)
            if (idSource instanceof SequentialIdentifierGenerator
                    && idSource.getName().equals(generatorName)
                    && idSource.getIdentifierType().equals(patientIdentifierType)) {
                newId = idSourceService.generateIdentifier(idSource, "AUTO GENERATED");
                break;
            }

        Context.closeSession();
        return newId;
    }

}
