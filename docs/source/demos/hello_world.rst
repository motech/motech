=================
Demo: Hello World
=================

.. contents:: Table of Contents
   :depth: 3

Overview
========

This "Hello World" tutorial aims to get you started with Motech development. The tutorial is separated into two phases. In the first, we generate the Hello World module incrementally from a series of archetypes provided by the Motech platform. We briefly tour the project code, configuration, and layout that make up a minimal Motech module, and then add in additional archetypes to support web requests and data services. Finally, we build and deploy the module to our Motech server.

In the second phase, we introduce an essential feature of the Motech platform: the Event system. The Event system allows our module to communicate with other modules by emitting and listening for events. We also introduce the Tasks module, which allows us to wire up Motech events using a graphical user interface. Finally, we introduce Motech Data Services, which we use to persist entities. Using these tools, we modify the archetype-generated code to save a new record whenever users request a URL defined by our module.

This tutorial assumes you are at least somewhat familiar with Java, `Maven <http://maven.apache.org/>`_, and the `Spring Framework <http://projects.spring.io/spring-framework/>`_, and have completed the instructions to :doc:`set up your development machine <../development/dev_setup/dev_install>`. We use `Intellij IDEA 13 Community Edition <http://www.jetbrains.com/idea/>`_ as the integrated development environment, but this is not a requirement.

This tutorial was written with Motech 0.24.

Generating the Module from Archetypes
=====================================

Motech is a modular system. Modules are units of functionality that encapsulate application-specific business logic in a reusable package. The Open Service Gateway initiative specification (OSGi) provides the framework to describe this modular architecture: each Motech module is also an OSGi bundle. The OSGi host manages the lifecycle of a module (adding, starting, stopping, and removing), and allows a module to expose services for use by other modules.

To mitigate some of the complexity of configuring OSGi bundles, the Motech platform provides a number of Maven archetypes. The archetypes are also modular: each new module begins from the minimal bundle archetype, and additional capabilities--serving web requests, managing data, and more--are enabled by adding their respective archetypes to the minimal bundle's foundation.

Generating the Minimal Bundle
-----------------------------

The minimal bundle archetype generates the basic project layout and configuration sufficient to begin module development in Motech. To generate the bundle, open a terminal from the directory you wish to contain the project, then enter the following command::

    mvn archetype:generate -DinteractiveMode=false -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.24-SNAPSHOT -DgroupId=org.motechproject -DartifactId=helloworld -Dversion=0.1-SNAPSHOT -DbundleName="Hello World Module" -Dpackage=org.motechproject.helloworld -Dhttp=true -Drepository=true

The flags given with the Maven command instruct Maven to generate a minimal bundle from the 0.24-SNAPSHOT branch of the Motech repository. Once the process completes, the generated project resides in a folder named after the artifact ID, in this case "helloworld."

Tour of the Minimal Bundle
--------------------------

First, import the module into your favorite IDE. If using Intellij IDEA, from the "Welcome to Intellij IDEA" splash screen, select *Import Project*, navigate to the *helloworld* folder, and click *OK*. Select *Import project from external model*, select *Maven*, and accept the defaults by clicking "Next" and finally "Finish" through the remaining options.

The minimal bundle archetype generates a standard Maven project layout. In the Java folder, the archetype created a service package under our top level :code:`org.motechproject.helloworld package`. Inside the package, the archetype created the interface and implementation of a very simple Spring service. We'll modify this service later in the tutorial to make it a bit more interesting.

As for configuration, the *resources/META-INF* directory contains folders for Motech and Spring XML files. In the *motech* folder, the *applicationContext.xml* file enables Spring component scanning of our base Java package, and declares details of our module as a :code:`ModuleRegistrationData` bean. In the *spring* folder, the *blueprint.xml* file exposes our :code:`HelloWorldService` as an OSGi service. Since we activated repository support when generating the minimal bundle, the :code:`HelloWorldRecordService` is also exposed. This code will be added later in the tutorial, when we add the repository archetype. Similarly, we get an OSGi reference to the :code:`HelloWorldRecordsDataService`, also to be added later. Any additional references to Motech platform services will be added to this blueprint file.

