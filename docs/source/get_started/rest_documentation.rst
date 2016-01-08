=============================================
Automatic REST API documentation UI in MOTECH
=============================================

MOTECH uses `Swagger <http://swagger.io/>`_ for generating a user interface that documents and allows
testing of REST APIs. An interface can be generated for each module that wishes to register a REST API for documenting.
This document will describe the process of registering a REST API for the module with the system.

It is worth noting that this documentation will always be generated for :doc:`MDS <model_data/model_data>` entities that have REST access enabled.

Overview of the UI
##################

Swagger will generate documentation for each endpoint specified in the API description:

            .. image:: img/rest_docs_operations.png
                    :scale: 100 %
                    :alt: Swagger UI - multiple operations
                    :align: center

For each HTTP method allowed for a given endpoint, the user will be able to view the details of the operation,
such as the structure of the response, structure of the expected request, allowed parameters.

            .. image:: img/rest_docs_single_operation.png
                    :scale: 100 %
                    :alt: Swagger UI - multiple operations
                    :align: center

Users can use that UI to easily execute REST calls against the API and view the responses.

Registering REST documentation
##############################

The first step for registering rest documentation is creating a Swagger spec file that will describe the API.
More information on spec files, ways of generating them and so on can be found on the `Swagger spec Wiki <https://github.com/swagger-api/swagger-spec/wiki>`_.

After generating the file, it has to be exposed by the module through HTTP. You can achieve this either by placing the
file in your webapp resources or by creating a Spring controller that will serve this content. For more information on exposing a resource,
refer to the :doc:`UI documentation <ui>`.

After the resource is exposed through the UI, its path should be specified in the ModuleRegistrationData bean using
the **restDocsPath** property. Below is an example of a simple module registration that registers the spec file
with the system.

.. code-block:: xml

        <bean id="moduleRegistrationData" class="org.motechproject.osgi.web.ModuleRegistrationData">
            <constructor-arg name="url" value="../mymodule/resources/index.html"/>
            <constructor-arg name="moduleName" value="my-module"/>
            <property name="restDocsPath" value="/mymodule/resources/spec.json"/>
        </bean>

After these steps, the module and its API will be incorporated into the Swagger UI exposed by MOTECH.
