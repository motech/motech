==============
Security Model
==============

.. contents:: Table of Contents
   :depth: 2


############
Introduction
############

Security aspects in MOTECH are handled by the web-security module. It is responsible for users, roles and permissions
management, authentication, filtering requests and more. The web-security module uses `Spring security <http://projects.spring.io/spring-security/>`_
in the backend and allows to use certain tools from the Spring security, like @PreAuthorize and @PostAuthorize annotations or
SecurityContextHolder, that makes it possible to retrieve current authentication data. The web-security module provides an
ability to administer users, roles, permissions and security rules via UI, for users that have been given the necessary
permissions. The web-security module uses MDS as the datastore. User details, as well as roles, permissions and configured
security rules are kept there. User passwords are hashed before storing, using bcrypt hashing algorithm.


############################
MOTECH roles and permissions
############################

MOTECH permissions can be used to secure an access to methods, functionalities or even whole modules. MOTECH roles
are containers, that can keep one or more MOTECH permissions. MOTECH roles can be assigned to MOTECH users. MOTECH
defines several permissions and roles that secure access to certain parts of the system, but both users and developers
may create further roles and permissions, based on their needs.


Default MOTECH roles
####################

MOTECH defines 5 default roles.

+-----------------------------+---------------------------------------------------------------------------------------+
|MOTECH role                  |Description                                                                            |
+=============================+=======================================================================================+
|Motech Admin                 |Contains all MOTECH permissions. Any new permissions will get added to this role       |
|                             |automatically.                                                                         |
+-----------------------------+---------------------------------------------------------------------------------------+
|Email Admin                  |Grants access to the Email module, allowing to send e-mails, change Email module       |
|                             |settings and view detailed e-mail logs (containing e-mail addresses and e-mail         |
|                             |contents).                                                                             |
+-----------------------------+---------------------------------------------------------------------------------------+
|Email Junior Admin           |Similar to the Email Admin, grants access to the Email module, however the Junior      |
|                             |Admin can only see basic e-mail logs (status and timestamp).                           |
+-----------------------------+---------------------------------------------------------------------------------------+
|Bundle Admin                 |Allows to install, uninstall, update, start and stop modules.                          |
+-----------------------------+---------------------------------------------------------------------------------------+
|Campaign Manager             |Grants access to the Message Campaign module.                                          |
+-----------------------------+---------------------------------------------------------------------------------------+


#################
Access to modules
#################

You can use the MOTECH permissions to secure an access to the module. To do so, you must set the **roleForPermission**
property of the **org.motechproject.osgi.web.ModuleRegistrationData** bean. The value of this property can either be
a String, that represents a permission name, or a list of permission names, in which case, the user will be granted an
access if they have at least one of these permissions.

The sample definition of the ModuleRegistrationData bean in the blueprint context, with the roleForAccess property is
shown below.

.. code-block:: xml

    <bean id="moduleRegistrationData" class="org.motechproject.osgi.web.ModuleRegistrationData">
        <constructor-arg name="url" value="../email/resources/index.html"/>
        <constructor-arg name="moduleName" value="email"/>
        <property name="roleForAccess">
            <list>
                <value>viewBasicEmailLogs</value>
                <value>viewDetailedEmailLogs</value>
            </list>
        </property>
        <property name="settingsURL" value="/email/settings" />
        <property name="defaultURL" value="/email/send"/>
    </bean>


################
Securing methods
################

MOTECH permissions can also be used to secure the execution of methods. This is achieved, using the Spring annotations. To
enable Spring security annotations, add the following entry to the blueprint.xml context file of your module:

.. code-block:: xml

        <security:global-method-security pre-post-annotations="enabled" proxy-target-class="true"/>

Once the security is enabled, you can annotate the methods to secure them using Spring security annotations. You can find
the description of the supported annotations in the
`Spring documentation <http://docs.spring.io/spring-security/site/docs/3.1.x/reference/el-access.html>`_. The most
commonly used annotation in the MOTECH modules is the **@PreAuthorize** annotation, which checks logged user credentials
before executing the method and if they are insufficient, an attempt to execute the method will be denied. A method signature, that
would get executed only for the users with the "admin" permission, could look like this:

.. code-block:: java

    @PreAuthorize("hasRole('admin')")
    public void mySecureMethod() {
        doSomething();
    }


Similar to the above, we can specify a set of roles. The execution will be allowed, if the user has got at least one of
the listed permissions. The sample code could look like this:

.. code-block:: java

    @PreAuthorize("hasAnyRole('admin', 'junior_admin')")
    public void mySecureMethod() {
        doSomething();
    }


.. note::

    Do not get fooled by the hasRole and hasAnyRole names. Despite the name suggesting otherwise, you should place MOTECH
    permissions there, not MOTECH roles!

The MOTECH web-security module will look for the **@PreAuthorize** and **@PostAuthorize** annotations in the modules, and
add the permissions, that are not yet present in the system.


###########################
Retrieving security context
###########################

If you want to implement a custom security processor for your module or retrieve certain security information, you can
do so, using the **org.springframework.security.core.context.SecurityContextHolder** util class. It allows you to
retrieve information about current authentication. See the code below for the example on retrieving current user
and his permissions.

.. code-block:: java

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
        User user = (User) auth.getPrincipal(); // RETRIEVE USER
        Collection<GrantedAuthority> authorities = auth.getAuthorities(); // RETRIEVE PERMISSIONS
    }


###########
Login modes
###########

The MOTECH platform allows two ways of authenticating users. The two modes are called:

