package org.motechproject.openmrs.omod.service;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

public class OmodIdentifierService {
    private static final String AUTO_GENERATED = "AUTO GENERATED";
    private IdentifierSourceService idSourceService;
    private PatientService patientService;

    public OmodIdentifierService() {
        idSourceService = Context.getService(IdentifierSourceService.class);
        patientService = Context.getService(PatientService.class);
    }

    public String getIdFor(String generatorName, String patientIdTypeName) {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByName(patientIdTypeName);
        List<IdentifierSource> idSources = idSourceService.getAllIdentifierSources(false);
        for (IdentifierSource idSource : idSources)
            if (idSource instanceof SequentialIdentifierGenerator
                    && idSource.getName().equals(generatorName)
                    && idSource.getIdentifierType().equals(patientIdentifierType))
                return idSourceService.generateIdentifier(idSource, AUTO_GENERATED);
        return StringUtils.EMPTY;
    }

}