Adding the HTTP Archetype
-------------------------

Now that we have some perspective on the basic module layout, let's add the HTTP archetype into the mix. With a terminal open at the same folder you issued the first Maven command, enter the following::

    mvn archetype:generate -DinteractiveMode=false -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=http-bundle-archetype -DarchetypeVersion=0.24-SNAPSHOT -DgroupId=org.motechproject -DartifactId=helloworld -Dpackage=org.motechproject.helloworld -Dversion=0.1-SNAPSHOT -DbundleName="Hello World Module"

This archetype pulls in additional dependencies to interact with and integration test Spring web servlets. Returning to the IDE, notice that our top level package now contains an additional subpackage called web, with a simple Spring controller. The controller makes our module a little more interesting by injecting our previously generated :code:`HelloWorldService` and providing a URL route to exercise the service's public API.

The HTTP archetype also creates a *webapp* folder in the *resources* directory. This folder contains the module's static files, including HTML partials, stylesheets, JavaScripts, and internationalized messages. Motech modules use the `AngularJS <https://angularjs.org/>`_ framework to drive the front-end client, so the archetype created the top-level module, controllers, directives, and services necessary for a simple Angular client in the *js* folder.

Adding the Repository Archetype
-------------------------------

As the final step in setting up our basic Hello World module, let's generate the repository archetype code with the following Maven command::

    mvn archetype:generate -DinteractiveMode=false -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=repository-bundle-archetype -DarchetypeVersion=0.24-SNAPSHOT -DgroupId=org.motechproject -DartifactId=helloworld -Dpackage=org.motechproject.helloworld -Dversion=0.1-SNAPSHOT -DbundleName="Hello World Module"

The repository archetype created two new packages, :code:`domain` and :code:`repository`, which contain a simple data model and repository service, respectively. In addition, the archetype added an interface and implementation to the service package, so we can interact with the data layer.

Taking a closer look at the domain and repository packages, the :code:`HelloWorldRecord` is a typical Java bean that models a record with name and message fields. The class-level :code:`@Entity` annotation identifies the record as a data type to be managed and persisted by the core :doc:`Motech Data Services (MDS) <../modules/data_services>` module. The MDS :code:`@Field` annotations provide object-relational mappings between the bean's fields and columns in the database. The :code:`HelloWorldRecordsDataService` interface extends the base :code:`MotechDataService` interface, inheriting functionality to provide basic CRUD operations for our :code:`HelloWorldRecord` objects. Using the MDS :code:`@Lookup` annotation, we provide a custom method by which to find a :code:`HelloWorldRecord`, in this case by name. Additional custom lookups can be defined here.

In the service package, the :code:`HelloWorldRecordService` injects the data service and exposes a public interface by which to retrieve and persist records.

Building and Deploying the Module
---------------------------------

To build the project in Maven, create a new run configuration in Intellij by clicking *Run -> Edit Configurations...*. Click the green plus sign to add a new configuration, and select Maven. Name the configuration "Maven clean install", enter "clean install" in the command line field, and click *OK*. Finally, click the *Run* button in the upper right hand corner to package the module.

Alternatively, from the command line, change directory to the *helloworld* module's folder and type "mvn clean install".

Once the build succeeds, the project directory will contain our module packaged as a jar in the */target* subfolder. With your Tomcat server running, open a browser and navigate to http://localhost:8080/motech-platform-server/, then log in with your administrator credentials. Click the *Admin* tab, then click *Manage Modules* from the sidebar navigation. In the dropdown labeled *Install module from*, choose *File*, then click the *Select File* and choose our packaged *helloworld-0.1-SNAPSHOT.jar*. Click the *Start on Install* button to toggle the icon to a check mark, and then finally click *Install or Update*.

If all goes well, our *Hello World Module* should appear alongside the other installed modules in the user interface. The state should be "Active."