- Repository
- Open ID

The login mode is chosen by the administrator, during first server startup or in the motech-settings.properties file,
depending on the chosen :doc:`config source </get_started/config>`.

Using the **Repository** login mode, MOTECH will provide a way to create an initial user, during first server startup.
The initial user is granted all default permissions (Motech Admin role). New users can be created via UI, or using
the MotechUserService from web-security module. All the users are stored in the MOTECH database, using MDS.

If the chosen login mode is **Open ID**, it is also necessary to provide a valid URL to the Open ID provider,
that will handle authentication. For example, to set **Google** as your Open ID provider, the URL should
be **https://www.google.com/accounts/o8/id**. It will be possible to authenticate to MOTECH, by logging in at the
provider. The first user that logs in will be granted all default permissions (Motech Admin role). Next users that
log in will not be given any permissions, but that may be altered via UI, or using the MotechUserService.

.. warning::
    When choosing **Open ID** as the login mode, please remember that everyone who has got an account at the
    specified provider will be able to access your server. If that's not what you want, use the **Repository** login mode.

###########
HTTP access
###########

If you are not authenticated, the access to any MOTECH resources is blocked by default. Therefore, most of the requests
will get a 301 HTTP response, with a redirection to the login page. It is possible to configure exceptions to this rule,
by creating :doc:`dynamic URLs </get_started/dynamic_urls_security_rules>`. They can be used to alter the security settings
for specified URLs or to disable the security at all (meaning everyone will be able to access the resource). Security rules
can be altered via UI or via properties file, placed in your module.

.. _security-configuration:

######################
Security Configuration
######################

The MOTECH platform allows you to configure security options. You can easily change those setting via Admin UI(:code:`Settings` tab),
or by editing :code:`motech-settings.properties` file. For more details you should read the :std:ref:`configuration system section <configuration-system>`.
Below you can find available options.

**Email required** (:code:`security.required.email`) - Indicates whether you must provide an email address when creating
the user. Possible values: :code:`true`, :code:`false`.

**Failure login limit** (:code:`security.failure.login.limit`) - The permissible number of incorrect login attempts, default
value is 0. After this limit is reached the user is blocked. After a successful login counter is reset. If the value is 0
then blocking is inactive.

**Session timeout** (:code:`security.session.timeout`) - The session timeout in seconds, default 30 minutes. After this
time session will be closed.

**Minimum password length** (:code:`security.password.minlength`) - The minimum length of the password, default 0. If the
value is 0 then length checking is disabled.

**Password restriction** (:code:`security.password.validator`) - Name of the password validator which will be used for checking
passwords. Validator specifies password restriction e.g. 1 number, 1 special character. You can use 1 of 4 validators implemented
in MOTECH(default is :code:`none`) or you can :std:ref:`create your own password validator <password-validator>`. Below you
can find the names of validators provided by MOTECH.

- :code:`none`
- :code:`lower_upper` - at least 1 uppercase and lowercase
- :code:`lower_upper_digit` - at least 1 uppercase lowercase and digit
- :code:`lower_upper_digit_special` - at least 1 uppercase, lowercase, digit and special character

**Days until password expiration** (:code:`security.password.reset.days`) - The number of days after which the users
password will expire. When a password expires the user will be force to change his new password after logging into MOTECH.
The user will have to provide the old password for verification and the new password which has to be different from the
old one. An expired password will never result in the user being absolutely blocked out of the system - they will always
be able to regain access by changing their password to a new one. The last password change date is used to calculate the
date of the password expiration. The default is 0, meaning password will never expire.

**Enable password expiration reminders** (:code:`security.password.reminder.sendReminder`) - Whether email reminders
should be sent for users with their passwords nearing expiration. Takes true or false, false is default. The number
of days until sending reminder is controlled by :code:`security.password.reminder.daysBeforeExpiration` setting.

**Days before password expiration to send the reminder** (:code:`security.password.reminder.daysBeforeExpiration`) - The number
of days **before** password expiration that mark the date on which an email reminder will be sent to the user if
:code:`security.password.reminder.sendReminder is set to true. The reminders are sent on midnight.

.. _password-validator:

###########################
Creating Password Validator
###########################

Description
###########

To create your own password validator you must create new validator class which will implement :code:`PasswordValidator`
interface. The next step is to create OSGI service from your new validator. To do this you must add
:code:`@Service` annotation to the class and service reference information in the blueprint file.

:code:`PasswordValidator` interface contains following methods:

- void validate(String password) throws PasswordValidatorException - validates password.
- String getValidationError(Locale locale) - returns the error message(should be treated as a literal) for the validator. Message should explain what is expected in password.
- String getName() - Returns the name of the validator used for retrieval. Must match the value from the configuration in order to be used.

Example Code
############

Below you can find example of a validator which requires 3 special characters in password.

.. code-block:: java

    @Service("serviceName")
    public class MyValidator implements PasswordValidator {

        private String name = "validator_name";

        @Override
        public void validate(String password) {
            CharacterCount count = new CharacterCount(password);

            if (count.getSpecial() < 3) {
                throw new PasswordValidatorException("Invalid password, validator name - " + name);
            }
        }

        @Override
        public String getValidationError(Locale locale) {
            return "Password must have 3 special characters";
        }

        @Override
        public String getName() {
            return name;
        }
    }

To enable it you must add :code:`<osgi:service ref="serviceName" interface="org.motechproject.security.validator.PasswordValidator"/>`
to the blueprint file and set it in config(security.password.validator=validator_name).