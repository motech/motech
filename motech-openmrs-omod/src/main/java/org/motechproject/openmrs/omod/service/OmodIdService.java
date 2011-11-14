package org.motechproject.openmrs.omod.service;

import org.motechproject.openmrs.omod.domain.PatientIdTypeName;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OmodIdService {
    private Logger log = LoggerFactory.getLogger(OmodIdService.class);

    private static final String IDGEN_SEQ_ID_GEN_MOTECH_ID = "MoTeCH ID Generator";
    private static final String IDGEN_SEQ_ID_GEN_STAFF_ID = "MoTeCH Staff ID Generator";
    private static final String IDGEN_SEQ_ID_GEN_FACILITY_ID = "MoTeCH Facility ID Generator";
    private static final String IDGEN_SEQ_ID_GEN_MOTECH_ID_GEN_COMMENT = "AUTO GENERATED";

    private IdentifierSourceService idSourceService;
    private PatientService patientService;

    public OmodIdService() {
        idSourceService = Context.getService(IdentifierSourceService.class);
        patientService = Context.getService(PatientService.class);
    }

    public String generateFacilityId() {
        return generateId(IDGEN_SEQ_ID_GEN_FACILITY_ID, patientIdTypeFor(PatientIdTypeName.PATIENT_IDENTIFIER_FACILITY_ID));
    }

    public String generateStaffId() {
        return generateId(IDGEN_SEQ_ID_GEN_STAFF_ID, patientIdTypeFor(PatientIdTypeName.PATIENT_IDENTIFIER_STAFF_ID));
    }

    public String generateMotechId() {
        return generateId(IDGEN_SEQ_ID_GEN_MOTECH_ID, patientIdTypeFor(PatientIdTypeName.PATIENT_IDENTIFIER_MOTECH_ID));
    }


    private String generateId(String generatorName, PatientIdentifierType identifierType) {
        SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(generatorName, identifierType);
        return idSourceService.generateIdentifier(idGenerator, IDGEN_SEQ_ID_GEN_MOTECH_ID_GEN_COMMENT);
    }

    private PatientIdentifierType patientIdTypeFor(PatientIdTypeName patientIdentifierTypeName) {
        return patientService.getPatientIdentifierTypeByName(patientIdentifierTypeName.getKey());
    }

    private SequentialIdentifierGenerator getSeqIdGenerator(String name, PatientIdentifierType identifierType) {
        SequentialIdentifierGenerator idGenerator = null;
        try {
            List<IdentifierSource> idSources = idSourceService.getAllIdentifierSources(false);
            for (IdentifierSource idSource : idSources) {
                if (idSource instanceof SequentialIdentifierGenerator
                        && idSource.getName().equals(name)
                        && idSource.getIdentifierType().equals(identifierType)) {
                    idGenerator = (SequentialIdentifierGenerator) idSource;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving " + name + " for " + identifierType
                    + " in Idgen module", e);
        }
        return idGenerator;
    }
}