As a small test of our web controller, navigate your browser to http://localhost:8080/motech-platform-server/helloworld/sayHello. The controller should respond with the JSON string :code:`{"message":"Hello World"}`.

An Event-Driven Hello World
===========================

In this second phase of the tutorial, we implement a feature that creates a new record whenever a user requests the :code:`sayHello` URL route defined by our module. To accomplish this, we emit an event when the URL is accessed, listen for an event instructing our module to create a new record, and finally wire these two separate events together using the :doc:`Tasks <../modules/tasks>` module.

Declaring the Event System Dependency
-------------------------------------

To include events in our module, we first need to declare the dependency on the event system in our *pom.xml* file. Between the :code:`<dependencies>` opening and closing tags, add the following:

.. code-block:: xml

    <dependency>
        <groupId>org.motechproject</groupId>
        <artifactId>motech-platform-event</artifactId>
        <version>${motech.version}</version>
    </dependency>

Secondly, our module will hook into Motech's Event Relay, so we need to add a reference to the relay alongside our other OSGi configuration in *blueprint.xml*:

.. code-block:: xml

    <osgi:reference id="eventRelayOsgi" cardinality="0..1" interface="org.motechproject.event.listener.EventRelay" />

With these dependencies, we are now able to inject the event relay into our own code, send events, and define event listeners.

Event Subjects and Event Parameters
-----------------------------------

Motech events are identified by a String value called the subject. To encapsulate these values as constant values, let's create an event package under our main module package to contain the following class:

.. code-block:: java

    package org.motechproject.helloworld.event;

    public final class HelloWorldEventSubjects {

        public static final String SEND_HELLO = "helloworld_hello_event";

        public static final String CREATE_HELLO_RECORD = "helloworld_create_hello_record";

        private HelloWorldEventSubjects() {}
    }

Similarly, each event can be supplied with parameters, represented by a map of key-value pairs in which the keys are also constant String values. This class encapsulates those values:

.. code-block:: java

    package org.motechproject.helloworld.event;

    public final class HelloWorldEventParams {

        public static final String NAME = "name";

        public static final String MESSAGE = "message";

        private HelloWorldEventParams() {}
    }

Finally, let's create a helper class to simplify packaging our parameters together into a functional Motech event:

.. code-block:: java

    package org.motechproject.helloworld.event;

    import org.motechproject.event.MotechEvent;

    import java.util.HashMap;
    import java.util.Map;

    public final class HelloWorldEvents {
        public static MotechEvent sendHelloWorldEvent(String name, String message) {
            Map<String, Object> params = new HashMap<>();
            params.put(HelloWorldEventParams.NAME, name);
            params.put(HelloWorldEventParams.MESSAGE, message);
            return new MotechEvent(HelloWorldEventSubjects.SEND_HELLO, params);
        }

        private HelloWorldEvents() {}
    }

Given a name and a message, our static helper method will bundle up the parameters and return a Motech event, which in turn will be passed to the event relay.

Sending an Event
----------------

To send our event, let's modify our implementation of the :code:`HelloWorldService` to send events whenever the :code:`sayHello` method is called:

.. code-block:: java

    package org.motechproject.helloworld.service.impl;

    import org.motechproject.event.listener.EventRelay;
    import org.motechproject.helloworld.event.HelloWorldEvents;
    import org.motechproject.helloworld.service.HelloWorldService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    @Service("helloWorldService")
    public class HelloWorldServiceImpl implements HelloWorldService {

        @Autowired
        private EventRelay eventRelay;

        @Override
        public String sayHello() {
            eventRelay.sendEventMessage(HelloWorldEvents.sendHelloWorldEvent("HWEvent", "Hello world!"));
            return "Hello World";
        }

    }

In the :code:`HelloWorldServiceImpl` listing, we declare a private field to contain a reference to the Event Relay. Annotating the field with Spring's :code:`@Autowired` annotation injects the dependency we declared earlier in the *blueprint.xml* configuration file. Then, in the body of the :code:`sayHello` method, prior to the return statement, we use the event relay's :code:`sendEventMessage` method to send our Motech event.

