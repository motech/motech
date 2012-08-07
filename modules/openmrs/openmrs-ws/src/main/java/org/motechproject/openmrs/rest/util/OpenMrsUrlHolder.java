package org.motechproject.openmrs.rest.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

/**
 * Stores all OpenMRS REST URLs that are configured through openmrs-urls.properties files
 */
@Component
public class OpenMrsUrlHolder implements InitializingBean {
    @Value("${openmrs.url}")
    private String openmrsUrl;

    @Value("${openmrs.rest.resource.patient}")
    private String patientPath;

    @Value("${openmrs.rest.resource.patient.search}")
    private String patientSearchPath;

    @Value("${openmrs.rest.resource.patient.element.full}")
    private String patientFullByUuidPath;

    @Value("${openmrs.rest.resource.personidentifiertype.element.full}")
    private String patientIdentifierTypeListPath;

    @Value("${openmrs.rest.resource.person}")
    private String personPath;

    @Value("${openmrs.rest.resource.person.element.full}")
    private String personFullPath;

    @Value("${openmrs.rest.resource.person.element}")
    private String personUpdatePath;

    @Value("${openmrs.rest.resource.person.element.attribute}")
    private String personAttributePath;

    @Value("${openmrs.rest.resource.personattribute.search}")
    private String personAttributeTypePath;

    @Value("${openmrs.rest.resource.person.element.name}")
    private String personName;

    @Value("${openmrs.rest.resource.person.element.address}")
    private String personAddress;

    @Value("${openmrs.rest.resource.location}")
    private String facilityPath;

    @Value("${openmrs.rest.resource.location.full}")
    private String facilityListAllPath;

    @Value("${openmrs.rest.resource.location.full.search}")
    private String facilityListAllByNamePath;

    @Value("${openmrs.rest.resource.location.element}")
    private String facilityFindByUuidPath;

    @Value("${openmrs.rest.resource.encounter}")
    private String encounterPath;

    @Value("${openmrs.rest.resource.encounter.search.full}")
    private String encounterByPatientUuidPath;

    @Value("${openmrs.rest.resource.concept.search}")
    private String conceptPath;

    @Value("${openmrs.rest.resource.user}")
    private String userResourcePath;

    @Value("${openmrs.rest.resource.user.list.full}")
    private String userListFull;

    @Value("${openmrs.rest.resource.user.list.full.query}")
    private String userListFullQuery;

    @Value("${openmrs.rest.resource.user.query}")
    private String userQuery;

    @Value("${openmrs.rest.resource.user.full}")
    private String userResourceFull;

    @Value("${openmrs.rest.resource.role.list.full}")
    private String roleResourcePath;

    @Value("${openmrs.rest.resource.observation.element}")
    private String observationResource;

    @Value("${openmrs.rest.resource.observation.element.search}")
    private String observationSearchQuery;

    @Value("${openmrs.rest.resource.observation.element.delete.reason}")
    private String observationResourceDeleteReason;

    private URI patient;
    private URI patientIdentifierTypeList;
    private UriTemplate patientSearchPathTemplate;
    private UriTemplate patientFullByUuidTemplate;

    private URI person;
    private UriTemplate personAttributeAdd;
    private UriTemplate personUpdateTemplate;
    private UriTemplate personFullTemplate;
    private UriTemplate personAttributeType;
    private UriTemplate personNameTemplate;
    private UriTemplate personAddressTemplate;

    private URI facilityListUri;
    private URI facilityCreateUri;
    private UriTemplate facilityListUriTemplate;
    private UriTemplate facilityFindUriTemplate;

    private UriTemplate encounterByPatientUuidTemplate;
    private UriTemplate conceptSearchByNameTemplate;
    private UriTemplate creatorByUuidTemplate;
    private URI encounterPathUri;

    private UriTemplate observationResourceByIdTemplate;
    private UriTemplate observationSearchQueryTemplate;
    private UriTemplate observationDeleteWithReasonTemplate;

    private URI userResource;
    private URI userListFullUri;
    private UriTemplate userListFullQueryTemplate;
    private UriTemplate userQueryTemplate;
    private UriTemplate userResourceFullTemplate;

    private URI roleResourceListFull;

    @Override
    public void afterPropertiesSet() throws Exception {
        createPatientUris();
        createPersonUris();
        createFacilityUris();
        createEncounterUris();
        createConceptUris();
        createObservationUris();
        createUserUris();

        roleResourceListFull = new URI(openmrsUrl + roleResourcePath);
    }

    private void createUserUris() throws URISyntaxException {
        userResource = new URI(openmrsUrl + userResourcePath);
        userListFullUri = new URI(openmrsUrl + userListFull);
        userListFullQueryTemplate = new UriTemplate(openmrsUrl + userListFullQuery);
        userQueryTemplate = new UriTemplate(openmrsUrl + userQuery);
        userResourceFullTemplate = new UriTemplate(openmrsUrl + userResourceFull);
    }

