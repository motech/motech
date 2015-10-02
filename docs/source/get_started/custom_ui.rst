=========
Custom UI
=========

.. contents:: Table of Contents

------------
Introduction
------------

Custom UI is a feature for enhancing default behavior of the embedded grids that originate from the MDS module. It
allows high customization of those grids by overriding the default implementation of the javascript methods.

-----------
Preparation
-----------

There are few steps you need to take in order to be able to use the Custom UI feature. First of all, you're going to
need a controller that will handle the :code:`/mds-databrowser-config` requests sent to your module. This controller
should return a string containing the overriding javascript methods that will be used in place of the default MDS ones.

Here's a code example:

.. code-block:: java

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/mds-databrowser-config", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomUISettings() throws IOException {
        return IOUtils.toString(settingsFacade.getRawConfig(Constants.UI_CONFIG));
    }

Here you can see that :code:`custom-ui.js` file is loaded through the settings facade as a raw configuration file. In
order to use this code snippet you'll have to include the setting facade bean in your modules application context
(:code:`blueprint.xml file`) and add a :code:`custom-ui.js` file as the raw configuration.

Here's a code example:

.. code-block:: xml

    <bean id="settingsFacade" class="org.motechproject.server.config.SettingsFacade" autowire="byType">
        <property name="rawConfigFiles">
            <list>
                <value>classpath:custom-ui.js</value>
            </list>
        </property>
    </bean>

You also need to add the :code:`custom-ui.js` file to your modules resource directory.

-----
Usage
-----

Once done with the preparation, your module will now support Custom UI feature, but won't use it out of the box. In
order to do that some URL changes must be made. If your module has been using MDS embedded grid before your javascript
code probably contains some redirects to the MDS grids, like this:

.. code-block:: javascript

    $routeProvider.when('/{moduleName}/tab', {
        redirectTo: 'mds/dataBrowser/' + entityId
    });

This will load the default MDS grid and WON'T load the Custom UI for it. In order for it to load it you need to add
module name as a query parameter, like this:

.. code-block:: javascript

    $routeProvider.when('/{moduleName}/tab', {
        redirectTo: 'mds/dataBrowser/' + entityId + "/{moduleName}"
    });

This will result in loading the :code:`custom-ui.js` and using method implementation stored in it (if a method isn't
defined in the file the default implementation will be used).

Now all you need to do is to place some javascript code in the :code:`custom-ui.js` file and check out your new,
Custom UI.


--------
Examples
--------

Here's some code examples with explanation:

.. code-block:: javascript

    $scope.showBackToEntityListButton
    $scope.showAddInstanceButton
    $scope.showLookupButton
    $scope.showFieldsButton
    $scope.showImportButton
    $scope.showExportButton
    $scope.showViewTrashButton
    $scope.showFiltersButton
    $scope.showDeleteInstanceButton

Those variables let you decide which buttons will be visible in the embedded MDS grid.

.. code-block:: javascript

    $scope.backToEntityList

Overriding this method lets you change the action of the :code:`Back to entity list` button behavior.