And that's it! Any Motech module that defines a listener for our :code:`SEND_HELLO` subject can inspect our event and its parameters.

Listening for Events
--------------------

In our Hello World module, we provide a data service layer to create :code:`HelloWorldRecord` instances, but currently do not make use of it. Rather than keep that functionality to ourselves, let's define an event listener that allows other modules to send us requests to create new records. First, create a new subpackage to the event package called handler, then add the following class:

.. code-block:: java

    package org.motechproject.helloworld.event.handler;

    import org.motechproject.event.MotechEvent;
    import org.motechproject.event.listener.annotations.MotechListener;
    import org.motechproject.helloworld.domain.HelloWorldRecord;
    import org.motechproject.helloworld.event.HelloWorldEventParams;
    import org.motechproject.helloworld.event.HelloWorldEventSubjects;
    import org.motechproject.helloworld.service.HelloWorldRecordService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;

    @Component
    public class HelloWorldEventHandler {

        @Autowired
        private HelloWorldRecordService helloWorldRecordService;

        @MotechListener(subjects = {HelloWorldEventSubjects.CREATE_HELLO_RECORD})
        public void handleCreateHelloEvents(MotechEvent event) {
            String name = (String)event.getParameters().get(HelloWorldEventParams.NAME);
            String message = (String)event.getParameters().get(HelloWorldEventParams.MESSAGE);
            HelloWorldRecord record = new HelloWorldRecord(name, message);
            helloWorldRecordService.add(record);
        }
    }

In our event handling class, the :code:`@MotechListener` annotation does the work of transforming a standard Java method into an event-handling method. Using the annotation's subjects element, we declare our interest in receiving :code:`CREATE_HELLO_RECORD` events. Our event-handling method accepts the :code:`MotechEvent` as an argument, extracts the name and message parameters, creates a new :code:`HelloWorldRecord`, and finally uses the injected data service to persist the new record.

Since we are listening for a different event than the one we emit, there is no interaction between our module's event-emitting functionality and event-listening functionality. In the next section, we look at a way to integrate these two systems with the Tasks module's graphical user interface.

Task Triggers and Task Actions
------------------------------

The Tasks module allows implementers to define interactions between modules through a graphical user interface. A Task consists of a trigger and an action. A trigger is a standard Motech event that has been exposed to the Task module. An action is an event for which the implementer's module provides a listener. Between the trigger and action, an implementer can add filters to control whether a task should run based on conditional logic. Also, tasks may use data loaders to query Motech Data Services for additional information.

In the listings that follow, we will declare a Task channel for our module in the form of a JSON document. We will expose our :code:`SEND_HELLO` event as a task trigger, declare our :code:`CREATE_HELLO_RECORD` event as a Task action, and then link the two in Motech's GUI.

First, create a new file called *task-channel.json* in our module's *main/resources* directory. Each Task channel must define a display name, a module name, and a module version as JSON properties. A Task channel may provide a list of triggers, identified by the :code:`triggerTaskEvents` property. Each trigger must define a display name and event subject, and may provide a list of parameters passed with the event, in which case each event has an event key and a display name. Similarly, a Task channel may provide a list of Task actions, identified by the :code:`actionTaskEvents` property. Like triggers, each action must provide a display name and an event subject, and an optional list of parameters identified by key and display name:

.. code-block:: json

    {
        "displayName": "helloworld.task.channel.name",
        "moduleName": "${project.artifactId}",
        "moduleVersion": "${parsedVersion.osgiVersion}",
        "triggerTaskEvents" : [
            {
                "displayName" : "helloworld.task.trigger.send_hello.name",
                "subject" : "helloworld_hello_event",
                "eventParameters" : [
                    {
                        "eventKey" : "name",
                        "displayName" : "helloworld.task.trigger.send_hello.param.name"
                    },
                    {
                        "eventKey" : "message",
                        "displayName" : "helloworld.task.trigger.send_hello.param.message"
                    }
                ]
            }
        ],
        "actionTaskEvents" : [
            {
                "displayName" : "helloworld.task.action.create_hello_record.name",
                "subject" : "helloworld_create_hello_record",
                "actionParameters" : [
                    {
                        "key" : "name",
                        "displayName" : "helloworld.task.action.create_hello_record.param.name"
                    },
                    {
                        "key" : "message",
                        "displayName" : "helloworld.task.action.create_hello_record.param.message"
                    }
                ]
            }
        ]
    }

