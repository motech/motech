

# Overview #
“Hello World” aims to get you started with Motech development.  It gives an overview of installing the Motech server on your local development machine.  Then it describes a “Hello World” project that demonstrates a simple Motech module including: description of module behavior, Java coding and bundle configuration, building, testing, deployment, and running.

This tutorial assumes you are at least somewhat familiar with Java, [Maven](http://maven.apache.org/), and the [Spring Framework](http://www.springsource.org/spring-framework).

# Set Up #
First, you should [set up your development machine](DeveloperMachineSetup.md).

This tutorial was written with the [Spring Tool Suite](http://www.springsource.org/downloads/sts-ggts) (STS) development application in mind.  STS is somewhat different from the [IntelliJ IDEA](http://www.jetbrains.com/idea/download/) development application mentioned in the development machine setup instructions.  This tutorial should work either way.

This tutorial was written using Motech version 0.21.

# Hello World #
Now you can start a “Hello World” project.  Motech is modular and event-driven, so this tutorial helps you produce a Motech module that emits and consumes a “Hello World” event.

Once the module is deployed on your local Motech server, you will be able to use the module like this:
  * You visit a particular URL from your web browser.
  * A controller object in the module responds to the URL request by invoking the module’s “event emitter”.
  * The event emitter emits a “Hello World” Motech event.
  * The module’s “event listener” consumes the “Hello World” event.
  * The event emitter and event listener each add a message to the Motech server log, which you can read.

## Java Code ##
To begin coding, open STS and start a new project.  For this tutorial, all you need is a Maven “simple project”.
  * Click **File** -> **New** -> **Project...** -> **Maven** -> **Maven Project**
  * Check the box for “Create a simple project” and click **Next**.
  * Enter properties for the new project, as below.

|groupId|org.motechproject|
|:------|:----------------|
|artifactId|motech-hello-world-tutorial|
|packaging|jar              |
|name   |Hello World      |

Then click **Finish**.

Later you will change the project's packaging to `bundle`.  For now, choosing `jar` will make STS happy.

Now you can implement the module behavior with a few short Java classes.

First, declare the “Hello world” string that will be the subject of the “Hello World” event.

`EventKeys.java`
```
package org.motechproject.helloworld;

public final class EventKeys {

    public static final String HELLO_SUBJECT = "Hello, World!";

    private EventKeys() {
    }
}
```

You should create this class in your project.  In STS, go to the Package Explorer on the left and right-click on your project name.  Click **New** -> **Class**, and type in the package name and class name.  Then edit the new file to contain the code above.

You should keep adding class files and other files to your project, as they appear below.

Now you can trace out module’s behavior and implement classes along the way.  You will start by visiting a URL in your web browser, so define the controller class that will handle URL request.

`EventEmitterController.java`
```
package org.motechproject.helloworld.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.helloworld.event.EventEmitter;

@Controller
public class EventEmitterController {
    
    @Autowired
    private EventEmitter eventEmitter;

    @RequestMapping("/event-emitter")
    @ResponseBody
    public String emitEvent() {
        eventEmitter.emitEvent();
        String message = "Will emit an event with subject " + eventEmitter.getSubject();
        return message;
    }
}
```

You can let Spring do most of the work here, by decorating the class with annotations:
  * `@Controller` and `@Autowired` tell spring how to instantiate this class automatically.
  * `@RequestMapping` tells spring which URL the `emitEvent()` method should handle.
  * `@ResponseBody` tells spring that `emitEvent()` will return plain text to display in the browser (as opposed to the name of an MVC view).

The `emitEvent()` method does two things: it invokes the module’s event emitter, and it returns a string to be displayed in your browser.

The module’s event emitter will actually emit the event.

`EventEmitter.java`
```
package org.motechproject.helloworld.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

import org.motechproject.helloworld.EventKeys;

@Component
public class EventEmitter {

    private static final String SUBJECT = EventKeys.HELLO_SUBJECT;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventRelay eventRelay;

    public EventEmitter() {
    }

    public EventEmitter(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void emitEvent() {
        Map<String, Object> helloEventParams = new HashMap<>();
        helloEventParams.put("hello", "world");
        MotechEvent motechEvent = new MotechEvent(SUBJECT, helloEventParams);
        eventRelay.sendEventMessage(motechEvent);

        String message = "Emitted an event with subject " + SUBJECT;
        logger.info(message);
    }

    public String getSubject() {
        return SUBJECT;
    }
}
```

This event emitter will always emit “Hello World” events.  It exposes its `SUBJECT` field with `getSubject()`, which allows the controller to return informative text to the web browser.

The `emitEvent()` method does three things: it creates a new Motech event with the “Hello World” subject and a simple “hello" parameter.  It passes the new event to the Motech event relay.  Finally, it makes an “info” log entry, which will appear in the Motech server console output.

The event emitter defines two constructors, one of which accepts an event relay object.  Spring will use the default constructor.  Unit tests will use the event relay constructor (below).

After the “Hello World” event is emitted, the event listener will consume it.

`EventListener.java`
```
package org.motechproject.helloworld.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.helloworld.EventKeys;

@Component
public class EventListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @MotechListener(subjects = { EventKeys.HELLO_SUBJECT })
    public void handleEvent(MotechEvent event) {
        String message = "Handled an event with subject " + event.getSubject();
        logger.info(message);
    }
}
```

The event listener listens for “Hello World” events.  The `@MotechListener` annotation tells the Motech platform to invoke the `handleEvent()` method whenever any module emits a “Hello World” event.

The `handleEvent()` message makes an “info” log entry, which will appear in the Motech server console output.

## Config ##
Maven and Spring do most of the work for this tutorial.  But you must configure them so that they know what you need.

The Maven pom file is by far the longest config file.  It declares all of the module’s dependencies and build rules.  It has three sections, roughly.

`src/pom.xml` (top)
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>org.motechproject</groupId>
    <artifactId>motech-hello-world</artifactId>
    <version>0.1-SNAPSHOT</version>
    <packaging>bundle</packaging>
    <name>Hello World</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <motech.version>0.21</motech.version>
        <spring.version>3.1.0.RELEASE</spring.version>
    </properties>
```

The top of the pom file declares basics, like the which Maven schema we’re using, the name, Id, and version of our module, and some properties of the project, like which version of Spring to use.

`src/pom.xml` (dependencies and repositories)
```
    <dependencies>
        <!-- emit and consume motech events -->
        <dependency>
            <groupId>org.motechproject</groupId>
            <artifactId>motech-platform-event</artifactId>
            <version>${motech.version}</version>
        </dependency>

        <!-- respond when user hits a url -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>org.springframework.web.servlet</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!-- test -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.9</version>
        </dependency>

        <!-- use a mock event relay during testing -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>1.9.5</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <repositories>
        <!-- locate motech dependencies -->
        <repository>
            <id>motech-repo</id>
            <name>MOTECH Maven Repository</name>
            <url>http://nexus.motechproject.org/content/repositories/public</url>
        </repository>
    </repositories>
```

The `<dependencies>` element of the pom declares the module’s dependencies:
  * In order to emit and consume events, the module needs a `motech-platform-event` jar.
  * In order to use a web controller, the module needs an `org.springframework.web.servlet` jar.
  * For unit testing (below), the module needs `junit` and `mockito-core` jars.

The `<repositories>` element of the pom tells Maven where to find `org.motechproject` dependencies.

`src/pom.xml` (build)
```
    <build>
        <plugins>

            <!-- java 7 compiler -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>

            <!-- create manifest for osgi loading -->
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <version>2.3.5</version>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <Context-File>META-INF/spring/blueprint.xml</Context-File>
                        <Context-Path>helloworld</Context-Path>
                        <Resource-Path>helloworld/resources</Resource-Path>
                        <Blueprint-Enabled>true</Blueprint-Enabled>
                        <Export-Package>
                            org.motechproject.helloworld;version=${project.version},
                        </Export-Package>
                    </instructions>
                </configuration>
            </plugin>

            <!-- copy hello-world bundle along side other motech bundles -->
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <version>2.5</version>
                <executions>
                    <execution>
                        <id>copy-bundles</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${user.home}/.motech/bundles</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>target</directory>
                                    <includes>
                                        <include>*.jar</include>
                                    </includes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

The `<build>` element of the pom declares what Maven should do when it builds the Hello World module.  Maven should:
  * compile with the Java 7 compiler
  * create an OSGI bundle that you load into your Motech server
  * copy the OSGI bundle to a common “.motech” folder

Note the `<Context-Path>` element of the maven-bundle-plugin plugin.  This value, `helloworld`, is part of the URL that you will visit once the module is deployed.

The pom configures Maven.  You must also configure Spring.

First, you must tell Spring to scan the module for annotations, so that it can instantiate your objects, and wire up their fields.

`src/main/resources/META-INF/motech/helloWorldApplication.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd">

    <!-- load beans from annotations -->
    <context:annotation-config />
    <context:component-scan base-package="org.motechproject.helloworld" />

</beans>
```

You must also tell Spring about OSGI.  You want the module to be registered with the Motech server as an OSGI bundle.  You also want the module to have access to the event relay OSGI service that’s part of the Motech server.

`src/main/resources/META-INF/spring/blueprint.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:osgi="http://www.eclipse.org/gemini/blueprint/schema/blueprint"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
        http://www.eclipse.org/gemini/blueprint/schema/blueprint http://www.eclipse.org/gemini/blueprint/schema/blueprint/gemini-blueprint.xsd">

    <import resource="classpath*:META-INF/motech/helloWorldApplication.xml" />

    <!-- register hello-world as loadable osgi module -->
    <bean id="moduleRegistrationData" class="org.motechproject.osgi.web.ModuleRegistrationData">
        <constructor-arg name="moduleName" value="helloWorld" />
        <constructor-arg name="url"
            value="../helloworld/resources/index.html" />
    </bean>

    <!-- get motech event relay from another osgi module -->
    <osgi:reference id="eventRelayOsgi" cardinality="0..1"
        interface="org.motechproject.event.listener.EventRelay" />

</beans>
```

Note the `moduleRegistrationData` bean.  One of its constructor arguments is the path to an `index.html`.  The Motech server will use this page as the default user interface for configuring with the bundle.  For this module, you can supply a simple HTML document in the style of “Hello World”.

`src/main/resources/webapp/index.html`
```
<html>
<header>
    <title>Motech Hello World</title>
</header>
<body>Hello, Motech
</body>
</html>
```

## Building ##
With all these files in your project, you should now be able to build the module.  In the STS package explorer, right-click on your project name and choose **Run As** -> **Maven clean**.  Then right-click again and choose **Run As** -> **Maven install**.

## Test Code ##
This Hello World module is simple.  But it's worth adding some unit tests that demonstrate the Maven test runner and [mockito](https://code.google.com/p/mockito/) mock objects.

`src/test/java/EventEmitterTest.java`
```
package org.motechproject.helloworld;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.helloworld.event.EventEmitter;

public class EventEmitterTest {

	@Mock
	private EventRelay eventRelay;

	private EventEmitter eventEmitter;

	@Before
	public void setUp() {
		initMocks(this);
		this.eventEmitter = new EventEmitter(eventRelay);
	}

	@Test
	public void shouldNotBeNullEmitter() {
		assertNotNull(eventEmitter);
	}

	@Test
	public void shouldEmitEvent() {
		ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor
				.forClass(MotechEvent.class);
		eventEmitter.emitEvent();
		verify(eventRelay).sendEventMessage(motechEventCaptor.capture());
		MotechEvent motechEvent = motechEventCaptor.getValue();
		assertEquals(motechEvent.getSubject(), eventEmitter.getSubject());
	}
}
```

Note the name of the test class, `EventEmitterTest`.  The Maven test runner looks for test class names that match certain patterns, including `*Test.java`.

The `setUp()` method is called before each test method.  It uses mockito to instantiate a mock object that mimics the Motech event relay.  It also instantiates an event emitter that will work with the mock relay.

The `shouldNotBeNullEmitter()` test method verifies that `setUp()` is being called--otherwise the event emitter we want to test would be null.

The `shouldEmitEvent()` test method verifies that the event emitter is sending events to the Motech event relay.  In this case, the event relay is really a mock object.  It can capture the event object that the event emitter passed in, and check it for sanity.

Now if you run **Maven clean** and **Maven install** again, you should see results from running the test suite.

## Deploying ##
Now that you’ve built and tested the module, you can deploy it to your local Motech server.  If you used the installation instructions on the public Motech site, the Motech server will be running on your local machine already.  You should also have created a motech user account.

You can log in to your Motech server and access administrator controls from your browser by visiting http://localhost:8080/motech-platform-server.

Before deploying your bundle, you should modify the Motech server’s logging behavior so that it will write "info" messages related to the Hello World module:
  * Log in to your Motech server.
  * In the left column, click **Server Log**.
  * Near the top, click where it says "To change the log level click **here**."
  * In the blank text field, type "org.motechproject.helloworld".
  * In the nearby drop-down menu choose **INFO**.
  * Click the nearby **+** button.
  * Click **Save**.

Now you can deploy the Hello World module.
  * Log in to your [Motech server](http://localhost:8080/motech-platform-server/).
  * In the left column, click **Manage Modules**.
  * In the modules page, find “Install module from” and choose **File**.
  * Click on the **Select file** button and choose your bundle’s .jar file.  This should be located in your “.motech” folder, at a path like `~/.motech/bundles/motech-hello-world-0.1-SNAPSHOT.jar`
  * Click on the **Install or Update** button.

You should see a new record appear, for the Hello World module.  To start the module running, click on the triangular start button under the "Actions" heading on the right.  The module should now be deployed!

## Runing ##
Now that the module is deployed, you can try using it.
  * Log in to your [Motech server](http://localhost:8080/motech-platform-server/).
  * Visit the URL for the Hello World module’s controller, located at http://localhost:8080/motech-platform-server/module/helloworld/event-emitter

Note the anatomy of this URL:
  * `http://localhost:8080/motech-platform-server/module/` is the base URL for accessing modules on your local Motech server
  * `helloworld` is the module’s `<Context-Path>` from the project pom file.
  * `event-emitter` is the `@RequestMapping` value for the module’s controller

When you visit this URL, you should see a message in your browser, such as, `Will emit an event with subject Hello, World!`.  This indicates that the module’s controller was invoked, and it that was able to access the subject field of the event emitter.

The event emitter and event listener each should have made “info” entries in the Motech server log.  This log should be located at a path like `$CATALINA_HOME/logs/catalina.out`.  You should have set the value of `CATALINA_HOME` when [set up your development machine](DeveloperMachineSetup.md).  Open `catalina.out` and scroll to the bottom.  You should see log messages like
```
2013-08-21 17:27:13,690 INFO  [org.motechproject.helloworld.event.EventEmitter] Emitted an event with subject Hello, World!
2013-08-21 17:27:13,733 INFO  [org.motechproject.helloworld.event.listener.EventListener] Handled an event with subject Hello, World!
```

If you see these log entries, you win!  You have installed the Motech server and deployed your own module.

# Bonus: default module page #
In the Spring configuration `blueprint.xml`, you specified a placeholder `index.html` as part of the Motech OSGI module registration.  If you want to view this page in your browser:
  * Log in to your [Motech server](http://localhost:8080/motech-platform-server/).
  * Visit the URL for the Hello World module’s default page http://localhost:8080/module/server/home?moduleName=helloWorld

In this URL, the value of `moduleName` comes from the `moduleName` constructor argument of the `moduleRegistrationData` bean in your `blueprint.xml`.

# Bonus: Event Logging module and CouchDB #
Motech modules should work together.  Motech’s Event Logging module can work with your Hello World module, to make it easier to see your “Hello World” events.

When the Event Logging and Hello World modules are both deployed, you’ll be able to:
  * Visit the URL for your Hello World module’s controller, and emit a “Hello World” event.
  * Visit the “Futon” interface for your local CouchDB server.
  * View “Hello World” events that the Event Logging module stored with  CouchDB.

Using the Event Logging module and CouchDB to track events is much nicer than reading the Motech server’s log file.

## Deploying ##
The Event Logging module should already be deployed on your local Motech server.  If you don't see it listed on the Manage Modules page, you can try installing the module.  The easiest way is to use Motech's Manage Modules interface:
  * Log in to your Motech server.
  * In the left column, click **Manage Modules**.
  * In the modules page, find “Install module from” and choose **Repository**.
  * Click on the **Select a module** menu and choose **Event Logging**.
  * Click on the **Install or Update** button.

You should now see the "Event Logging" module listed. Activate the module by clicking on the triangle-shaped "start" button on the right hand side, under the "Actions" heading.

Now whenever your Hello World module emits an event, the Event Logging module will see it, and store a record of the event with CouchDB.

You can look at documents in CouchDB using CouchDB’s web-based admin console, called Futon.  Visit your local Futon with your browser: http://localhost:5984/_utils/.  You should see some Motech databases listed, including one called “motech-event-logging”.  This is where you will find records of your Hello World events.

## Running ##
To try it out, make sure you've emitted some “Hello World” events:
  * Log in to your [Motech server](http://localhost:8080/motech-platform-server/).
  * Visit the URL for the Hello World module’s controller, located at http://localhost:8080/module/helloworld/event-emitter

Then look for the events in CouchDB:
  * Visit your [local Futon](http://localhost:5984/_utils/).
  * Click on the **motech-event-logging** database.
  * Click on one of the database documents that are listed.

Note the `subject` field of the document.  Some documents will have the subject “Hello World!”

You can get a cleaner look at your events using a CouchDB temporary view.
  * Visit your [local Futon](http://localhost:5984/_utils/).
  * Click on the **motech-event-logging** database.
  * At the top right, find “View” and  choose **Temporary view...**.
  * For the map function on the left, write a simple map function, as below.

simple map function
```
function(doc) {
  emit(doc.timeStamp, doc.subject);
}
```

Then click **Run**.  You should see a list of event time stamps and subjects, sorted by time stamp.

Now each time you emit a Hello World event, you can return to this temporary view and click **Run**.  You should see your latest event listed.  You should also see that the number of results returned for your view has been incremented.

If you see Hello World events in your CouchDB view, you win!  You've deployed two Motech modules and let them work together.