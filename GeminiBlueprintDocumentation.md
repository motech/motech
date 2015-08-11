# Introduction #
Eclipse Gemini blueprint (or Spring Dynamic Modules as it was previously known) brings you the benefits provided by spring in your OSGI applications. You can import and export your OSGI services like any other spring bean.

Documentation on the Gemini blueprint framework can be found [here](http://www.eclipse.org/gemini/blueprint/documentation/).

We have recently made some changes in the motech-platform where we are using the blueprint framework (in particular the extender, more on it later) to isolate registration/export of services and registration of http servlets (Spring Dispatcher servlet) within a given bundle.

# The Problem #
Lets assume we have two bundles, sms-bundle (which as the name suggests exposes a service to send SMS) and patient-bundle (which registers a patient and sends an SMS to the patient when she is registered successfully).

The patient-bundle thus has to depend on the service provided by sms-bundle.

When we start the server, OSGI framework tries to start up the sms-bundle.

However we also have some UI for this bundle and therefore need to register a servlet. So in the Activator we start listening for the HttpService to come up and once it is up, we register the servlet with our context file(let us call it smsApplicationContext.xml).Only when this context file is read (and the context created)the SMS sending service is registered and available for import to patient-bundle.
Meanwhile the patient-bundle, having declared the SMS service as a bean in its context file has to wait patiently this service is made available by the other bundle.

If the process takes too long, the patient bundle context creation will fail due to timeout and the bundle itself will not be able to transition to Active mode.

And god forbid, if SMS bundle (let’s assume due to some weird reason) also depended on patient-module.

Well, we are dead(-locked).

We found this issue again and again in the tests.

# The Solution #
Isolate bundle’s application context creation and servlet registration.
This is where the Gemini blueprint extender bundle comes in.

As mentioned in the [documentation](http://static.springsource.org/osgi/docs/1.2.1/reference/html/bnd-app-ctx.html):

> The extender bundle creates applications contexts asynchronously, on a
> different thread then the one starting the bundle. This behaviour
> ensures that starting an OSGi Service Platform is fast and that bundles
> with service inter-dependencies do not cause deadlock (waiting for each
> other) on startup

The extender bundle scans all other bundles which and looks for any bundle which is in ACTIVE state. Whenever such a bundle is found, it checks the existence of META-INF/spring/**.xml within the bundle and if there are bean definitions within this path, it does two things**

  1. Creates an application context with the definitions as provided in xml files.
  1. Registers this context object as a service in the OSGI service registry with some properties so that you can query the service for a particular bundle.

# How do we use it in platform? #
Each bundle now has the following structure (or will have this structure in near future if it is absent for the module) for XML configuration files:

_+ META-INF/motech/ module\_application\_context.xml:_ Define all normal spring beans here.

_+ META-INF/servlet/module-servlet.xml:_ This is more like a placeholder now. Do not add anything in this file.

_+ META-INF/spring/blueprint.xml:_ All OSGI related beans (OSGI service imports and exports) to go here.

Once the extender bundle detects the application context files, creates the application context object (let us call it context1) and registers this object as a service, we register the servlet for the module(if the module requires one) and set context1 (the application context previously created by extender) as the parent context of  the web application context created by the dispatcher servlet.

This way your controller dependencies can be injected with beans defined in module\_application\_context.xml.

http://wiki.motech.googlecode.com/git/Blueprint.PNG

# So what do you have to do as a developer to ensure that in your bundle context creation is not tied to servlet registration? #

  * Have the following structure for your context files. The actual XML file names can be anything that you like. We are using blueprint as a convention.

> + META-INF/spring/blueprint.xml

> + META-INF/servlet/module-servlet.

> + META-INF/motech/module\_application\_context.xml

  * Your bundle manifest should have the following headers (configure in the felix plugin if you are using maven)

| Header Name | Value |
|:------------|:------|
| Blueprint-Enabled | true  |
| Context-Path | Path mapping for the servlet |
| Context-File | Location of context file for the dispatcher servlet. E.g. META-INF/servlet/module-servlet.xml <br>If the header is absent then META-INF/motech/<code>*</code>.xml will be considered. <br>The path here <b>SHOULD NOT</b> point to the bundle's blueprint.xml. We are in the process of deprecating this file, so please <b>DO NOT</b> add anything here. <br>
<tr><td> Resource-Path </td><td> Path that will be mapped to static resources (<code>*</code>.css,<code>*</code>.js,<code>*</code>.html). Let us assume that the value here is admin-ui. <br>Then a request for /motech/module/admin-ui/css/foo.css will map to webapp/css/foo.css within your bundle. </td></tr></tbody></table>

<ul><li>If you want your module to be represented by a tab on the left sidebar on the dashboard page, you are also expected to define a bean with the id “moduleRegistrationData” within your bundle’s application context (do notice the usage of admin-ui declared as Resource-Path value above).</li></ul>

<pre><code>&lt;bean id="moduleRegistrationData" class="org.motechproject.osgi.web.ModuleRegistrationData"&gt;<br>
        &lt;constructor-arg name="url" value="../admin-ui/index.html"/&gt;<br>
        &lt;constructor-arg name="moduleName" value="admin"/&gt;<br>
        &lt;constructor-arg name="angularModules"&gt;<br>
            &lt;list&gt;<br>
                &lt;value&gt;motech-admin&lt;/value&gt;<br>
            &lt;/list&gt;<br>
        &lt;/constructor-arg&gt;<br>
        &lt;constructor-arg name="i18n"&gt;<br>
            &lt;map&gt;<br>
                &lt;entry key="messages" value="../admin-ui/bundles/"/&gt;<br>
            &lt;/map&gt;<br>
        &lt;/constructor-arg&gt;<br>
        &lt;constructor-arg name="header" ref="header"/&gt;<br>
        &lt;property name="subMenu"&gt;<br>
            &lt;map&gt;<br>
                &lt;entry key="manageModules" value="#/bundles"/&gt;<br>
                &lt;entry key="queues" value="#/queues"/&gt;<br>
            &lt;/map&gt;<br>
        &lt;/property&gt;<br>
    &lt;/bean&gt;<br>
</code></pre>

