==================
Embedded MDS grids
==================

.. contents:: Table of Contents

------------
Introduction
------------

Embedded MDS grids are grids that originate from the MDS module, but are used by other modules in their own UI. MDS
allows it by providing easy and highly-customizable functionality that will not only let you embed the grid but also
give you the possibility of overriding the default behavior of that grid.

-----------
Preparation
-----------

Before you can use the embedded MDS grid you to add the support for this functionality to your module. There are only
few steps that you need to take in order to achieve this. Below I'll illustrate the whole process along with some
examples. I will expand the example javascript file and explain everything as we go.

The example javascript:

.. code-block:: javascript

    (function () {
        'use strict';

        var modulename = angular.module('moduleName');
    }());

The first thing you will have to do is to include MDS module as a dependency in you modules :code:`app.js`. Here's a
code snippet:

.. code-block:: javascript

    (function () {
        'use strict';

        var modulename = angular.module('moduleName', ['mds']);
    }());

This is required for proper loading of the angular controllers that will be used by the embedded grid.

You are also going to need the ID of the entity you want to embed the grid for. Keep in mind you can load multiple grids
for multiple entities. All you need to is to add the following code snippet to your javascript file.

.. code-block:: javascript

        var entityId;

        $.ajax({
            url: '../mds/entities/getEntity/Module Name/{entityName}',
            success:  function(data) {
                entityId = data.id;
            },
            async: false
        });

This will send a request to the MOTECH server and retrieve the ID of the entity with the given name. This ID will be
needed later to build the URL pointing to the MDS grid for that entity.

Your javascript should look similar to this.

.. code-block:: javascript

    (function () {
        'use strict';

        var modulename = angular.module('moduleName', ['mds']), entityOneId, entityTwoId;

        $.ajax({
            url: '../mds/entities/getEntity/Module Name/{entityOneName}',
            success:  function(data) {
                entityOneId = data.id;
            },
            async: false
        });

        $.ajax({
            url: '../mds/entities/getEntity/Module Name/{entityTwoName}',
            success:  function(data) {
                entityTwoId = data.id;
            },
            async: false
        });

    }());

Now that you have the IDs, you can add some redirects that will load the embedded MDS grid to the AngularJS view(more
information about this will be provided later on). Here's the code snippet you need to include.

.. code-block:: javascript

    modulename.config(function ($routeProvider) {
        $routeProvider.when('/{moduleName}/EntityOne', {redirectTo: 'mds/dataBrowser/' + entityOneId});
        $routeProvider.when('/{moduleName}/EntityTwo', {redirectTo: 'mds/dataBrowser/' + entityTwoId});
    });

Adding this will result in embedding MDS grid in the AngularJS view whenever the :code:`'/{moduleName}/EntityOne` and
:code:`/{moduleName}/EntityTwo` URLs are called.

Your javascript file should look something like this now.

.. code-block:: javascript

    (function () {
        'use strict';

        var modulename = angular.module('moduleName', ['mds']), entityOneId, entityTwoId;

        $.ajax({
            url: '../mds/entities/getEntity/Module Name/{entityOneName}',
            success:  function(data) {
                entityOneId = data.id;
            },
            async: false
        });

        $.ajax({
            url: '../mds/entities/getEntity/Module Name/{entityTwoName}',
            success:  function(data) {
                entityTwoId = data.id;
            },
            async: false
        });

        modulename.config(function ($routeProvider) {
            $routeProvider.when('/{moduleName}/EntityOne', {redirectTo: 'mds/dataBrowser/' + entityOneId});
            $routeProvider.when('/{moduleName}/EntityTwo', {redirectTo: 'mds/dataBrowser/' + entityTwoId});
        });
    }());

This was the last step of preparing the module for embedded MDS grids support. For information about how to display it,
please proceed to the next section.

-------------------
Displaying the grid
-------------------

Now that you're done with preparing the module you need to display the grid with our HTML. There are two ways of doing
this and both of them will be explained in the following subsections. I will use the following :code:`index.html` file
as a base for all the examples.

