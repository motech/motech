(function () {

    'use strict';

    var mds = angular.module('mds');

    /**
    * The SchemaEditorCtrl controller is used on the 'Schema Editor' view.
    */
    mds.controller('SchemaEditorCtrl', function ($scope, $http, Entities, Fields) {
        /**
        * This function is used to set fields array. If fields are properly taken from server,
        * the related $scope fields will be also set.
        */
        var setFields = function () {
            $scope.fields = Fields.query({entityId: $scope.selectedEntity.id}, function () {
                $scope.originalFields = cloneArray($scope.fields);
                $scope.toRemove = [];

                unblockUI();
            });
        };

        /**
        * The $scope.selectedEntity contains selected entity. By default no entity is selected.
        */
        $scope.selectedEntity = null;

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
        * The $scope.SELECT_ENTITY_CONFIG contains configuration for selecting entity tag on UI.
        */
        $scope.SELECT_ENTITY_CONFIG = {
            ajax: {
                url: '../mds/entities',
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

        /* ~~~~~ FIELD FUNCTIONS ~~~~~ */

        /**
        * Create new field and add it to an entity schema. If displayName, name or type was not
        * set, error message will be shown and a field will not be created. The additional message
        * will be shown if a field name is not unique.
        */
        $scope.createField = function () {
            var selector, validate;

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
                    basic: {
                        displayName: $scope.newField.displayName,
                        name: $scope.newField.name
                    }
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
        * Validate all information inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all information inside the field are correct; otherwise false.
        */
        $scope.validateField = function (field) {
            return $scope.validateFieldBasic(field) && $scope.validateFieldSettings(field);
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
        * Find out if user made changes in field definitions.
        *
        * @return {boolean} true if there are changes; otherwise false.
        */
        $scope.changed = function () {
            var fields = $scope.fields !== undefined,
                original = $scope.originalFields !== undefined,
                clone = {
                    fields: fields ? cloneArray($scope.fields) : [],
                    original: original ? cloneArray($scope.originalFields) : []
                };

            angular.forEach(clone.fields, function (obj) {
                delete obj.$$hashKey;
            });

            angular.forEach(clone.original, function (obj) {
                delete obj.$$hashKey;
            });

            return fields && original
                ? !arraysEqual(clone.fields, clone.original)
                : (fields && !original) || (!fields && original);
        };

        /**
        * Construct appropriate url according with a field type for form used to set correct
        * value of default value property.
        *
        * @param {string} type The type of a field.
        * @return {string} url to appropriate form.
        */
        $scope.loadDefaultValueForm = function (type) {
            var value = type.displayName.substring(type.displayName.lastIndexOf('.'));

            return '../mds/resources/partials/widgets/field-basic-defaultValue-{0}.html'
                .format(value.substring(value.lastIndexOf('.') + 1).toLowerCase());
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
    mds.controller('SettingsCtrl', function ($scope) {});



}());
