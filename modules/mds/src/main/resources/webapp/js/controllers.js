(function () {

    'use strict';

    var mds = angular.module('mds'),
        workInProgress = {
            list: [],
            actualEntity: undefined,
            setList: function (service) {
                this.list = service.getWorkInProggress();
            },
            setActualEntity: function (service, entityId) {
                this.setList(service);
                this.actualEntity = entityId;
            }
        },
        loadEntity;

    mds.controller('MdsBasicCtrl', function ($scope, $location, $route, Entities) {
        var schemaEditorPath = '/{0}'.format($scope.AVAILABLE_TABS[0]);

        workInProgress.setList(Entities);

        $scope.hasWorkInProgress = function () {
            var expression = workInProgress.list.length > 0,
                idx;

            for (idx = 0; expression && idx < workInProgress.list.length; idx += 1) {
                if (workInProgress.list[idx].id === workInProgress.actualEntity) {
                    expression = false;
                }
            }

            return expression;
        };

        $scope.getWorkInProgress = function () {
            var list = [];

            angular.forEach(workInProgress.list, function (entity) {
                if (entity.id !== workInProgress.actualEntity) {
                    list.push(entity);
                }
            });

            return list;
        };

        $scope.resumeEdits = function (entityId) {
            if (schemaEditorPath !== $location.path()) {
                $location.path(schemaEditorPath);
            } else {
                $route.reload();
            }

            loadEntity = entityId;
        };

        $scope.discard = function (entityId) {
            motechConfirm('mds.wip.info.discard', 'mds.warning', function (val) {
                if (val) {
                    Entities.abandon({id: entityId}, function () {
                        workInProgress.setList(Entities);
                    });
                }
            });
        };
    });

    /**
    * The SchemaEditorCtrl controller is used on the 'Schema Editor' view.
    */
    mds.controller('SchemaEditorCtrl', function ($scope, $timeout, Entities) {
        var setAdvancedSettings, setRest, setBrowsing, draft, setIndexesLookupsTab;

        workInProgress.setList(Entities);

        if (loadEntity) {
            $.ajax("../mds/entities/" + loadEntity).done(function (data) {
                $scope.selectedEntity = data;
                loadEntity = undefined;
            });
        }

        /**
        * This function is used to get entity advanced rest data from controller and prepare it for further usage.
        */
        setRest = function () {
            $scope.selectedEntityAdvancedFields = [];
            $scope.selectedEntityAdvancedAvailableFields = [];
            $scope.selectedEntityRestLookups = [];

            if ($scope.advancedSettings.restOptions) {
                angular.forEach($scope.advancedSettings.restOptions.fieldIds, function (id) {
                    $scope.selectedEntityAdvancedFields.push($scope.findFieldById(id));
                });
            }

            angular.forEach($scope.fields, function (field) {
                if (!$scope.findFieldInArrayById(field.id, $scope.selectedEntityAdvancedFields)) {
                    $scope.selectedEntityAdvancedAvailableFields.push($scope.findFieldById(field.id));
                }
            });

            if ($scope.advancedSettings.indexes) {
                angular.forEach($scope.advancedSettings.indexes, function (lookup, index) {
                    if ($.inArray(lookup.lookupName, $scope.advancedSettings.restOptions.lookupIds) !== -1) {
                        $scope.selectedEntityRestLookups[index] = true;
                    } else {
                        $scope.selectedEntityRestLookups[index] = false;
                    }
                });
            }
        };

        /**
        * This function splits fields to ones that are displayed and ones that are not
        */
        setBrowsing = function() {
            if($scope.fields !== undefined && $scope.advancedSettings.browsing !== undefined) {
                $scope.browsingAvailable = $.grep($scope.fields, function(field) {
                    return $scope.advancedSettings.browsing.displayedFields.indexOf(field.id) < 0;
                });

                $scope.browsingDisplayed = $.grep($scope.fields, function(field) {
                    return $scope.advancedSettings.browsing.displayedFields.indexOf(field.id) >= 0;
                });

                $scope.browsingAvailable.sort(function(a,b) {
                    if (a.basic.displayName < b.basic.displayName) { return -1; }
                    if (a.basic.displayName > b.basic.displayName) { return 1; }
                    return 0;
                });
            }
        };

        /**
        * This function defines default behaviour on indexesLookupsTab shown event
        */
        setIndexesLookupsTab = function() {
            $('#indexesLookupsTabLink').on('shown.bs.tab', function (e) {
                $scope.setLookupFocus();
            });

            $('#advancedObjectSettingsModal').on('shown.bs.modal', function () {
                $scope.setLookupFocus();
            });
        };

        /**
        * This function is used to set advanced settings. If settings is properly taken from server,
        * the related $scope fields will be also set.
        */
        setAdvancedSettings = function () {
            $scope.advancedSettings = Entities.getAdvanced({id: $scope.selectedEntity.id},
                function () {
                    if (!_.isNull($scope.advancedSettings)
                            && !_.isUndefined($scope.advancedSettings)
                            && $scope.advancedSettings.indexes.length > 0) {
                        $scope.setActiveIndex(0);
                    } else {
                        $scope.setActiveIndex(-1);
                    }

                    setRest();
                    setBrowsing();
                    setIndexesLookupsTab();
                    unblockUI();
                });
        };

        draft = function (data, callback) {
            var pre = { id: $scope.selectedEntity.id },
                func = function () {
                    $scope.selectedEntity.draft = true;

                    if (_.isFunction(callback)) {
                        callback();
                    }
                };

            Entities.draft(pre, data, func);
        };

        /**
        * The $scope.selectedEntityAdvancedAvailableFields contains fields available for use in REST.
        */
        $scope.selectedEntityAdvancedAvailableFields = [];

        /**
        * The $scope.selectedEntityAdvancedFields contains fields selected for use in REST.
        */
        $scope.selectedEntityAdvancedFields = [];

        /**
        * The $scope.selectedEntityRestLookups contains lookups selected for use in REST.
        */
        $scope.selectedEntityRestLookups = [];

        /**
        * The $scope.selectedEntityMetadata contains orignal metadata for selected entity used to check
        * for changes in it.
        */
        $scope.originalSelectedEntityMetadata = undefined;

        /**
        * The $scope.selectedEntity contains selected entity. By default no entity is selected.
        */
        $scope.selectedEntity = null;

        /**
        * The $scope.advancedSettings contains advanced settings of selected entity. By default
        * there are no advanced settings
        */
        $scope.advancedSettings = null;

        /**
        * The $scope.fields contains entity fields. By default there are no fields.
        */
        $scope.fields = undefined;

        /**
        * The $scope.newField contains information about new field which will be added to an
        * entity schema. By default no field is created.
        */
        $scope.newField = {};

        /**
        * The $scope.tryToCreate is used to ensure that error messages in the new field form will
        * be not visible until a user try to add a new field
        */
        $scope.tryToCreate = false;

        /**
        * The $scope.availableFields array contains information about fields, that can be selected
        * as lookup fields for certain index
        */
        $scope.availableFields = [];

        $scope.activeIndex = -1;

        /**
        * The $scope.lookup persists currently active (selected) index
        */
        $scope.lookup = undefined;

        /**
        * The $scope.browsingAvailable and $scope.browsingDisplayed separates fields that are
        * visible from the ones that are not.
        */
        $scope.browsingAvailable = [];
        $scope.browsingDisplayed = [];

        /**
        * The $scope.filterableTypes contains types that can be used as filters.
        */
        $scope.filterableTypes = [
            "mds.field.combobox", "mds.field.boolean", "mds.field.date",
            "mds.field.time", "mds.field.datetime"
        ];

        /**
        * The $scope.SELECT_ENTITY_CONFIG contains configuration for selecting entity tag on UI.
        */
        $scope.SELECT_ENTITY_CONFIG = {
            ajax: {
                url: '../mds/selectEntities',
                dataType: 'json',
                quietMillis: 100,
                data: function (term, page) {
                    return {
                        term: term,
                        pageLimit: 5,
                        page: page
                    };
                },
                results: function (data) {
                    return data;
                }
            },
            initSelection: function (element, callback) {
                var id = $(element).val();

                if (!isBlank(id)) {
                    $.ajax("../mds/entities/" + id).done(function (data) {
                        callback(data);
                    });
                }
            },
            formatSelection: function (entity) {
                var name = entity && entity.name ? entity.name : '',
                    module = entity && entity.module ? ' {0}: {1}'
                        .format($scope.msg('mds.module'), entity.module) : '',
                    namespace = entity && entity.namespace ? ' {0}: {1}'
                        .format($scope.msg('mds.namespace'), entity.namespace) : '',
                    info = $.trim('{0} {1}'.format(module, namespace)),
                    label = !isBlank(info) && !isBlank(name)
                        ? '{0} ({1})'.format(name, info) : !isBlank(name) ? name : '';

                return isBlank(label) ? $scope.msg('mds.error') : label;
            },
            formatResult: function (entity) {
                var strong = entity && entity.name
                        ? angular.element('<strong>').text(entity.name)
                        : undefined,
                    name = strong
                        ? angular.element('<div>').append(strong)
                        : undefined,
                    module = entity && entity.module
                        ? angular.element('<span>')
                            .text(' {0}: {1}'.format($scope.msg('mds.module'), entity.module))
                        : undefined,
                    namespace = entity && entity.namespace
                        ? angular.element('<span>')
                            .text(' {0}: {1}'.format(
                                $scope.msg('mds.namespace'),
                                entity.namespace
                            ))
                        : undefined,
                    info = (module || namespace)
                        ? angular.element('<div>').append(module).append(namespace)
                        : undefined,
                    parent = (name || info)
                        ? angular.element('<div>').append(name).append(info)
                        : undefined;

                return parent || $scope.msg('mds.error');
            },
            containerCssClass: "form-control-select2"
            ,
            escapeMarkup: function (markup) {
                return markup;
            }
        };

        /**
        * The $scope.SELECT_FIELD_TYPE_CONFIG contains configuration for selecting field type tag
        * on UI.
        */
        $scope.SELECT_FIELD_TYPE_CONFIG = {
            ajax: {
                url: '../mds/available/types',
                dataType: 'json',
                quietMillis: 100,
                data: function (term, page) {
                    return {
                        term: term,
                        pageLimit: 5,
                        page: page
                    };
                },
                results: function (data) {
                    return data;
                }
            },
            initSelection: function (element, callback) {
                var id = $(element).val();

                if (!isBlank(id)) {
                    $.ajax('../mds/available/types').done(function (data) {
                        var found, i;

                        for (i = 0; i < data.results.length; i += 1) {
                            if (data.results[i].id === id) {
                                found = data.results[i];
                                break;
                            }
                        }

                        callback(found);
                    });
                }
            },
            formatSelection: function (type) {
                return $scope.msg((type && type.type && type.type.displayName) || 'mds.error');
            },
            formatResult: function (type) {
                var strong = type && type.type && type.type.displayName
                        ? angular.element('<strong>').text($scope.msg(type.type.displayName))
                        : undefined,
                    name = strong
                        ? angular.element('<div>').append(strong)
                        : undefined,
                    description = type && type.type && type.type.description
                        ? angular.element('<span>')
                            .text($scope.msg(type.type.description))
                        : undefined,
                    info = description
                        ? angular.element('<div>').append(description)
                        : undefined,
                    parent = (name || info)
                        ? angular.element('<div>').append(name).append(info)
                        : undefined;

                return parent || $scope.msg('mds.error');
            },
            escapeMarkup: function (markup) {
                return markup;
            }
        };

        /* ~~~~~ ENTITY FUNCTIONS ~~~~~ */

        /**
        * Create and save a new entity with a name from related input tag. If the value of input
        * tag is blank, error message will be shown and the entity will be not created.
        */
        $scope.createEntity = function () {
            var form = angular.element("#newEntityModalForm"),
                input = form.find('#inputEntityName'),
                help = input.next('span'),
                value = input.val(),
                entity = {};

            if (isBlank(value)) {
                help.removeClass('hide');
            } else {
                entity.name = value;

                Entities.save({}, entity, function (response) {
                    $scope.selectedEntity = response;
                    angular.element('#selectEntity').select2('val', response.id);

                    $scope.clearEntityModal();
                }, function (response) {
                    handleResponse('mds.error', 'mds.error.cantSaveEntity', response);
                });
            }
        };

        /**
        * Remove value from the name input tag and hide the error message. This method also hides
        * the new entity modal window.
        */
        $scope.clearEntityModal = function () {
            var modal = angular.element('#newEntityModal'),
                form = modal.find('form'),
                spans = form.find('span.help-block');

            angular.forEach(spans, function (span) {
                var that = angular.element(span);

                if (!that.hasClass('hide')) {
                    that.addClass('hide');
                }
            });

            form.resetForm();
            modal.modal('hide');
        };

        /**
        * Deletes the selected entity. If the entity is read only (provided by module), action is
        * not allowed. If entity does not exist, error message is shown.
        */
        $scope.deleteEntity = function () {
            if ($scope.selectedEntity !== null) {
                Entities.remove({id: $scope.selectedEntity.id}, function () {
                    $scope.selectedEntity = null;
                    handleResponse('mds.success', 'mds.delete.success', '');
                }, function (response) {
                    handleResponse('mds.error', 'mds.error.cantDeleteEntity', response);
                });
            }
        };

        /* ~~~~~ METADATA FUNCTIONS ~~~~~ */

        /**
        * Adds new metadata with empty key/value to field.
        */
        $scope.addMetadata = function (field) {
            draft({
                edit: true,
                values: {
                    path: '$addEmptyMetadata',
                    fieldId: field.id
                }
            }, function () {
                $scope.safeApply(function () {
                    if (!field.metadata) {
                        field.metadata = [];
                    }

                    field.metadata.push({key: '', value: ''});
                });
            });
        };

        /**
        * Removes selected metadata entry from field.
        */
        $scope.removeMetadata = function (field, idx) {
            draft({
                edit: true,
                values: {
                    path: '$removeMetadata',
                    fieldId: field.id,
                    value: [idx]
                }
            }, function () {
                $scope.safeApply(function () {
                    field.metadata.remove(idx);
                });
            });
        };

        $scope.draftRestLookup = function (index) {
            var value = $scope.selectedEntityRestLookups[index],
                lookup = $scope.advancedSettings.indexes[index];

            draft({
                edit: true,
                values: {
                    path: 'restOptions.${0}'.format(value ? 'addLookup' : 'removeLookup'),
                    advanced: true,
                    value: [lookup.lookupName]
                }
            }, function () {
                $scope.safeApply(function () {
                    if (value) {
                        $scope.advancedSettings.restOptions.lookupIds.push(
                            lookup.lookupName
                        );
                    } else {
                        $scope.advancedSettings.restOptions.lookupIds.removeObject(
                            lookup.lookupName
                        );
                    }
                });
            });
        };

        /**
        * Callback function called each time when user adds, removes or moves items in 'Displayed Fields' on
        * 'REST API' view. Responsible for updating the model.
        */
        $scope.onRESTDisplayedChange = function(container) {
            $scope.advancedSettings.restOptions.fieldIds = [];

            angular.forEach(container, function(field) {
                $scope.advancedSettings.restOptions.fieldIds.push(field.id);
            });

            draft({
                edit: true,
                values: {
                    path: 'restOptions.$setFieldIds',
                    advanced: true,
                    value: [$scope.advancedSettings.restOptions.fieldIds]
                }
            });
        };

        /* ~~~~~ FIELD FUNCTIONS ~~~~~ */

        /**
        * Create new field and add it to an entity schema. If displayName, name or type was not
        * set, error message will be shown and a field will not be created. The additional message
        * will be shown if a field name is not unique.
        */
        $scope.createField = function () {
            var validate, selector;

            $scope.tryToCreate = true;
            validate = $scope.newField.type
                && $scope.newField.displayName
                && $scope.newField.name
                && $scope.findFieldsByName($scope.newField.name).length === 0;

            if (validate) {
                draft({
                    create: true,
                    values: {
                        typeClass: $scope.newField.type.type.typeClass,
                        displayName: $scope.newField.displayName,
                        name: $scope.newField.name
                    }
                }, function () {
                    var field;

                    field = Entities.getField({
                        id: $scope.selectedEntity.id,
                        param: $scope.newField.name
                    }, function () {
                        $scope.fields.push(field);
                        setBrowsing();

                        selector = '#show-field-details-{0}'.format($scope.fields.length - 1);
                        $scope.newField = {};
                        angular.element('#newField').select2('val', null);
                        $scope.tryToCreate = false;

                        angular.element(selector).livequery(function () {
                            var elem = angular.element(selector);

                            elem.click();
                            elem.expire();
                        });
                    });
                });
            }
        };

        /**
        * Remove a field from an entity schema. The selected field will be removed only if user
        * confirms that the user wants to remove the field from the entity schema.
        *
        * @param {object} field The field which should be removed.
        */
        $scope.removeField = function (field) {
            motechConfirm('mds.warning.removeField', 'mds.warning', function (val) {
                if (val) {
                    $scope.safeApply(function () {
                        var filterableIndex;
                        $scope.fields.removeObject(field);

                        if ($scope.findFieldInArrayById(field.id, $scope.selectedEntityAdvancedAvailableFields)) {
                            $scope.selectedEntityAdvancedAvailableFields.removeObject(field);
                        } else {
                            $scope.selectedEntityAdvancedFields.removeObject(field);
                        }

                        filterableIndex = $scope.advancedSettings.browsing.filterableFields.indexOf(field.id);
                        if(filterableIndex >= 0) {
                            $scope.advancedSettings.browsing.filterableFields.splice(filterableIndex, 1);
                        }

                        draft({
                            remove: true,
                            values: {
                                fieldId: field.id
                            }
                        });
                    });
                }
            });
        };

        /**
        * Abandon all changes made on an entity schema.
        */
        $scope.abandonChanges = function () {
            blockUI();

            Entities.abandon({id: $scope.selectedEntity.id}, function () {
                $scope.selectedEntity.draft = false;

                $scope.fields = Entities.getFields({id: $scope.selectedEntity.id}, function () {
                        setAdvancedSettings();
                    });

                unblockUI();
            });
        };

        /**
        * Check if the given field is unique.
        *
        * @param {string} fieldName The field name to check.
        * @return {boolean} true if the given field is unique; otherwise false.
        */
        $scope.uniqueField = function (fieldName) {
            return $scope.findFieldsByName(fieldName).length === 1;
        };

        /**
        * Check if the given metadata key is unique.
        *
        * @param {string} key metadata key to check..
        * @return {boolean} true if the given key is unique; otherwise false.
        */
        $scope.uniqueMetadataKey = function (field, key) {
            return !_.isUndefined(key)
                && find(field.metadata, [{ field: 'key', value: key}], false).length === 1;
        };

        /**
        * Validate all information inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all information inside the field are correct; otherwise false.
        */
        $scope.validateField = function (field) {
            return $scope.validateFieldBasic(field)
                && $scope.validateFieldSettings(field)
                && $scope.validateFieldValidation(field);
        };

        /**
        * Validate the basic information ('Basic' tab on UI) inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all basic information inside the field are correct;
        *                   otherwise false.
        */
        $scope.validateFieldBasic = function (field) {
            return field.basic.displayName
                && field.basic.name
                && $scope.uniqueField(field.basic.name);
        };

        /**
        * Validate the settings information ('Settings' tab on UI) inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all settings information inside the field are correct;
        *                   otherwise false.
        */
        $scope.validateFieldSettings = function (field) {
            var expression = true;

            if (field.settings) {
                angular.forEach(field.settings, function (setting) {
                    expression = expression && $scope.checkOptions(setting);
                });
            }

            return expression;
        };

        /**
        * Validate the validation information ('Validation' tab on UI) inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all validation information inside the field are correct;
        *                   otherwise false.
        */
        $scope.validateFieldValidation = function (field) {
            var expression = true;

            if (field.validation) {
                angular.forEach(field.validation.criteria, function (criterion) {
                    if ($scope.validateCriterion(criterion, field.validation.criteria)) {
                        expression = false;
                    }
                });
            }

            return expression;
        };

        /**
        * Check if a user can save field definitions to database.
        *
        * @return {boolean} true if field definitions are correct; otherwise false.
        */
        $scope.canSaveChanges = function () {
            var expression = true;

            angular.forEach($scope.fields, function (field) {
                expression = expression && $scope.validateField(field);

                angular.forEach(field.metadata, function (meta) {
                    expression = expression && $scope.uniqueMetadataKey(field, meta.key);
                });
            });

            if ($scope.advancedSettings.indexes) {
                angular.forEach($scope.advancedSettings.indexes, function (index) {
                    expression = expression && index.lookupName !== undefined && index.lookupName.length !== 0;
                });
            }

            return expression;
        };

        /**
        * Save all changes made on an entity schema. Firstly this method tries to save fields to
        * database one by one. Next the method tries to delete existing fields from database.
        */
        $scope.saveChanges = function () {
            blockUI();

            Entities.commit({id: $scope.selectedEntity.id}, {}, function () {
                $scope.selectedEntity.draft = false;
                unblockUI();
            });
        };

        /* ~~~~~ ADVANCED FUNCTIONS ~~~~~ */

        /**
        * Add what field should be logged. If the field exists in the array, the field will be
        * removed from the array.
        *
        * @param {object} field The object which represent the entity field.
        */
        $scope.addFieldToLog = function (field) {
            var idx;

            if (!_.isNull($scope.advancedSettings) && !_.isUndefined($scope.advancedSettings)) {
                idx = $scope.advancedSettings.tracking.fields.indexOf(field.id);

                draft({
                    edit: true,
                    values: {
                        path: idx > -1 ? 'tracking.$removeField' : 'tracking.$addField',
                        advanced: true,
                        value: [field.id]
                    }
                }, function () {
                    if (idx > -1) {
                        $scope.advancedSettings.tracking.fields.remove(idx);
                    } else {
                        $scope.advancedSettings.tracking.fields.push(field.id);
                    }
                });
            }
        };

        /**
        * Add what kind of action should be logged. If the action exists in the array, the action
        * will be removed from the array.
        *
        * @param {string} action The name of the action to log.
        */
        $scope.addActionToLog = function (action) {
            var idx;

            if (!_.isNull($scope.advancedSettings) && !_.isUndefined($scope.advancedSettings)) {
                idx = $scope.advancedSettings.tracking.actions.indexOf(action);

                draft({
                    edit: true,
                    values: {
                        path: idx > -1 ? 'tracking.$removeAction' : 'tracking.$addAction',
                        advanced: true,
                        value: [action]
                    }
                }, function () {
                    if (idx > -1) {
                        $scope.advancedSettings.tracking.actions.remove(idx);
                    } else {
                        $scope.advancedSettings.tracking.actions.push(action);
                    }
                });
            }
        };

        /**
        * Adds a new index and sets it as the active one
        */
        $scope.addNewIndex = function () {
            var newLookup = {
                lookupName: "New lookup",
                singleObjectReturn: true,
                fieldList: []
            };

            draft({
                edit: true,
                values: {
                    path: '$addNewIndex',
                    advanced: true
                }
            }, function () {
                $scope.advancedSettings.indexes.push(newLookup);
                $scope.setActiveIndex($scope.advancedSettings.indexes.length-1);
            });
        };

        /**
        * Specifies, whether a certain index is a currently active one
        *
        * @param index An index in array of index object to check
        * @return {boolean} True, if passed index is the active one. False otherwise.
        */
        $scope.isActiveIndex = function (index) {
            return $scope.activeIndex === index;
        };

        /**
        * Sets certain index as the currently active one
        *
        * @param index  An index in array of index object to set active
        */
        $scope.setActiveIndex = function (index) {
            $scope.activeIndex = index;
            if ($scope.activeIndex > -1) {
                $scope.lookup = $scope.advancedSettings.indexes[$scope.activeIndex];
                $scope.setAvailableFields();
                $scope.setLookupFocus();
            } else {
                $scope.lookup = undefined;
            }
        };

        /**
        * Changes active index depending on which arrow key user pressed
        * up arrow - decrements active index
        * down arrow - increments active index
        */
        $scope.changeActiveIndex = function($event) {
            if ($event.keyCode === 38 && $scope.activeIndex > 0) { // up arrow
                $scope.setActiveIndex($scope.activeIndex - 1);
            } else if ($event.keyCode === 40 && $scope.activeIndex < $scope.advancedSettings.indexes.length - 1) { // down arrow
                $scope.setActiveIndex($scope.activeIndex + 1);
            }
        };

        /**
        * Sets focus on lookup with active index
        */
        $scope.setLookupFocus = function() {
            var selector;
            if ($scope.activeIndex !== -1) {
                selector = '#lookup-{0}'.format($scope.activeIndex);
                $(selector).livequery(function () {
                    var elem = $(selector);
                    elem.focus();
                    elem.expire();
                });
            }
        };

        /**
        * Removes currently active index
        */
        $scope.deleteLookup = function () {
            draft({
                edit: true,
                values: {
                    path: '$removeIndex',
                    advanced: true,
                    value: [$scope.activeIndex]
                }
            }, function () {
                $scope.advancedSettings.indexes.remove($scope.activeIndex);
                $scope.selectedEntityRestLookups.splice($scope.activeIndex, 1);
                $scope.setActiveIndex(-1);
            });
        };

        /**
        * Adds new lookup field to the currently active index
        */
        $scope.addLookupField = function () {
            var value = $scope.availableFields[0] && $scope.availableFields[0].id;

            draft({
                edit: true,
                values: {
                    path: 'indexes.{0}.$addField'.format($scope.activeIndex),
                    advanced: true,
                    value: [value]
                }
            }, function () {
                $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.push(value);
                $scope.setAvailableFields();
            });
        };

        /**
        * Handles field selection. When clicking on one of the available fields from dropdown list,
        * selected field is added or replaced on the list of selected fields of the currently
        * active index
        *
        * @param oldField Previously selected field index
        * @param field Selected field index
        */
        $scope.selectField = function (oldField, newField) {
            var selectedIndex = $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.indexOf(oldField);

            draft({
                edit: true,
                values: {
                    path: 'indexes.{0}.$insertField'.format($scope.activeIndex),
                    advanced: true,
                    value: [selectedIndex, newField]
                }
            }, function () {
                $scope.advancedSettings.indexes[$scope.activeIndex].fieldList[selectedIndex] = newField;
                $scope.setAvailableFields();
            });
        };

        /**
        * Refreshes available fields for the currently active index. A field is considered available
        * if it is not yet present in the lookup field list of the currently active index.
        */
        $scope.setAvailableFields = function () {
            var availableFields = [], func, selectedFields, i;

            if ($scope.activeIndex !== -1) {
                func = function (num) { return num === $scope.fields[i].id; };
                selectedFields = $scope.advancedSettings.indexes[$scope.activeIndex].fieldList;

                for (i = 0; i < $scope.fields.length; i += 1) {
                    if (_.filter(selectedFields, func).length === 0) {
                        availableFields.push($scope.fields[i]);
                    }
                }

                $scope.availableFields = availableFields;
            }
        };

        /**
        * Removes given field from the lookup fields list of the currently active index
        *
        * @param field A field object to remove
        */
        $scope.removeLookupField = function (field) {
            draft({
                edit: true,
                values: {
                    path: 'indexes.{0}.$removeField'.format($scope.activeIndex),
                    advanced: true,
                    value: [field]
                }
            }, function () {
                $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.removeObject(field);
                $scope.setAvailableFields();
            });
        };

        /**
        * Checks if user can still add more lookup fields.
        *
        * @return {boolean} False if all available fields have already been selected
        *                   or the amount of added fields is equal to amount of all fields for
        *                   that object. True otherwise.
        */
        $scope.canAddLookupFields = function () {
            return $scope.activeIndex !== -1
                            && $scope.availableFields.length > 0
                            && $scope.lookup.fieldList.length < $scope.fields.length;
        };

        /**
        * Checks if there are fields selected to move left in REST view.
        */
        $scope.canMoveLeftRest = function() {
             return $('.target-item.rest-fields.selected').size() > 0;
        };

        /**
        * Checks if there are fields to move left in REST view.
        */
        $scope.canMoveAllLeftRest = function() {
            return $scope.selectedEntityAdvancedFields.length > 0;
        };

        /**
        * Checks if there are fields selected to move right in REST view.
        */
        $scope.canMoveRightRest = function() {
             return $('.source-item.rest-fields.selected').size() > 0;
        };

        /**
        * Checks if there are fields to move right in REST view.
        */
        $scope.canMoveAllRightRest = function() {
            return $scope.selectedEntityAdvancedAvailableFields.length > 0;
        };

        /* VALIDATION FUNCTIONS */

        /**
        * Checks if criterion value is valid
        * @param {object} criterion to validate
        * @param {object} list containing all field's validation criteria
        * @return {string} empty if criterion is valid (otherwise contains validation error)
        */
        $scope.validateCriterion = function(criterion, validationCriteria) {
            var anotherCriterion;

            if (criterion.enabled) {
                if (criterion.value === null || criterion.value.length === 0) {
                    return 'mds.error.requiredField';
                }

                switch (criterion.displayName) {
                    case 'mds.field.validation.minLength':
                        if (criterion.value < 0) {
                            return 'mds.error.lengthMustBePositive';
                        } else {
                            anotherCriterion = $scope.findCriterionByName(validationCriteria, 'mds.field.validation.maxLength');

                            if (anotherCriterion !== null && anotherCriterion.enabled && anotherCriterion.value
                                && anotherCriterion.value < criterion.value) {
                                    return 'mds.error.minCannotBeBigger';
                            }
                        }
                        break;
                    case 'mds.field.validation.maxLength':
                        if (criterion.value < 0) {
                            return 'mds.error.lengthMustBePositive';
                        } else {
                            anotherCriterion = $scope.findCriterionByName(validationCriteria, 'mds.field.validation.minLength');

                            if (anotherCriterion !== null && anotherCriterion.enabled && anotherCriterion.value
                                && anotherCriterion.value > criterion.value) {
                                    return 'mds.error.maxCannotBeSmaller';
                            }
                        }
                        break;
                    case 'mds.field.validation.minValue':
                        anotherCriterion = $scope.findCriterionByName(validationCriteria, 'mds.field.validation.maxValue');

                        if (anotherCriterion !== null && anotherCriterion.enabled && anotherCriterion.value
                            && anotherCriterion.value < criterion.value) {
                                return 'mds.error.minCannotBeBigger';
                        }
                        break;
                    case 'mds.field.validation.maxValue':
                        anotherCriterion = $scope.findCriterionByName(validationCriteria, 'mds.field.validation.minValue');

                        if (anotherCriterion !== null && anotherCriterion.enabled && anotherCriterion.value
                            && anotherCriterion.value > criterion.value) {
                                return 'mds.error.maxCannotBeSmaller';
                        }
                        break;
                }
            }

            return '';
        };

        /* BROWSING FUNCTIONS */

        /**
        * Checks if field is filterable.
        */
        $scope.isFilterable = function(field) {
            if ($scope.filterableTypes.indexOf(field.type.displayName) < 0) {
                return false;
            } else {
                return true;
            }
        };

        /**
        * Function called each time when user changes the checkbox state on 'Browsing settings' view.
        * Responsible for updating the model.
        */
        $scope.onFilterableChange = function(field) {
            var selected = $scope.advancedSettings.browsing.filterableFields.indexOf(field.id);

            draft({
                edit: true,
                values: {
                    path: 'browsing.${0}'.format(selected ? 'addFilterableField' : 'removeFilterableField'),
                    advanced: true,
                    value: [field.id]
                }
            }, function () {
                if(selected) {
                    $scope.advancedSettings.browsing.filterableFields.push(field.id);
                } else {
                    $scope.advancedSettings.browsing.filterableFields.removeObject(field.id);
                }
            });
        };

        /**
        * Callback function called each time when user adds, removes or moves items in 'Displayed Fields' on
        * 'Browsing Settings' view. Responsible for updating the model.
        */
        $scope.onDisplayedChange = function(container) {
            $scope.advancedSettings.browsing.displayedFields = [];

            angular.forEach(container, function(field) {
                $scope.advancedSettings.browsing.displayedFields.push(field.id);
            });

            draft({
                edit: true,
                values: {
                    path: 'browsing.$setDisplayedFields',
                    advanced: true,
                    value: [$scope.advancedSettings.browsing.displayedFields]
                }
            });
        };

        /**
        * Function moving "Fields to display" item up (in model).
        */
        $scope.targetItemMoveUp = function(index) {
            var tmp;
            if (index > 0) {
                tmp = $scope.browsingDisplayed[index];
                $scope.browsingDisplayed[index] = $scope.browsingDisplayed[index - 1];
                $scope.browsingDisplayed[index - 1] = tmp;
            }
        };

        /**
        * Function moving "Fields to display" item down (in model).
        */
        $scope.targetItemMoveDown = function(index) {
            var tmp;
            if (index < $scope.browsingDisplayed.length - 1) {
                tmp = $scope.browsingDisplayed[index + 1];
                $scope.browsingDisplayed[index + 1] = $scope.browsingDisplayed[index];
                $scope.browsingDisplayed[index] = tmp;
            }
        };

        /**
        * Function moving selected "Fields to display" items up (in model).
        */
        $scope.itemsUp = function() {
            var items = $(".connected-list-target.browsing").children(),
                indices = [],
                firstUnselectedIndex = parseInt(items.filter(':not(.selected)').first().attr('item-index'),10),
                selected = {},
                array = [];

            items.filter('.selected').each(function() {
                var item = $(this),
                index =  parseInt($(item).attr('item-index'), 10);
                    // save 'selected' state
                    selected[$scope.browsingDisplayed[index].id] = true;
                    if(firstUnselectedIndex < index) {
                        indices.push(index);
                    }
            });

            angular.forEach(indices, function(index) {
                $scope.targetItemMoveUp(index);
            });

            angular.forEach($scope.browsingDisplayed, function (item) {
                array.push(item.id);
            });

            draft({
                edit: true,
                values: {
                    path: 'browsing.$setDisplayedFields',
                    advanced: true,
                    value: [array]
                }
            }, function () {
                // restore 'selected' state
                $timeout(function() {
                    $(".connected-list-target.browsing").children().each(function(index) {
                        if(selected[$scope.browsingDisplayed[index].id]) {
                            $(this).addClass('selected');
                        }
                    });
                });
            });
        };

        /**
        * Function moving selected "Fields to display" items down (in model).
        */
        $scope.itemsDown = function() {
            var items = $(".connected-list-target.browsing").children(),
                indices = [],
                lastUnselectedIndex = parseInt(items.filter(':not(.selected)').last().attr('item-index'),10),
                selected = {},
                array = [];

            items.filter('.selected').each(function() {
                var item = $(this),
                index =  parseInt($(item).attr('item-index'), 10);
                // save 'selected' state
                selected[$scope.browsingDisplayed[index].id] = true;
                if(lastUnselectedIndex > index) {
                    indices.push(index);
                }
            });

            angular.forEach(indices, function(index) {
                $scope.targetItemMoveUp(index);
            });

            angular.forEach($scope.browsingDisplayed, function (item) {
                array.push(item.id);
            });

            draft({
                edit: true,
                values: {
                    path: 'browsing.$setDisplayedFields',
                    advanced: true,
                    value: [array]
                }
            }, function () {
                angular.forEach(indices.reverse(), function(index) {
                    $scope.targetItemMoveDown(index);
                });
                // restore 'selected' state
                $timeout(function() {
                    $(".connected-list-target.browsing").children().each(function(index) {
                        if(selected[$scope.browsingDisplayed[index].id]) {
                            $(this).addClass('selected');
                        }
                    });
                });
            });
        };

        /**
        * Checks if there are fields allowed to move up in 'Browsing Settings' view.
        */
        $scope.canMoveUp = function() {
            var items = $('.target-item.browsing'),
                wasLastSelected = true,
                ret = false;
            if (items.filter('.selected').size() === 0) {
                return false;
            }
            items.each(function() {
                var isThisSelected = $(this).hasClass('selected');
                if (!wasLastSelected && isThisSelected) {
                    ret = true;
                }
                wasLastSelected = isThisSelected;
            });
            return ret;
        };

        /**
        * Checks if there are fields allowed to move up in 'Browsing Settings' view.
        */
        $scope.canMoveDown = function() {
            var items = $('.target-item.browsing'),
                wasLastSelected = true,
                ret = false;
            if (items.filter('.selected').size() === 0) {
                return false;
            }
            $(items.get().reverse()).each(function() {
                var isThisSelected = $(this).hasClass('selected');
                if (!wasLastSelected && isThisSelected) {
                    ret = true;
                }
                wasLastSelected = isThisSelected;
            });
            return ret;
        };

        /**
        * Checks if there are fields selected to move left in 'Browsing Settings' view.
        */
        $scope.canMoveLeft = function() {
             return $('.target-item.browsing.selected').size() > 0;
        };

        /**
        * Checks if there are fields to move left in 'Browsing Settings' view.
        */
        $scope.canMoveAllLeft = function() {
            return $scope.browsingDisplayed.length > 0;
        };

        /**
        * Checks if there are fields selected to move right in 'Browsing Settings' view.
        */
        $scope.canMoveRight = function() {
             return $('.source-item.browsing.selected').size() > 0;
        };

        /**
        * Checks if there are fields to move right in 'Browsing Settings' view.
        */
        $scope.canMoveAllRight = function() {
            return $scope.browsingAvailable.length > 0;
        };

        /* UTILITY FUNCTIONS */

        /**
        * Find all lookups with given name.
        *
        * @param {string} name This value will be used to find lookups.
        * @param {Array} array Array in which we're looking for name.
        * @return {Array} array of lookups with the given name.
        */
        $scope.findLookupByName = function (name, array) {
            var lookup = find(array, [{ lookup: 'lookupName', value: name}], false);
            return $.isArray(lookup) ? lookup[0] : lookup;
        };

        /**
        * Find validation criterion with given name in field's validation criteria
        * @param {object} list of field's validation criteria
        * @param {string} name of criteria to be found
        * @return {object} found criterion with given name or null if no such criterion exists
        */
        $scope.findCriterionByName = function (validationCriteria, name) {
            var foundCriterion = null;
            angular.forEach(validationCriteria, function (criterion) {
                if (criterion.displayName === name) {
                    foundCriterion = criterion;
                }
            });

            return foundCriterion;
        };

        /**
        * Find unique field with given id.
        *
        * @param {string} id This value will be used to find fields.
        * @return {object} unique field with the given id.
        */
        $scope.findFieldById = function (id) {
            return find($scope.fields, [{ field: 'id', value: id}], true);
        };

        /**
        * Find all fields with given id.
        *
        * @param {string} id This value will be used to find fields.
        * @param {Array} array Array in which we're looking for id.
        * @return {Array} array of fields with the given id.
        */
        $scope.findFieldInArrayById = function (id, array) {
            var field = find(array, [{ field: 'id', value: id}], false);
            return $.isArray(field) ? field[0] : field;
        };

        /**
        * Find all fields with given name.
        *
        * @param {string} name This value will be used to find fields.
        * @return {Array} array of fields with the given name.
        */
        $scope.findFieldsByName = function (name) {
            return find($scope.fields, [{ field: 'basic.name', value: name}], false);
        };

        /**
        * Find validation criterion with given name in field's validation criteria.
        *
        * @param {object} validationCriteria list of field's validation criteria.
        * @param {string} name The name of criteria to be found.
        * @return {object} found criterion with given name or null if no such criterion exists.
        */
        $scope.findCriterionByName = function (validationCriteria, name) {
            return find(validationCriteria, [{ field: 'displayName', value: name}], true);
        };

        /**
        * Find field setting with given name.
        *
        * @param {Array} settings A array of field settings.
        * @param {string} name This value will be used to find setting.
        * @return {object} a single object which represent setting with the given name.
        */
        $scope.findSettingByName = function (settings, name) {
            return find(settings, [{field: 'name', value: name}], true);
        };

        /*
        * Gets type information from TypeDto object.

        * @param {object} typeObject TypeDto object containing type information
        * @return {string} type information taken from parameter object.
        */
        $scope.getTypeSingleClassName = function (type) {
            return type.displayName.substring(type.displayName.lastIndexOf('.') + 1);
        };

        /**
        * Construct appropriate url according with a field type for form used to set correct
        * value of default value property.
        *
        * @param {string} type The type of a field.
        * @return {string} url to appropriate form.
        */
        $scope.loadDefaultValueForm = function (type) {
            var value = $scope.getTypeSingleClassName(type);

            return '../mds/resources/partials/widgets/field-basic-defaultValue-{0}.html'
                .format(value.substring(value.toLowerCase()));
        };

        /**
        * Check if the given number has appropriate precision and scale.
        *
        * @param {number} number The number to validate.
        * @param {object} settings Object with precision and scale properties.
        * @return {boolean} true if number has appropriate precision and scale or it is undefined;
        *                   otherwise false.
        */
        $scope.checkDecimalValue = function (number, settings) {
            var precision = $scope.findSettingByName(settings, 'mds.form.label.precision'),
                scale = $scope.findSettingByName(settings, 'mds.form.label.scale');

            return _.isNumber(number)
                ? validateDecimal(number, precision.value, scale.value)
                : true;
        };

        /**
        * Return message in correct language that inform user the decimal value has incorrect
        * precision and/or scale.
        *
        * @param {Array} settings A array of field settings.
        * @return {string} A appropriate error message.
        */
        $scope.getInvalidDecimalMessage = function (settings) {
            var precision = $scope.findSettingByName(settings, 'mds.form.label.precision'),
                scale = $scope.findSettingByName(settings, 'mds.form.label.scale');

            return $scope.msg('mds.error.incorrectDecimalValue', precision.value, scale.value);
        };

        /**
        * Return available values for combobox field.
        *
        * @param {Array} setting A array of field settings.
        * @return {Array} A array of possible combobox values.
        */
        $scope.getComboboxValues = function (settings) {
            return find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value;
        };

        /**
        * Check that all options in the given setting are valid.
        *
        * @param {object} setting The given setting to check
        * @return {boolean} true if all options are valid; otherwise false.
        */
        $scope.checkOptions = function (setting) {
            var expression = true;

            angular.forEach(setting.options, function (option) {
                switch (option) {
                case 'REQUIRE':
                    expression = expression && $scope.hasValue(setting);
                    break;
                case 'POSITIVE':
                    expression = expression && $scope.hasPositiveValue(setting);
                    break;
                default:
                    break;
                }
            });

            return expression;
        };

        /**
        * Check if the given setting has a option with the given name.
        *
        * @param {object} setting The setting to check.
        * @param {string} name Option name.
        * @return {boolean} true if the setting has a option; otherwise false.
        */
        $scope.hasOption = function (setting, name) {
            return setting.options && $.inArray(name, setting.options) !== -1;
        };

        /**
        * Check if the given setting has a value.
        *
        * @param {object} setting The setting to check.
        * @return {boolean} true if the setting has a value; otherwise false.
        */
        $scope.hasValue = function (setting) {
            return setting
                && !_.isNull(setting.value)
                && !_.isUndefined(setting.value)
                && (_.isArray(setting.value) ? setting.value.length > 0 : true);
        };

        /**
        * Check if the given setting has a positive value.
        *
        * @param {object} setting The setting to check.
        * @return {boolean} true if the setting has a positive value; otherwise false.
        */
        $scope.hasPositiveValue = function (setting) {
            return $scope.hasValue(setting) && _.isNumber(setting.value) && setting.value >= 0;
        };

        /**
        * Check if the given setting has a given type.
        *
        * @param {object} setting The setting to check.
        * @param {string} type The given type.
        * @return {boolean} true if the setting has a given type; otherwise false.
        */
        $scope.hasType = function (setting, type) {
            var fullName = setting.type.typeClass,
                singleName = fullName.substring(fullName.lastIndexOf('.') + 1);

            return _.isEqual(singleName.toLowerCase(), type.toLowerCase());
        };

        /**
        * Set an additional watcher for $scope.selectedEntity. Its role is to get fields related to
        * the entity in situation in which the entity was selected from the entity list.
        */
        $scope.$watch('selectedEntity', function () {
            blockUI();

            if ($scope.selectedEntity && $scope.selectedEntity.id) {
                workInProgress.setActualEntity(Entities, $scope.selectedEntity.id);

                $scope.fields = Entities.getFields({id: $scope.selectedEntity.id}, function () {
                    setAdvancedSettings();
                });

                unblockUI();
            } else {
                workInProgress.setActualEntity(Entities, undefined);

                delete $scope.fields;
                delete $scope.advancedSettings;
                unblockUI();
            }
        });

        /**
        * Set an additional watcher for $scope.newField.type. Its role is to set name for created
        * field using defaultName property from the selected field type but only if a user did not
        * enter name. If field with name equal to value of defaultName property already exists,
        * the unique id will be added to end of created field name.
        */
        $scope.$watch('newField.type', function () {
            var found;

            if (isBlank($scope.newField.name) && $scope.newField.type) {
                found = $scope.findFieldsByName($scope.newField.type.defaultName);

                $scope.newField.name = $scope.newField.type.defaultName;

                if (found.length !== 0) {
                    $scope.newField.name += '-{0}'.format(_.uniqueId());
                }
            }
        });
    });

    /**
    * The DataBrowserCtrl controller is used on the 'Data Browser' view.
    */
    mds.controller('DataBrowserCtrl', function ($scope, $http, Entities,Instances) {
        workInProgress.setActualEntity(Entities, undefined);

        /**
        * An array perisisting currently hidden modules in data browser view
        */
        $scope.hidden = [];

        /**
        * A map containing names of all entities in Seuss, indexed by module names
        */
        $scope.modules = undefined;

        /**
        * This variable is set after user clicks "View" button next to chosen entity
        */
        $scope.selectedEntity = undefined;

        /**
        * Fields that belong to a certain lookup
        */
        $scope.lookupFields = [];

        /**
        * Object that represents selected lookup options
        */
        $scope.lookupBy = {};

        /**
        * This variable is set after user clicks "Add" button next to chosen entity.
        */
        $scope.addedEntity= undefined;

        /**
        * This variable is set after user choose Entity in instance view.
        */
        $scope.selectedInstance = undefined;

        /**
        * This variable is set after user clicks "History" button in entity detail view.
        */
        $scope.instanceId = undefined;

        /**
        * This variable is set after user choose field in history instance view.
        */
        $scope.previousInstance = undefined;

        /**
        * An array of selected instance fields.
        */
        $scope.loadedFields = [];

        /**
        * Initializes a map of all entities in Seuss indexed by module name
        */
        $scope.setEntities = function () {
            blockUI();
            $http.get('../mds/entities/byModule').success(function (data) {
                $scope.modules = data;
                unblockUI();
            });
        };

        /**
        * Sets selected entity by module and entity name
        */
        $scope.addInstance = function(module, entityName) {
            blockUI();
            $scope.addedEntity  = Entities.getEntity({
                param:  module,
                params: entityName},
                function () {
                    $scope.fields = Entities.getFields({id: $scope.addedEntity.id},function () {
                    unblockUI();
                    });
                });
        };

        /**
        * Sets selected entity by module and entity name
        */
        $scope.editInstance = function(id) {
            blockUI();
            $scope.loadedFields = Entities.selectInstance({
                id: $scope.selectedEntity.id,
                 param: id},
                function () {
                    $scope.selectedInstance = id;
                    unblockUI();
                });
        };

        /**
        *  Sets loadedFields from previous instance
        */
        $scope.historyInstance = function(id) {
            blockUI();
            $scope.loadedFields = Instances.getPreviousVersion(
                {id: $scope.selectedInstance,
                param: id},
                function () {
                    $scope.previousInstance = id;
                    unblockUI();
                });
        };

        /**
        * Unselects adding or editing instance to allow user to return to entities list by modules
        */
        $scope.unselectAdd = function() {
                $scope.addedEntity = undefined;
                $scope.selectedInstance = undefined;
                $scope.loadedFields = undefined;
                $scope.historyFields = undefined;
                $scope.unselectEntity();
        };

        /**
        * Find field setting with given name.
        *
        * @param {Array} settings A array of field settings.
        * @param {string} name This value will be used to find setting.
        * @return {object} a single object which represent setting with the given name.
        */
        $scope.findSettingByName = function (settings, name) {
            return find(settings, [{field: 'name', value: name}], true);
        };

        /**
        * Sets selected instance history by id
        */
        $scope.selectInstanceHistory = function (id) {
            blockUI();
            Instances.getHistory({id: id},function () {
                unblockUI();
                $scope.previousInstance = undefined;
                $scope.instanceId = id;
            });
        };

        $scope.backToInstance = function() {
            $scope.unselectInstanceHistory();
            $scope.editInstance($scope.selectedInstance);
        };

        /**
        * Unselects instace history to allow user to return to instance view
        */
        $scope.unselectInstanceHistory = function() {
            // Temporary - should return to instance view
                $scope.instanceId = undefined;
                $scope.previousInstance = undefined;
        };

        /**
        * Sets selected entity by module and entity name
        */
        $scope.selectEntity = function (module, entityName) {
            blockUI();
            $http.get('../mds/entities/getEntity/' + module + '/' + entityName).success(function (data) {
                $scope.selectedEntity = data;
                $scope.fields = Entities.getFields({id: $scope.selectedEntity.id});
                unblockUI();
            });

            setTimeout(function() {
                $scope.entityAdvanced = Entities.getAdvanced({id: $scope.selectedEntity.id});
                $http.get('../mds/entities/'+$scope.selectedEntity.id+'/fields').success(function (data) {
                    $scope.allEntityFields = data;
                });
                $(".multiselect-all input").click();
            }, 1000);
        };

        /**
        * Marks passed lookup as selected. Sets fields that belong to the given lookup and resets lookupBy object
        * used to filter instances by given values
        */
        $scope.selectLookup = function(lookup) {
            var i;

            $scope.selectedLookup = lookup;
            $scope.lookupFields = [];
            $scope.lookupBy = {};

            for(i=0; i<$scope.allEntityFields.length; i+=1) {
                if ($.inArray($scope.allEntityFields[i].id, $scope.selectedLookup.fieldList) !== -1) {
                    $scope.lookupFields.push($scope.allEntityFields[i]);
                }
            }
        };

        /**
        * Depending on the field type, includes proper html file containing visual representation for
        * the object type. Radio input for boolean, select input for list and text input as default one.
        */
        $scope.loadInputForLookupField = function(field) {
            var value = "default";

            if (field.type.typeClass === "java.lang.Boolean") {
                value = "boolean";
            } else if (field.type.typeClass === "java.util.List") {
                value = "list";
            }

            return '../mds/resources/partials/widgets/lookups/field-{0}.html'
                .format(value.substring(value.toLowerCase()));
        };

        /**
        * Hides lookup dialog and sends signal to refresh the grid with new data
        */
        $scope.filterInstancesByLookup = function() {
            $scope.showLookupDialog();
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        /**
        * Removes lookup and resets all fields associated with a lookup
        */
        $scope.removeLookup = function() {
            $scope.lookupBy = {};
            $scope.selectedLookup = undefined;
            $scope.lookupFields = [];
            $scope.filterInstancesByLookup();
        };

        /**
        * Unselects entity to allow user to return to entities list by modules
        */
        $scope.unselectEntity = function () {
            $scope.selectedEntity = undefined;
        };

        /**
        * Exports selected entity's instances to CSV file
        */
        $scope.exportEntityInstances = function() {
            $http.get("../mds/entities/" + $scope.selectedEntity.id + "/exportInstances")
            .success(function (data) {
                 window.location.replace("../mds/entities/" + $scope.selectedEntity.id + "/exportInstances");
            });
        };

        /**
        * Hides/Shows all entities under passed module name
        *
        * @param {string} module  Module name
        */
        $scope.collapse = function (module) {
            if ($.inArray(module, $scope.hidden) !== -1) {
                $scope.hidden.remove($scope.hidden.indexOf(module));
            } else {
                $scope.hidden.push(module);
            }
        };

        /*
        *  Gets field from FieldRecord to set field-edit value
        */
        $scope.getFieldValue = function(name) {
            var myField = false;
            angular.forEach($scope.loadedFields,function(loadedField) {
                        if (_.isEqual(loadedField.displayName.toLowerCase(), name.toLowerCase())) {
                            myField = loadedField;
                            return loadedField;
                        }
            });
            return myField;
        };

        /**
        *  Gets history fields of entity instance
        */
        $scope.getHistory = function() {
           blockUI();
           var data =null;
           data = Instances.getHistory({
                      id:$scope.selectedInstance},
                      function () {
                          unblockUI();
                          $scope.historyFields = data.rows;
                      });
        };

        /*
        * Return string with information about CRUD action
        */
        $scope.getMsg = function(name,field) {
                var answer = "";
                angular.forEach(field.fields,function(row) {
                        if(_.isEqual(name,row.name )) {
                            answer = row.value;
                            return row.value;
                        }
                });
                return answer;
        };

        /**
        * Construct appropriate url according with a field type for form used to set correct
        * value of edit value property.
        */
        $scope.loadEditValueForm = function (field) {
            var value = $scope.getTypeSingleClassName(field.type);
            if ($scope.getFieldValue(field.basic.displayName) || $scope.previousInstance) {
              return '../mds/resources/partials/widgets/field-edit-Value-{0}.html'
                              .format(value.substring(value.toLowerCase()));
            }
            else {
              return '../mds/resources/partials/widgets/field-basic-defaultValue-{0}.html'
                              .format(value.substring(value.toLowerCase()));
            }
        };

        /*
        * Gets type information from TypeDto object.
        */
        $scope.getTypeSingleClassName = function (type) {
            return type.displayName.substring(type.displayName.lastIndexOf('.') + 1);
        };

        /**
        * Return available values for combobox field.
        *
        * @param {Array} setting A array of field settings.
        * @return {Array} A array of possible combobox values.
        */
        $scope.getComboboxValues = function (settings) {
            return find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value;
        };

        /**
        * Checks if entities belonging to certain module are currently visible
        *
        * @param {string} module  Module name
        * @return {boolean} true if entities for given module name are visible, false otherwise
        */
        $scope.visible = function (module) {
            return $.inArray(module, $scope.hidden) !== -1 ? false : true;
        };

        /**
        * Check if the given number has appropriate precision and scale.
        *
        * @param {number} number The number to validate.
        * @param {object} settings Object with precision and scale properties.
        * @return {boolean} true if number has appropriate precision and scale or it is undefined;
        *                   otherwise false.
        */
        $scope.checkDecimalValue = function (number, settings) {
            var precision = $scope.findSettingByName(settings, 'mds.form.label.precision'),
                scale = $scope.findSettingByName(settings, 'mds.form.label.scale');

            return _.isNumber(number)
                ? validateDecimal(number, precision.value, scale.value)
                : true;
        };

        /**
        * Return message in correct language that inform user the decimal value has incorrect
        * precision and/or scale.
        *
        * @param {Array} settings A array of field settings.
        * @return {string} A appropriate error message.
        */
        $scope.getInvalidDecimalMessage = function (settings) {
            var precision = $scope.findSettingByName(settings, 'mds.form.label.precision'),
                scale = $scope.findSettingByName(settings, 'mds.form.label.scale');

            return $scope.msg('mds.error.incorrectDecimalValue', precision.value, scale.value);
        };

        $scope.arrow = function (module) {
            return $scope.visible(module) ? "icon-chevron-down" : "icon-chevron-right";
        };

        /**
        * Shows/Hides lookup dialog
        */
        $scope.showLookupDialog = function() {
            $("#lookup-dialog").toggle();
        };

        /**
        * Handles hiding lookup dialog while clicking outside the dialog
        */
        $(document).mouseup(function (e) {
            var container = $("#lookup-dialog"),
            button = $("#lookupDialogButton");

            if (!container.is(e.target) && container.has(e.target).length === 0 &&
                !button.is(e.target) && button.has(e.target).length === 0 && container.is(":visible")) {
                    $scope.showLookupDialog();
            }
        });
    });

    /**
    * The SettingsCtrl controller is used on the 'Settings' view.
    */
    mds.controller('SettingsCtrl', function ($scope, Entities, MdsSettings) {
        workInProgress.setActualEntity(Entities, undefined);

        var result = [];
        $scope.settings = MdsSettings.getSettings();
        $scope.timeUnits = [
            $scope.msg('mds.dataRetention.hours'),
            $scope.msg('mds.dataRetention.days'),
            $scope.msg('mds.dataRetention.weeks'),
            $scope.msg('mds.dataRetention.months'),
            $scope.msg('mds.dataRetention.years')];
        $scope.entities = Entities.query();

        /**
        * This function is used to get entity metadata from controller and convert it for further usage
        */
        $scope.getEntities = function () {
            if (result.length === 0) {
                angular.forEach($scope.entities, function (entity) {
                    var module = entity.module === null ? "Seuss" : entity.module.replace(/ /g, ''),
                        found = false;
                    angular.forEach(result, function (mod) {
                        if (module === mod.name) {
                            mod.entities.push(entity.name + (entity.namespace !== null ? " (" + entity.namespace + ")" : ""));
                            found = true;
                        }
                    });
                    if (!found) {
                        result.push({name: module, entities: [entity.name + (entity.namespace !== null ? " (" + entity.namespace + ")" : "")]});
                    }
                });
            }
            return result;
        };

        /**
        * This function checking if input and select fields for time selection should be disabled.
        * They are only enabled when deleting is set to "trash" and checkbox is selected
        */
        $scope.checkTimeSelectDisable = function () {
            return $scope.settings.deleteMode !== "trash" || !$scope.settings.emptyTrash;
        };

        /**
        * This function checking if checkbox in UI should be disabled. It is should be enabled when deleting is set to "trash"
        */
        $scope.checkCheckboxDisable = function () {
            return $scope.settings.deleteMode !== "trash";
        };

        /**
        * This function it is called when we change the radio. It's used for dynamically changing availability to fields
        */
        $scope.checkDisable = function () {
            $scope.checkTimeSelectDisable();
            $scope.checkCheckboxDisable();
        };

        /**
        * Get imported file and sends it to controller.
        */
        $scope.importFile = function () {
            MdsSettings.importFile($("#importFile")[0].files[0]);
        };

        /**
        * Sending information what entities we want to export to controller
        */
        $scope.exportData = function () {
            MdsSettings.exportData($("table")[0]);
        };

        /**
        * Sends new settings to controller
        */
        $scope.saveSettings = function () {
            MdsSettings.saveSettings({}, $scope.settings,
                function () {
                    handleResponse('mds.success', 'mds.dataRetention.success', '');
                }, function (response) {
                    handleResponse('mds.error', 'mds.dataRetention.error', response);
                });
        };

        /**
        * Checks all schema checkboxes and setting the general schema checkbox state
        */
        $scope.updateAllSchemasCheckbox = function () {
            if ($('input[id^="schema"]:checked').length >= $('input[id^="schema"]').length - 1) {
                $("#schema-all").prop("checked", true);
                $("#schema-all").prop("indeterminate", false);
            } else if ($('input[id^="schema"]:checked').length === 0) {
                $("#schema-all").prop("checked", false);
                $("#schema-all").prop("indeterminate", false);
            } else {
                $("#schema-all").prop("checked", false);
                $("#schema-all").prop("indeterminate", true);
            }
        };

        /**
        * Checks all data checkboxes and setting the general data checkbox state
        */
        $scope.updateAllDataCheckbox = function () {
            if ($('input[id^="data"]:checked').length >= $('input[id^="data"]').length - 1) {
                $("#data-all").prop("checked", true);
                $("#data-all").prop("indeterminate", false);
            } else if ($('input[id^="data"]:checked').length === 0) {
                $("#data-all").prop("checked", false);
                $("#data-all").prop("indeterminate", false);
            } else {
                $("#data-all").prop("checked", false);
                $("#data-all").prop("indeterminate", true);
            }
        };

        /**
        * Checks all schema checkboxes in the module and setting the module schema checkbox state
        */
        $scope.updateModuleSchemaCheckbox = function (module) {
            if ($('input[id^="schema-' + module + '-"]:checked').length === $('input[id^="schema-' + module + '-"]').length) {
                $("#schema-" + module).prop("checked", true);
                $("#schema-" + module).prop("indeterminate", false);
            } else if ($('input[id^="schema-' + module + '-"]:checked').length === 0) {
                $("#schema-" + module).prop("checked", false);
                $("#schema-" + module).prop("indeterminate", false);
            } else {
                $("#schema-" + module).prop("checked", false);
                $("#schema-" + module).prop("indeterminate", true);
            }
        };

        /**
        * Checks all data checkboxes in the module and setting the module data checkbox state
        */
        $scope.updateModuleDataCheckbox = function (module) {
            if ($('input[id^="data-' + module + '-"]:checked').length === $('input[id^="data-' + module + '-"]').length) {
                $("#data-" + module).prop("checked", true);
                $("#data-" + module).prop("indeterminate", false);
            } else if ($('input[id^="data-' + module + '-"]:checked').length === 0) {
                $("#data-" + module).prop("checked", false);
                $("#data-" + module).prop("indeterminate", false);
            } else {
                $("#data-" + module).prop("checked", false);
                $("#data-" + module).prop("indeterminate", true);
            }
        };

        /**
        * Called when we change state of "Schema" checkbox on the top of the table
        * When checked: select all schema checkboxes in the table
        * When unchecked: deselect all schema and data checkboxes
        */
        $scope.checkAllSchemas = function () {
            $('input[id^="schema"]').prop("indeterminate", false);
            if ($("#schema-all")[0].checked) {
                $('input[id^="schema"]').prop("checked", true);
            } else {
                $('input[id^="data"]').prop("indeterminate", false);
                $('input[id^="schema"]').prop("checked", false);
                $('input[id^="data"]').prop("checked", false);
            }
        };

        /**
        * Called when we change state of "Data" checkbox on the top of the table
        * When checked: select all schema and data checkboxes in the table
        * When unchecked: deselect all data checkboxes
        */
        $scope.checkAllData = function () {
            $('input[id^="schema"]').prop("indeterminate", false);
            if ($("#data-all")[0].checked) {
                $('input[id^="data"]').prop("indeterminate", false);
                $('input[id^="data"]').prop("checked", true);
                $('input[id^="schema"]').prop("checked", true);
            } else {
                $('input[id^="data"]').prop("checked", false);
            }
        };

        /**
         * Called when we change state of "Schema" checkbox on the module header
         * When checked: select all schema checkboxes in the module section
         * When unchecked: deselect all schema and data checkboxes in the module section
         */
        $scope.checkModuleSchemas = function (id) {
            if ($("#schema-" + id.name)[0].checked) {
                $('input[id^="schema-' + id.name + '"]').prop("checked", true);
            } else {
                $('input[id^="schema-' + id.name + '"]').prop("checked", false);
                $('input[id^="data-' + id.name + '"]').prop("checked", false);
            }

            $scope.updateModuleSchemaCheckbox(id.name);
            $scope.updateModuleDataCheckbox(id.name);

            $scope.updateAllSchemasCheckbox();
            $scope.updateAllDataCheckbox();
        };

        /**
        * Called when we change state of "Data" checkbox on the module header
        * When checked: select all schema and data checkboxes in the module section
        * When unchecked: deselect all data checkboxes in the module section
        */
        $scope.checkModuleData = function (id) {
            if ($("#data-" + id.name)[0].checked) {
                $('input[id^="data-' + id.name + '"]').prop("checked", true);
                $('input[id^="schema-' + id.name + '"]').prop("checked", true);
            } else {
                $('input[id^="data-' + id.name + '"]').prop("checked", false);
            }

            $scope.updateModuleSchemaCheckbox(id.name);
            $scope.updateModuleDataCheckbox(id.name);

            $scope.updateAllSchemasCheckbox();
            $scope.updateAllDataCheckbox();
        };

        /**
        * Called when we change state of "Data" checkbox in the entity row
        * When checked: select schema checkbox in the entity row
        */
        $scope.checkSchema = function (id, entity) {
            var name = id.name + '-' + entity;
            if ($('input[id^="data-' + name + '"]')[0].checked) {
                $('input[id^="schema-' + name + '"]').prop("checked", true);
            }

            $scope.updateModuleSchemaCheckbox(id.name);
            $scope.updateModuleDataCheckbox(id.name);

            $scope.updateAllSchemasCheckbox();
            $scope.updateAllDataCheckbox();
        };

        /**
        * Called when we change state of "Schema" checkbox in the entity row
        * When unchecked: deselect data checkbox in the entity row
        */
        $scope.uncheckData = function (id, entity) {
            var name = id.name + '-' + entity;
            if (!$('input[id^="schema-' + name + '"]')[0].checked) {
                $('input[id^="data-' + name + '"]').prop("checked", false);
            }

            $scope.updateModuleSchemaCheckbox(id.name);
            $scope.updateModuleDataCheckbox(id.name);

            $scope.updateAllSchemasCheckbox();
            $scope.updateAllDataCheckbox();
        };

        /**
        * Hiding and collapsing module entities and changing arrow icon
        * after clicking on arrow next to module name.
        */
        $scope.hideModule = function (id) {
            if ($("." + id.name + ":hidden").length > 0) {
                $("." + id.name).show("slow");
                $("#" + id.name + "-arrow").addClass("icon-caret-down");
                $("#" + id.name + "-arrow").removeClass("icon-caret-right");
            } else {
                $("." + id.name).hide("slow");
                $("#" + id.name + "-arrow").addClass("icon-caret-right");
                $("#" + id.name + "-arrow").removeClass("icon-caret-down");
            }
        };
    });
}());