.. code-block:: html

    <!--Module main div-->
    <div id="inner-center" class="inner-center ui-layout-center ui-layout-pane ui-layout-pane-center">
        <!--div responsible for tabs handling-->
    </div>

Displaying the grid using directives
------------------------------------

To include the grid we need to add two divs with directives, one for the grid itself and the second for the filters tab
that will allow you to filter the grid.

.. code-block:: html

    <!--Module main div-->
    <div id="inner-center" class="inner-center ui-layout-center ui-layout-pane ui-layout-pane-center">
        <!--div responsible for tabs handling-->

        <!--This div is responsible for embedding the grid-->
        <div tab-layout-with-mds-grid></div>

    </div>

    <!--This div is responsible for embedding the filters panel-->
    <div embedded-mds-filters></div>

With those divs added the default implementation of the embedded grid will be loaded. For more information about
customizing it, please visit the `Customizing embedded MDS grids`_ section.

Displaying the grid through extending modules main controller
-------------------------------------------------------------

The more advanced way of displaying the grid(and also the one that allows more customization) is extending your module
main controller with :code:`MdsEmbeddableCtrl` provided by the MDS module. To achieve this you need to include the
following code snippet into your modules main controller.

.. code-block:: javascript

    angular.extend(this, $controller('MdsEmbeddableCtrl', {
        $scope: $scope,
        MDSUtils: MDSUtils
    }));

With this code snippet your controller should look something like this.

.. code-block:: javascript

    controllers.controller('ModuleBasicCtrl', function ($scope, $location, $route, $controller, MDSUtils) {

        angular.extend(this, $controller('MdsEmbeddableCtrl', {
            $scope: $scope,
            MDSUtils: MDSUtils
        }));

    });

And the :code:`index.html` file should look something like this.

.. code-block:: html

    <!--Module main div-->
    <div ng-controller='ModuleBasicCtrl' id="inner-center" class="inner-center ui-layout-center ui-layout-pane ui-layout-pane-center">
        <!--div responsible for tabs handling-->
    </div>

Now you need to include divs responsible for displaying the grid itself.

.. code-block:: html

    <div class="ui-layout-content">
        <div class="tab-content" id="tab-content">
            <div id="main-content" class="active">
                <div ng-view></div>
            </div>
        </div>
    </div>

This contains the AngularJS that will display the embedded MDS grid.

Here's the whole :code:`.html` file so far.

.. code-block:: html

    <!--Module main div-->
    <div ng-controller='ModuleBasicCtrl' id="inner-center" class="inner-center ui-layout-center ui-layout-pane ui-layout-pane-center">

        <!--div responsible for tabs handling-->

        <div class="ui-layout-content">
            <div class="tab-content" id="tab-content">
                <div id="main-content" class="active">
                    <div ng-view></div>
                </div>
            </div>
        </div>
    </div>

Some of the entities allows filtering by several fields and values. To use this functionality you need to add the
following divs to your :code:`.html` file.

.. code-block:: html

    <div id="inner-east" class="mds inner-east ui-layout-pane ui-layout-pane-east">
        <div class="header-toolbar header-footer"></div>
        <div class="filter-header">{{msg('mds.filters')}}</div>
        <div class="ui-layout-content" ng-controller="MdsFilterCtrl">
            <div class="inside">
                <fieldset class="inside" ng-repeat="filter in filters">
                    <legend>{{filter.displayName}}</legend>
                    <div class="form-group btn-group-vertical">
                        <button clickfilter singleSelect='{{(filter.type === "java.util.Date" || filter.type === "org.joda.time.DateTime" || filter.type === "org.joda.time.LocalDate")}}' ng-repeat="filterType in filter.types"  class="btn btn-info btn-sm"
                                type="button" ng-click="selectFilter(filter.field, filterType, filter.type)"><i class="fa fa-fw fa-square-o"></i> {{msgForFilter(filterType)}}</button>
                    </div>
                </fieldset>
            </div>
        </div>
    </div>

This will load the right filter panel along with it's controller.

The whole :code:`.html` file should look similar to the one belowe.

