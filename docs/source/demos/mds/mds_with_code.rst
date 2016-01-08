=================================================
Demo: Defining a Data Model with Code Annotations
=================================================

.. contents:: Table of Contents
   :depth: 3

Overview
========

This walkthrough illustrates how to define a custom data model with :doc:`MOTECH Data Services (MDS) <../../modules/data_services>` using code annotations. We'll be developing a simple electronic medical records (EMR) system, as described in the :doc:`Introduction <index>`. Each of the entities described in the Introduction will be implemented as a Developer-Defined Entity (DDE) in MDS.

Snippets of the code are included in the discussion below; for the full module's source code including integration tests, see the `repository on Github <https://github.com/motech-community-modules/simple-emr>`_.

Setup
=====

First, we need to create a new MOTECH module. The easiest way to get up and running is with the Maven archetype, which is described in detail :doc:`here <../../get_started/archetype>`.

Create a minimal bundle with configuration modified for repository::

    mvn archetype:generate -DinteractiveMode=false \
    -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases \
    -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype \
    -DarchetypeVersion=0.25-SNAPSHOT -DgroupId=org.motechproject -DartifactId=simpleemr \
    -Dpackage=org.motechproject -Dversion=0.1-SNAPSHOT -DbundleName="simple-emr" \
    -Drepository=true

Add new source files from the repository archetype::

    mvn archetype:generate -DinteractiveMode=false \
    -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases \
    -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=repository-bundle-archetype \
    -DarchetypeVersion=0.25-SNAPSHOT -DgroupId=org.motechproject -DartifactId=simpleemr  \
    -Dpackage=org.motechproject -Dversion=0.1-SNAPSHOT -DbundleName="simple-emr"

Build the module from the simple-emr directory by executing **mvn install**. Once everything is building, we're ready to start adding some entities.

Person Entity
=============

A typical MOTECH module defines and exposes its data model and business logic at three layers:

#. **Domain** - Java classes that define the data model (fields, getters/setters)
#. **Repository** - CRUD operations on entities in the data store
#. **Service** - Higher level business logic, events, and the publicly-exposed API live here

Each layer in our demo corresponds to a java package in our project. We'll start by developing all three layers for one of the core entities of the EMR: Person.

Domain: Person
--------------

Defining an entity in MDS is as simple as writing a Java class and defining a few fields. The @Entity and @Field annotations tell MDS that the class and its fields represent our data model. Here is the relevant part of the definition of the Person object (with getters/setters, equals, toString and hashCode methods omitted for brevity - refer to the full demo code for these):

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;
    import org.motechproject.mds.annotations.UIDisplayable;

    import org.apache.commons.lang.ObjectUtils;
    import org.joda.time.DateTime;
    import java.util.List;
    import java.util.Objects;

    @Entity
    public class Person {

        @Field(required = true)
        @UIDisplayable(position = 0)
        private String firstName;

        @Field
        @UIDisplayable(position = 1)
        private String middleName;

        @Field
        @UIDisplayable(position = 2)
        private String lastName;

        @Field
        @UIDisplayable(position = 3)
        private DateTime dateOfBirth;

        @Field
        @UIDisplayable(position = 4)
        private Gender gender;

        @Field
        @UIDisplayable(position = 5)
        private String address;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // getters/setters and other methods omitted for brevity
    }

`Person.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Person.java>`_ | `Raw <https://raw.githubusercontent.com/motech-community-modules/simple-emr/master/src/main/java/org/motechproject/simpleemr/domain/Person.java>`_

The :doc:`@Entity </org/motechproject/mds/annotations/Entity>` annotation tells the MDS annotation processor that Person is an entity in our data model. We've decorated each field with the :doc:`@Field </org/motechproject/mds/annotations/Field>` annotation to indicate that these should be fields of the Person object in the database. Notice that for the first field (firstName), we've also indicated that this field is required.

