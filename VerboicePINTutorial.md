

# Overview #
This Verboice PIN tutorial shows how to combine Motech with a service called Verboice, which allows Motech to interact with your own phone.  It gives an overview of setting up a Skype account that lets you place phone calls programmatically, and setting up a Verboice account that lets you define _call flows_ with audio messages and touch tone user interactions. Then it describes a Verboice PIN Tutorial Motech module that makes calls using Skype and Verboice and checks whether you enter the correct PIN into your phone.

This tutorial assumes you already have a development environment set up, and that you're somewhat familiar with developing Motech modules.  It refers to the [Spring Tool Suite](http://www.springsource.org/sts) but other development environments should work, too, including    [IntelliJ IDEA](http://www.jetbrains.com/idea/download/).

See DeveloperMachineSetup and MotechHelloWorld for more about getting started.

Unlike the MotechHelloWorld tutorial, this tutorial assumes that you have Motech deployed on a public-facing web server.  This will allow Verboice to interact with your module.  You also need access to  your server's file system via ssh, remote desktop, or similar, so that you can edit a module configuration file.

Finally, this tutorial was written using Motech version 0.21.

# Skype Config #
This tutorial combines Motech with a [SIP](http://en.wikipedia.org/wiki/Session_Initiation_Protocol) provider, which is able to make calls to your phone.  This tutorial assumes you are using Skype, which is one of several web-based SIP provides.

Skypw provides instructions for setting up a [Skype SIP Profile](https://support.skype.com/en/faq/FA10572/how-do-i-manage-sip-profiles#profile1).  This service may cost a few dollars, but not much.  For this tutorial, you only need one channel (one call at a time).

When your SIP Profile is created, make note of your SIP Profile credentials, including the `SIP User` and `password`.  Note that these are different from your regular Skype user credentials.

# Verboice Config #
This module also uses Verboice, which will interact with you during phone calls, playing audio messages and reacting to your touch tone inputs.  You will need to create a [Verboice account](http://verboice.instedd.org/) and confirm it from your email.

Once you're logged in at Verboice, you can connect Verboice to your Skype SIP Profile.  At the top of your Verboice page, click on **Channels**, then click **Create new...** -> **Skype channel**.  Fill in the Skype channel fields:
|Name|any name, like `myChannelName`|
|:---|:-----------------------------|
|Call flow|leave blank                   |
|Username|your Skype SIP Profile `SIP User`|
|Password|your Skype SIP Profile `Password`|
|Number|leave blank                   |

Then click **Save**.  You should now see your channel listed under "My Channels".  You might see an orange square and the word "connecting...".  Eventually the square should turn green to indicate that Verboice is connected to your Skype SIP Profile.  You might need to refresh the page.

# Verboice PIN Tutorial Module #
Now you can create the Verboice PIN Tutorial Motech module.  This will involve Java code and configuration similar to the MotechHelloWorld module.  It will incorporate a Verboice call flow which specifies audio messages, responses to user touch tone inputs, and interactions with your Motech server.

Once the module is deployed on your Motech server, you will be able to use the module like this:
  * You visit a particular URL from your web browser.
  * An `InitiateCallController` object in your module responds to the URL request by invoking the module’s `CallMaker`.
  * The call maker initiates a Verboice call using the Motech IVR-Verboice module.
  * You get a call on your phone.
  * When prompted, you enter a PIN using your phone's number pad.
  * Verboice sends an HTTP request to your Motech server, asking if the PIN was correct.
  * A `VerboiceWebApiController` object in your module handles the HTTP request, compares your PIN entry to an expected PIN, and responds to Verboice.
  * You hear a message about whether your PIN was correct.
  * Verboice sends another HTTP request to your Motech server, with additional data about the call.
  * Your `VerboiceWebApiController` handles the request and logs a summary of the call.
  * Verboice hangs up.

If you start a new Maven project, then fill in files as they appear below, you should be able to build this module from scratch.  In STS, you can start with the Maven "simple project" template.

## Verboice Manifest ##
The Verboice manifest is an XML document that defines the interactions between Verboice and your Motech module.  It declares _steps_ that occur during a call, and variables to pass between Verboice and Motech during each step.

Once deployed, your module will serve the manifest document to Verboice.  Then you will be able to use the steps declared in the manifest as you define a Verboice call flow, which will also include audio messages,  and touch tone user input.

First, you should define the manifest.  This will make it easier to implement the [Motech side](VerboicePINTutorial#The_Motech_Side.md) and the [Verboice side](VerboicePINTutorial#The_Verboice_Side.md) of the tutorial, below.

`src/main/resources/verboicePinTutorialManifest.xml`
```
<?xml version="1.0" encoding="UTF-8" ?>
<verboice-service>
    <name>Verboice PIN Tutorial</name>
    <steps>
        <step name="checkPin" display-name="Check the User's Pin" icon="medicalkit" type="callback" callback-url="URL_BASE/checkPin">
            <settings>
                <variable name="pin" display-name="Attempted Pin" type="string" />
            </settings>
            <response type="variables">
                <variable name="checkPinResult" display-name="Pin Check Result" type="string" />
            </response>
        </step>
    </steps>
    <steps>
        <step name="reportData" display-name="Report Call Data" icon="medicalkit" type="callback" callback-url="URL_BASE/reportData">
            <settings>
                <variable name="pin" display-name="Attempted Pin" type="string" />
                <variable name="isCorrectPin" display-name="Pin Was Correct" type="string" />
            </settings>
            <response type="variables">
                <variable name="reportDataResult" display-name="Data Report Result" type="string" />
            </response>
        </step>
    </steps>
</verboice-service>
```

The manifest declares a `<name>`, which can be anything your want.  It also declares two `<steps>` that describe interactions between Verboice and Motech.  The steps have similar attributes and elements:
  * `name` a unique name for each step: `checkPin` and `reportData`
  * `display-name` the name that Verboice will display in its web interface
  * `icon` the icon that Verboice will display in its web interface (`medicalkit` might be the only option.)
  * `type` steps of type `callback` will make HTTP requests at the specified `callback-url`
  * `callback-url` where to make an HTTP request for each step.  These URLs will contain the base URL of your Motech module, followed by an MVC controller path that we will define below.  At run time, your module will replace the `URL_BASE` with the correct address of your Motech server.
  * `<settings>` these elements declare variables that we want Verboice to send to Motech, as HTTP request parameters.  The `pin` variable will contain the PIN that the user enters into their phone.
  * `<response>` these elements declare variables that Motech will send to Verboice in the HTTP response body (as JSON strings).  The `checkPinResult` response variable will contain `true` or `false`, depending on whether the user entered the correct pin.

Now we have declared two interactions between Verboice and Motech, called `checkPin` and `reportData`.  We have also declared variables to exchange during the interactions.  Below, we will implement the interactions on the [Motech side](VerboicePINTutorial#The_Motech_Side.md), then on the [Verboice side](VerboicePINTutorial#The_Verboice_Side.md).

You can compare this manifest to another Verboice [sample manifest](http://verboice.instedd.org/docs/sample_manifest.xml).

## Module Properties ##
Your module will need configuring in order to work with your Verboice account and call your own phone.  We can use a properties file to store values like these.  Motech provides a settings mechanism to make these values available to the module at run time.

`src/main/resources/verboicePinTutorial.properties`
```
# use the base URL for your server
motech.base.url=http://user:password@my.domain.name:8080/motech-server-path
motech.ivr.status.path=/module/verboice/web-api/ivr/callstatus

# choose any Id and pin
user.Id=myUserId
user.pin=12345

# use your own phone number and Verboice channel name
ivr.channel.name=myChannelName
ivr.phone.number=yourPhoneNumber
ivr.timeout=120
ivr.extra.payload=myExtraData

# use your own Verboice call flow Id
verboice.callflow.Id=yourCallFlowId
verboice.manifest.file=verboicePinTutorialManifest.xml
verboice.callback.path=/module/verboicePINTutorial/web-api
```

These are default property values.  After you deploy your module, you will change some of these values by editing a configuration file on your Motech server.  For example, you will edit the `motech.base.url`, with the address of your Motech server.  This value will replace the `URL_BASE` that appears in the Verboice manifest file above.

## The Motech Side ##
The Motech side of the Verboice PIN tutorial includes Java code and configuration for Spring, Maven, and the module itself, as well as building, and deployment.

### Java Code ###
You can implement the Verboice PIN module behavior using 4 short Java classes.

The first class defines a few constants for use by other classes.

`Constants.java`
```
package org.motechproject.verboicepin;

public final class Constants {

    // these agree with constants used by the ivr-verboice module
    public static final String STATUS_CALLBACK_URL = "status_callback_url";
    public static final String FLOW_ID = "call_flow_id";

    // these are custom for this pin tutorial module
    public static final String USER_ID = "userId";
    public static final String CUSTOM_PAYLOAD = "custom_payload";

    private Constants() {
    }
}
```

To create this first class in STS, you might get an error like “Source folder is not a Java project”.  If so, go to the command line, and type `mvn eclipse:eclipse` from the project folder.  Then delete the project from the STS package explorer and re-import it.  This seems to be a bug with STS.

Two constants will be used for configuring a Verboice phone call: `STATUS_CALLBACK_URL`, and `FLOW_ID`.  These constants are duplicates of constants in Motech's IVR-Verboice module.  In a future Motech version, these constants might be exposed explicitly.

Two additional constants are custom for this module: `USER_ID` and `CUSTOM_PAYLOAD`.  These are examples of user data that you might want to associate with each phone call.

The next class is the entry point for using your module.  It's an MVC controller that will respond when you visit a URL from your browser.

`InitiateCallController.java`
```
package org.motechproject.verboicepin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.verboicepin.ivr.CallMaker;
    
@Controller
public class InitiateCallController {
    
    @Autowired
    private CallMaker callMaker;

    @RequestMapping("/makeCall")
    @ResponseBody
    public String makeCall() {
        // display summary of initiated call in browser
        String summary = callMaker.makeCall();
        return summary;
    }
}
```
This controller really just delegates to the module's `CallMaker`, and returns a message for you to read in your browser.

The `CallMaker` actually initiates the call.

`CallMaker.java`
```
package org.motechproject.verboicepin.ivr;

import java.util.Map;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.verboicepin.Constants;
import org.motechproject.ivr.service.contract.CallRequest;
import org.motechproject.ivr.service.contract.IVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallMaker {

    private final IVRService ivrService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    public CallMaker(IVRService ivrService) {
        this.ivrService = ivrService;
    }

    public String makeCall() {

        // build an ivr call request
        int callTimeoutSecs;
        try {
            String callTimeoutStr = settingsFacade.getProperty("ivr.timeout");
            callTimeoutSecs = Integer.parseInt(callTimeoutStr);
        } catch (NumberFormatException e) {
            callTimeoutSecs = 120;
        }
        String phoneNum = settingsFacade.getProperty("ivr.phone.number");
        String channelName = settingsFacade.getProperty("ivr.channel.name");
        CallRequest callRequest = new CallRequest(phoneNum, callTimeoutSecs,
                channelName);

        // configure the call request payload for Verboice
        Map<String, String> payload = callRequest.getPayload();
        String callFlowId = settingsFacade.getProperty("verboice.callflow.Id");
        payload.put(Constants.FLOW_ID, callFlowId);
        String statusCallbackUrl = settingsFacade
                .getProperty("motech.base.url")
                + settingsFacade.getProperty("motech.ivr.status.path");
        payload.put(Constants.STATUS_CALLBACK_URL, statusCallbackUrl);

        // configure the call request payload with custom data
        payload.put(Constants.USER_ID, settingsFacade.getProperty("user.Id"));
        payload.put(Constants.CUSTOM_PAYLOAD,
                settingsFacade.getProperty("ivr.extra.payload"));

        // place the call
        String summary = "Initiating call to: " + phoneNum + " on channel: "
                + channelName + " for call flow " + callFlowId;
        logger.info(summary);
        ivrService.initiateCall(callRequest);

        return (summary);
    }
}
```

The `makeCall()` method creates a call request for use with Motech's Interactive Voice Response (IVR) module.  You will load this module on your Motech server when you deploy your module.  The method fills in basic call parameters using values from your properties file: a timeout value, the name of your Verboice Skype channel, and your own phone number.

`makeCall()` also configures the call "payload" with values from your properties file: `FLOW_ID` and `STATUS_CALLBACK_URL` tell Verboice what to do when it calls your phone; `USER_ID` and `CUSTOM_PAYLOAD` are custom values that your module associates with the call.

Finally, `makeCall()` logs a summary of the call configuration, passes the call request to the `initiateCall()` method of Motech's IVR module, and returns a summary that the call maker will display in your browser.

After `makeCall()` is invoked, your call will be in the hands of Motech's IVR module and Verboice.  This is when your phone will ring.  During the course of the phone call, Verboice will interact with your module via HTTP requests.  Your module's `VerboiceWebAPIController` will handle these requests.

`VerboiceWebApiController.java`
```
package org.motechproject.verboicepin.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.verboicepin.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/web-api")
public class VerboiceWebApiController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SettingsFacade settingsFacade;

    @RequestMapping("/manifest")
    @ResponseBody
    public String manifest(HttpServletRequest request) throws IOException {
        logger.info("Serving Manifest.");

        String manifestFileName = settingsFacade
                .getProperty("verboice.manifest.file");
        InputStream manifest = settingsFacade.getRawConfig(manifestFileName);
        String manifestBody = IOUtils.toString(manifest);

        String callbackBaseUrl = settingsFacade.getProperty("motech.base.url")
                + settingsFacade.getProperty("verboice.callback.path");

        return manifestBody.replace("URL_BASE", callbackBaseUrl).replace(
                "EXTRA_PAYLOAD", Constants.CUSTOM_PAYLOAD);
    }

    @ResponseBody
    @RequestMapping("/checkPin")
    public String checkPin(HttpServletRequest request) {
        logger.info("Checking Pin");

        String attemptedPin = request.getParameter("pin");
        String userPin = settingsFacade.getProperty("user.pin");
        logger.info("attemptedPin: " + attemptedPin + " userPin: " + userPin);

        if (StringUtils.isNotBlank(attemptedPin)
                && attemptedPin.equals(userPin)) {
            logger.info("Correct pin.");
            return "{\"checkPinResult\": \"true\"}";
        } else {
            logger.info("Wrong pin.");
            return "{\"checkPinResult\": \"false\"}";
        }
    }

    @ResponseBody
    @RequestMapping("/reportData")
    public String reportData(HttpServletRequest request) {
        String callData = "Call Flow Data: ";
        Map<String, String[]> results = request.getParameterMap();
        for (String name : results.keySet()) {
            callData += name + "={";
            for (String value : results.get(name)) {
                callData += value + " ";
            }
            callData += "}";
        }
        logger.info(callData);

        return "{\"reportDataResult\": \"true\"}";
    }
}
```

The `VerboiceWebAPIController` defines three request handlers: one to serve the manifest XML document to Verboice, one to validate the PIN that the user enters into their phone during a call, and one to receive data summarizing the call.

All three handlers include `web-api` as part of their request mapping.  This is important because it will allow Verboice to make requests without having to log in to your Motech server.  Instead, Motech permits `web-api` requests using [Basic Authentication](http://en.wikipedia.org/wiki/Basic_access_authentication).  When you configure your Motech module below, you will need to include your Motech user name and password as part of your Motech server's address.  You might want to create a special Motech user for this purpose.

The `manifest()` method handles requests at `web-api/manifest`.  It reads the XML document defined above, and replaces the `URL_BASE` strings with your Motech server address, which you will specify when you [configure your module](VerboicePINTutorial#Configure_the_Module.md), below.  Then Verboice will be able to access the `manifest` URL when you configure your [call flow](VerboicePINTutorial#Verboice_Call_Flow.md).

The `checkPin()` method handles requests at `web-api/checkPin`.  It reads a `pin` variable that Verboice sends as a request parameter, and compares it to the `user.pin` stored in your module properties file.  You will be able to specify any PIN you want when you configure your module, below.  `checkPin()` returns a [JSON](http://en.wikipedia.org/wiki/JSON) string to Verboice, with a `checkPinResult` field that will have the value `true`, if the `pin` from Verboice matches the `user.pin` stored on the server.

The `reportData()` method handles requests at `web-api/reportData`.  It reads all of the request parameters sent by Verboice and prints them in a log message.  Some of these parameters are variables specifically named in the manifest XML document, like `pin` and `isCorrectPin`.  Others are included automatically by Verboice, like `CallSid`.

That is all of the Java code you need to write for this module.  The rest is handled by Spring, other Motech modules, and other web services.

### Spring Configuration ###
The Module uses Spring to instantiate its beans automatically based on annotations.  It also needs Spring to supply a Motech `SettingsFacade` for working with configuration files, including the Verboice manifest XML document and module properties file defined above.

`src/main/resources/META-INF/motech/applicationVerboicePINTutorial.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd">

    <context:annotation-config />
    <context:component-scan base-package="org.motechproject.verboicepin" />

    <!-- copy config files from bundle to Motech server config folder (if 
        missing) -->
    <bean id="settings" class="org.motechproject.server.config.SettingsFacade"
        autowire="byType">
        <property name="moduleName" value="verboicePinTutorial" />
        <property name="configFiles">
            <list>
                <value>classpath:verboicePinTutorial.properties</value>
            </list>
        </property>
        <property name="rawConfigFiles">
            <list>
                <value>classpath:verboicePinTutorialManifest.xml</value>
            </list>
        </property>
    </bean>
</beans>
```

Setting the `configFiles` and `rawConfigFiles` properties of the `SettingsFacade` bean will cause Motech to copy the manifest XML document and read the module properties file and from module bundle, at deployment time.  When you configure your module after deployment, you will be able to find and edit these settings on your server, at a path like `~/.motech/config/verboicePinTutorial`.

The _blueprint_ configuration file configures the Verboice PIN Tutorial module as an OSGI bundle.  It registers the module itself, and declares the module's dependencies on Motech's IVR service (which is uses to initiate phone calls) and Platform Settings service (which supports the `SettingsFacade` declared above).

`src/main/resources/META-INF/spring/blueprint.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:osgi="http://www.eclipse.org/gemini/blueprint/schema/blueprint"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
        http://www.eclipse.org/gemini/blueprint/schema/blueprint http://www.eclipse.org/gemini/blueprint/schema/blueprint/gemini-blueprint.xsd">

    <import
        resource="classpath*:META-INF/motech/applicationVerboicePINTutorial.xml" />

    <bean id="moduleRegistrationData" class="org.motechproject.osgi.web.ModuleRegistrationData">
        <constructor-arg name="moduleName" value="verboicePINTutorial" />
        <constructor-arg name="url"
            value="../verboicePINTutorial/resources/index.html" />
    </bean>

    <!-- find the ivr service loaded on the server -->
    <osgi:reference id="ivrServiceOsgi" cardinality="0..1"
        interface="org.motechproject.ivr.service.contract.IVRService" />

    <!-- find the settings service loaded on the server (for SettingsFacade 
        bean) -->
    <osgi:reference id="platformSettingsServiceOsgi"
        cardinality="0..1"
        interface="org.motechproject.server.config.service.PlatformSettingsService" />
</beans>
```

As part of registering this module as an OSGI bundle, we specified an `index.html`.  We need to include this file, even if it's just a placeholder.

`src/main/resources/webapp/index.html`
```
<html>
<header>
    <title>Verboice PIN Tutorial</title>
</header>
<body>Verboice PIN Tutorial
</body>
</html>
```

### Maven Configuration ###
Finally, we need to configure Maven to build the module.  The POM for this module can be simple because, as it turns out, it can receive all of its dependencies transitively via the Motech IVR-Verboice module.
`pom.xml`
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>org.motechproject</groupId>
    <artifactId>verboice-pin-tutorial</artifactId>
    <version>0.1-SNAPSHOT</version>
    <packaging>bundle</packaging>
    <name>Verboice PIN Tutorial</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <motech.version>0.21</motech.version>
        <spring.version>3.1.0.RELEASE</spring.version>
    </properties>

    <dependencies>
        <!-- interact with Verboice -->
        <dependency>
            <groupId>org.motechproject</groupId>
            <artifactId>motech-ivr-verboice</artifactId>
            <version>${motech.version}</version>
        </dependency>
    </dependencies>

    <!-- locate Motech dependencies -->
    <repositories>
        <repository>
            <id>motech-repo</id>
            <name>MOTECH Maven Repository</name>
            <url>http://nexus.motechproject.org/content/repositories/public</url>
        </repository>
    </repositories>

    <build>
        <plugins>
            <!-- use Java 7 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>
            <!-- produce an OSGI bundle -->
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <version>2.3.5</version>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <Context-File>META-INF/spring/blueprint.xml</Context-File>
                        <Context-Path>verboicePINTutorial</Context-Path>
                        <Resource-Path>verboicePINTutorial/resources</Resource-Path>
                        <Blueprint-Enabled>true</Blueprint-Enabled>
                        <Export-Package>
                            org.motechproject.verboicepin;version=${project.version},
                        </Export-Package>
                        <Import-Package>
                            org.motechproject.server.config.service;version=${motech.version},
                            org.motechproject.server.config;version=${motech.version},
                            *
                        </Import-Package>
                    </instructions>
                </configuration>
            </plugin>
            <!-- copy bundle to common location with Motech bundles -->
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
The crucial `<dependency>` is the `motech-ivr-verboice` module, which enables Verboice phone calls and in turn contains every other dependency that this module uses!  This module is found in the `<repository>` called `motech-repo`.

The `<build>` rules specify the Java 7 compiler.

The `<build>` rules also describe the module's `bundle` packaging with the `maven-bundle-plugin`.  The bundle `<Context-Path>` is `verboicePINTutorial`, which will be part of every HTTP request to this module.  The bundle `<Import-Package>` element list some Motech packages that Spring must resolve at deploy time.

Finally, the `<build>` rules specify that the bundle should be copied to a common Motech bundle folder at `~/.motech/bundles/`.

### Building the Module ###
With all of the above in place, project should now build.  From the command line you could do `mvn clean install`.  In STS you could choose **Run As** -> **Maven clean**, then **Run As** -> **Maven install**.

### Deploying the Module ###
Deploying the Verboice PIN Tutorial module requires deploying some other Motech modules, configuring the Motech IVR-Verboice module, and choosing the level of logging detail for the Verboice PIN Tutorial module itself.  Once deployed, you'll be able to test the module's behavior on the Motech side.

#### Module Dependencies ####
Before you can deploy your module to your Motech server, you must deploy
the Motech IVR-Verboice module and its dependencies.  The easiest way is to use Motech's Manage Modules interface:
  * Log in to your Motech server.
  * In the left column, click **Manage Modules**.
  * In the modules page, find “Install module from” and choose **Repository**.
  * Click on the **Select a module** menu and choose **IVR Verboice**.
  * Click on the **Install or Update** button.

The interface may turn gray for a while, then you should see several new modules listed: IVR Verboice, IVR, Decision Tree Core, and Call Flow.  For each of these, click on the triangle-shaped "start" button on the right hand side, under the "Actions" heading.

If you can't install the modules from repository, an alternative is to build the modules yourself.  You can check out the code from the Motech [platform-communications](https://github.com/motech/platform-communications) repository.  If you build the top-level project with 'mvn clean install', then restart your Motech server, you should see the IVR-Verboice and other modules loaded.

#### Deploy Module from File ####
Now you can deploy your Verboice PIN Tutorial module.
  * Log in to your Motech server.
  * In the left column, click **Manage Modules**.
  * In the modules page, find “Install module from” and choose **File**.
  * Click on the **Select file** button and choose your bundle’s .jar file.  This should be located in your “.motech” folder, at a path like `~/.motech/bundles/verboice-pin-tutorial-0.1-SNAPSHOT.jar`
  * Click on the **Install or Update** button.

You should now see the "Verboice Pin Tutorial" module listed.  Activate the module by clicking on the triangle-shaped "start" button on the right hand side, under the "Actions" heading.

#### Configure the IVR-Verboice Module ####
Now you need to configure the IVR-Verboice module to use your Verboice account.
  * Log in to your Motech server.
  * Click the **Settings** button for the IVR-Verboice module, on the right side, under the "Actions" header.  This button has an icon with a hammer and a wrench.
  * Fill in your Verboice credentials, as blow.

|channel|leave blank|
|:------|:----------|
|host   |verboice.instedd.org|
|password|your Verboice account password|
|port   |80         |
|username|your Verboice account username/email|

Then click **Submit and restart**.

#### Set Info Logging Level ####
You should also change the logging level for your module so that you will see module "Info" messages:
  * Log in to your Motech server.
  * In the left column, click **Server Log**.
  * Near the top, click where it says "To change the log level click **here**."
  * In the blank text field, type "org.motechproject.verboicepin".
  * In the nearby drop-down menu choose **INFO**.
  * Click the nearby **+** button.
  * Click **Save**.

#### Test the Module ####
Now you should be able to test your module with an HTTP request.  Try getting your module to serve its Verboice manifest XML document to your browser.  Visit the URL of your module's `VerboiceWebAPIController`, similar to `http://user:password@my.domain.name:8080/motech-server-path/module/verboicePINTutorial/web-api/manifest`.

You should replace `user:password` with your Motech username and password.  You should replace `my.domain.name:8080` with the actual domain name and port of your Motech server, and you should replace `motech-server-path` with the actual server path to your Motech server (if any).  The rest of the URL, starting from `module/`, should be the same.

A concrete URL might look like this: `http://motech:motech@111.222.333.444:8080/motech-platform-server/module/verboicePINTutorial/web-api/manifest`

When you access this URL, you should receive your manifest document.  If you choose **show source** with your browser, you should seen a document similar to the manifest XML document discussed above.  You should also see an "INFO" message in your server's console that says "Serving Manifest."

You can test the module's other controllers too: `makeCall`, `web-api/checkPin`, and `web-api/reportData`.  These won't function properly yet, but they should return something that you can view in the browser.  They should also make "INFO" log entries.

If you were able access your module web controller from your browser and see results such as your manifest XML document, you win!  You implemented the Motech side of the Verboice PIN Tutorial tutorial.

### Configuring the Module ###
To make your module functional, you need to set some configuration values such as the web address of your Motech server and your own phone number.  You can do this by editing a configuration file located on your Motech server.  Find the properties file located at `~/.motech/config/org.motechproject.motech-verboicePinTutorial-bundle/verboicePINTutorial.properties`.  This file contains all the keys and values from the properties file discussed above.

You should edit the following values:
|motech.base.url|your Motech server base URL, plus Basic authentication `user:password`|
|:--------------|:---------------------------------------------------------------------|
|user.Id        |any userID, like `myUserId`|
|user.pin       |any pin with 1-10 digits, like `12345`                                |
|ivr.channel.name|your Verboice Skype channel name, like `myChannelName`                |
|ivr.phone.number|your own phone number, like `18881234567                              |
|ivr.extra.payload|any data you want associate with each call                            |

Now, try to access your manifest document again.  You should see your Motech server's address on the left side of each `callback-url`.

You will need to edit one more value later, after you've created a Verboice call flow.

## The Verboice Side ##
The Verboice side of the Verboice PIN Tutorial module includes a Verboice call flow, and a little more module configuration.

### Verboice Call Flow ###
Above, you implemented the Motech side of the Verboice PIN Tutorial tutorial, now you must implement the Verboice side.  This means defining a Verboice _call flow_.

#### Read the Manifest ####
The first step is to serve the manifest document to Verboice:
  * Log in to your Verboice account.
  * Click **View your projects**.
  * Click **Create Project**
  * Give your project a name like `Verboice PIN Tutorial` and click **Save**.  Leave the other fields as they are.
  * Near the top, click on the **External Services** tab.
  * Click **Add External service**.
  * Paste in URL for your manifest request handler, similar to `http://user:password@my.domain.name:8080/motech-server-path/module/verboicePINTutorial/web-api/manifest`.
  * Click the large **Update** button
  * Click the small **Update Manifest** button

You should now see the `<name>` of your manifest XML document appear above the manifest URL.  If you click on **Edit**, you should also see two steps with "medkit" icons, named "Check the User's Pin" and "Report Call Data".

#### Build the Call Flow ####
Now that Verboice has read your manifest XML document, you can integrate the `checkPin` and `reportData` external services steps into a Verboice call flow.  This process takes a lot of clicking in order to complete, via the Verboice web interface:
  * Near the top, click on the **Call Flows** tab.
  * Click **Add Call flow**.
  * Type in a name like `Pin Checker` and click **Save**.
  * Click **Go to Designer**.

Now you will see a large blank area on the left.  On the right you will see several icons under the header "Add step".  The goal is to make a call flow that will greet the caller and prompt them to enter a pin, then check the pin with your Motech module, then tell the user the result of the pin check, and finally report additional call data back to your module.

When you're done, your call flow designer should look like this:
![http://wiki.motech.googlecode.com/git/Verboice-PIN-Tutorial-Call-Flow.png](http://wiki.motech.googlecode.com/git/Verboice-PIN-Tutorial-Call-Flow.png)

Some of the steps in the call flow are self-explanatory, like "Hang up".  Some of them require a bit of configuration.  These are listed below, with a description of how to configure each one.  The descriptions are based on the input fields in the Verboice call flow designer.
  * **Welcome** should play a text-to-speech message such as "Welcome to the Motech Verboice PIN Tutorial."
  * **Pin** should prompt the user to enter a PIN.  It has many fields to configure:
    * It should play text-to-speect instructions such as "Please enter your pin, then press pound."
    * It should play a text-to-speech message for an invalid entry such as "Please try your pin again."
    * It should allow about 10 seconds before repeating the instructions.
    * It should allow about 2 pin attempts.
    * After the final pin attempt fails, it should branch to a "Mark as failed" step.
    * It should allow inputs ranging from 1-10 digits, finishing on key `#`.
    * It should store its result as a variable called "pin".
  * **Report No Pin** should use the "Report Call Data" external service step defined in your manifest XML document:
    * It should fill in the "Attempted Pin" parameter using the call flow's "pin" variable.
    * It should fill in the "Pin Was Correct" parameter with the value "false".
  * **No Pin** should play a text-to-speech message such as "You did not enter a pin.  Try another call."
  * **Check the User's Pin** should use the "Check the User's Pin" external service step defined in your manifest XML document:
    * It should fill in the "Attempted Pin" parameter using the call flow's "pin" variable.
  * **Report Pin** should use the "Report Call Data" external service step defined in your manifest XML document:
    * It should fill in the "Attempted Pin" parameter using the call flow's "pin" variable.
    * It should fill in the "Pin Was Correct" parameter using the callback response called "Check the User's Pin - Pin Check Result".
  * **Branches** should branch to one of two messages, depending on whether the user entered the correct pin:
    * It should branch to the "Correct Pin" message, if the callback response called "Check the User's Pin - Pin Check Result" is equal to true.
    * By default, it should branch to the "Wrong Pin" message.
  * **Correct Pin** should play a text-to-speech message such as "Congratulations, you entered the correct pin!"
  * **Wrong Pin** should play a text-to-speech message such as "Unfortunately, you entered the wrong pin."

#### Call Flow Id ####
When you're done editing your call flow, note the call flow Id, which you will need when you configure your module below.  The call flow Id appears in the address bar of your browser: if your call flow Id is `123` then the address bar should look like `http://verboice.instedd.org/call_flows/123/designer`.

#### Test the Call Flow ####
Now that your call flow is defined in Verboice, you can try initiating a test call from the Verboice web interface.
  * Near the top, click on the **Overview** tab.
  * On the right, where it says "Enqueue new calls"
  * Choose your call flow from the first drop-down menu.
  * Choose your Skype channel from the next drop-down menu.
  * Enter your own phone number near the bottom, starting with your country code.
  * Click **Enqueue calls**.

If your phone rings and you hear a text-to-speech voice reading your call flow messages, you win!  You implemented the Verboice side of the Verboice PIN Tutorial module.  Don't worry if Verboice hangs up on you.  Below you will finish connecting Verboice with your module in order to complete the call flow.

### Configure the Module Call Flow Id ###
Now that you know your Verboice call flow Id, you must configure your module to use this Id.  As above, edit your module properties file located on your Motech server at `~/.motech/config/verboicePINTutorial.properties`.

Enter your call flow Id number:
|verboice.callflow.Id|your Verboice call flow Id, like `123`|
|:-------------------|:-------------------------------------|

Now your module will be able to initiate calls using your new call flow.

## Using the Module ##
Now that you have implemented the Verboice PIN Tutorial from the Motech side and the Verboice side, you should be able to put everything together.

Try making a successful pin check:
  * Log in to your Motech server.
  * Visit the URL for the Verboice PIN Tutorial module’s `makeCall` controller, similar to `http://user:password@my.domain.name:8080/motech-server-path/module/verboicePINTutorial/makeCall'.
  * You should see a summary message in your browser, and your phone should ring.
  * You should hear a text-to-speech "welcome" message.
  * When prompted, enter your pin.  This should be the same value that you entered for your module's `user.pin` property.
  * You should hear a text-to-speech "congratulations" message.
  * Verboice should hang up.
  * You should see log messages in your Motech server's console with a summary of call data, including the PIN you entered and the Verboice `CallSid`.  The `isCorrectPin` variable should have the value `true` or `1`.

You can also try entering the wrong pin.
  * Log in to your Motech server.
  * Visit the URL for the Verboice PIN Tutorial module’s makeCall controller, similar to `http://user:password@my.domain.name:8080/motech-server-path/module/verboicePINTutorial/makeCall'.
  * You should see a summary message in your browser, and your phone should ring.
  * You should hear a text-to-speech "welcome" message.
  * When prompted, enter an incorrect pin.
  * You should hear a text-to-speech "unfortunately" message.
  * Verboice should hang up.
  * You should see log messages in your Motech server's console with a summary of call data, including the PIN you entered and the Verboice `CallSid`.  The `isCorrectPin` variable should have the value `false` or `0`.

Finally, you can fail to enter any pin.
  * Log in to your Motech server.
  * Visit the URL for the Verboice PIN Tutorial module’s makeCall controller, similar to `http://user:password@my.domain.name:8080/motech-server-path/module/verboicePINTutorial/makeCall'.
  * You should see a summary message in your browser, and your phone should ring.
  * You should hear a text-to-speech "welcome" message.
  * When prompted, press the "#" key, or wait for about 30 seconds without entering anything.
  * You should hear a text-to-speech "you did not enter a pin" message.
  * Verboice should hang up.
  * You should see log messages in your Motech server's console with a summary of call data, including the PIN you entered and the Verboice `CallSid`.  The `pin` variable should be empty.

If you're receiving calls from Verboice, hearing audio messages, and the messages are responding to your PIN entries, you win!  You got Motech to work with Verboice, Skype, and your phone.

This tutorial represents one of the myriad ways for Motech to interact with Verboice and your phone.  You should play around with your manifest XML document and define new behaviors with your call flow and `web-api` request handlers.

# Bonus: Call Detail Record and CouchDB #
The Verboice PIN Tutorial module interacts with Verboice and logs some data about each call, using its `reportData` request handler.  The Motech IVR module also stores a _call detail record_ for each call which contains a lot more data, including time stamps and overall call status.

You can look at call detail records in CouchDB using CouchDB’s web-based admin console, called "Futon".  The Motech stores call detail records in a database called "flowsession".

Make sure you've made at least one call with your module, then look for the call detail record:
  * Visit your server's Futon, at an address like `http://my.domain.name:5984/_utils/`.
  * Find the database that ends in "flowsession", and click on it.
  * Click on any of the database documents that are listed.

The document should contain several time stamps, an overall call "disposition" or status, and other meta-data.  The document should also have a `data` field, which contains arbitrary data associated with the call.  If you are viewing a Verboice PIN Tutorial call record, then in the `data` field should contain values that you typed in the module properties file, like your `user.Id`, `verboice.callflow.Id`, and `ivr.extra.payload`.

You can isolate your Verboice PIN Tutorial call detail records using a CouchDB temporary view:
  * Visit your server's Futon, at an address like `http://my.domain.name:5984/_utils/`.
  * Click on the "flowsession" database.
  * At the top right, find “View” and choose **Temporary view...**.
  * For the map function on the left, write a simple map function, like the one below.

simple map function
```
function(doc) {
  emit([doc.callDetailRecord.phoneNumber, doc.callDetailRecord.startDate], doc);
}
```

Then click **Run**. You should see a list of call detail records, sorted by phone number and start date/time.  You should be able to find your own phone number and the call detail record for one of your own Verboice PIN Tutorial calls.

Each time you make a call with the Verboice PIN Tutorial module, you can return to this temporary view and click **Run**. You should see your latest call detail record in the list. You should also see that the number of results returned for your view has been incremented.

If you see your call detail records in the CouchDB temporary view, you win!  You have access to call metadata that Motech recorded for you, without your having to ask.