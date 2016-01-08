===========================
MOTECH first-run experience
===========================

.. contents:: Table of Contents
    :depth: 4

################
Bootstrap screen
################

Once you've installed MOTECH and started it for the first time, you'll be welcomed by the following bootstrap screen.
Here you'll have to provide some basic configuration information for the MOTECH to load properly.

        .. image:: img/first_run_experience/bootstrap_settings.png
            :scale: 100 %
            :alt: MOTECH bootstrap screen
            :align: center

    **ActiveMQ Broker URL**
        The URL pointing to the ActiveMQ server. If you've installed ActiveMQ using the default settings, or by using
        the provided :code:`install.sh` script, you can simply click :code:`Use` button next to the first suggestion
        right under the text box. Otherwise, you must provide the correct URL.

    **SQL URL**
        The URL pointing to the SQL server. If you've installed MySQL, or by using the provided :code:`install.sh`
        script, you can simply click :code:`Use` button next to the first suggestion. If you've installed Postgres using
        the default settings, you can simply click :code:`Use` button next to the second suggestion. Otherwise, you must
        provide the correct URL.

    **SQL Database Driver**
        JDBC driver for the chosen database.
        :code:`com.mysql.jdbc.Driver` for MySQL database.
        :code:`org.postgresql.Driver` for Postgresql.

    **SQL Username**
        The username that MOTECH will be using for managing the database.

    **SQL Password**
        The password for the account with the username provided in the **SQL Username**.

    **Felix path**
        The location where OSGi cache should be stored.

    **Configuration Mode**
        The mode that will be used for further MOTECH configuration. Both modes will be explained in the following
        sections. Keep in mind that if you select the :code:`FILE` mode, you must provide a configuration file BEFORE
        progressing further. See `Configuring MOTECH through FILE`_ section for more information about how to prepare
        the configuration file.

After you've filled up the bootstrap form and clicked :code:`Continue` button, you will have to wait for the MOTECH to
start. The following screen will inform you about to the progress during MOTECH startup process.

        .. image:: img/first_run_experience/server_starting.png
            :scale: 100 %
            :alt: MOTECH is starting
            :align: center

####################
MOTECH configuration
####################

Once the MOTECH have started, it might require some more configuration depending on the selected configuration mode. If
you've selected :code:`UI` as configuration mode jump to the `Configuring MOTECH through UI`_ section or to the
`Configuring MOTECH through FILE`_ section if you've selected :code:`FILE`.

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Configuring MOTECH through the UI
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you've selected the UI as the configuration mode, the following screen will be displayed.

        .. image:: img/first_run_experience/startup_settings.png
            :scale: 100 %
            :alt: MOTECH Startup Settings
            :align: center

Here you are able to select the language MOTECH will be using, as well as one of the following login mode:

    - **Repository** Using this mode will result in creating accounts locally.

    - **OpenID** Using this mode will result in using the OpenID standard. For more information about the OpenID, please visit the `www.openid.net`_ website.

---------------------
Repository login mode
---------------------

If you've selected :code:`Repository` as the login mode, the following screen will be displayed. Here you have to
provide the credentials for the administrator account.

        .. image:: img/first_run_experience/repository_startup_settings.png
            :scale: 100 %
            :alt: MOTECH Repository Startup Settings
            :align: center

    **Admin login**
        The username of the administrator account.

    **Admin password**
        The password of the administrator account.

    **Repeat admin password**
        The password of the administrator account.

    **Admin e-mail**
        The e-mail of the administrator.

Once you're done just hit :code:`Submit` button and then continue to the `First login through repository`_ section.

-----------------
OpenID login mode
-----------------

If you've selected OpenID as the login mode, the following screen will be displayed. Here you have to provide the
information about OpenID provider. For more information about the OpenID please visit `www.openid.net`_ website.

        .. image:: img/first_run_experience/openid_startup_settings.png
            :scale: 100 %
            :alt: MOTECH OpenID Startup Settings
            :align: center

    **Provider name**
        Here you should type how do you want to call the provider.

    **Provider URL**
        Here you should put the URL that your OpenID provider uses for authentication.

Once you're done just hit :code:`Submit` button and then continue to the `First login through OpenID`_ section.

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Configuring MOTECH through FILE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you've selected this method of configuring MOTECH you'll have to provide the configuration file. The file must have
the :code:`.properties` extension and must be placed in the :code:`config` subfolder of the MOTECH folder(most likely
:code:`/home/{username}/.motech/config`) and must store the following properties.

    **system.language**
        The language the MOTECH will be using. The accepted values are :code:`en`, :code:`pl` etc.

    **login.mode**
        The login mode that will be used. The accepted values are :code:`repository` and :code:`openid`.

---------------------
Repository login mode
---------------------

If you've selected repository as the login mode, the following screen will pop up. Here you'll be able to create admin
account by providing the credentials. Keep in mind that this is admin account and those credentials should be strong.

    .. image:: img/first_run_experience/file_startup_settings.png
        :scale: 100 %
        :alt: MOTECH File Startup Settings
        :align: center

    **Admin login**
        The username of the administrator account.

    **Admin password**
        The password of the administrator account.

    **Repeat admin password**
        The password of the administrator account.

    **Admin e-mail**
        The e-mail of the administrator.

Once you're done just hit :code:`Submit` button and then continue to the `First login through repository`_ section.

-----------------
OpenID login mode
-----------------

If you've selected OpenID login mode, you'll have to include two more properties into the file.

    **provider.name**
        The name of the OpenID provider.

    **provider.url**
        The URL that the OpenID provider uses for authentication.

Here's an example file that uses OpenID mode.

.. code::

    system.language=en
    login.mode=openid
    provider.name=UbuntuOne
    provider.url=https://login.launchpad.net/

Once you're done just hit :code:`Submit` button and then continue to the `First login through OpenID`_ section.

###########
First login
###########

Once you're done with installing and configuring MOTECH, the first login screen(depending on the selected login mode)
will pop up.

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
First login through repository
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you've chosen repository as the login mode, the following window will be displayed. Here you'll have to enter the
credentials you provided in the `MOTECH configuration`_ step.

    .. image:: img/first_run_experience/repository_login_screen.png
        :scale: 100 %
        :alt: MOTECH first login using repository mode
        :align: center

^^^^^^^^^^^^^^^^^^^^^^^^^^
First login through OpenID
^^^^^^^^^^^^^^^^^^^^^^^^^^

If you've chosen OpenID as the login mode, the following window will be displayed. Here, you'll have to click on the
:code:`Sign in with Provide name` button, which will result in redirecting you to the OpenID provider login page, where
you'll have to enter valid credentials for that provider.

    .. image:: img/first_run_experience/openid_login_screen.png
        :scale: 100 %
        :alt: MOTECH first login using repository mode
        :align: center

.. _www.openid.net: http://openid.net/