package org.motechproject.mrs.helper;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;
import org.motechproject.mrs.model.MRSFacilityDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;

public final class MRSDtoHelper {
    private MRSDtoHelper() {
        // static utility class
    }

    public static MRSPatientDto createPatientDto(MRSPatient patient) {
        final MRSPatientDto patientDto = new MRSPatientDto();
        patientDto.setFacility(createFacility(patient.getFacility()));
        patientDto.setPerson(createPersonDto(patient.getPerson()));
        patientDto.setMotechId(patient.getMotechId());
        patientDto.setPatientId(patient.getPatientId());

        return patientDto;
    }

    public static List<MRSPatientDto> createPatientDtoList(List<MRSPatient> patients) {
        List<MRSPatientDto> result = new ArrayList<>();
        for (MRSPatient patient : patients) {
            result.add(createPatientDto(patient));
        }
        return result;
    }

    public static List<MRSAttribute> getAttributesList(List<MRSAttributeDto> attributesDto) {
        final List<MRSAttribute> attributesList = new ArrayList<>();

        for (MRSAttribute attribute : attributesDto) {
            MRSAttributeDto attributeDto = new MRSAttributeDto();
            attributeDto.setName(attribute.getName());
            attributeDto.setValue(attribute.getValue());

            attributesList.add(attributeDto);
        }

        return attributesList;
    }

    public static MRSPerson createPersonDto(MRSPerson person) {
        final MRSPersonDto personDto = new MRSPersonDto();
        final List<MRSAttribute> attributeList = new ArrayList<>();

        if (person != null) {
            for (MRSAttribute attribute : person.getAttributes()) {
                MRSAttributeDto attributeDto = new MRSAttributeDto();
                attributeDto.setName(attribute.getName());
                attributeDto.setValue(attribute.getValue());

                attributeList.add(attributeDto);
            }

            personDto.setPersonId(person.getPersonId());
            personDto.setFirstName(person.getFirstName());
            personDto.setLastName(person.getLastName());
            personDto.setMiddleName(person.getMiddleName());
            personDto.setPreferredName(person.getPreferredName());
            personDto.setDateOfBirth(person.getDateOfBirth());
            personDto.setBirthDateEstimated(person.getBirthDateEstimated());
            personDto.setAddress(person.getAddress());
            personDto.setGender(person.getGender());
            personDto.setDead(person.isDead());
            personDto.setAttributes(attributeList);
            personDto.setDeathDate(person.getDeathDate());

            if (person.getDateOfBirth() != null) {
                personDto.setAge(Years.yearsBetween(person.getDateOfBirth(), new DateTime(now())).getYears());
            }
        }
        return personDto;
    }

    private static MRSFacility createFacility(MRSFacility facility) {
        final MRSFacilityDto facilityDto = new MRSFacilityDto();

        if (facility != null) {
            facilityDto.setFacilityId(facility.getFacilityId());
            facilityDto.setName(facility.getName());
            facilityDto.setCountry(facility.getCountry());
            facilityDto.setRegion(facility.getRegion());
            facilityDto.setCountyDistrict(facility.getCountyDistrict());
            facilityDto.setStateProvince(facility.getStateProvince());
        }

        return facilityDto;
    }
}