    private void createObservationUris() {
        observationResourceByIdTemplate = new UriTemplate(openmrsUrl + observationResource);
        observationSearchQueryTemplate = new UriTemplate(openmrsUrl + observationSearchQuery);
        observationDeleteWithReasonTemplate = new UriTemplate(openmrsUrl + observationResourceDeleteReason);
    }

    private void createPatientUris() throws URISyntaxException {
        patient = new URI(openmrsUrl + patientPath);
        patientIdentifierTypeList = new URI(openmrsUrl + patientIdentifierTypeListPath);
        patientSearchPathTemplate = new UriTemplate(openmrsUrl + patientSearchPath);
        patientFullByUuidTemplate = new UriTemplate(openmrsUrl + patientFullByUuidPath);
    }

    private void createPersonUris() throws URISyntaxException {
        person = new URI(openmrsUrl + personPath);
        personAttributeAdd = new UriTemplate(openmrsUrl + personAttributePath);
        personAttributeType = new UriTemplate(openmrsUrl + personAttributeTypePath);
        personUpdateTemplate = new UriTemplate(openmrsUrl + personUpdatePath);
        personFullTemplate = new UriTemplate(openmrsUrl + personFullPath);
        personNameTemplate = new UriTemplate(openmrsUrl + personName);
        personAddressTemplate = new UriTemplate(openmrsUrl + personAddress);
    }

    private void createFacilityUris() throws URISyntaxException {
        facilityListUriTemplate = new UriTemplate(openmrsUrl + facilityListAllByNamePath);
        facilityFindUriTemplate = new UriTemplate(openmrsUrl + facilityFindByUuidPath);
        facilityListUri = new URI(openmrsUrl + facilityListAllPath);
        facilityCreateUri = new URI(openmrsUrl + facilityPath);
    }

    private void createEncounterUris() throws URISyntaxException {
        encounterByPatientUuidTemplate = new UriTemplate(openmrsUrl + encounterByPatientUuidPath);
        encounterPathUri = new URI(openmrsUrl + encounterPath);
    }

    private void createConceptUris() {
        conceptSearchByNameTemplate = new UriTemplate(openmrsUrl + conceptPath);
    }

    public URI getPatient() {
        return patient;
    }

    public URI getPatientIdentifierTypeList() {
        return patientIdentifierTypeList;
    }

    public URI getPatientSearchPathWithTerm(String term) {
        return patientSearchPathTemplate.expand(term);
    }

    public URI getFullPatientByUuid(String uuid) {
        return patientFullByUuidTemplate.expand(uuid);
    }

    public URI getPersonAttributeType(String attributeName) {
        return personAttributeType.expand(attributeName);
    }

    public URI getPersonAttributeAdd(String uuid) {
        return personAttributeAdd.expand(uuid);
    }

    public URI getPerson() {
        return person;
    }

    public URI getPersonByUuid(String uuid) {
        return personUpdateTemplate.expand(uuid);
    }

    public URI getPersonFullByUuid(String personUuid) {
        return personFullTemplate.expand(personUuid);
    }

    public URI getPersonNameByUuid(String personUuid, String nameUuid) {
        return personNameTemplate.expand(personUuid, nameUuid);
    }

    public URI getPersonAddressByUuid(String personUuid, String addressUuid) {
        return personAddressTemplate.expand(personUuid, addressUuid);
    }

    public URI getFacilityListUri() {
        return facilityListUri;
    }

    public URI getFacilityCreateUri() {
        return facilityCreateUri;
    }

    public URI getFacilityListUri(String facilityName) {
        return facilityListUriTemplate.expand(facilityName);
    }

    public URI getFacilityFindUri(String facilityUuid) {
        return facilityFindUriTemplate.expand(facilityUuid);
    }

    public URI getEncountersByPatientUuid(String uuid) {
        return encounterByPatientUuidTemplate.expand(uuid);
    }

    public URI getEncounterPath() {
        return encounterPathUri;
    }

    public URI getConceptSearchByName(String name) {
        return conceptSearchByNameTemplate.expand(name);
    }

    public URI getCreatorByUuid(String uuid) {
        return creatorByUuidTemplate.expand(uuid);
    }

    public URI getUserResource() {
        return userResource;
    }

    public URI getUserListFullPath() {
        return userListFullUri;
    }

    public URI getUserListFullByTerm(String term) {
        return userListFullQueryTemplate.expand(term);
    }

    public URI getUserByUsername(String username) {
        return userQueryTemplate.expand(username);
    }

    public URI getUserResourceById(String uuid) {
        return userResourceFullTemplate.expand(uuid);
    }

    public URI getRoleResourceListFull() {
        return roleResourceListFull;
    }

    public URI getObservationById(String id) {
        return observationResourceByIdTemplate.expand(id);
    }

    public URI getObservationsByPatient(String patientId) {
        return observationSearchQueryTemplate.expand(patientId);
    }

    public URI getObservationDeleteWithReason(String id, String reason) {
        return observationDeleteWithReasonTemplate.expand(id, reason);
    }
}