Note that using the @Field annotation isn't the only way to include a member in the MDS data model. Defining a public getter or setter for a member will also qualify it as an entity field, even without the annotation. If we wanted to define a public getter and/or setter for a private member, but *exclude* it from the data model, there's an annotation for that too: :doc:`@Ignore </org/motechproject/mds/annotations/Ignore>`.

The :doc:`@UIDisplayable </org/motechproject/mds/annotations/UIDisplayable>` annotation is optional, and supports a parameter called "position" which we've used here to specify the order in which fields will be rendered in the data editor UI.

We've defined a field gender, of type Gender, so we also need to define the associated enum:

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    public enum Gender {

        UNSPECIFIED,
        MALE,
        FEMALE,
    }

`Gender.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Gender.java>`_ | `Raw <https://raw.githubusercontent.com/motech-community-modules/simple-emr/master/src/main/java/org/motechproject/simpleemr/domain/Gender.java>`_

As one would hope, enum types are rendered as drop-down menus in the MDS UI.

Repository: PersonDataService
-----------------------------

:doc:`MotechDataService </org/motechproject/mds/service/MotechDataService>` provides the base class for CRUD operations on an MDS entity. When defining a custom module that uses MDS, the typical pattern is to define an interface that inherits from MotechDataService in the module's "repository" package, and to define one or more Lookups. More about Lookups in a bit.

In order to be used, the generated service has to be retrieved from the bundle context. You can retrieve and inject it straight into the Spring context by declaring an <osgi:reference> for the service in the module's XML configuration file. So for this example, the blueprint.xml file will need to contain the following:

<osgi:reference id="personDataService" interface="org.motechproject.simpleemr.repository.PersonDataService"/>

If retrieved in this manner, the service can then be then used as a regular Spring bean.

.. note::

    If you do not provide an interface, one will be generated for you. The name of this interface will be:

    org.motechproject.mds.entities.FooService

    where Foo is the name of the entity. For a Patient class, the generated service will have the following name: org.motechproject.mds.entities.PatientService. A service of this interface can be retrieved and then be used as a MotechDataService, though this will not leverage generics and will produce unchecked code.

That's a long-winded introduction for what follows, which is just a few lines of code to define the interface and one lookup:

.. code-block:: java

    package org.motechproject.simpleemr.repository;

    import org.motechproject.simpleemr.domain.Person;
    import org.motechproject.mds.annotations.Lookup;
    import org.motechproject.mds.annotations.LookupField;
    import org.motechproject.mds.service.MotechDataService;

    import java.util.List;

    public interface PersonDataService extends MotechDataService<Person> {
        @Lookup
        List<Person> findByName(@LookupField(name = "firstName") String firstName);
    }

`PersonDataService.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/repository/PersonDataService.java>`_ | `Raw <https://raw.githubusercontent.com/motech-community-modules/simple-emr/master/src/main/java/org/motechproject/simpleemr/repository/PersonDataService.java>`_

In the interface definition above, we've defined one Lookup, called findByName, which will retrieve all Person objects with the supplied first name (in a more comprehensive example we would likely define additional Lookups for other search parameter combinations). A lookup is a public method that acts as a method for retrieving one or more copies of the entity from the datastore.

In order to be a valid lookup, a method must be annotated with :doc:`@Lookup </org/motechproject/mds/annotations/Lookup>`. Moreover it must take parameters which will define what values will be used in the query. Every parameter will be treated as a lookup field, but it can also be annotated with :doc:`@LookupField </org/motechproject/mds/annotations/LookupField>`. This is useful if the parameter name differs from the name of the entity field. The lookup must return either a single entity instance or a java.util.List of the given entity.

Service: PersonService
----------------------

Finally, the service layer injects the data service and exposes a public interface by which to retrieve and persist Patient records.
First, the interface:

.. code-block:: java

    package org.motechproject.simpleemr.service;

    import java.util.List;

    import org.motechproject.simpleemr.domain.Person;

    public interface PersonService {

        void create(String firstName, String lastName);

        void add(Person person);

        List<Person> findPersonsByName(String firstName);

        List<Person> getPersons();

        void delete(Person person);

        void update(Person person);
    }

