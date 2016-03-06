===========================================
Adding MOTECH-UI to Development Environment
===========================================

You may need to add the MOTECH-UI to your development environment if you want to:
- Develop the MOTECH-UI
- Develop modules that have UI packages that need to be rendered in the UI

#. In a new directory, install the MOTECH-UI by following the `instructions on github.`_

    .. _instructions on github.: https://github.com/motech/motech-ui

#. Build the MOTECH-UI. By default MOTECH-UI will look for MOTECH at http://localhost:8080/motech-platform-server/.

    .. code-block:: bash

        gulp build:app
        # Changing the default MOTECH server url
        gulp build:app --motech_server_url=http://MOTECH_URL

#. Configure Tomcat to accept all CORS requests by editing Tomcat's web.xml file (follow the CORS configuration instructions here).

#. Check that MOTECH is installed, then start or resetart Tomcat.

#. Run the local MOTECH-UI development server by running

    .. code-block:: bash

        gulp serve

    The MOTECH-UI will be running at http://localhost:5000
