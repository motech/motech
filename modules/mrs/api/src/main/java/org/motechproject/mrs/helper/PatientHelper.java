package org.motechproject.mrs.helper;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.model.AttributeDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;

public final class PatientHelper {
    private PatientHelper() {
        // static utility class
    }

    public static PatientDto createPatientDto(Patient patient) {
        final PatientDto patientDto = new PatientDto();
        patientDto.setFacility(createFacility(patient.getFacility()));
        patientDto.setPerson(createPersonDto(patient.getPerson()));
        patientDto.setMotechId(patient.getMotechId());
        patientDto.setPatientId(patient.getPatientId());

        return patientDto;
    }

    public static List<PatientDto> createPatientDtoList(List<Patient> patients) {
        List<PatientDto> result = new ArrayList<>();
        for (Patient patient : patients) {
            result.add(createPatientDto(patient));
        }
        return result;
    }

    public static List<Attribute> getAttributesList(List<AttributeDto> attributesDto) {
        final List<Attribute> attributesList = new ArrayList<>();

        for (Attribute attribute : attributesDto) {
            AttributeDto attributeDto = new AttributeDto();
            attributeDto.setName(attribute.getName());
            attributeDto.setValue(attribute.getValue());

            attributesList.add(attributeDto);
        }

        return attributesList;
    }

    private static Person createPersonDto(Person person) {
        final PersonDto personDto = new PersonDto();
        final List<Attribute> attributeList = new ArrayList<>();

        if (person != null) {
            for (Attribute attribute : person.getAttributes()) {
                AttributeDto attributeDto = new AttributeDto();
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

    private static Facility createFacility(Facility facility) {
        final FacilityDto facilityDto = new FacilityDto();

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