`PersonService.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/service/PersonService.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/service/PersonService.java>`_

The implementation uses the data service (repository package) to perform CRUD operations on Person objects in MDS.

.. code-block:: java

    package org.motechproject.simpleemr.service.impl;

    import org.motechproject.simpleemr.domain.Person;
    import org.motechproject.simpleemr.repository.PersonDataService;
    import org.motechproject.simpleemr.service.PersonService;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service("personService")
    public class PersonServiceImpl implements PersonService {

        @Autowired
        private PersonDataService personDataService;

        @Override
        public void create(String firstName, String lastName) {
            personDataService.create(new Person(firstName, lastName));
        }

        @Override
        public void add(Person person) {
            personDataService.create(person);
        }

        @Override
        public List<Person> findPersonsByName(String firstName) {
            List<Person> persons = personDataService.findByName(firstName);
            if (null == persons) {
                return null;
            }
            return persons;
        }

        @Override
        public List<Person> getPersons() {
            return personDataService.retrieveAll();
        }

        @Override
        public void update(Person person) {
            personDataService.update(person);
        }

        @Override
        public void delete(Person person) {
            personDataService.delete(person);
        }
    }

`PersonServiceImpl.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/service/impl/PersonServiceImpl.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/service/impl/PersonServiceImpl.java>`_

Person UI
---------

Data Browser - Viewing DDEs
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Now that we've defined the Person entity, we can create instances of Person using the Data Services UI. Before that, we need to:

#. Build the module - **mvn clean install** in the simple-emr directory.
#. Deploy it - either through the Manage Modules UI if your server is already running, or just start up MOTECH if not already running and it will be picked up from your ~/.motech/bundles directory.

Now navigate to the Data Services module, choose the Data Browser tab, and scroll down to the Simple EMR module. You'll see an entry for the Person object. Click there to see the list of Person entities we've created so far (i.e. none -- an empty list).

    .. image:: img/data_browser_person.png
        :scale: 100 %
        :alt: MDS Demo - data browser, no records yet
        :align: center

Now, let's try adding an instance using the data browser UI. Click on the "Add" button, and you'll be presented with a form. Notice that all of the form fields are in the same order that we specified using the @UIDisplayable annotation on our Person fields.

    .. image:: img/data_browser_create_person.png
        :scale: 100 %
        :alt: MDS Demo - create person
        :align: center

Go crazy, create a whole slew of people if you're so inclined. After each record that you create, you'll be taken back to the Person Instances list in the data browser. Here's how it might look with two records:

    .. image:: img/data_browser_people.png
        :scale: 100 %
        :alt: MDS Demo - data browser, two records
        :align: center

Schema Editor - Enhancing DDEs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

While we're looking at the UI, let's take time for a short digression to look at enhancing DDEs in the Schema Editor. Suppose that at some point in the future, a customer of your module might want to customize the data model of the DDEs that you have defined. Say for example that XYZ organization is happily using the Simple EMR, but they require storing a phone number on each Person record. They could fork your code and add a phone number field to Person... assuming that they have software engineers on staff. Or they can just add a new field using the Schema Editor UI.

Let's try it out. Navigate to the Schema Editor tab, and select Person (you can type it into the select box to filter to objects).

    .. image:: img/schema_editor_select_entity.png
        :scale: 100 %
        :alt: MDS Demo - schema editor, select entity
        :align: center

You'll see a list of the existing Person fields. You can start typing to create a new one, then click on the plus (+) sign to configure additional attributes.

    .. image:: img/schema_editor_person_fields.png
        :scale: 100 %
        :alt: MDS Demo - schema editor, view person fields
        :align: center