In the listing above, the subjects for the trigger and action are the hard-coded String constants we defined in our :code:`HelloWorldEventSubjects` class. Since our :code:`HelloWorldRecord` provides fields for name and message, we pass that data as parameters on the trigger, and accept that data as parameters of the action.

Throughout the JSON listing, the values we provide as display names are not String literals, but rather references to Strings in our message properties files. To provide the String literals for the references above, open our *main/resources/webapp/messages/messages.properties* file and append the following::

    #Tasks
    helloworld.task.channel.name=Hello World

    helloworld.task.trigger.send_hello.name=Hello World
    helloworld.task.trigger.send_hello.param.name=Name
    helloworld.task.trigger.send_hello.param.message=Message

    helloworld.task.action.create_hello_record.name=Create Hello World Record
    helloworld.task.action.create_hello_record.param.name=Name
    helloworld.task.action.create_hello_record.param.message=Message

Now, all of the components of our task channel will display correct English values in Motech's graphical user interface. Before we turn to the user interface, let's rebuild the module with mvn clean install and install our module in the *Admin -> Manage Modules* user interface, as we did when touring the minimal bundle.

Creating a Task in the User Interface
-------------------------------------

Once the module has been updated, we'll create the relationship between task trigger and action in the Motech user interface. Navigate to the Tasks interface by clicking *Modules -> Tasks*. Click the *New Task* button and enter "Create Hello Record" in the *Task Name* field. Our module appears in the Trigger dropdown with the default module icon, a clipboard. Click on the icon, and select Hello World from the available triggers. 

New buttons will appear allowing us to add filters, data sources, and actions. Click the *Add action* button, then, from the *Channel* dropdown menu, select our Hello World module. Click the *Action* dropdown menu, and select the *Create Hello World Record* action that we defined as part of our Task channel.

Finally, notice that the fields we declared as parameters of our Task trigger appear as draggable elements in the *Available Fields* list. Also notice that our Task action allows us to enter a name and message as text in two form input fields. To complete the association between the trigger and action parameters, drag the *Name* element to the *Name* input field, and the *Message* element to the *Message* input field. Once the form fields are populated, click the *Save & Enable* button to activate our new task.

Final Walkthrough
-----------------

To review, in the Event-driven Hello World section of the tutorial, we added the Motech event system to our module. We defined two event subjects: a :code:`SEND_HELLO` event that our module emits to other modules in the system, and a :code:`CREATE_HELLO_RECORD` event to which our module listens for and responds. Our module emits the :code:`SEND_HELLO` event whenever the HelloWorldService's :code:`sayHello` method is called (in this case, when a user loads the /sayHello route as defined by the :code:`HelloWorldController`). Using the Tasks module's user interface, we defined a task linking the :code:`SEND_HELLO` and :code:`CREATE_HELLO_RECORD` events as a task trigger and task action, respectively. Our :code:`HelloWorldEventHandler` listens for :code:`CREATE_HELLO_RECORD` and adds new :code:`HelloWorldRecord`instances to our Motech Data Services repository.

To verify that we implemented our feature correctly, load http://localhost:8080/motech-platform-server/helloworld/sayHello in your browser. The response sent to the browser remains the same. To check for the new record, click *Modules -> Data Services* in the Motech user interface. Select the *Data Browser* tab, then click on the :code:`HelloWorldRecord` under the heading for our *Hello World* module. In the list of record instances that appear, we should find a record with the name "HWEvent" and the message "Hello world!"
