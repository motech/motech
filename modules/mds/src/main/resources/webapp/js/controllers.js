(function () {

    'use strict';

    var mds = angular.module('mds');

    /**
    * The SchemaEditorCtrl controller is used on the 'Schema Editor' view.
    */
    mds.controller('SchemaEditorCtrl', function ($scope, $http, Entities, Fields, FieldsValidation) {
        var setFields, setAdvancedSettings, setMetadata;

        /**
        * This function is used to set fields array. If fields are properly taken from server,
        * the related $scope fields will be also set.
        */
        setFields = function () {
            $scope.fields = Fields.query({entityId: $scope.selectedEntity.id}, function () {
                $scope.originalFields = cloneArray($scope.fields);
                $scope.toRemove = [];
                setMetadata($scope.selectedEntity.id);

                unblockUI();
            });
        };

        /**
        * This function is used to get entity metadata from controller and convert it for further usage.
        */
        setMetadata = function (id) {
            $scope.selectedEntityMetadata = [];
            $.each($scope.fields, function(inKey, field) {
                if (typeof field.metadata !== "undefined" && field.metadata !== null) {
                    $.each(field.metadata, function(valueKey, valueValue) {
                        if (typeof $scope.selectedEntityMetadata[inKey] === "undefined") {
                            $scope.selectedEntityMetadata[inKey] = [];
                        }
                        $scope.selectedEntityMetadata[inKey].push({
                            key: valueKey,
                            value: valueValue
                        });
                    });
                }
            });

            $scope.originalSelectedEntityMetadata = [];

            angular.forEach($scope.flattenArray($scope.selectedEntityMetadata), function (object) {
                var newObj = $.extend(true, {}, object);

                $scope.originalSelectedEntityMetadata.push(newObj);
            });
        };

        /**
        * This function is used to set advanced settings. If settings is properly taken from server,
        * the related $scope fields will be also set.
        */
        setAdvancedSettings = function () {
            $scope.advancedSettings = Entities.getAdvanced({id: $scope.selectedEntity.id},
                function () {
                    $scope.originalAdvancedSettings = cloneObj($scope.advancedSettings);
                    $scope.originalAdvancedSettings = cloneObj($scope.advancedSettings);

                    if (!_.isNull($scope.advancedSettings) && !_.isUndefined($scope.advancedSettings) &&
                        $scope.advancedSettings.indexes.size > 0) {

                        $scope.activeIndex = 0;
                    } else {
                        $scope.activeIndex = -1;
                    }

                    unblockUI();
                }
            );
        };

        /**
        * The $scope.selectedEntityMetadata contains metadata for selected entity.
        */
        $scope.selectedEntityMetadata = [];

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
        * The $scope.originalAdvancedSettings constains copy of advanced settings. There is used to
        * check if a user made some changes in advanced settings.
        */
        $scope.originalAdvancedSettings = undefined;

        /**
        * The $scope.fields contains entity fields. By default there are no fields.
        */
        $scope.fields = undefined;

        /**
        * The $scope.originalFields contains copy of entity fields. There are used to check if
        * a user made some changes in fields.
        */
        $scope.originalFields = undefined;

        /**
        * The $scope.toRemove contains entity fields that should be removed when a user wants to
        * save changes. The array contains only the existing fields. By default there are no fields
        * to remove.
        */
        $scope.toRemove = undefined;

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

        /**
        * The $scope.lookup persists currently active (selected) index
        */
        $scope.lookup = undefined;

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
        $scope.deleteEntity = function() {
            if ($scope.selectedEntity !== null) {
                 Entities.remove({id:$scope.selectedEntity.id}, function () {
                     $scope.selectedEntity = null;
                     handleResponse('mds.success', 'mds.delete.success', '');
                 }, function (response) {
                     handleResponse('mds.error', 'mds.error.cantDeleteEntity', response);
                 });
            }
        };

        /* ~~~~~ METADATA FUNCTIONS ~~~~~ */

        /**
        * Adds new key/value pair.
        */
        $scope.addMetadata = function (index) {
            if (typeof $scope.selectedEntityMetadata[index] === "undefined") {
                $scope.selectedEntityMetadata[index] = [{
                    key: "",
                    value: ""
                }];
            } else {
                $scope.selectedEntityMetadata[index].push({
                    key: "",
                    value: ""
                });
            }
        };

        /**
        * Removes selected key/value pair.
        */
        $scope.removeMetadata = function (parentIndex, index) {
            $scope.selectedEntityMetadata[parentIndex].splice(index,1);
        };

        /**
        * Converts metadata into controller format and sends it.
        */
        $scope.saveMetadata = function () {
            angular.forEach($scope.fields, function (field, idx) {
                var metadata = {};
                if (typeof $scope.selectedEntityMetadata[idx] !== "undefined") {
                    $.each($scope.selectedEntityMetadata[idx], function(inKey, inValue) {
                        metadata[inValue.key] = inValue.value;
                    });
                    $scope.fields[idx].metadata = metadata;
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
            var validate, type, selector;

            $scope.tryToCreate = true;
            validate = $scope.newField.type
                && $scope.newField.displayName
                && $scope.newField.name
                && $scope.findFieldsByName($scope.newField.name).length === 0;

            if (validate) {
                $scope.fields.push({
                    entityId: $scope.selectedEntity.id,
                    type: $scope.newField.type.type,
                    settings: $scope.newField.type.settings,
                    validation: $scope.newField.validation,
                    basic: {
                        displayName: $scope.newField.displayName,
                        name: $scope.newField.name
                    },
                    metadata: {}
                });

                selector = '#show-field-details-{0}'.format($scope.fields.length - 1);
                $scope.newField = {};
                angular.element('#newField').select2('val', null);
                $scope.tryToCreate = false;

                angular.element(selector).livequery(function () {
                    var elem = angular.element(selector);

                    elem.click();
                    elem.expire();
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
                        $scope.fields.removeObject(field);

                        // add only a existing field
                        if (field.id) {
                            $scope.toRemove.push(field);
                        }
                    });
                }
            });
        };

        /**
        * Abandon all changes made on an entity schema.
        */
        $scope.abandonChanges = function () {
            blockUI();
            setFields();
            setAdvancedSettings();
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
        * Check if the given metadata key/value pair is unique.
        *
        * @param {string} key metadata key to check..
        * @return {boolean} true if the given key is unique; otherwise false.
        */
        $scope.uniqueMetadataKey = function (key) {
            if (key.key !== "" && $scope.findMetadataByKey(key).length > 1) {
                return false;
            } else {
                return true;
            }
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
        * Check if a user can save field definitions to database.
        *
        * @return {boolean} true if field definitions are correct; otherwise false.
        */
        $scope.canSaveChanges = function () {
            var expression = true;

            angular.forEach($scope.fields, function (field) {
                expression = expression && $scope.validateField(field);
            });

            angular.forEach($scope.flattenArray($scope.selectedEntityMetadata), function (metadata) {
                expression = expression && $scope.uniqueMetadataKey(metadata);
            });

            angular.forEach($scope.advancedSettings.indexes, function(index) {
                expression = expression && index.lookupName !== undefined && index.lookupName.length !== 0;
            });

            return expression;
        };

        /**
        * Save all changes made on an entity schema. Firstly this method tries to save fields to
        * database one by one. Next the method tries to delete existing fields from database.
        */
        $scope.saveChanges = function () {
            var save = [],
                toSave = $scope.fields.length,
                toRemove = $scope.toRemove.length,
                total = toSave + toRemove,
                setSave = function (idx) {
                    save[idx] = true;

                    if (!_.contains(save, false)) {
                        // all fields were saved and/or deleted
                        setFields();
                    }
                };

            _.times(total, function (idx) {
                save[idx] = false;
            });

            blockUI();

            $scope.saveMetadata();

            angular.forEach($scope.fields, function (field, idx) {
                Fields.save({entityId: $scope.selectedEntity.id}, field, function () {
                    setSave(idx);
                }, function (response) {
                    handleResponse('mds.error', 'mds.error.cantSaveField', response);
                });
            });

            angular.forEach($scope.toRemove, function (field, idx) {
                Fields.remove({entityId: $scope.selectedEntity.id}, field, function () {
                    setSave(toSave + idx);
                }, function (response) {
                    handleResponse('mds.error', 'mds.error.cantRemoveField', response);
                });
            });

            Entities.saveAdvanced({id: $scope.selectedEntity.id}, $scope.advancedSettings,
                function () {
                    blockUI();

                    setAdvancedSettings();
                }
            );
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

                if (idx > -1) {
                    $scope.advancedSettings.tracking.fields.remove(idx);
                } else {
                    $scope.advancedSettings.tracking.fields.push(field.id);
                }
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

                if (idx > -1) {
                    $scope.advancedSettings.tracking.actions.remove(idx);
                } else {
                    $scope.advancedSettings.tracking.actions.push(action);
                }
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

            $scope.advancedSettings.indexes.push(newLookup);
            $scope.setActiveIndex(newLookup);
        };

        /**
        * Specifies, whether a certain index is a currently active one
        *
        * @param index An index object to check
        * @return {boolean} True, if passed index is the active one. False otherwise.
        */
        $scope.isActiveIndex = function(index) {
            return $scope.lookup === index ? true : false;
        };

        /**
        * Sets certain index as the currently active one
        *
        * @param index An index object to set active
        */
        $scope.setActiveIndex = function(index) {
            $scope.activeIndex = $scope.advancedSettings.indexes.indexOf(index);
            $scope.lookup = $scope.advancedSettings.indexes[$scope.activeIndex];
            $scope.setAvailableFields();
        };

        /**
        * Removes currently actve index
        */
        $scope.deleteLookup = function() {
            $scope.advancedSettings.indexes.remove($scope.activeIndex);
            $scope.setActiveIndex(-1);
        };

        /**
        * Adds new lookup field to the currently active index
        */
        $scope.addLookupField = function() {
            var newField = {
                displayName: "",
                name: "",
                required: false,
                defaultValue: "",
                tooltip: ""
            };
            $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.push(newField);
        };

        /**
        * Handles field selection. When clicking on one of the available fields from dropdown list,
        * selected field is added or replaced on the list of selected fields of the currently active index
        *
        * @param oldField Previously selected field
        * @param field Selected field
        */
        $scope.selectField = function(oldField, field) {
            var selectedIndex;

            selectedIndex = $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.indexOf(oldField);
            $scope.advancedSettings.indexes[$scope.activeIndex].fieldList[selectedIndex] = field.basic;
            $scope.setAvailableFields();
        };

        /**
        * Refreshes available fields for the currently active index. A field is considered available
        * if it is not yet present in the lookup field list of the currently active index.
        */
        $scope.setAvailableFields = function() {
            if ($scope.activeIndex !== -1) {
                var availableFields = [],
                selectedFields = $scope.advancedSettings.indexes[$scope.activeIndex].fieldList,
                i;

                for (i = 0; i < $scope.fields.length; i += 1) {
                    if (find(selectedFields, [{ field: 'name', value: $scope.fields[i].basic.name}], false ).length === 0) {
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
        $scope.removeLookupField = function(field) {
            $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.remove(
                $scope.advancedSettings.indexes[$scope.activeIndex].fieldList.indexOf(field)
            );
            $scope.setAvailableFields();
        };

        /**
        * Checks if user can still add more lookup fields.
        *
        * @return {boolean} False if all available fields have already been selected
        * or the amount of added fields is equal to amount of all fields for that object. True otherwise.
        */
        $scope.canAddLookupFields = function() {

            return ($scope.activeIndex !== -1 && $scope.availableFields.length>0 &&
                    $scope.lookup.fieldList.length < $scope.fields.length) ?
                true : false;
        };


        /* VALIDATION FUNCTIONS */

        /**
        * Validate the validation information ('Validation' tab on UI) inside the given field.
        *
        * @param {object} field to validate.
        * @return {boolean} true if all validation information inside the field are correct;
        *                   otherwise false.
        */
        $scope.validateFieldValidation = function (field) {
            var expression = true;

            if (field.validation) {
                angular.forEach(field.validation.validationCriteria, function (criterion) {
                    if ($scope.validateCriterion(criterion, field.validation.validationCriteria)) {
                        expression = false;
                    }
                });
            }

            return expression;
        };

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


        /* UTILITY FUNCTIONS */

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
        * Flatten array of arrays of objects into simple array of objects and removes hashkeys from them.
        *
        * @return {Array} array of objects.
        */
        $scope.flattenArray = function (array) {
            var objects = [];
            angular.forEach(array, function (objArrays) {
                if ($.isArray(objArrays)) {
                    angular.forEach(objArrays, function (value) {
                        delete value.$$hashKey;
                        objects.push(value);
                    });
                } else {
                   angular.forEach(objArrays, function (obj) {
                        angular.forEach(obj, function (value) {
                            delete value.$$hashKey;
                            objects.push(value);
                        });
                   });
                }
            });
            return objects;
        };

        /**
        * Find all metadata values for given key.
        *
        * @param {string} name Key used to find values.
        * @return {Array} array of fields with the given key.
        */
        $scope.findMetadataByKey = function (key) {
            var objects = [];
            return find($scope.flattenArray($scope.selectedEntityMetadata), [{field: 'key', value: key.key}]);
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
        * Gets type information from TypeDto object
        * @param {object} TypeDto object containing type information
        * @return {string} type information taken from parameter object
        */
        $scope.getTypeFromTypeObject = function(typeObject) {
            return typeObject.displayName.substring(typeObject.displayName.lastIndexOf('.') + 1);
        };

        /**
        * Checks if given field contains not empty validation object
        * @param {object} field - field to be checked for including validation object
        * @return {boolean} true if given field contains not empty validation
        */
        $scope.containsValidation = function(field) {
            return field.validation && field.validation.validationCriteria.length;
        };

        /**
        * Find out if user made changes in field definitions or advanced settings.
        *
        * @return {boolean} true if there are changes; otherwise false.
        */
        $scope.changed = function () {
            var fields = $scope.fields !== undefined,
                original = $scope.originalFields !== undefined,
                clone = {
                    fields: fields ? cloneArray($scope.fields) : [],
                    original: original ? cloneArray($scope.originalFields) : []
                },
                validationClone = {
                    fields: [],
                    original: [],
                    flattedFields: [],
                    flattedOriginal: []
                },
                metadataClone = $scope.flattenArray($scope.selectedEntityMetadata),
                changedFields,
                changedAdvancedSettings,
                changedMetadata;

            angular.forEach(clone.fields, function (obj) {
                delete obj.$$hashKey;
                validationClone.fields.push(obj.validation.validationCriteria);
                obj.validation.validationCriteria = undefined;
            });

            angular.forEach(clone.original, function (obj) {
                delete obj.$$hashKey;
                validationClone.original.push(obj.validation.validationCriteria);
                obj.validation.validationCriteria = undefined;
            });

            validationClone.flattedFields = $scope.flattenArray(validationClone.fields);
            validationClone.flattedOriginal = $scope.flattenArray(validationClone.original);

            angular.forEach(validationClone.flattedFields, function (obj, index) {
                if (obj.enabled === false && index < validationClone.flattedOriginal.length) {
                    obj.value = validationClone.flattedOriginal[index].value;
                }
            });

            changedFields = fields && original
                ? (!arraysEqual(clone.fields, clone.original) ||
                !arraysEqual(validationClone.flattedFields, validationClone.flattedOriginal))
                : (fields && !original) || (!fields && original);

            angular.forEach(clone.fields, function (obj, index) {
                obj.validation.validationCriteria = validationClone.fields[index];
            });

            angular.forEach(clone.original, function (obj, index) {
                obj.validation.validationCriteria = validationClone.original[index];
            });

            angular.forEach($scope.advancedSettings.indexes, function(obj) {
                delete obj.$$hashKey;
            });

            changedAdvancedSettings = ! (_.isEqual(
                $scope.advancedSettings.tracking, $scope.originalAdvancedSettings.tracking
            ) && arraysEqual(
                $scope.advancedSettings.indexes, $scope.originalAdvancedSettings.indexes
            )
            );

            angular.forEach(metadataClone, function (obj) {
                delete obj.$$hashKey;
            });

            changedMetadata = !arraysEqual(
                metadataClone, $scope.originalSelectedEntityMetadata
            );

            return changedFields || changedAdvancedSettings || changedMetadata;
        };

        /**
        * Construct appropriate url according with a field type for form used to set correct
        * value of default value property.
        *
        * @param {string} type The type of a field.
        * @return {string} url to appropriate form.
        */
        $scope.loadDefaultValueForm = function (type) {
            var value = $scope.getTypeFromTypeObject(type);

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
            return setting.value && !_.isNull(setting.value) && !_.isUndefined(setting.value);
        };

        /**
        * Check if the given setting has a positive value.
        *
        * @param {object} setting The setting to check.
        * @return {boolean} true if the setting has a positive value; otherwise false.
        */
        $scope.hasPositiveValue = function (setting) {
            return $scope.hasValue(setting.value)
                && _.isNumber(setting.value)
                && setting.value >= 0;
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
                singleName = fullName.substring(fullName.lastIndexOf('.'));

            return _.isEqual(singleName.toLowerCase(), type.toLowerCase());
        };

        /**
        * Set an additional watcher for $scope.selectedEntity. Its role is to get fields related to
        * the entity in situation in which the entity was selected from the entity list.
        */
        $scope.$watch('selectedEntity', function () {
            blockUI();

            if ($scope.selectedEntity && $scope.selectedEntity.id) {
                setFields();
                setAdvancedSettings();
            } else {
                delete $scope.fields;
                delete $scope.originalFields;
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
            var found, type, obj;

            if (isBlank($scope.newField.name) && $scope.newField.type) {
                found = $scope.findFieldsByName($scope.newField.type.defaultName);

                $scope.newField.name = $scope.newField.type.defaultName;

                if (found.length !== 0) {
                    $scope.newField.name += '-{0}'.format(_.uniqueId());
                }
            }

            if ($scope.newField.type) {
                type = $scope.getTypeFromTypeObject($scope.newField.type.type);
                $scope.newField.validation = FieldsValidation.getForType({type: type});
            }
        });
    });

    /**
    * The AdvancedObjectSettingsCtrl controller is used on 'Schema Editor/Data Browsing' view.
    */
    mds.controller('AdvancedObjectSettingsCtrl', function($scope) {
        $scope.onAdvancedClose = function() {
            var modal = angular.element('#advancedObjectSettingsModal');

            modal.modal('hide');
        };
    });

    /**
    * The DataBrowserCtrl controller is used on the 'Data Browser' view.
    */
    mds.controller('DataBrowserCtrl', function ($scope) {});

    /**
    * The SettingsCtrl controller is used on the 'Settings' view.
    */
    mds.controller('SettingsCtrl', function ($scope, Entities, MdsSettings) {
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
                }
            );
        };

        /**
        * Called when we change state of "Schema" checkbox on the top of the table
        * When checked: select all schema checkboxes in the table
        * When unchecked: deselect all schema and data checkboxes
        */
        $scope.checkAllSchemas = function () {
            if ($("#schema-all")[0].checked) {
                $('input[id^="schema"]').prop("checked", true);
            } else {
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
            if ($("#data-all")[0].checked) {
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
        };

        /**
        * Called when we change state of "Data" checkbox in the entity row
        * When checked: select schema checkbox in the entity row
        */
        $scope.checkSchema = function (id) {
            if ($('input[id^="data-' + id.name + '"]')[0].checked) {
                $('input[id^="schema-' + id.name + '"]').prop("checked", true);
            }
        };

        /**
        * Called when we change state of "Schema" checkbox in the entity row
        * When unchecked: deselect data checkbox in the entity row
        */
        $scope.uncheckData = function (id, entity) {
            var name = id.name + entity;
            if (!$('input[id^="schema-' + name + '"]')[0].checked) {
                $('input[id^="data-' + name + '"]').prop("checked", false);
            }
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