Let's define a field for phone number. Give it a display name, a symbolic name, and a data type, designate whether or not it is required, and define any default values if relevant (in this case, most likely not). You can define other settings on the other tabs, like validation rules and custom metadata. Make sure to click the "Save changes" button when finished.

    .. image:: img/schema_editor_custom_field.png
        :scale: 100 %
        :alt: MDS Demo - schema editor, add new field
        :align: center

Facility Entity
===============

Now let's shift gears back to the code. Facility is a very simple little entity and a good one to tackle next. Let's look at all three layers, like we did for Person.

Domain: Facility
----------------

First, the domain pojo. Ours has just two fields, name and address (feel free to add others if you're so inclined):

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;

    import javax.jdo.annotations.Unique;

    @Entity
    public class Facility {

        @Field(required = true)
        @Unique
        private String name;

        @Field
        private String address;

        public Facility(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public Facility(String name) {
            this(name, null);
        }

        // getters/setters and other methods omitted for brevity

    }

`Facility.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Facility.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/domain/Facility.java>`_

Repository: FacilityDataService
-------------------------------

Naturally, our FacilityDataService has a Lookup that supports finding facilities by name:

.. code-block:: java

    package org.motechproject.simpleemr.repository;

    import org.motechproject.simpleemr.domain.Facility;
    import org.motechproject.mds.annotations.Lookup;
    import org.motechproject.mds.annotations.LookupField;
    import org.motechproject.mds.service.MotechDataService;

    public interface FacilityDataService extends MotechDataService<Facility> {
        @Lookup
        Facility findByName(@LookupField(name = "name") String facilityName);
    }

`FacilityDataService.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/repository/FacilityDataService.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/repository/FacilityDataService.java>`_

Service: FacilityService
------------------------

And finally, the FacilityService interface defines all of our basic CRUD operations for Facility:

.. code-block:: java

    package org.motechproject.simpleemr.service;

    import java.util.List;

    import org.motechproject.simpleemr.domain.Facility;

    public interface FacilityService {

        void create(String name, String address);

        void add(Facility facility);

        Facility findFacilityByName(String name);

        List<Facility> getFacilities();

        void delete(Facility facility);

        void update(Facility facility);

    }

`FacilityService.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/service/FacilityService.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/service/FacilityService.java>`_

We'll skip the FacilityServiceImpl -- feel free to code that one up on your own, or copy from the `reference implementation <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/service/impl/FacilityServiceImpl.java>`_. Now, if desired, we can build and redeploy the module, and start playing with Facility entities in the Data Browser and Schema Editor.

    .. image:: img/data_browser_create_facility.png
        :scale: 100 %
        :alt: MDS Demo - data browser, create new facility
        :align: center

Patient and Provider Entities
=============================

To define Patient and Provider entities in our EMR, we need to look at the concept of relationships. Recall our diagram from the :doc:`Introduction <index>` -- Patient and Provider each have a 1:1 relationship with a Person object. Moreover, the relationship is a master-detail relationship -- we want the Patient (master) record to control behaviors of the Person (detail) record. For example,  when a Patient is deleted from the system, we want the corresponding Person object to be removed as well. Fortunately, this is as simple as defining a field of type Person on the domain objects for Patient and Provider, and decorating the field with some annotations.

Let's take a look at Patient. Here is how we might specify the entity:

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;
    import org.motechproject.mds.annotations.Cascade;

    import org.apache.commons.lang.ObjectUtils;
    import java.util.List;
    import java.util.Objects;

    @Entity
    public class Patient {

        @Field(required = true)
        @Cascade(delete = true)
        private Person person;

        @Field
        private Facility facility;

        public Patient(Person person) {
            this.person = person;
        }

        public Patient(Person person, Facility facility) {
            this(person);
            this.facility = facility;
        }

        // getters/setters and other methods omitted for brevity

    }

`Patient.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Patient.java>`_ | `Raw <https://raw.githubusercontent.com/motech-community-modules/simple-emr/master/src/main/java/org/motechproject/simpleemr/domain/Patient.java>`_

You'll notice that the person field is decorated with two annotations. The first is the @Field annotation that we now know quite well; the "required" parameter is exactly what you might expect. The :doc:`@Cascade </org/motechproject/mds/annotations/Cascade>` annotation describes the desired behavior for cascading changes among objects that are related. The supported options are persist, update, and delete; the default values for each are true, true, and false, respectively. In this example, we set delete=true to indicate that when a Patient object is removed from the database, its associated Person should be deleted along with it.

Patient also has a 1:1 relationship with Facility, but we haven't specified any Cascade behavior. This is because the defaults (persist = true; update = true; delete = false) are the desired settings for this relationship (we're assuming in this simple model that a Patient typically visits the same Facility but a Provider may work in multiple Facilities).

The domain object for Provider is similar:

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;
    import org.motechproject.mds.annotations.Cascade;

    import org.apache.commons.lang.ObjectUtils;
    import java.util.List;
    import java.util.Objects;

    @Entity
    public class Provider {

        @Field(required = true)
        @Cascade(delete = true)
        private Person person;

        @Field
        private String type;

        public Provider(Person person) {
            this.person = person;
        }

        // getters/setters and other methods omitted for brevity

    }

`Provider.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Provider.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/domain/Provider.java>`_

We won't walk through the process of defining the repository and service layers for Patient and Provider. Feel free to take those on as a homework assignment, or take a peek at the reference implementation.

Concepts, Observations, and Encounters
======================================

Now we've arrived at the most interesting entities in our EMR: the objects that allow us to store information about interactions among Providers and Patients. More sophisticated EMRs support a rich data model for this; for the purposes of our demo we'll keep things relatively simple and introduce three new types: Concepts, Observations, and Encounters.

Concept Entity (and some Background on Concept Dictionaries)
------------------------------------------------------------

At the heart of many EMR systems is the concept dictionary -- an extensible repository of coded questions and answers. The questions correspond to the questions that a patient might be asked, or observations that a health care provider would make about a patient -- e.g. blood pressure, weight, blood type, marital status. A concept dictionary could be populated by modeling all of the fields from legacy paper forms when migrating a project or clinic to an EMR, or one of a number of standard concept dictionaries could be reused.

At the very least, a Concept in a concept dictionary requires a name and a data type (text, boolean, numeric, etc.). For our concept dictionary, we'll also support a `concept class <https://wiki.openmrs.org/display/docs/Managing+Concept+Classes>`_ field borrowed from OpenMRS -- this allows for classification of concepts as questions and answers. Our demo currently doesn't make use of concept classes in any sophisticated way, but future versions could.

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;
    import org.motechproject.mds.annotations.UIDisplayable;

    import javax.jdo.annotations.Unique;
    import org.apache.commons.lang.ObjectUtils;
    import java.util.List;
    import java.util.Objects;

    @Entity
    public class Concept {

        @Field(required = true)
        @Unique
        @UIDisplayable(position = 0)
        private String name;

        @Field(required = true)
        @UIDisplayable(position = 1)
        private DataType dataType;

        @Field(required = true)
        @UIDisplayable(position = 2)
        private ConceptClass conceptClass;

        @Field(required = true)
        @UIDisplayable(position = 3)
        private String display;

        public Concept(String name, DataType dataType, ConceptClass conceptClass, String display) {
            this.name = name;
            this.dataType = dataType;
            this.conceptClass = conceptClass;
            this.display = display;
        }

        // getters/setters and other methods omitted for brevity

    }

`Concept.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Concept.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/domain/Concept.java>`_

We also need to define corresponding enums for DataType and ConceptClass -- again, feel free to try this on your own or peek at the reference implementation.

Observation Entity
------------------

Now that we have Concepts, we can define Observations. An Observation ties together a question and an answer into an individual data point about a Patient. In a full-fledged EMR, Observation would support using Concepts for both questions and answers (in addition to free-form text answers). To keep this demo simple, we'll just use Concepts for questions and keep the answers free-form (of type String).

Hence, our Observation entity look like this:

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;

    import javax.jdo.annotations.Persistent;
    import org.apache.commons.lang.ObjectUtils;
    import java.util.List;
    import java.util.Objects;
    import java.util.Date;

    @Entity
    public class Observation {

        @Field
        private Date date;

        @Field
        private Concept concept;

        @Field
        private String value;

        @Field
        private Patient patient;

        public Observation(Date date, Concept concept, String value) {
            this.date = date;
            this.concept = concept;
            this.value = value;
        }

        public Observation(Date date, Concept concept, String value, Patient patient) {
            this(date, concept, value);
            this.patient = patient;
        }

        // getters/setters and other methods omitted for brevity

    }

`Observation.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Observation.java>`_ | `Raw <https://github.com/motech-community-modules/simple-emr/raw/master/src/main/java/org/motechproject/simpleemr/domain/Observation.java>`_

Encounter Entity
----------------

Encounters are where we tie everything together. An Encounter represents an interaction between a Provider and a Patient, and it encapsulates a great deal of information:

#. The Provider
#. The Patient he/she saw
#. The Facility where the visit took place
#. The date of the visit
#. The type of visit (exam, procedure, lab visit, etc.)
#. The set of Observations that the Provider made about the Patient

Because an Encounter may (and typically will) include numerous Observations about a Patient, we need to use a one-to-many relationship. We can do this by including a field that is a Collection type in our entity -- in this case, we'll capture multiple Observations using Set<Observation>. Our entity will also have 1:1 relationships with Provider, Patient, and Facility.

.. code-block:: java

    package org.motechproject.simpleemr.domain;

    import org.motechproject.mds.annotations.Entity;
    import org.motechproject.mds.annotations.Field;

    import org.apache.commons.lang.ObjectUtils;
    import java.util.Objects;
    import java.util.Date;
    import java.util.Set;

    @Entity
    public class Encounter {

        @Field(required = true)
        private Provider provider;

        @Field
        private Facility facility;

        @Field(required = true)
        private Date date;

        @Field
        private Set<Observation> observations;

        @Field(required = true)
        private Patient patient;

        @Field
        private String encounterType;

        public Encounter(Date date, Patient patient, Provider provider) {
            this.date = date;
            this.patient = patient;
        }

        // getters/setters and other methods omitted for brevity

    }

`Encounter.java <https://github.com/motech-community-modules/simple-emr/blob/master/src/main/java/org/motechproject/simpleemr/domain/Encounter.java>`_ | `Raw <https://raw.githubusercontent.com/motech-community-modules/simple-emr/master/src/main/java/org/motechproject/simpleemr/domain/Encounter.java>`_

Et voila, now we have a super cool Encounter object that ties together all of the other data types in our EMR. The example above marks only three fields as required: provider, patient, and date. This allows for flexibility of *where* the visit might take place (in case it doesn't actually happen in a medical facility), and allows the Observations to be added after the Encounter object is created and saved.

Once we add the repository and service layers for our new entities (again, as a homework exercise, or copied from the reference implementation), we have a simple, but fully functional EMR module!

Summary
=======

Let's build one more time (**mvn clean install** in the simple-emr directory) and deploy the module. Take a look at our list of entities in the MDS data browser.

    .. image:: img/data_browser_entity_list.png
        :scale: 100 %
        :alt: MDS Demo - data browser, entity list
        :align: center

It's fun to browse the entity list, create new instances, and play around with the UI and advanced settings. One thing to be warned about is that it's not yet possible to do much with relationships in either the schema editor or data browser UI -- more will be possible once `MOTECH-1049 <https://applab.atlassian.net/browse/MOTECH-1049>`_ is implemented.

Of course, no code is complete without test cases! The `test <https://github.com/motech-community-modules/simple-emr/tree/master/src/test>`_ subdirectory in the reference implementation contains integration tests that exercise all of our service classes. The tests can be run by executing **mvn install -PIT**.