.. code-block:: html

    <!--Module main div-->
    <div ng-controller='ModuleBasicCtrl' id="inner-center" class="inner-center ui-layout-center ui-layout-pane ui-layout-pane-center">

        <!--div responsible for tabs handling-->

        <div class="ui-layout-content">
            <div class="tab-content" id="tab-content">
                <div id="main-content" class="active">
                    <div ng-view></div>
            </div>
        </div>
    </div>

    <div id="inner-east" class="mds inner-east ui-layout-pane ui-layout-pane-east">
        <div class="header-toolbar header-footer"></div>
        <div class="filter-header">{{msg('mds.filters')}}</div>
        <div class="ui-layout-content" ng-controller="MdsFilterCtrl">
            <div class="inside">
                <fieldset class="inside" ng-repeat="filter in filters">
                    <legend>{{filter.displayName}}</legend>
                    <div class="form-group btn-group-vertical">
                        <button clickfilter singleSelect='{{(filter.type === "java.util.Date" || filter.type === "org.joda.time.DateTime" || filter.type === "org.joda.time.LocalDate")}}' ng-repeat="filterType in filter.types"  class="btn btn-info btn-sm"
                                type="button" ng-click="selectFilter(filter.field, filterType, filter.type)"><i class="fa fa-fw fa-square-o"></i> {{msgForFilter(filterType)}}</button>
                    </div>
                </fieldset>
            </div>
        </div>
    </div>

You don't have to worry yourself with variables used by the code added, they are all handled by the
:code:`MdsFilterCtrl` and :code:`MdsEmbeddableCtrl` controllers.

When you're done your module will now be using the embedded MDS grid. For more information about customizing it, please
visit the `Customizing embedded MDS grids`_ section.

------------------------------
Customizing embedded MDS grids
------------------------------

MDS allows enhancing the default behavior of the embedded grids by overriding the default implementation of javascript
methods handling those grids. For this to work, the user needs to provide file with its own implementation of those
methods. Next sections will explain how to do this.

Preparation
-----------

There are a few steps you need to take in order to be able to customize the embedded MDS grid. First of all, you're
going to need a controller that will handle the :code:`/mds-databrowser-config` requests sent to your module. This
controller should return a string containing the overriding javascript methods that will be used in place of the default
MDS ones. This doesn't have to be a file loaded through the settings facade. It can also be a static file or the whole
content can be hardcoded(though it isn't the best programming practice).

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
(:code:`blueprint.xml file`) and add a :code:`custom-ui.js` file as the raw configuration. This doesn't have to be a
file loaded through the settings facade. It can also be a static file or the whole content can be hardcoded(though it
isn't the best programming practice).

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

Usage
-----

Once you are done with the preparation, your module will now support UI customization for the embedded MDS grid, but
won't use it out of the box. In order to do that some URL changes must be made. If your module has been using MDS
embedded grid before your javascript code probably contains some redirects to the MDS grids, like this:

.. code-block:: javascript

    $routeProvider.when('/{moduleName}/tab', {
        redirectTo: 'mds/dataBrowser/' + entityId
    });

This will load the default MDS grid and WON'T load the custom UI for it. In order for it to load it you need to add
module name as a query parameter, like this:

.. code-block:: javascript

    $routeProvider.when('/{moduleName}/tab', {
        redirectTo: 'mds/dataBrowser/' + entityId + "/{moduleName}"
    });

This will result in loading the :code:`custom-ui.js` and using method implementation stored in it (if a method isn't
defined in the file the default implementation will be used).

Now all you need to do is to place some javascript code in the :code:`custom-ui.js` file and check out your new,
Custom UI.

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

    $scope.showToolbox

This variable lets you decide if whole toolbox containing buttons will be visible.

.. code-block:: javascript

    $scope.backToEntityList
    $scope.showLookupDialog
    $scope.importEntityInstances
    $scope.exportEntityInstances
    $scope.showInstanceTrash

Overriding those methods lets you change the action of the :code:`Back to entity list`, :code:`Lookup`,
:code:`Import CSV`, :code:`Export Data`, :code:`View trash` buttons.