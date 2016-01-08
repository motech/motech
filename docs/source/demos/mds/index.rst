=====================================================
Demo: Modeling a New System with MOTECH Data Services
=====================================================

:doc:`MOTECH Data Services (MDS) <../../modules/data_services>` is a user-configurable data store, that allows a MOTECH admin or developer to define the objects that are relevant for her application. To illustrate the use of MDS, we'll build out the data model for a simple electronic medical records (EMR) system stored in MOTECH. This tutorial will describe two different methods for defining the data model:

#. :doc:`Using code annotations in a custom module <mds_with_code>`
#. :doc:`Using the MDS Schema Editor user interface <mds_with_ui>`

Both demos will define a simple EMR with the following entities:

* **Person** - contains basic demographic information (name, gender, DOB) about a person in the EMR.
* **Patient** - represents a patient; has a 1:1 relationship to a Person object.
* **Provider** - represents a health care provider (nurse, physician, community health worker); has a 1:1 relationship to a Person object.
* **Facility** - represents a clinic or other health care facility.
* **Observation** - represents an observation made by a provider about a specific patient; consists of a concept (question) and a value
* **Concept** - an individual data point or question collected from patients (e.g. blood type or eye color). OpenMRS provides detailed documentation for their `concept dictionary <https://wiki.openmrs.org/display/docs/Concept+Dictionary+Basics>`_; we'll develop only a very limited version of it for this demo.
* **Encounter** - represents a provider's encounter with a patient, and has a 1:n relationship with the Observation entity.

These entities are inspired by (but represent only a small subset of) the domain model of OpenMRS. MOTECH's OpenMRS module implements a similar domain model. In our simplified model, the relationships among these entities can be represented by the following diagram:

    .. image:: img/relationships.png
        :scale: 100 %
        :alt: MDS Demo - relationships
        :align: center

Choose your adventure (:doc:`code <mds_with_code>` or :doc:`UI <mds_with_ui>`) and let's get started!
