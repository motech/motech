(function () {

    'use strict';

    /**
    * Checks if the given entity field has a metadata with the given key and value.
    *
    * @param {Object} instance of entity field
    * @param {string} metadata key related with the value
    * @param {string} metadata value related with the key
    * @return {boolean} true if field has a metadata with the given key and value; otherwise
    *                   false
    */
    function hasMetadata(field, key, value) {
        var found, i;

        if (field.metadata) {
            for (i = 0; i < field.metadata.length; i += 1) {
                if (_.isEqual(field.metadata[i].key, key)) {
                    found = field.metadata[i];
                }
            }
        }

        return found && _.isEqual(found.value, value);
    }

    var controllers = angular.module('data-services.controllers', []).filter('orderObj', function () {
            return function (obj) {
                if (!obj) {
                    return obj;
                }
                var insensitive = function (s1, s2) {
                    var s1Lower = s1.toLowerCase(), s2Lower = s2.toLowerCase();
                    return s1Lower > s2Lower? 1 : (s1Lower < s2Lower? -1 : 0);
                };
                return Object.keys(obj).sort(insensitive).map(function (key) {
                    return Object.defineProperty(obj[key], '$key', {enumerable: false, value: key});
                });
            };
        }).filter('findModules', function () {
            return function (obj, inputSearchText) {
                if (!inputSearchText) {
                    return obj;
                }
                var filtered = [],
                searchText = inputSearchText.toLowerCase();
                angular.forEach(obj, function (val, i) {
                    var existsEntity = val.toString().toLowerCase().indexOf(searchText),
                    existsModule = val.$key.toLowerCase().indexOf(searchText);
                    if (existsModule !== -1 || existsEntity !== -1) {
                        filtered.push(val);
                    }
                });
                return filtered;
            };
        }).filter('findEntity', function () {
            return function (obj, moduleName, inputSearchText, dataBrowser) {
                if (!inputSearchText) {
                    return obj;
                }
                var filtered = [],
                searchText = inputSearchText.toLowerCase();
                angular.forEach(obj, function (val, i) {
                    var existsModule = moduleName.toLowerCase().indexOf(searchText),
                    existsEntity = function () {
                        return dataBrowser ? val.toString().toLowerCase().indexOf(searchText) : val.entityName.toLowerCase().indexOf(searchText);
                    };
                    if (existsModule !== -1 || existsEntity() !== -1) {
                        filtered.push(val);
                    }
                });
                return filtered;
            };
        }).filter('findModulesObj', function () {
            return function (obj, inputSearchText) {
                if (!inputSearchText) {
                    return obj;
                }
                var filtered = [],
                searchText = inputSearchText.toLowerCase(),
                existsEntity = function (entityInstances, searchText) {
                    var result = false;
                    $.each(entityInstances, function (index, instance) {
                        if (instance.entityName.toLowerCase().indexOf(searchText) !== -1) {
                            result = true;
                        } else {
                            result = false;
                        }
                        return (!result);
                    });
                    return result;
                };
                angular.forEach(obj, function (val) {
                    var existsModule = val.$key.toLowerCase().indexOf(searchText);
                    if (existsModule !== -1 || existsEntity(val, searchText)) {
                        filtered.push(val);
                    }
                });
                return filtered;
            };
        }),
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

    controllers.controller('MdsEmbeddableCtrl', function ($scope, MDSUtils) {

        $scope.maps = [];

        /**
        * Return available values for combobox field.
        *
        * @param {Array} setting A array of field settings.
        * @return {Array} A array of possible combobox values.
        */
        $scope.getComboboxValues = function (settings) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value, keys = [], key;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return labelValues;
            } else {
                if (labelValues !== undefined && labelValues[0].indexOf(":") !== -1) {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    for(key in labelValues) {
                        keys.push(key);
                    }
                    return keys;
                } else {        // there is no colon, so we are dealing with a string set, not a map
                    return labelValues;
                }
            }
        };

        $scope.getAndSplitComboboxValues = function (labelValues) {
            var doublet, i, map = {};
            for (i = 0; i < labelValues.length; i += 1) {
                doublet = labelValues[i].split(":");
                map[doublet[0]] = doublet[1];
            }
            return map;
        };

        $scope.getComboboxDisplayName = function (settings, value) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return value;
            } else {
                if (labelValues !== undefined && labelValues[0].indexOf(":") !== -1) {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    return labelValues[value];
                } else {         // there is no colon, so we are dealing with a string set, not a map
                    return value;
                }
            }
        };

        /**
        * Convert map to java map object.
        */
        $scope.mapToMapObject = function (maps) {
            var result = {};
            angular.forEach(maps,
                function (map, index) {
                    if (map.key && map.value) {
                        result[map.key] = map.value;
                    }
                }, result);
            return result;
        };

        $scope.initMap = function (mapObject, fieldId) {
            var resultMaps = [], map = [];
            angular.forEach($scope.maps, function (scopeMap, index) {
                if (scopeMap.id === fieldId) {
                    $scope.maps.splice(index, 1);
                }
            });
            if (mapObject !== null && typeof mapObject === "object" && mapObject !== undefined && Object.keys(mapObject).length > 0) {
                angular.forEach(Object.keys(mapObject), function (key, index) {
                        resultMaps.push({key: '', value: ''});
                        resultMaps[index].key = key;
                        resultMaps[index].value = mapObject[key];
                },
                resultMaps);
            } else if (mapObject !== null && typeof mapObject === "string" && mapObject !== undefined && mapObject.toString().indexOf(':') > 0) {
                map = mapObject.split('\n');
                angular.forEach(map, function (map, index) {
                    var str;
                    str = map.split(':');
                    if (str.length > 1) {
                        resultMaps.push({key: '', value: ''});
                        resultMaps[index].key = str[0].trim();
                        resultMaps[index].value = str[1].trim();
                    }
                },
               resultMaps);
            } else {
                resultMaps.push({key: '', value: ''});
            }
            resultMaps.reverse();
            $scope.maps.push({id: fieldId, fieldMap: resultMaps});
        };

        /**
        * Get map from field data by field id.
        */
        $scope.getMap = function (fieldId) {
            var resultMap = [];
            angular.forEach($scope.maps, function (map, index) {
                if (parseInt(fieldId, 10) === parseInt(map.id, 10)) {
                    resultMap = map;
                }
            }, resultMap);
            return resultMap;
        };

        /**
        * Add new map with empty key/value fields.
        */
        $scope.addMap = function (fieldId) {
            angular.forEach($scope.maps, function (map, index) {
                if (fieldId === map.id) {
                    $scope.maps[index].fieldMap.push({key: '', value: ''});
                }
            });
        };

        /**
        * Removes the key/value pair with the specified id field and index element.
        */
        $scope.deleteElementMap = function (fieldId, keyIndex) {
            fieldId = parseInt(fieldId, 10);
            angular.forEach($scope.maps, function (map, index) {
                if (fieldId === map.id) {
                    angular.forEach($scope.maps[index].fieldMap, function (fieldMap, indexElement) {
                        if (indexElement === keyIndex) {
                            $scope.safeApply(function () {
                                $scope.maps[index].fieldMap.splice(indexElement, 1);
                            });
                        }
                    });
                }
            });
        };

        /**
        * Checks if the keys are unique.
        */
        $scope.uniqueMapKey = function (mapKey, fieldId, elementIndex) {
            elementIndex = parseInt(elementIndex, 10);
            var fieldMaps = $scope.getMap(fieldId),
            keysList = function () {
                var resultKeysList = [];
                angular.forEach(fieldMaps.fieldMap, function (map, index) {
                    if (map !== null && map.key !== undefined && map.key.toString() !== '') {
                        if (index !== elementIndex) {
                            resultKeysList.push(map.key.toString());
                        }
                    }
                }, resultKeysList);
                return resultKeysList;
            };
            return $.inArray(mapKey, keysList()) !== -1;
        };

        /**
        * Checks if the pair is empty.
        */
        $scope.emptyMap = function (mapKey, mapValue) {
            return mapKey.toString().length > 0 && mapValue.toString().length < 1;
        };

        $scope.getMapLength = function (obj) {
            return Object.keys(obj).length;
        };

        $scope.getMdsEntityCsvImportPath = function(selectedEntityId) {
            return '../mds/instances/' + selectedEntityId + '/csvimport';
        };
    });

    controllers.controller('MdsBasicCtrl', function ($scope, $location, $state, $stateParams, $http, $controller, Entities, MDSUtils, ModalFactory) {

        angular.extend(this, $controller('MdsEmbeddableCtrl', {
            $scope: $scope,
            MDSUtils: MDSUtils
        }));

        var schemaEditorPath = '/mds/{0}'.format($scope.MDS_AVAILABLE_TABS[1]);

        $scope.DATA_BROWSER = "dataBrowser";
        $scope.SCHEMA_EDITOR = "schemaEditor";
        $scope.searchText = "";

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

        $scope.getProperFieldName = function (fieldName) {
            var field =  JSON.parse(JSON.stringify(fieldName));
            return field;
        };

        $scope.generateRandomUUID = function (field, isDefault) {
            if (isDefault) {
                field.basic.defaultValue = MDSUtils.generateUUID($http);
            } else {
                field.value = MDSUtils.generateUUID($http);
            }
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
                $state.reload();
            }

            loadEntity = entityId;
        };

        $scope.discard = function (entityId) {
            ModalFactory.showConfirm({
                title: $scope.msg('mds.warning'),
                message: $scope.msg('mds.wip.info.discard'),
                type: 'type-warning',
                callback: function(result) {
                    if (result) {
                        Entities.abandon({id: entityId}, function () {
                            workInProgress.setList(Entities);
                        });
                    }
                }
            });
        };

        /**
        * Convert string to map.
        */
        $scope.stringToMap = function (stringValue) {
            var resultMaps = [], map = [];
            if (stringValue !== null && stringValue !== undefined && stringValue.toString().indexOf(':') > 0) {
                map = stringValue.split('\n');
                angular.forEach(map, function (map, index) {
                    var str;
                    str = map.split(':');
                    if (str.length > 1) {
                    resultMaps.push({key: '', value: ''});
                    resultMaps[index].key = str[0].trim();
                    resultMaps[index].value = str[1].trim();
                    }
                },
                resultMaps);
                }
            return resultMaps;
        };

        /**
        * Convert map to string .
        */
        $scope.mapToString = function (maps) {
            var result = '';
            angular.forEach(maps,
                function (map, index) {
                    if (map.key && map.value) {
                        result = result.concat(map.key, ':', map.value,'\n');
                    }
                }, result);
            return result;
        };

        /**
        * Init map values.
        */
        $scope.initDefaultValueMap = function (stringValue, fieldId) {//only string to map
            var resultMaps = [], map = [];
            angular.forEach($scope.maps, function (scopeMap, index) {
                if (scopeMap.id === fieldId) {
                    $scope.maps.splice(index, 1);
                }
            });
            if (stringValue !== null && stringValue !== undefined && stringValue.toString().indexOf(':') > 0) {
                map = stringValue.split('\n');
                angular.forEach(map, function (map, index) {
                    var str;
                    str = map.split(':');
                    if (str.length > 1) {
                        resultMaps.push({key: '', value: ''});
                        resultMaps[index].key = str[0].trim();
                        resultMaps[index].value = str[1].trim();
                    }
                },
                resultMaps);
            } else {
                resultMaps.push({key: '', value: ''});
            }
            $scope.maps.push({id: fieldId, fieldMap: resultMaps});
        };

        /**
        * Sets initial values of list.
        */
        $scope.initValueList = function (fieldSettingValues) {
            if (fieldSettingValues !== undefined && fieldSettingValues.length < 1) {
                fieldSettingValues.push('');
            }
        };

        /**
        * Adds new element to the list of values.
        */
        $scope.addValueList = function (fieldSettingValues) {    // ng-click="setting.value.push('')"
            fieldSettingValues.push('');
        };

        /**
        * Updates the list of values.
        */
        $scope.updateList = function (fieldSettingValues, elementValue, elementIndex) {
            elementIndex = parseInt(elementIndex, 10);
            if (!$scope.uniqueListValue(fieldSettingValues, elementValue, elementIndex)) {
                fieldSettingValues[elementIndex] = elementValue;
            }
        };

        /**
        * Deletes selected value from list of values.
        */
        $scope.deleteElementList = function (fieldSettingValues, elementIndex) {
            elementIndex = parseInt(elementIndex, 10);
            angular.forEach(fieldSettingValues, function (value, index) {
                if (elementIndex === index) {
                    fieldSettingValues.splice(elementIndex, 1);
                }
            }, fieldSettingValues);
            return fieldSettingValues;
        };

        /**
        * Checks if the value is unique.
        */
        $scope.uniqueListValue = function (fieldSettingValues, elementValue, elementIndex) {
            var valuesList = [];
            elementIndex = parseInt(elementIndex, 10);
            angular.forEach(fieldSettingValues, function (value, index) {
                if (value !== undefined && value.toString() !== '' && index !== elementIndex) {
                    valuesList.push(value.toString().trim());
                }
            }, valuesList);
            return $.inArray(elementValue, valuesList) !== -1;
        };

        $scope.criterionValuesList = [];

        $scope.initCriterionValuesList = function (fieldId, criterion, stringValue) {
            var result = [], strValues, i;
            angular.forEach($scope.criterionValuesList, function (textValuesList, index) {
                if (textValuesList !== undefined && textValuesList.id === fieldId && textValuesList.criteria.toString() === criterion.displayName.toString()) {
                    if ($scope.criterionValuesList[index].values.isArray && $scope.criterionValuesList[index].values.length > 1) {
                        $scope.criterionValuesList[index].values = [''];
                    } else {
                        $scope.criterionValuesList[index] = undefined;
                    }

                }
            });
            if (stringValue !== null && stringValue !== undefined) {
                stringValue = stringValue.toString().trim();
                if (stringValue.indexOf(' ') > 0) {
                    strValues = stringValue.split(' ');
                    angular.forEach(strValues, function (strValue, index) {
                        result[index] = strValue.trim();
                    },
                    result);
                    for (i = result.length - 1; i >= 0; i -= 1) {
                        if (result[i] === '') {
                            result.splice(i, 1);
                        }
                    }
                } else if (stringValue.indexOf(' ') < 1) {
                    result[0] = stringValue;
                }
            } else {
                result.push('');
            }
            return result
                ? $scope.criterionValuesList.push({id: fieldId, criteria: criterion.displayName, values: result})
                : false;
        };

        /**
        * Adds new element to the list of values.
        */
        $scope.addValue = function (fieldId, criterion) {
            var result = false;
            $.each($scope.criterionValuesList, function (index, list) {
                if (list !== undefined && fieldId === list.id && list.criteria.toString() === criterion.displayName.toString()) {
                    $scope.criterionValuesList[index].values.push('');
                    result = true;
                } else {
                    result = false;
                }
                return (!result);
            });
        };

        /**
        * Updates criterion values list.
        */
        $scope.updateCriterionValuesList = function (fieldId, criterionName, elementValue, elementIndex) {
            var result = false;
            elementIndex = parseInt(elementIndex, 10);
            if (elementValue !== null && elementValue !== undefined) {
                $.each($scope.criterionValuesList, function (index, list) {
                    if (list !== undefined && list.id === fieldId && list.criteria.toString() === criterionName.toString()) {
                        list.values[elementIndex] = elementValue.toString().trim();
                        result = true;
                    } else {
                        result = false;
                    }
                    return (!result);
                });
            }
        };

        /**
        * Gets criterion values list.
        */
        $scope.getCriterionValuesList = function (fieldId, criterionName) {
            var result = [];
            fieldId = parseInt(fieldId, 10);
            angular.forEach($scope.criterionValuesList, function (list, index) {
                if (list !== undefined && list.id === fieldId && list.criteria.toString() === criterionName.toString()) {
                    result = list.values;
                }
            }, result);
            return result;
        };

        /**
        * Gets criterion values list.
        */
        $scope.getCriterionValues = function (fieldId, criterionName) {
            var result = [];
            fieldId = parseInt(fieldId, 10);
            angular.forEach($scope.criterionValuesList, function (list, index) {
                if (list !== undefined && list.id === fieldId && list.criteria.toString() === criterionName.toString()) {
                    result = list.values.join(' ');
                }
            }, result);
            return result;
        };

        /**
        * Deletes selected value from criterion list of values.
        */
        $scope.deleteValueList = function (fieldId, criterionName, valueIndex) {
            var result = [];
            valueIndex = parseInt(valueIndex, 10);
            fieldId = parseInt(fieldId, 10);
            angular.forEach($scope.criterionValuesList, function (list, index) {
                if (list !== undefined && list.id === fieldId && list.criteria.toString() === criterionName.toString()) {
                    $scope.criterionValuesList[index].values.splice(valueIndex, 1);
                    result = $scope.getCriterionValues(fieldId, criterionName);
                }
            }, result);
            return result;
        };

        /**
        * Checks if the value is unique between criteria.
        */
        $scope.uniqueBetweenCriteria = function (fieldId, criterionName, elementValue) {
            var valuesList = [], stringValue;
            fieldId = parseInt(fieldId, 10);
            stringValue = $scope.getCriterionValues(fieldId, criterionName);
            if (stringValue !== undefined && stringValue.toString().indexOf(' ') > 0) {
                valuesList = stringValue.split(' ');
            } else if (stringValue !== undefined && stringValue.toString().indexOf(' ') < 1) {
                valuesList[0] = stringValue;
            } else {
                valuesList.push('');
            }
            return $.inArray(elementValue, valuesList) !== -1 && elementValue !== '';
        };

        /**
        * Checks whether the user has access to the given functionality.
        */
        $scope.hasAccessTo = function (functionality) {
            return $scope.MDS_AVAILABLE_TABS.indexOf(functionality) !== -1;
        };

        $scope.selectedEntity = undefined;

        $scope.sortInsensitive = function (strArray) {
            return strArray.sort(function (s1, s2) {
                var s1Lower = s1.toLowerCase(), s2Lower = s2.toLowerCase();
                return s1Lower > s2Lower? 1 : (s1Lower < s2Lower? -1 : 0);
            });
        };

        innerLayout({});
    });

    /**
    * The MdsSchemaEditorCtrl controller is used on the 'Schema Editor' view.
    */
    controllers.controller('MdsSchemaEditorCtrl', function ($scope, $timeout, $http, Entities, MDSUsers, Permissions, MDSUtils, Locale, ModalFactory, LoadingModal) {

        MDSUtils.setCustomOperatorFunctions($scope);

        var setAdvancedSettings, updateAdvancedSettings, setRest, setBrowsing, setSecuritySettings, setIndexesLookupsTab, checkLookupName, checkActiveIndex;

        $scope.lookupExists = true;
        $scope.defaultValueValid = [];
        $scope.selectedRegexPattern = '';
        $scope.regexInfoList = [];
        $scope.listRegexPattern = [
            {name: $scope.msg('mds.regex.email'), description: $scope.msg('mds.regex.emailInfo'), pattern: '^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$'},
            {name: $scope.msg('mds.regex.phone'), description: $scope.msg('mds.regex.phoneInfo'), pattern: '\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*(\\d{1,2})$'},
            {name: $scope.msg('mds.regex.lowercase'), description: $scope.msg('mds.regex.lowercaseInfo'), pattern: '^[a-z]+$'},
            {name: $scope.msg('mds.regex.uppercase'), description: $scope.msg('mds.regex.uppercaseInfo'), pattern: '^[A-Z]+$'},
            {name: $scope.msg('mds.regex.number'), description: $scope.msg('mds.regex.numberInfo'), pattern: '^\\d+$'},
            {name: $scope.msg('mds.regex.integer'), description: $scope.msg('mds.regex.integerInfo'), pattern: '^([-][1-9])?(\\d)*$'},
            {name: $scope.msg('mds.regex.decimal'), description: $scope.msg('mds.regex.decimalInfo'), pattern: '^\\s*-?[0-9]\\d*(\\.\\d{1,})?\\s*$'},
            {name: $scope.msg('mds.regex.alphanumeric'), description: $scope.msg('mds.regex.alphanumericInfo'), pattern: '^[A-Za-z0-9]+$'},
            {name: $scope.msg('mds.regex.date'), description: $scope.msg('mds.regex.dateInfo'), pattern: '^(19|20)\\d\\d[-/.](0[1-9]|1[012])[-/.](0[1-9]|[12][0-9]|3[01])$'},
            {name: $scope.msg('mds.regex.dateTime'), description: $scope.msg('mds.regex.dateTimeInfo'), pattern: '^((((19|[2-9]\\d)\\d{2})[\\/\\.-](0[13578]|1[02])[\\/\\.-](0[1-9]|[12]\\d|3[01])\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))|(((19|[2-9]\\d)\\d{2})[\\/\\.-](0[13456789]|1[012])[\\/\\.-](0[1-9]|[12]\\d|30)\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))|(((19|[2-9]\\d)\\d{2})[\\/\\.-](02)[\\/\\.-](0[1-9]|1\\d|2[0-8])\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))[\\/\\.-](02)[\\/\\.-](29)\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])))$'}
        ];

        $scope.lookupFieldTypes = ["VALUE", "RANGE", "SET"];

        $scope.setRegexPattern = function (itemPattern) {
            $scope.selectedRegexPattern = itemPattern;
        };

        $scope.initRegexInfoList = function (fieldId) {
             $scope.regexInfoList.push({id: fieldId, value: ''});
        };

        $scope.setRegexInfoList = function (fieldId, itemDescription) {
            var result = false;
            $.each($scope.regexInfoList, function (index, field) {
                if (field.id === fieldId) {
                    $scope.regexInfoList[index].value = itemDescription;
                    result = true;
                }
                return (!result);
            });
        };

        $scope.$watch('currentError', function() {
            if ($scope.currentError !== undefined) {
                $('.ui-layout-content').animate({
                    scrollTop: 0
                });
            }
        });

        $scope.resetRegexInfo = function (fieldId) {
            var result = false;
            $.each($scope.regexInfoList, function (index, field) {
                if (field.id === fieldId) {
                    $scope.regexInfoList[index].value = '';
                    result = true;
                }
                return (!result);
            });
        };

        $scope.getRegexInfo = function (fieldId) {
            var result = false, value;
            $.each($scope.regexInfoList, function (index, field) {
                if (field.id === fieldId) {
                    value = $scope.regexInfoList[index].value;
                    result = true;
                } else {
                    result = false;
                }
                return (!result);
            });
            return value;
        };

        $scope.setBasicDefaultValueValid = function (valid, fieldName) {
            var result;
            $.each($scope.defaultValueValid, function (index) {
                if($scope.defaultValueValid[index].name === fieldName) {
                    $scope.defaultValueValid[index].valid = valid;
                    result = true;
                }
                else {
                    result = false;
                }
                return (!result);
            });
        };

        $scope.getBasicDefaultValueValid = function (fieldName) {
            var result, valid = true;
            $.each($scope.defaultValueValid, function (index) {
                if($scope.defaultValueValid[index].name === fieldName) {
                    result = true;
                    valid = $scope.defaultValueValid[index].valid;
                } else {
                    result = false;
                }
                return (!result);
            });
        return valid;
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        workInProgress.setList(Entities);

        if (loadEntity) {
            LoadingModal.open();
            $.ajax("../mds/entities/" + loadEntity).done(function (data) {
                $scope.selectedEntity = data;
                loadEntity = undefined;
                LoadingModal.close();
            });
        }

        if ($scope.$parent.selectedEntity) {
            LoadingModal.open();
            $.ajax("../mds/entities/getEntity/" + $scope.$parent.selectedEntity.module + "/" + $scope.$parent.selectedEntity.name).done(function (data) {
                $scope.selectedEntity = data;
                $scope.$parent.selectedEntity = undefined;
                $scope.selectedEntityChanged();
                LoadingModal.close();
            });
        }

        /**
        * This function is used to get entity advanced rest data from controller and prepare it for further usage.
        */
        setRest = function () {
            $scope.restExposedFields = [];
            $scope.restAvailableFields = [];
            $scope.restExposedLookups = [];

            if ($scope.advancedSettings.restOptions) {
                angular.forEach($scope.advancedSettings.restOptions.fieldNames, function (name) {
                    $scope.restExposedFields.push($scope.findFieldByName(name));
                });
            }

            angular.forEach($scope.fields, function (field) {
                if (!$scope.findFieldInArrayByName(field.basic.name, $scope.restExposedFields)) {
                    $scope.restAvailableFields.push($scope.findFieldByName(field.basic.name));
                }
            });

            if ($scope.advancedSettings.indexes) {
                if ($scope.lookupExists !== ($scope.advancedSettings.indexes.length > 0)) {
                    $scope.lookupExists = !$scope.lookupExists;
                }
                angular.forEach($scope.advancedSettings.indexes, function (lookup, index) {
                    if ($.inArray(lookup.lookupName, $scope.advancedSettings.restOptions.lookupNames) !== -1) {
                        $scope.restExposedLookups[index] = true;
                    } else {
                        $scope.restExposedLookups[index] = false;
                    }
                });
            }
        };

        /**
        * This function splits fields to ones that are displayed and ones that are not
        */
        setBrowsing = function() {
            if($scope.fields !== undefined && $scope.advancedSettings.browsing !== undefined) {
                $scope.browsingDisplayed = [];

                $scope.browsingAvailable = $.grep($scope.fields, function(field) {
                    return $scope.advancedSettings.browsing.displayedFields.indexOf(field.id) < 0;
                });

                $scope.browsingDisplayed.length = 0;
                angular.forEach($scope.advancedSettings.browsing.displayedFields, function(fieldId) {
                    $scope.browsingDisplayed.push($.grep($scope.fields, function(field) {
                        return field.id === fieldId;
                    })[0]);
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
        * This function checks and sets proper active index.
        */
        checkActiveIndex = function (initialSetTrue) {
            if (!_.isNull($scope.advancedSettings)
                    && !_.isUndefined($scope.advancedSettings)
                    && !_.isUndefined($scope.advancedSettings.indexes)
                    && $scope.advancedSettings.indexes.length > 0) {
                if ($scope.activeIndex === -1 || initialSetTrue) {
                    $scope.setActiveIndex(0);
                } else {
                    if ($scope.advancedSettings.indexes.length > $scope.activeIndex) {
                        $scope.setActiveIndex($scope.activeIndex);
                    } else {
                        $scope.setActiveIndex($scope.advancedSettings.indexes.length - 1);
                    }
                }
            } else {
                $scope.setActiveIndex(-1);
            }
        };

        /**
        * This function checks lookupName and sets blockLookups.
        */
        checkLookupName = function () {
            angular.forEach($scope.advancedSettings.indexes, function(index) {
                var result = $.grep($scope.advancedSettings.indexes, function(lookup) {
                    return lookup.lookupName === index.lookupName;
                });

                if (result.length > 1) {
                    $scope.setActiveIndex($scope.advancedSettings.indexes.indexOf(index));
                    $scope.blockLookups = true;
                    return;
                }
            });
        };

        /**
        * This function is used to set advanced settings. If settings is properly taken from server,
        * the related $scope fields will be also set.
        */
        setAdvancedSettings = function () {
            $scope.advancedSettings = Entities.getAdvanced({id: $scope.selectedEntity.id},
                function () {
                    $scope.blockLookups = false;
                    $scope.isNewLookupFieldButtonDisabled = false;
                    $scope.isNewLookupButtonDisabled = false;
                    checkActiveIndex(true);
                    setRest();
                    setBrowsing();
                    setIndexesLookupsTab();
                    checkLookupName();
                });
        };

        updateAdvancedSettings = function () {
            $scope.blockLookups = false;
            checkActiveIndex(false);
            setRest();
            setBrowsing();
            setIndexesLookupsTab();
            checkLookupName();
        };

        /**
        * This function is used to set security settings by getting them from the server.
        */
        setSecuritySettings = function () {
            $scope.securitySettings = {};
            $scope.securitySettings.permissions = [];
            $scope.securitySettings.users = [];
            $scope.securitySettings.readOnlyPermissions = [];
            $scope.securitySettings.readOnlyUsers = [];

            if ($scope.selectedEntity === null) {
                return;
            }

            $scope.securitySettings.securityMode = $scope.selectedEntity.securityMode.valueOf();
            $scope.securitySettings.readOnlySecurityMode = $scope.selectedEntity.readOnlySecurityMode === null ? 'NO ACCESS' : $scope.selectedEntity.readOnlySecurityMode.valueOf();

            if ($scope.securitySettings.securityMode === 'USERS'){
                $scope.securitySettings.users = $scope.selectedEntity.securityMembers;
            } else if ($scope.securitySettings.securityMode === 'PERMISSIONS'){
                $scope.securitySettings.permissions = $scope.selectedEntity.securityMembers;
            }

            if ($scope.securitySettings.readOnlySecurityMode === 'USERS'){
                $scope.securitySettings.readOnlyUsers = $scope.selectedEntity.readOnlySecurityMembers;
            } else if ($scope.securitySettings.readOnlySecurityMode === 'PERMISSIONS'){
                $scope.securitySettings.readOnlyPermissions = $scope.selectedEntity.readOnlySecurityMembers;
            }

            $('#usersSelect').select2('val', $scope.securitySettings.users);
            $('#permissionsSelect').select2('val', $scope.securitySettings.permissions);
            $('#readOnlyUsersSelect').select2('val', $scope.securitySettings.readOnlyUsers);
            $('#readOnlyPermissionsSelect').select2('val', $scope.securitySettings.readOnlyPermissions);
        };

        /**
        * The $scope.restAvailableFields contains fields available for use in REST.
        */
        $scope.restAvailableFields = [];

        /**
        * The $scope.restExposedFields contains fields selected for use in REST.
        */
        $scope.restExposedFields = [];

        /**
        * The $scope.restExposedLookups contains lookups selected for use in REST.
        */
        $scope.restExposedLookups = [];

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
        * The $scope.securitySettings contains security settings of selected entity. By default
        * there are no security settings
        */
        $scope.securitySettings = {};
        $scope.securitySettings.permissions = [];
        $scope.securitySettings.users = [];
        $scope.securitySettings.securityMode = undefined;
        $scope.securitySettings.readOnlyPermissions = [];
        $scope.securitySettings.readOnlyUser = [];
        $scope.securitySettings.readOnlySecurityMode = undefined;

        /**
        * The $scope.fields contains entity fields. By default there are no fields.
        */
        $scope.fields = undefined;

        $scope.waitForResponse = false;

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

        $scope.lookupAvailableRelatedFields = [];

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
            "mds.field.datetime", "mds.field.localDate", "mds.field.date8",
            "mds.field.datetime8"
        ];

        $scope.relationshipClasses = [{
                id: 0,
                className: 'java.util.Set'
            }, {
                id: 1,
                className: 'java.util.List'
            }
        ];

        $scope.availableUsers = MDSUsers.query();

        $scope.availablePermissions = Permissions.query();
        $scope.availableLocale = Locale.get();

        $scope.currentError = undefined;


        $scope.setError = function(error, params) {
            $scope.currentError = $scope.msg(error, params);
        };

        $scope.setErrorFromData = function(error) {
            var responseData, errorCode, errorParams;

            if (error) {
                responseData = error.data;
                if (responseData && (typeof(responseData) === 'string') && responseData.startsWith('key:') && !responseData.endsWith('key')) {
                     if (responseData.indexOf('params:') !== -1) {
                        errorCode = responseData.split('\n')[0].split(':')[1];
                        errorParams = responseData.split('\n')[1].split(':')[1].split(',');
                     } else {
                        errorCode = responseData.split(':')[1];
                     }
                }
            }

            $scope.currentError = $scope.msg(errorCode, errorParams);
        };

        $scope.unsetError = function() {
            $scope.currentError = undefined;
        };

        $scope.draft = function (data, callback, errorCallback) {
            var pre = { id: $scope.selectedEntity.id },
            func = function (data) {
                $scope.unsetError();

                $scope.selectedEntity.modified = data.changesMade;
                $scope.selectedEntity.outdated = data.outdated;

                if (_.isFunction(callback)) {
                    callback();
                }

                // update advanced settings
                updateAdvancedSettings();
            },
            errorHandler = function(msg, title, params) {
                $scope.setError(msg, params);
                errorCallback();
            };

            Entities.draft(pre, data, func,
                function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.error.draftSave', 'mds.error', response, errorHandler);
                }
            );
        };

        $scope.dateDefaultValueChange = function (val, id) {
            var fieldPath = 'basic.defaultValue';

            $scope.draft({
                edit: true,
                values: {
                    path: fieldPath,
                    fieldId: id,
                    value: [val]
                }
            });
        };

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
                    info1 = module
                        ? angular.element('<div>').append(module)
                        : undefined,
                    info2 = namespace
                        ? angular.element('<div>').append(namespace)
                        : undefined,
                    parent = (name || info1 || info2)
                        ? angular.element('<div>').append(name).append(info1).append(info2)
                        : undefined;

                return parent || $scope.msg('mds.error');
            },
            containerCssClass: "form-control-select2",
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
                return $scope.msg((type && type.displayName) || 'mds.error');
            },
            formatResult: function (type) {
                var strong = type && type.displayName
                        ? angular.element('<strong>').text($scope.msg(type.displayName))
                        : undefined,
                    name = strong
                        ? angular.element('<div>').append(strong)
                        : undefined,
                    description = type && type.description
                        ? angular.element('<span>')
                            .text($scope.msg(type.description))
                        : undefined,
                    info = description
                        ? angular.element('<div>').append(description)
                        : undefined,
                    parent = (name || info)
                        ? angular.element('<div>', {
                            id: 'field-type-'+$scope.msg(type.displayName)
                        }).append(name).append(info)
                        : undefined;

                return parent || $scope.msg('mds.error');
            },
            containerCssClass: "form-control-select2",
            escapeMarkup: function (markup) {
                return markup;
            }
        };

        /**
        * The $scope.SELECT_RELATIONSHIP_ENTITY contains configuration for selecting entity for relationship
        */
        $scope.SELECT_RELATIONSHIP_ENTITY = {
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
                    var results = [];
                    angular.forEach(data.results, function (entity) {
                        results.push(entity.className);
                    });
                    return data;
                }
            },
            initSelection: function (element, callback) {
                var id = $(element).val();

                if (!isBlank(id)) {
                    $.ajax("../mds/entities/getEntityByClassName?entityClassName=" + id).done(function (data) {
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
                    info1 = module
                        ? angular.element('<div>').append(module)
                        : undefined,
                    info2 = namespace
                        ? angular.element('<div>').append(namespace)
                        : undefined,
                    parent = (name || info1 || info2)
                        ? angular.element('<div>').append(name).append(info1).append(info2)
                        : undefined;

                return parent || $scope.msg('mds.error');
            },
            containerCssClass: "form-control-select2",
            escapeMarkup: function (markup) {
                return markup;
            }
        };

        /**
        * The $scope.SELECT_RELATIONSHIP_COLLECTION_TYPE contains configuration for selecting
        * collection type for relationship
        */
        $scope.SELECT_RELATIONSHIP_COLLECTION_TYPE = {
            data : {
                results: $scope.relationshipClasses,
                text: 'className'
            },
            formatSelection: function (item) {
                return item.className;
            },
            formatResult: function (item) {
                return item.className;
            },
            initSelection: function (element, callback) {
                var className = $(element).val();

                angular.forEach($scope.relationshipClasses, function (item) {
                    if (item.className === className) {
                        callback(item);
                    }
                });
            },
            containerCssClass: "form-control-select2",
            escapeMarkup: function (markup) {
                return markup;
            }
        };

        $scope.relationMetadataChanged = function (data) {
            var className, i, j, id, key;
            id = this.attributes.getNamedItem('mds-field-id').value;
            key = this.attributes.getNamedItem('meta-key').value;
            for (i = 0; i < $scope.fields.length; i += 1) {
                if ($scope.fields[i].id === Number(id)) {
                    for (j = 0; j < $scope.fields[i].metadata.length; j += 1) {
                        if ($scope.fields[i].metadata[j].key === key) {
                            className = $scope.fields[i].metadata[j].value.className;
                        }
                    }
                }
            }
            $scope.metadataChanged(this.attributes.getNamedItem('mds-path').value, id, className);
        };

        $scope.metadataChanged = function (metaPath, id, className) {
            $scope.draft({
                edit: true,
                values: {
                    path: metaPath,
                    fieldId: id,
                    value: [className]
                }
            });
        };

        $scope.isMetadataNotEditable = function (key) {
            if (key === 'related.class' || key === 'related.collectionType'
                || key === 'related.field' || key === 'related.owningSide' || key === 'enum.className') {
                return true;
            }
            return false;
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
                LoadingModal.open();
                $scope.clearEntityModal();
                Entities.save({}, entity, function (response) {
                    $scope.selectedEntity = response;
                    angular.element('#selectEntity').select2('val', response.id);
                    LoadingModal.close();
                }, function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.error.cantSaveEntity', 'mds.error', response);
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
                spans = form.find('span.form-hint.form-hint-bottom');

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
                    LoadingModal.close();
                    ModalFactory.showSuccessAlert('mds.delete.success');
                }, function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.error.cantDeleteEntity', 'mds.error', response);
                });
            }
        };

        /* ~~~~~ METADATA FUNCTIONS ~~~~~ */

        /**
        * Adds new metadata with empty key/value to field.
        */
        $scope.addMetadata = function (field) {
            $scope.draft({
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
            $scope.draft({
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
            var value = $scope.restExposedLookups[index],
                lookup = $scope.advancedSettings.indexes[index];

            $scope.draft({
                edit: true,
                values: {
                    path: 'restOptions.${0}'.format(value ? 'addLookup' : 'removeLookup'),
                    advanced: true,
                    value: [lookup.lookupName]
                }
            }, function () {
                $scope.safeApply(function () {
                    if (value) {
                        $scope.advancedSettings.restOptions.lookupNames.push(
                            lookup.lookupName
                        );
                    } else {
                        $scope.advancedSettings.restOptions.lookupNames.removeObject(
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
            $scope.advancedSettings.restOptions.fieldNames = [];

            angular.forEach(container, function(field) {
                $scope.advancedSettings.restOptions.fieldNames.push(field.basic.name);
            });

            $scope.draft({
                edit: true,
                values: {
                    path: 'restOptions.$setFieldNames',
                    advanced: true,
                    value: [$scope.advancedSettings.restOptions.fieldNames]
                }
            });
        };

        /* ~~~~~ FIELD FUNCTIONS ~~~~~ */

        $scope.isUniqueEditable = function(field) {
            if (field && field.type) {
                return !field.readOnly
                                   && field.type.typeClass !== 'java.util.Map'
                                   && field.type.typeClass !== "org.motechproject.mds.domain.OneToManyRelationship"
                                   && field.type.typeClass !== "org.motechproject.mds.domain.ManyToManyRelationship"
                                   && field.type.typeClass !== "java.util.Collection";
            }
            return false;

        };

        /**
        * Create new field and add it to an entity schema. If displayName, name or type was not
        * set, error message will be shown and a field will not be created. The additional message
        * will be shown if a field name is not unique or not valid java field name.
        */
        $scope.createField = function () {
            var validate, selector;

            $scope.tryToCreate = true;
            validate = $scope.newField.type
                && $scope.newField.displayName
                && $scope.isValidJavaFieldName($scope.newField.name)
                && $scope.findFieldsByName($scope.newField.name).length === 0;

            if (validate) {
                $scope.draft({
                    create: true,
                    values: {
                        typeClass: $scope.newField.type.typeClass,
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
                        if ($scope.advancedSettings !== undefined && $scope.advancedSettings.browsing !== undefined) {
                            $scope.advancedSettings.browsing.displayedFields.push(field.id);
                        }
                        if ($scope.advancedSettings !== undefined && $scope.advancedSettings.restOptions !== undefined) {
                            $scope.advancedSettings.restOptions.fieldNames.push(field.basic.name);
                        }
                        setBrowsing();
                        setRest();

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
            ModalFactory.showConfirm({
                message: $scope.msg('mds.warning.removeField'),
                type: 'type-warning',
                callback: function(result) {
                    if (result) {
                        $scope.draft({
                            remove: true,
                            values: {
                                fieldId: field.id
                            }
                        }, function() {
                             $scope.safeApply(function () {
                                var filterableIndex;
                                $scope.fields.removeObject(field);

                                if ($scope.findFieldInArrayByName(field.basic.name, $scope.restAvailableFields)) {
                                    $scope.restAvailableFields.removeObject(field);
                                } else {
                                    $scope.restExposedFields.removeObject(field);
                                }

                                filterableIndex = $scope.advancedSettings.browsing.filterableFields.indexOf(field.id);
                                if(filterableIndex >= 0) {
                                    $scope.advancedSettings.browsing.filterableFields.splice(filterableIndex, 1);
                                }

                                setBrowsing();
                                setRest();
                            });
                        });
                    }
                }
            });
        };

        $scope.shouldShowField = function(field) {
            return !hasMetadata(field, 'autoGenerated', 'true') && !hasMetadata(field, 'autoGeneratedEditable', 'true');
        };

        /**
        * Abandon all changes made on an entity schema.
        */
        $scope.abandonChanges = function () {
            $scope.unsetError();

            LoadingModal.open();

            $scope.selectedEntity.outdated = false;

            Entities.abandon({id: $scope.selectedEntity.id}, function () {
                var entity;

                entity = Entities.get({id: $scope.selectedEntity.id },  function () {
                    $scope.selectedEntity = entity;
                });
            });
        };

        /**
        * Update the draft.
        */
        $scope.updateDraft = function () {
            var entity;

            LoadingModal.open();

            $scope.unsetError();
            $scope.selectedEntity.outdated = false;

            entity = Entities.update({id: $scope.selectedEntity.id}, function () {
                $scope.selectedEntity = entity;
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
                && MDSUtils.find(field.metadata, [{ field: 'key', value: key}], false).length === 1;
        };

        /**
        * Check if field is used in a referenced lookup.
        *
        * @param {object} field The field to check.
        * @return {boolean} true if the field is use in a referenced lookup; otherwise false.
        */
        $scope.fieldUsedInReferencedLookup = function (field) {
            var i;
            if (field && field.lookups) {
                for (i = 0; i < field.lookups.length; i += 1) {
                    if (field.lookups[i].referenced) {
                        return true;
                    }
                }
            }
            return false;
        };

        /* VALIDATION FUNCTIONS */

        /**
        * Checks if criterion value is valid
        * @param {object} field to validate
        * @param {object} criterion to validate
        * @param {object} list containing all field's validation criteria
        * @return {string} empty if criterion is valid (otherwise contains validation error)
        */
        $scope.validateCriterion = function(field, criterion, validationCriteria) {
            var anotherCriterion, criterionValueList;

            if (criterion.enabled) {
                if ((criterion.value === null || criterion.value.length === 0) && !(criterion.displayName === 'mds.field.validation.cannotBeInSet' || criterion.displayName === 'mds.field.validation.mustBeInSet')) { //.trim()
                    return 'mds.error.requiredField';
                } else if (criterion.displayName === 'mds.field.validation.cannotBeInSet' || criterion.displayName === 'mds.field.validation.mustBeInSet') {
                    criterionValueList = $scope.getCriterionValuesList(field.id, criterion.displayName);
                    if (criterionValueList[0] === undefined || criterionValueList[0].toString() === null || criterionValueList[0] === '') {
                        return 'mds.error.requiredField';
                    }
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

        /**
        * Validate all information inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all information inside the field are correct; otherwise false.
        */
        $scope.validateField = function (field) {
            return $scope.validateFieldBasic(field)
                && $scope.validateFieldMetadata(field)
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
                && $scope.isValidJavaFieldName(field.basic.name)
                && $scope.uniqueField(field.basic.name)
                && $scope.getBasicDefaultValueValid(field.basic.name);
        };

        /**
        * Validate the metadata ('Metadata' tab on UI) inside the given field.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if all metadata inside the field are correct;
        *                   otherwise false.
        */
        $scope.validateFieldMetadata = function (field) {
            var expression = true;

            if (field.metadata) {
                angular.forEach(field.metadata, function (meta) {
                    expression = expression && $scope.uniqueMetadataKey(field, meta.key);
                    var relationship = true;
                    if (meta.key === "related.class" && (meta.value === "" || meta.value === null)) {
                            relationship = false;
                    }
                    if (meta.key === "related.collectionType" && (meta.value === "" || meta.value === null)) {
                            relationship = false;
                    }
                    if (meta.key === "related.field" && (meta.value === "" || meta.value === null)) {
                            relationship = false;
                    }
                    expression = expression && relationship;
                });
            }

            return expression;
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
                if ($scope.getTypeSingleClassName(field.type) === 'combobox' && $scope.checkIfAllowSupplied(field)) {
                    expression = true;
                } else {
                    angular.forEach(field.settings, function (setting) {
                        expression = expression && $scope.checkOptions(setting);
                    });
                }
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
                    if ($scope.validateCriterion(field, criterion, field.validation.criteria)) {
                        expression = false;
                    }
                });
            }

            return expression;
        };

        /**
        * Validate the field name, checking if it is valid field name in java.
        * Valid java field name starts from letter, $ or _ symbol followed by
        * letters, numbers, $ or _ symbols.
        *
        * @param {fieldName} string representing name of validating field
        * @return {boolean} true if fieldName is valid java field name, false otherwise
        */
        $scope.isValidJavaFieldName = function (fieldName) {
            return MDSUtils.validateRegexp(fieldName, new RegExp('^[a-zA-Z_$][\\w$]*$'));
        };

        /**
        * Check if a user can save field definitions to database.
        *
        * @return {boolean} true if field definitions are correct; otherwise false.
        */
        $scope.canSaveChanges = function () {
            var i, expression = true;

            angular.forEach($scope.fields, function (field) {
                expression = expression && $scope.validateField(field);

            });

            if ($scope.advancedSettings && $scope.advancedSettings.indexes) {
                angular.forEach($scope.advancedSettings.indexes, function (index) {
                    expression = expression && index.lookupName !== undefined && index.lookupName !== null && index.lookupName.length !== 0
                        && !$scope.blockLookups;
                        for (i = 0; i < index.lookupFields.length; i += 1) {
                            if ($scope.isLookupFieldInRelationship(index.lookupFields[i].id)) {
                                expression = expression && index.lookupFields[i].relatedName !== undefined && index.lookupFields[i].relatedName !== null
                                    && index.lookupFields[i].relatedName.length !== 0;
                            }
                        }
                });
            }

            return expression;
        };

        /**
        * Save all changes made on an entity schema. Firstly this method tries to save fields to
        * database one by one. Next the method tries to delete existing fields from database.
        */
        $scope.saveChanges = function () {
            var pre = { id: $scope.selectedEntity.id },
                data = {},
                successCallback = function (data) {
                    var pre = {id: $scope.selectedEntity.id},
                        successCallback = function () {
                            setAdvancedSettings();
                            LoadingModal.close();
                        };

                    $scope.selectedEntity.modified = false;
                    $scope.selectedEntity.outdated = false;
                    $scope.fields = Entities.getFields(pre, successCallback);
                    $scope.newField = {};
                    $scope.tryToCreate = false;
                },
                errorCallback = function (data) {
                    $scope.setErrorFromData(data);
                    LoadingModal.close();
                };

            LoadingModal.open();
            Entities.commit(pre, data, successCallback, errorCallback);
        };

        /* ~~~~~ ADVANCED FUNCTIONS ~~~~~ */

        $scope.checkLookupName = function (count) {
            return $.grep($scope.advancedSettings.indexes, function(lookup) {
                return (lookup.lookupName.toLowerCase() === "lookup " + count ||
                        lookup.lookupName.toLowerCase() === "lookup" + count);
            });
        };

        $scope.getLookupNameForNewLookup = function () {
            var count = 1, result;

            while (true) {
                result = $scope.checkLookupName(count);

                if (result.length === 0) {
                    return "Lookup " + count;
                }
                count += 1;
            }
        };

        /**
        * Adds a new index and sets it as the active one
        */
        $scope.addNewIndex = function () {
            $scope.isNewLookupButtonDisabled = true;
            var newLookup = {
                lookupName: $scope.getLookupNameForNewLookup(),
                singleObjectReturn: true,
                indexRequired: true,
                lookupFields: []
            };

            $scope.draft({
                edit: true,
                values: {
                    path: '$addNewIndex',
                    advanced: true,
                    value: [newLookup.lookupName]
                }
            }, function () {
                $scope.advancedSettings.indexes.push(newLookup);
                $scope.setActiveIndex($scope.advancedSettings.indexes.length-1);
                $scope.isNewLookupButtonDisabled = false;
            }, function () {
                $scope.isNewLookupButtonDisabled = false;
            });

        };

         $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState) {
            if(fromState.name === 'mds.schemaEditor') {
               var modal = angular.element('#advancedObjectSettingsModal');
               modal.modal('hide');
            }
         });

        $scope.blockLookups = false;
        $scope.$watch('lookup.lookupName', function () {
            var exists;

            if ($scope.advancedSettings !== null && $scope.lookup !== undefined && $scope.lookup.lookupName !== undefined) {
                $scope.validateLookupName($scope.lookup.lookupName);
            }
        });

        /**
        * Runs a validation for given lookup name. If there's a duplicate in the current array of
        * lookups, it will perform necessary actions (display error and block components on UI). Otherwise
        * the error message and component block will be removed.
        *
        * @lookupName   Name of the lookup to perform check for.
        */
        $scope.validateLookupName = function(lookupName) {
            var exists;
            exists = MDSUtils.find($scope.advancedSettings.indexes, [{ field: 'lookupName', value: lookupName }], false, true).length > 1;

            if (exists) {
                $(".lookupExists").show();
                $scope.blockLookups = true;
            } else {
                $(".lookupExists").hide();
                $scope.blockLookups = false;
            }
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
            if (!$scope.blockLookups) {
                $scope.activeIndex = index;
                if ($scope.activeIndex > -1) {
                    $scope.lookup = $scope.advancedSettings.indexes[$scope.activeIndex];
                    $scope.setAvailableFields();
                    $scope.setLookupFocus();
                } else {
                    $scope.lookup = undefined;
                }
            }
        };

        /**
        * Changes active index depending on which arrow key user pressed
        * up arrow - decrements active index
        * down arrow - increments active index
        */
        $scope.changeActiveIndex = function($event) {
            if (!$scope.blockLookups) {
                if ($event.keyCode === 38 && $scope.activeIndex > 0) { // up arrow
                    $scope.setActiveIndex($scope.activeIndex - 1);
                } else if ($event.keyCode === 40 && $scope.activeIndex < $scope.advancedSettings.indexes.length - 1) { // down arrow
                    $scope.setActiveIndex($scope.activeIndex + 1);
                }
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
            var deletedLookupName;
            $scope.draft({
                edit: true,
                values: {
                    path: '$removeIndex',
                    advanced: true,
                    value: [$scope.activeIndex]
                }
            }, function () {
                deletedLookupName = $scope.advancedSettings.indexes[$scope.activeIndex].lookupName;
                $scope.advancedSettings.indexes.remove($scope.activeIndex);
                $scope.restExposedLookups.splice($scope.activeIndex, 1);
                $scope.setActiveIndex(-1);
                $scope.validateLookupName(deletedLookupName);
            });
        };

        /**
        * Adds new lookup field to the currently active index
        */
        $scope.addLookupField = function () {
            $scope.isNewLookupFieldButtonDisabled = true;
            var value = $scope.availableFields[0] && $scope.availableFields[0].id;
            $scope.draft({
                edit: true,
                values: {
                    path: 'indexes.{0}.$addField'.format($scope.activeIndex),
                    advanced: true,
                    value: [value]
                }
            }, function () {
                $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields.push({
                    id: value,
                    type: "VALUE"
                });
                $scope.setAvailableFields();
                $scope.isNewLookupFieldButtonDisabled = false;
            }, function () {
                $scope.isNewLookupFieldButtonDisabled = false;
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
        $scope.selectField = function (selectedIndex, newField) {
            var lookupFields = $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields;

            $scope.draft({
                edit: true,
                values: {
                    path: 'indexes.{0}.$insertField'.format($scope.activeIndex),
                    advanced: true,
                    value: [selectedIndex, newField, ""]
                }
            }, function () {
                lookupFields[selectedIndex] = {
                    id: newField,
                    type: "VALUE"
                };
                $scope.setAvailableFields();
            });
        };

        /**
        * Sets the type of the lookup field (value, range or set) for the lookup field of the given id.
        *
        * @param lookupFieldId id of the lookup field to set the type for
        * @param lookupType the type of the lookup field to set for this field
        */
        $scope.selectLookupFieldType = function(selectedIndex, lookupType) {
            var lookupFields = $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields;

                $scope.draft({
                    edit: true,
                    values: {
                        path: 'indexes.{0}.$updateTypeForLookupField'.format($scope.activeIndex),
                        advanced: true,
                        value: [selectedIndex, lookupType]
                    }
                }, function () {

                    if (lookupType && (lookupType === 'RANGE' || lookupType === 'SET')) {
                        lookupFields[selectedIndex].customOperator = "";

                        $scope.draft({
                            edit: true,
                            values: {
                                path: 'indexes.{0}.$updateCustomOperatorForLookupField'.format($scope.activeIndex),
                                advanced: true,
                                value: [selectedIndex, ""]
                            }
                        });
                    }

                    lookupFields[selectedIndex] = {
                            id: lookupFields[selectedIndex].id,
                            customOperator: lookupFields[selectedIndex].customOperator,
                            relatedName: lookupFields[selectedIndex].relatedName,
                            type: lookupType
                        };
                });
        };

        /**
        * Sets the custom operator of the lookup field for the field of the given id.
        *
        * @param lookupFieldId id of the lookup field to set the type for
        * @param customOperator the type of the lookup field to set for this field
        */
        $scope.selectLookupFieldCustomOperator = function(selectedIndex, customOperator) {
            var lookupFields = $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields;

                $scope.draft({
                    edit: true,
                    values: {
                        path: 'indexes.{0}.$updateCustomOperatorForLookupField'.format($scope.activeIndex),
                        advanced: true,
                        value: [selectedIndex, customOperator]
                    }
                }, function () {
                    lookupFields[selectedIndex] = {
                            id: lookupFields[selectedIndex].id,
                            type: lookupFields[selectedIndex].type,
                            relatedName: lookupFields[selectedIndex].relatedName,
                            customOperator: customOperator
                        };
                });
        };

        /**
        * Refreshes available fields for the currently active index. A field is considered available
        * if it is not yet present in the lookup field list of the currently active index.
        */
        $scope.setAvailableFields = function () {
            var availableFields = [], func, selectedFields, i;

            if ($scope.activeIndex !== -1) {
                func = function (num) {
                    return num.id === $scope.fields[i].id && !$scope.isRelationshipField($scope.fields[i]);
                };
                selectedFields = $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields;

                for (i = 0; i < $scope.fields.length; i += 1) {
                    if (_.filter(selectedFields, func).length === 0) {
                        availableFields.push($scope.fields[i]);
                    }
                }

                $scope.availableFields = availableFields;
            }
        };

        $scope.isLookupFieldInRelationship = function (id) {
            var i;
            for (i = 0; i < $scope.fields.length; i += 1) {
                if ($scope.fields[i].id === id) {
                    return $scope.isRelationshipField($scope.fields[i]);
                }
            }
            return false;
        };

        $scope.isRelationshipField = function (field) {
            return field.type.typeClass === "org.motechproject.mds.domain.OneToOneRelationship"
                || field.type.typeClass  === "org.motechproject.mds.domain.OneToManyRelationship"
                || field.type.typeClass  === "org.motechproject.mds.domain.ManyToOneRelationship"
                || field.type.typeClass  === "org.motechproject.mds.domain.ManyToManyRelationship";
        };

        $scope.getRelatedEntityFields = function (lookupField) {
            var i, field, entityClassName;

            //we already have fields
            if ($scope.lookupAvailableRelatedFields[lookupField.id] !== undefined && $scope.lookupAvailableRelatedFields[lookupField.id] !== null) {
                return;
            }

            for(i = 0; i < $scope.fields.length; i += 1) {
                if (lookupField.id === $scope.fields[i].id) {
                    field = $scope.fields[i];
                    break;
                }
            }

            for(i = 0; i < field.metadata.length; i += 1) {
                if (field.metadata[i].key === 'related.class') {
                    entityClassName = field.metadata[i].value.className === undefined ? field.metadata[i].value
                        : field.metadata[i].value.className;
                    break;
                }
            }

            $http.get('../mds/entities/entityFieldsByClassName?entityClassName=' + entityClassName).success(function (data) {
                var j;
                $scope.lookupAvailableRelatedFields[lookupField.id] = [];
                for (j = 0; j < data.length; j += 1) {
                    if (!$scope.isRelationshipField(data[j])) {
                        $scope.lookupAvailableRelatedFields[lookupField.id].push(data[j]);
                    }
                }
            }).error(function(response) {
                $scope.lookupAvailableRelatedFields[lookupField.id] = undefined;
            });
        };

        $scope.selectLookupRelatedField = function(selectedIndex, value) {
            var lookupFields = $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields;

            lookupFields[selectedIndex].relatedName = value;
                $scope.draft({
                    edit: true,
                    values: {
                        path: 'indexes.{0}.$updateFieldRelatedName'.format( $scope.activeIndex),
                        advanced: true,
                        value: [selectedIndex, lookupFields[selectedIndex].relatedName !== undefined && lookupFields[selectedIndex].relatedName !== null ?
                            lookupFields[selectedIndex].relatedName : '']
                    }
                }, function () {
                    lookupFields[selectedIndex] = {
                            id: lookupFields[selectedIndex].id,
                            type: lookupFields[selectedIndex].type,
                            relatedName: lookupFields[selectedIndex].relatedName,
                            customOperator: lookupFields[selectedIndex].customOperator
                        };
                });
        };

        /**
        * Removes given field from the lookup fields list of the currently active index
        *
        * @param field A field object to remove
        */
        $scope.removeLookupField = function (idx) {
            $scope.draft({
                edit: true,
                values: {
                    path: 'indexes.{0}.$removeField'.format($scope.activeIndex),
                    advanced: true,
                    value: [parseInt(idx, 10)]
                }
            }, function () {
                var lookupFields = $scope.advancedSettings.indexes[$scope.activeIndex].lookupFields;
                lookupFields.splice(idx, 1);
                $scope.setAvailableFields();
            });
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
            return $scope.restExposedFields.length > 0;
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
            return $scope.restAvailableFields.length > 0;
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
        $scope.onFilterableChange = function(field, newValue) {

            $scope.draft({
                edit: true,
                values: {
                    path: 'browsing.${0}'.format(newValue ? 'addFilterableField' : 'removeFilterableField'),
                    advanced: true,
                    value: [field.id]
                }
            }, function () {
                if(newValue) {
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

            $scope.draft({
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

            $scope.draft({
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

            angular.forEach(indices.reverse(), function(index) {
                $scope.targetItemMoveDown(index);
            });

            angular.forEach($scope.browsingDisplayed, function (item) {
                array.push(item.id);
            });

            $scope.draft({
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
            var lookup = MDSUtils.find(array, [{ field: 'lookupName', value: name}], false, true);
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
        * Find unique field with given name.
        *
        * @param {string} name This value will be used to find fields.
        * @return {object} unique field with the given name.
        */
        $scope.findFieldByName = function (name) {
            return MDSUtils.find($scope.fields, [{ field: 'basic.name', value: name}], true);
        };

        /**
        * Find display name of the unique field with given id.
        *
        * @param {string} id This value will be used to find fields.
        * @return {object} display name of the unique field with the given id.
        */
        $scope.getFieldDisplayNameByFieldId = function (id) {
            var fields = $scope.fields.valueOf(), result, i;

            for (i = 0; i < fields.length; i += 1) {
                //toString() method is called because sometimes id has a numeric type
                if (id.toString() === fields[i].id.toString()) {
                    result = fields[i].basic.displayName;
                    break;
                }
            }

            return result;
        };

        /**
        * Find all fields with given name.
        *
        * @param {string} name This value will be used to find fields.
        * @param {Array} array Array in which we're looking for name.
        * @return {Array} array of fields with the given name.
        */
        $scope.findFieldInArrayByName = function (name, array) {
            var field = MDSUtils.find(array, [{ field: 'basic.name', value: name}], false);
            return $.isArray(field) ? field[0] : field;
        };

        /**
        * Find all fields with given name.
        *
        * @param {string} name This value will be used to find fields.
        * @return {Array} array of fields with the given name.
        */
        $scope.findFieldsByName = function (name) {
            return MDSUtils.find($scope.fields, [{ field: 'basic.name', value: name}], false, true);
        };

        /**
        * Find validation criterion with given name in field's validation criteria.
        *
        * @param {object} validationCriteria list of field's validation criteria.
        * @param {string} name The name of criteria to be found.
        * @return {object} found criterion with given name or null if no such criterion exists.
        */
        $scope.findCriterionByName = function (validationCriteria, name) {
            return MDSUtils.find(validationCriteria, [{ field: 'displayName', value: name}], true);
        };

        /**
        * Find field setting with given name.
        *
        * @param {Array} settings A array of field settings.
        * @param {string} name This value will be used to find setting.
        * @return {object} a single object which represent setting with the given name.
        */
        $scope.findSettingByName = function (settings, name) {
            return MDSUtils.find(settings, [{field: 'name', value: name}], true);
        };

        /*
        * Gets type information from TypeDto object.

        * @param {object} typeObject TypeDto object containing type information
        * @return {string} type information taken from parameter object.
        */
        $scope.getTypeSingleClassName = function (type) {
            if (type) {
                return type.displayName.substring(type.displayName.lastIndexOf('.') + 1);
            }
            return "";

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

            return _.isNumber(parseFloat(number))
                ? MDSUtils.validateDecimal(parseFloat(number), precision.value, scale.value)
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
        * Check if the given setting allowUserSupplied is checked.
        *
        * @param {object} field The field to validate.
        * @return {boolean} true if allowUserSupplied is checked;
        *                   otherwise false.
        */
        $scope.checkIfAllowSupplied = function (field) {
            var result = true;
            angular.forEach(field.settings, function (setting) {
                if (setting.name === 'mds.form.label.allowUserSupplied' && setting.value === true) {
                    result = true;
                } else if (setting.name === 'mds.form.label.allowUserSupplied') {
                    result = false;
                }
            });
            return result;
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
                && (_.isArray(setting.value) ? setting.value.length > 0 && setting.value[0].toString().trim().length > 0 : true);
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
            var fullName, singleName;

            if (!setting.type) {
                return false;
            } else {
                fullName = setting.type.typeClass;
                singleName = fullName.substring(fullName.lastIndexOf('.') + 1);

                return _.isEqual(singleName.toLowerCase(), type.toLowerCase());
            }
        };

        /**
        * Set an additional watcher for $scope.selectedEntity. Its role is to get fields related to
        * the entity in situation in which the entity was selected from the entity list.
        */
        $scope.$watch('selectedEntity', function () {
            $scope.selectedEntityChanged();
        });

        $scope.selectedEntityChanged = function() {
            if ($scope.selectedEntity && $scope.selectedEntity.id) {
                if (!$scope.waitForResponse) {
                    LoadingModal.open();
                    workInProgress.setActualEntity(Entities, $scope.selectedEntity.id);
                    $scope.waitForResponse = true;
                    $scope.fields = Entities.getFields({id: $scope.selectedEntity.id}, function () {
                        setSecuritySettings();
                        setAdvancedSettings();
                        $scope.draft({});
                        $scope.waitForResponse = false;
                    });
                    LoadingModal.close();
                }
            } else {
                workInProgress.setActualEntity(Entities, undefined);

                delete $scope.fields;
                delete $scope.advancedSettings;
                delete $scope.waitForResponse;
            }
        };

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
                    $scope.newField.name += '{0}'.format(_.uniqueId());
                }
            }
        });

        /* ~~~~~ SECURITY FUNCTIONS ~~~~~ */

        $scope.securityOptions = ['EVERYONE', 'OWNER', 'CREATOR', 'USERS', 'PERMISSIONS', 'NO ACCESS'];

        $scope.commitSecurity = function() {
            $scope.securityList = [];
            $scope.readOnlySecurityList = [];

            if ($scope.securitySettings.securityMode === 'USERS') {
                $scope.clearPermissions();
                $scope.securityList = $scope.securitySettings.users;
            } else if ($scope.securitySettings.securityMode === 'PERMISSIONS') {
                $scope.clearUsers();
                $scope.securityList = $scope.securitySettings.permissions;
            } else {
                $scope.clearUsers();
                $scope.clearPermissions();
            }

            if ($scope.securitySettings.readOnlySecurityMode === 'USERS') {
                $scope.clearReadOnlyPermissions();
                $scope.readOnlySecurityList = $scope.securitySettings.readOnlyUsers;
            } else if ($scope.securitySettings.readOnlySecurityMode === 'PERMISSIONS') {
                $scope.clearReadOnlyUsers();
                $scope.readOnlySecurityList = $scope.securitySettings.readOnlyPermissions;
            } else {
                $scope.clearReadOnlyUsers();
                $scope.clearReadOnlyPermissions();
            }

            $scope.draft({
                edit: true,
                values: {
                    path: "$securitySave",
                    security: true,
                    value: [$scope.securitySettings.securityMode, $scope.securityList, $scope.securitySettings.readOnlySecurityMode, $scope.readOnlySecurityList]
                }
            });
        };

        /**
        * Clears user list in 'Security' view
        */
        $scope.clearUsers = function() {
            $scope.securitySettings.users = [];
            $('#usersSelect').select2('val', $scope.securitySettings.users);
        };

        /**
        * Clears permissions list in 'Security' view
        */
        $scope.clearPermissions = function() {
            $scope.securitySettings.permissions = [];
            $('#permissionsSelect').select2('val', $scope.securitySettings.permissions);
        };

        /**
        * Clears readOnlyUsers list in 'Security' view
        */
        $scope.clearReadOnlyUsers = function() {
            $scope.securitySettings.readOnlyUsers = [];
            $('#readOnlyUsersSelect').select2('val', $scope.securitySettings.readOnlyUsers);
        };

        /**
        * Clears readOnlyPermissions list in 'Security' view
        */
        $scope.clearReadOnlyPermissions = function() {
            $scope.securitySettings.readOnlyPermissions = [];
            $('#readOnlyPermissionsSelect').select2('val', $scope.securitySettings.readOnlyPermissions);
        };

        /**
        * Callback function called when users list under 'Security' view changes
        */
        $scope.usersChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.securitySettings.users.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.securitySettings.users.removeObject(value);
            }
        };

        /**
        * Callback function called when permissions list under 'Security' view changes
        */
        $scope.permissionsChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.securitySettings.permissions.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.securitySettings.permissions.removeObject(value);
            }
        };

        /**
        * Callback function called when read only users list under 'Security' view changes
        */
        $scope.readOnlyUsersChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.securitySettings.readOnlyUsers.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.securitySettings.readOnlyUsers.removeObject(value);
            }
        };
        /**
        * Callback function called when read only permissions list under 'Security' view changes
        */
        $scope.readOnlyPermissionsChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.securitySettings.readOnlyPermissions.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.securitySettings.readOnlyPermissions.removeObject(value);
            }
        };

        /*
        * Gets validation criteria values.
        */
        $scope.getValidationCriteria = function (field, id) {
            var validationCriteria = '',
                value = $scope.getTypeSingleClassName(field.type);

            if (field.validation !== null && field.validation.criteria[id].enabled && field.validation.criteria[id].value !== null) {
                validationCriteria = field.validation.criteria[id].value;
            }
            return validationCriteria;
        };

        /**
        * Check if the given string match the RegExp pattern expression.
        *
        * @param {viewValue} value The string to validate.
        * @param {object} settings Object with RegExp pattern.
        * @return {boolean} true if string does not match the RegExp pattern expression;
        *                   otherwise false.
        */
        $scope.checkPattern = function (viewValue, field) {
            var regexp,
            enabled = false;
            if (field.validation.criteria[0].enabled && viewValue !== undefined) {
                enabled = true;
                regexp = new RegExp($scope.getValidationCriteria(field, 0));
            }
            return enabled && viewValue !== null && viewValue.length > 0
                ? MDSUtils.validateRegexp(viewValue, regexp)
                : true;
        };

        /**
        * Check if the given string has appropriate length.
        *
        * @param {viewValue} value The string to validate.
        * @param {object} settings Object with length value.
        * @return {boolean} true if string has to long length;
        *                   otherwise false.
        */
        $scope.checkMaxLength = function (viewValue, field) {
            var maxLength = $scope.getValidationCriteria(field, 2),
            enabled = false, result = false;
            if (field.validation !== null && maxLength !== '' && (field.validation.criteria[2].enabled && viewValue !== null && viewValue !== undefined && viewValue.toString().length > 0)) {
                enabled = true;
                viewValue = viewValue.toString().length;
            }
            return enabled
                ? MDSUtils.validateMaxLength(viewValue, maxLength)
                : false;
        };

        /**
        * Check if the given string has appropriate length.
        *
        * @param {viewValue} value The string to validate.
        * @param {object} settings Object with length value.
        * @return {boolean} true if string has too short length;
        *                   otherwise false.
        */
        $scope.checkMinLength = function (viewValue, field) {
            var minLength = $scope.getValidationCriteria(field, 1),
            enabled = false;
            if (field.validation !== null && minLength !== '' && (field.validation.criteria[1].enabled && viewValue !== null && viewValue !== undefined && viewValue.toString().length > 0)) {
                enabled = true;
                viewValue = viewValue.toString().length;
            }
            return enabled
                ? MDSUtils.validateMinLength(viewValue, minLength)
                : false;
        };

        /**
        * Check if the given number is not too big number.
        *
        * @param {viewValue} value The number to validate.
        * @param {object} settings Object with max number.
        * @return {boolean} true if value is too big number;
        *                   otherwise false.
        */
        $scope.checkMax = function (viewValue, field) {
            var max = $scope.getValidationCriteria(field, 1),
            enabled = false;
            if (field.validation !== null && max !== '' && (field.validation.criteria[1].enabled && viewValue !== null && viewValue !== undefined && viewValue !== '')) {
                enabled = true;
            }
            return enabled
                ? MDSUtils.validateMaxLength(parseFloat(viewValue), parseFloat(max))
                : false;
        };

        /**
        * Check if the given number is not too small number.
        *
        * @param {viewValue} value The number to validate.
        * @param {object} field Object with min value.
        * @return {boolean} true if value is too small number;
        *                   otherwise false.
        */
        $scope.checkMin = function (viewValue, field) {
            var min = $scope.getValidationCriteria(field, 0),
            enabled = false;
            if (field.validation !== null && min !== '' && (field.validation.criteria[0].enabled && viewValue !== null && viewValue !== undefined && viewValue !== '')) {
                enabled = true;
            }
            return enabled
                ? MDSUtils.validateMin(parseFloat(viewValue), parseFloat(min))
                : false;
        };

        /**
        * Check if the given number be in set.
        *
        * @param {viewValue} value The number to validate.
        * @param {object} field Object with set numbers.
        * @return {boolean} true if value be in set;
        *                   otherwise false.
        */
        $scope.checkInSet = function (viewValue, field) {
            var inset = $scope.getValidationCriteria(field, 2),
            enabled = false;
            if (field.validation !== null && inset !== '' && (field.validation.criteria[2].enabled && viewValue !== null && viewValue !== undefined && viewValue !== '')) {
                enabled = true;
            }
            return enabled
                ? MDSUtils.validateInSet(viewValue, inset)
                : false;
        };

        /**
        * Check if the given number not be in set.
        *
        * @param {viewValue} value The number to validate.
        * @param {object} field Object with set numbers.
        * @return {boolean} true if value not be in set;
        *                   otherwise false.
        */
        $scope.checkOutSet = function (viewValue, field) {
            var outset = $scope.getValidationCriteria(field, 3),
            enabled = false;
            if (field.validation !== null && outset !== '' && (field.validation.criteria[3].enabled && viewValue !== null && viewValue !== undefined && viewValue !== '')) {
                enabled = true;
            }
            return enabled
                ? MDSUtils.validateOutSet(viewValue, outset)
                : false;
        };

    });

    /**
    * The MdsDataBrowserCtrl controller is used on the 'Data Browser' view.
    */
    controllers.controller('MdsDataBrowserCtrl', function ($rootScope, $scope, $http, $location, $state, $stateParams, Entities, Instances, History,
                                $timeout, MDSUtils, Locale, MDSUsers, ModalFactory, LoadingModal) {

        MDSUtils.setCustomOperatorFunctions($scope);

        workInProgress.setActualEntity(Entities, undefined);

        $scope.modificationFields = ['modificationDate', 'modifiedBy'];

        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportColumns = ['All','selected'];
        $scope.availableExportFormats = ['csv','pdf'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'selected';
        $scope.exportFormat = 'csv';
        $scope.checkboxModel = {
            exportWithLookup : true,
            exportWithOrder : false
        };

        $scope.setDataRetrievalError = function (value, responseText) {
            if(responseText) {
                $scope.retrievalErrorText = $scope.msg(responseText.replace('key:', '').trim());
            }

            $scope.$apply(function () {
                $scope.dataRetrievalError = value;
            });
        };

        // checks if we're using URL with entity id
        $scope.checkForEntityId = function () {
            if ($stateParams.entityId !== undefined) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: '../mds/entities/getEntityById?entityId=' + $stateParams.entityId,
                    success: function (data) {
                        $scope.selectedEntity = data;
                    }
                });
            }
            return $scope.selectedEntity;
        };

        $scope.removeIdFromUrl = function () {
            $location.path("mds/dataBrowser");
            $location.replace();
            window.history.pushState(null, "", $location.absUrl());
        };
        
        /**
        * An array perisisting currently hidden modules in data browser view
        */
        $scope.hidden = [];

        /**
        * A map containing names of all entities in MDS, indexed by module names
        */
        $scope.modules = {};

        /**
        * A map containing names of all entities in MDS, and their nonEditable property value
        */
        $scope.entitiesByNonEditable = {};

        /**
        * A map containing names of all entities in MDS, and their readOnlyAccess property value
        */
        $scope.entitiesByReadOnlyAccess = {};

        /**
        * This variable is set after user clicks "View" button next to chosen entity
        */
        $scope.selectedEntity = ($stateParams.entityId === undefined) ? undefined : $scope.checkForEntityId();

        $scope.selectedFields = [];

        /**
        * Fields that belong to a certain lookup
        */
        $scope.lookupFields = [];

        /**
        * Object that represents selected lookup options
        */
        $scope.lookupBy = {};

        /**
        * Object that represents selected filter
        */
        $scope.filterBy = [];

        /**
        * This variable is set after user clicks "Add" button next to chosen entity.
        */
        $scope.addedEntity = undefined;

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
        * This variable contains a JSON string of checked instances of a particular entity.
        */
        $scope.checkedEntitiesJSON = undefined;
        /**
        * This variable contains a number of  checked instances of a particular entity.
        */
        $scope.checkedInstancesLength = undefined;


        /**
        * An array of selected instance fields.
        */
        $scope.loadedFields = [];

        $scope.currentRecord = undefined;

        $scope.allEntityFields = [];

        $scope.availableFieldsForDisplay = [];

        $scope.validatePattern = '';

        $rootScope.filters = [];

        $scope.instanceEditMode = false;

        $scope.showTrash = false;

        $scope.showFilters = true;

        $scope.showTrashInstance = false;

        $scope.instanceRevertable = false;

        $scope.availableLocale = Locale.get();

        $scope.selectedFieldId = 0;

        $scope.availableUsers = MDSUsers.query();

        // fields which won't be persisted in the user preferences
        $scope.autoDisplayFields = [];

        // Gets a JSON string containing the list of checked entities of a entity.
        $scope.getCheckedEntitiesJSON = function (){
            var selectedId = {};
            $('#instancesTable').find('input:checked').each(function(index){
                selectedId[index] =  $(this).parent().parent().attr('id');
            });
            return JSON.stringify(selectedId);
        };

        /**
        * Check if there are any entities to display
        */
        $scope.areModulesEmpty = function (map) {
            if (map && Object.keys(map).length > 0) {
                return false;
            } else {
                return true;
            }
        };

        /**
        * Initializes a map of all entities in MDS indexed by module name
        */
        $scope.setEntities = function () {
            LoadingModal.open();
            $http.get('../mds/entities/byModule').success(function (data) {
                angular.forEach(data, function (entitiesList, moduleName) {
                    $scope.modules[moduleName] = [];
                    angular.forEach(entitiesList, function (entity) {
                        $scope.modules[moduleName].push(entity.name);
                        $scope.entitiesByNonEditable[entity.name] = entity.nonEditable;
                        $scope.entitiesByReadOnlyAccess[entity.name] = entity.readOnlyAccess;
                    });
                });

                LoadingModal.close();
            });
        };

        /**
        * Sets module and entity name
        */
        $scope.setModuleEntity = function (module, entityName) {
            if (module === undefined || module === null) {
                $scope.tmpModuleName = "(No module)";
            } else {
                $scope.tmpModuleName = module;
            }
            $scope.tmpEntityName = entityName;
        };

        /**
        * Sets visible filters panel when filter exist
        */
        $scope.setVisibleIfExistFilters = function () {
            if ($scope.entityAdvanced !== undefined && $scope.entityAdvanced !== null
                && $scope.entityAdvanced.browsing.filterableFields !== null
                && $scope.entityAdvanced.browsing.filterableFields.length > 0) {
                $scope.showFilters = true;
                innerLayout({
                    spacing_closed: 30,
                    east__minSize: 200,
                    east__maxSize: 350
                }, {
                    show: true,
                    button: '#mds-filters'
                });
                } else {
                    $scope.showFilters = false;
                    innerLayout({
                        spacing_closed: 30,
                        east__minSize: 200,
                        east__maxSize: 350
                    }, {
                    show: false
                });
            }
            resizeLayout();
        };

        /**
        * Sets hidden filters panel
        */
        $scope.setHiddenFilters = function () {
            $scope.showFilters = false;
            innerLayout({
                spacing_closed: 30,
                east__minSize: 200,
                east__maxSize: 350
            }, {
            show: false
            });
            resizeLayout();
        };

        $scope.showBackToEntityListButton = true;
        $scope.showAddInstanceButton = true;
        $scope.showDeleteSelectedInstancesButton = true;
        $scope.showDeleteAllInstancesButton = true;
        $scope.showLookupButton = true;
        $scope.showFieldsButton = true;
        $scope.showImportButton = true;
        $scope.showExportButton = true;
        $scope.showViewTrashButton = true;
        $scope.showFiltersButton = true;
        $scope.showDeleteInstanceButton = true;
        $scope.editRelatedFields = null;
        $scope.newRelatedFields = null;
        $scope.relatedMode = {isNested: false};
        $scope.customModals = [];

        $scope.deleteSelectedInstance = function() {
            $('#deleteInstanceModal').modal('show');
        };

        $scope.importEntityInstances = function() {
            $('#importInstanceModal').modal('show');
        };

        $scope.deleteSelectedInstances = function() {
            $scope.checkedEntitiesJSON = $scope.getCheckedEntitiesJSON();
            $scope.checkedInstancesLength = $('#instancesTable').find('input:checked').length;
            $("#deleteSelectedInstancesModal").modal('show');
        };

        $scope.deleteAllInstances = function() {
            $("#deleteAllInstancesModal").modal('show');
        };

        /**
        * Sets selected entity by module and entity name
        */
        $scope.addInstance = function(module, entityName) {
            LoadingModal.open();
            $scope.setHiddenFilters();

            // load the entity if coming from the 'Add' link in the main DataBrowser page
            if (!$scope.selectedEntity) {
                $http.get('../mds/entities/getEntity/' + module + '/' + entityName)
                .success(function (data) {
                    $scope.selectedEntity = data;
                });
            }

            $scope.instanceEditMode = false;
            if (!module) {
                if ($scope.selectedEntity.module === null) {
                    module = '(No module)';
                } else {
                    module = $scope.selectedEntity.module;
                }
            }
            if (!entityName) {
                entityName = $scope.selectedEntity.name;
            }
            $scope.setModuleEntity(module, entityName);
            $scope.addedEntity = Entities.getEntity({
                param:  module,
                params: entityName
            },
            function () {
                Instances.newInstance({id: $scope.addedEntity.id}, function(data) {
                    $scope.currentRecord = data;
                    $scope.fields = data.fields;
                    angular.forEach($scope.fields, function(field) {
                        if ( field.type.typeClass === "java.util.List" && field.value !== null && field.value.length === 0 ) {
                            field.value = null;
                        }
                    });
                    LoadingModal.close();
                });
            });
        };


        $scope.addNewRelatedInstance = function (field) {
            $scope.relatedMode.isNested = true;
            var relatedClass  = $scope.getRelatedClass(field);
            $scope.editedField = angular.copy(field);
            $('ng-form[name=' + field.name + '_field_name] #new-related_' + field.id).modal({backdrop:'static', keyboard: false, show: true});
            $scope.editedInstanceId = undefined;
            $('body > #new-related_' + field.id).on('hide.bs.modal', function () {
                $scope.relatedMode.isNested = false;
                $scope.newRelatedFields = null;
                $scope.editRelatedFields = null;
            });

            $http.get('../mds/entities/getEntityByClassName?entityClassName=' + relatedClass).success(function (data) {
                Instances.newInstance({id: data.id}, function(dataInstance) {
                    $scope.currentRelationRecord = dataInstance;
                    $scope.newRelatedFields = angular.copy(dataInstance.fields);
                    angular.forEach($scope.newRelatedFields, function(field) {
                        if ( field.type.typeClass === "java.util.List" && field.value !== null && field.value.length === 0 ) {
                            field.value = null;
                        }
                    });
                    LoadingModal.close();
                });
            }).error(function(response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.instancesList', 'mds.error', response);
            });

        };

        $scope.cancelAddRelatedForm = function (fieldId) {
            $scope.newRelatedFields = null;
            $scope.relatedMode.isNested = false;
            $('body > #new-related_' + fieldId).modal('hide');
        };

        $scope.cancelEditRelatedForm = function (fieldId) {
            $scope.editRelatedFields = null;
            $scope.relatedMode.isNested = false;
            $('body > #edit-related_' + fieldId).modal('hide');
        };

        /**
        * Sets selected entity by module and entity name
        */
        $scope.editInstance = function(id, module, entityName) {
            LoadingModal.open();
            $scope.setHiddenFilters();
            $scope.instanceEditMode = true;
            $scope.setModuleEntity(module, entityName);
            $scope.loadedFields = Instances.selectInstance({
                id: $scope.selectedEntity.id,
                param: id
                },
                function (data) {
                    $scope.selectedInstance = id;
                    $scope.currentRecord = data;
                    $scope.fields = data.fields;
                    LoadingModal.close();
                }, function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.error.cannotUpdateInstance', 'mds.error', response);
                });
        };

        $scope.editInstanceOfEntity = function(instanceId, entityClassName) {
            ModalFactory.showConfirm({
                title: $scope.msg('mds.confirm'),
                message: $scope.msg('mds.confirm.disabledInstanceChanges'),
                callback: function(result) {
                    if (result) {
                        $scope.previouslyEdited = {
                            instanceId: $scope.selectedInstance,
                            entityClassName: $scope.selectedEntity.className,
                            previouslyEdited: $scope.previouslyEdited
                        };
                        $scope.selectEntityByClassName(entityClassName, function() {
                            $scope.editInstance(instanceId);
                        });
                    }
                }
            });
        };

        /**
        * Sets the selected instance to edit
        */
        $scope.editRelatedInstanceOfEntity = function(instanceId, relatedEntityId, field) {
            $scope.relatedMode.isNested = true;
            instanceId = parseInt(instanceId, 10);
            $scope.editedInstanceId = instanceId;
            $('ng-form[name=' + field.name + '_field_name] #edit-related_' + field.id).modal({backdrop:'static', keyboard: false, show: true});
            var addedNewRecords,
                editExisting = true,
                setExisting = function () {
                    $http.get('../mds/instances/' + relatedEntityId + '/instance/' + instanceId).success(function (data) {
                        $scope.editRelatedFields = angular.copy(data.fields);
                    }).error(function(response) {
                        LoadingModal.close();
                        ModalFactory.showErrorAlertWithResponse('mds.error.instancesList', 'mds.error', response);
                    });
                },
                setExistingWithoutId = function () {
                    var relatedClass = $scope.getRelatedClass(field);
                    $http.get('../mds/entities/getEntityByClassName?entityClassName=' + relatedClass).success(function (data) {
                        relatedEntityId = data.id;
                        setExisting();
                        LoadingModal.close();
                    }).error(function(response) {
                        LoadingModal.close();
                        ModalFactory.showErrorAlertWithResponse('mds.error.instancesList', 'mds.error', response);
                    });
                };

            if (instanceId !== null && field.value.addedNewRecords !== null && field.value.addedNewRecords.length > 0) {
                $scope.editedField = field;
                addedNewRecords = field.value.addedNewRecords;
                if (field.type.defaultName !== "manyToManyRelationship" && field.type.defaultName !== "oneToManyRelationship") {
                    editExisting = false;
                    $scope.safeApply(function () {
                        $scope.editRelatedFields = angular.copy(addedNewRecords[0].fields);
                     });
                } else {
                    angular.forEach(addedNewRecords, function (addedRecord, index) {
                        if (MDSUtils.find(addedNewRecords[index].fields, [{ field: 'name', value: 'id'}], true).value === instanceId) {
                            $scope.$apply(function () {
                                $scope.editRelatedFields = angular.copy(addedNewRecords[index].fields);
                                editExisting = false;
                            return;
                            });
                        }
                    });
                }
                if (editExisting) {
                    if (relatedEntityId === undefined) {
                        setExistingWithoutId();
                    } else {
                        setExisting();
                    }
                }
            } else {
                if (relatedEntityId === undefined) {
                    setExistingWithoutId();
                } else {
                    setExisting();
                }
            }
        };

        $scope.shouldHideEdition = function(field) {
            return field.nonEditable || $scope.previousInstance || $scope.isAutoGenerated(field) || ($scope.selectedEntity && ($scope.selectedEntity.nonEditable || $scope.selectedEntity.readOnlyAccess));
        };

        $scope.shouldHideButton = function() {
            return $scope.selectedEntity && ($scope.selectedEntity.nonEditable || $scope.selectedEntity.readOnlyAccess);
        };

        $scope.closeRelatedEntityModal = function(fieldId) {
            $("body > #instanceBrowserModal_" + fieldId).modal('hide');
        };

        $scope.addRelatedInstance = function(id, entity, field) {
            LoadingModal.open();
            id = parseInt(id, 10);
            $http.get('../mds/instances/' + entity.id + '/field/' + field.id + '/instance/' + id).success(function (data) {
                var closeModal = false;
                if ($scope.editedField.type.defaultName === "manyToManyRelationship"
                    || $scope.editedField.type.defaultName === "oneToManyRelationship") {

                    if ($scope.editedField.value === undefined || $scope.editedField.value === null || $scope.editedField.value === '') {
                        $scope.editedField.value = [];
                        $scope.editedField.displayValue = {};
                    }
                    if (!angular.isObject($scope.editedField.displayValue)) {
                        $scope.editedField.displayValue = {};
                    }
                    if ($scope.editedField.displayValue[id] === undefined) {
                        $scope.editedField.displayValue[id] = data.displayValue;
                        $scope.relatedData.addExisting(field, data.value.id);
                        closeModal = true;
                    } else {
                        ModalFactory.showAlert('mds.info.instanceAlreadyRelated', 'mds.info');
                    }
                } else {
                    if ($scope.editedField.value === null || $scope.editedField.value === undefined || $scope.editedField.value.addedIds === undefined) {
                        $scope.relatedData.initValue(field);
                    }
                    if (parseInt(field.displayValue, 10) !== id) {
                        if (!isNaN(parseInt(field.displayValue, 10))) {
                            $scope.relatedData.setAsRemoved(field, parseInt(field.displayValue, 10));
                        }
                    $scope.relatedData.addExisting($scope.editedField, id);
                    $scope.editedField.displayValue = id;
                    closeModal = true;
                    } else {
                        ModalFactory.showAlert('mds.info.instanceAlreadyRelated', 'mds.info');
                    }
                }
                LoadingModal.close();
                if (closeModal === true) {
                    $scope.closeRelatedEntityModal(field.id);
                }
            }).error(function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.cannotAddRelatedInstance', 'mds.error', response);
            });
        };

        $scope.refreshInstanceBrowserGrid = function() {
            $scope.instanceBrowserRefresh = !$scope.instanceBrowserRefresh;
        };

        $scope.removeOneRelatedData = function(field) {
           field.value = undefined;
        };

        $scope.removeManyRelatedData = function(field, relatedInstance) {
            if (field && relatedInstance !== undefined && relatedInstance.id !== undefined) {
                field.displayValue[relatedInstance.id] = undefined;
                field.value.removeObject(relatedInstance);
            }
        };

        /**
        * Set of methods used to manipulate the data relationships
        */
        $scope.relatedData = {

            initValue: function (field) {
                field.value = {
                    removedIds: [],
                    addedIds: [],
                    addedNewRecords: []
                };
            },

            addNew: function (field, relatedData) {
                var newId = -2147483648;
                $.jgrid.randId();
                newId = newId + $.jgrid.guid;
                angular.forEach(relatedData, function (rData, index) {
                    if (rData.name === 'id' && rData.value === null) {
                        relatedData[index].value = newId;
                        return;
                    }
                });
                if (field.value === null || field.value === "") {
                    $scope.relatedData.initValue(field);
                }
                $scope.safeApply(function () {
                    if (field.type.defaultName !== "manyToManyRelationship" && field.type.defaultName !== "oneToManyRelationship") {
                        field.value.addedIds = [];
                        field.value.addedNewRecords = [];
                        field.displayValue = '';
                    }
                    field.value.addedNewRecords.push({id: null, entitySchemaId: $scope.currentRelationRecord.entitySchemaId, fields: relatedData} );
                });
                $('body > #new-related_' + field.id).modal('hide');
                $scope.newRelatedFields = null;
                $scope.relatedMode.isNested = false;
            },

            addExisting: function (field, relatedDataId) {
            var i = this.checkIfInclude(field, relatedDataId, 'removedIds'),
                checkId = function(item) {
                   return item.name === 'id' && item.value === relatedDataId;
                };
                if (i >= 0 && i !== false) {
                    field.value.removedIds.splice(i, 1);
                    if (field.type.defaultName !== "manyToManyRelationship" && field.type.defaultName !== "oneToManyRelationship") {
                        field.value.addedNewRecords = [];
                    } else if (field.value.addedNewRecords !== undefined && field.value.addedNewRecords.length > 0) {
                        $timeout(function() {
                            for (i = 0; i < field.value.addedNewRecords.length; i += 1) {
                                if (field.value.addedNewRecords[i].fields.filter(checkId).length > 0) {
                                        field.value.addedNewRecords.splice(i, 1);
                                    return;
                                }
                            }
                        }, 250);
                    }
                } else {
                    if (field.value === null || field.value.addedIds === undefined) {
                        $scope.relatedData.initValue(field);
                    }
                    $scope.safeApply(function () {
                        if (field.type.defaultName !== "manyToManyRelationship" && field.type.defaultName !== "oneToManyRelationship") {
                            field.value.addedIds = [];
                            field.value.addedNewRecords = [];
                        }
                        field.value.addedIds.push(relatedDataId);
                    });
                }
            },

            update: function (field, editRelatedFields) {
                var editExisting = true,
                    setExisting = function () {
                        $scope.safeApply(function () {
                            $scope.relatedData.removeFromExisting(field, $scope.editedInstanceId);
                        });
                        $timeout(function() {
                            field.value.addedNewRecords.push({id: $scope.editedInstanceId, entitySchemaId: null, fields: editRelatedFields});
                        }, 150);
                    };
                if (field.type.defaultName !== "manyToManyRelationship" && field.type.defaultName !== "oneToManyRelationship") {
                    if (!isNaN(parseInt(field.displayValue, 10))) {
                        $scope.relatedData.setAsRemoved(field, parseInt(field.displayValue, 10));
                    }
                    field.value.addedIds = [];
                    field.value.addedNewRecords = [];
                    field.value.addedNewRecords.push({id: $scope.editedInstanceId, entitySchemaId: null, fields: editRelatedFields});
                } else {
                    if ($scope.editedInstanceId !== undefined && field.value.addedNewRecords !== null && field.value.addedNewRecords.length > 0) {
                        angular.forEach(field.value.addedNewRecords, function (addedNewRelatedData, index) {
                            if (MDSUtils.find(addedNewRelatedData.fields, [{ field: 'name', value: 'id'}], true).value === $scope.editedInstanceId) {
                                field.value.addedNewRecords[index] = angular.copy({id: $scope.editedInstanceId, entitySchemaId: null, fields: editRelatedFields});
                                editExisting = false;
                            return;
                            }
                        });
                        if (editExisting) {
                            setExisting();
                        }
                    } else {
                        if (field.displayValue !== null && field.displayValue[$scope.editedInstanceId] !== undefined && !isNaN(parseInt(field.displayValue[$scope.editedInstanceId], 10))) {
                            $scope.relatedData.setAsRemoved(field, parseInt($scope.editedInstanceId, 10));
                        }
                        setExisting();
                    }
                }
                $scope.editedInstanceId = undefined;
                $scope.editRelatedFields = null;
                $('body > #edit-related_' + field.id).modal('hide');
                $scope.relatedMode.isNested = false;
            },

            removeFromExisting: function (field, addedId) {
                var i, fieldLength,
                    remove = function () {
                        $scope.safeApply(function () {
                            field.value.addedIds.splice(i, 1);
                        });
                    };
                if (field.value.addedIds !== undefined) {
                    fieldLength = field.value.addedIds.length;
                    for (i = 0; i < fieldLength; i += 1) {
                        if (field.value.addedIds[i] === addedId) {
                            remove();
                        }
                    }
                }
            },

            removeNewAdded: function (field, removeId) {
                if (field !== null && removeId !== undefined) {
                    var i,
                    checkId = function(item) {
                       return item.name === 'id' && item.value === removeId;
                    };
                    if (field.value.addedNewRecords !== null && field.value.addedNewRecords !== undefined) {
                        if (removeId > 0) {
                            $scope.relatedData.setAsRemoved(field, removeId);
                        }
                        $timeout(function() {
                            for (i = 0; i < field.value.addedNewRecords.length; i += 1) {
                                if (field.value.addedNewRecords[i].fields.filter(checkId).length > 0) {
                                        field.value.addedNewRecords.splice(i, 1);
                                    return;
                                }
                            }
                        }, 250);
                    }
                }
            },

            setAsRemoved: function (field, id) {
                var i = this.checkIfInclude(field, id, 'addedIds');
                if (i < 0 && field.value.removedIds !== undefined && field.value.removedIds.indexOf(id) < 0) {
                    $scope.safeApply(function () {
                        field.value.removedIds.push(id);
                        $scope.relatedData.cleanDisplayValue(field, id);
                    });
                } else {
                    $scope.relatedData.removeFromExisting(field, id);
                    $scope.relatedData.cleanDisplayValue(field, id);
                }
            },

            checkIfInclude: function (field, newAddedId, fieldName) {
                var i, fieldLength;
                if (field.value[fieldName] !== undefined) {
                    fieldLength = field.value[fieldName].length;
                    for (i = 0; i < fieldLength; i += 1) {
                        if (field.value[fieldName][i] === newAddedId) {
                            return i;
                        }
                    }
                }
                return -1;
            },

            getRelatedId: function (field) {
                var i;
                if (field.value.addedIds !== null && field.value.addedIds !== undefined && field.value.addedIds.length > 0) {
                    return field.value.addedIds;
                } else {
                    return field.displayValue;
                }
            },

            cleanDisplayValue: function (field, id) {
                if (field.displayValue !== null && field.displayValue[id] !== undefined) {
                    field.displayValue[id] = undefined;
                } else if (field.displayValue !== null && field.displayValue !== undefined) {
                    field.displayValue = '';
                }
            }
        };

        $scope.getRelatedClass = function(field) {
            var i, relatedClass;
            if (field.metadata !== undefined && field.metadata !== null && field.metadata.isArray === true) {
                for (i = 0 ; i < field.metadata.length ; i += 1) {
                    if (field.metadata[i].key === "related.class") {
                        relatedClass = field.metadata[i].value;
                        break;
                    }
                }
            }
            return relatedClass;
        };

        $scope.setRelatedEntity = function(field) {
            var i, relatedClass;
            $('body > #instanceBrowserModal_' + field.id).on('hide.bs.modal', function () {
                $scope.relatedEntity = undefined;
                $scope.filterBy = [];
            });

            relatedClass = $scope.getRelatedClass(field);
            if (relatedClass !== undefined) {
                LoadingModal.open();
                $http.get('../mds/entities/getEntityByClassName?entityClassName=' + relatedClass).success(function (data) {
                    $scope.relatedEntity = data;
                    $scope.editedField = field;
                    $scope.refreshInstanceBrowserGrid();

                    //We need advanced options for related entity e.g. lookups
                    Entities.getAdvancedCommited({id: $scope.relatedEntity.id}, function(data) {
                        $scope.entityAdvanced = data;
                    });

                    //We need related entity fields for lookups
                    Entities.getEntityFields({id: $scope.relatedEntity.id},
                        function (data) {
                            $scope.allEntityFields = data;
                        },
                        function (response) {
                            LoadingModal.close();
                            ModalFactory.showErrorAlertWithResponse('mds.error.instancesList', 'mds.error', response);
                        }
                    );
                    LoadingModal.close();

                }).error(function(response) {
                    ModalFactory.showErrorAlertWithResponse('mds.error.instancesList', 'mds.error', response);
                });
            }
        };

        $scope.downloadBlob = function(fieldName) {
            $http.get('../mds/instances/' + $scope.selectedEntity.id + '/' + $scope.selectedInstance + '/' + fieldName)
            .success(function (data) {
                window.location.replace("../mds/instances/" + $scope.selectedEntity.id + "/" + $scope.selectedInstance + "/" + fieldName);
            })
            .error( function () {
                LoadingModal.close();
                ModalFactory.showErrorAlert('mds.error.cannotDownloadBlob');
            });
        };

        $scope.deleteBlobContent = function() {
            LoadingModal.open();
            $http.get('../mds/instances/deleteBlob/' + $scope.selectedEntity.id + '/' + $scope.selectedInstance + '/' + $scope.selectedFieldId)
            .success( function () {
                LoadingModal.close();
                ModalFactory.showSuccessAlert('mds.delete.deleteBlobContent.success');
            })
            .error( function () {
                LoadingModal.close();
                ModalFactory.showErrorAlert('mds.error.cannotDeleteBlobContent');
            });
        };

        $scope.selectField = function (fieldId) {
            $scope.selectedFieldId = fieldId;
        };

        /**
        * Sets loadedFields from previous instance and determines whether this version
        * of the instance is revertable.
        */
        $scope.historyInstance = function(id) {
            LoadingModal.open();
            if($scope.selectedEntity !== null) {
            $scope.loadedFields = History.getPreviousVersion(
                {
                entityId: $scope.selectedEntity.id,
                instanceId: $scope.selectedInstance,
                param: id},
                function (data) {
                    $scope.previousInstance = id;
                    $scope.fields = data.fields;
                    $scope.instanceRevertable = data.revertable;
                    LoadingModal.close();
                });
            }
        };

        $scope.revertPreviousVersion = function() {
           LoadingModal.open();
           if($scope.selectedEntity !== null) {
               $scope.loadedFields = History.revertPreviousVersion(
               {
                   entityId: $scope.selectedEntity.id,
                   instanceId: $scope.selectedInstance,
                   param: $scope.previousInstance
               },
               function (data) {
                   $scope.previousInstance = undefined;
                   LoadingModal.close();
               }, function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.error.cannotRevert', 'mds.error', response);
               });
           }
        };

        $scope.revertVersionButton = function() {
            if (!$scope.instanceRevertable) {
                return "disabled";
            } else {
                return "";
            }
        };

        /**
        * Revert selected instance from trash
        */
        $scope.revertFromTrash = function(selected) {
            LoadingModal.open();
            $scope.setVisibleIfExistFilters();
            $scope.loadedFields = Instances.revertInstanceFromTrash({
                id: $scope.selectedEntity.id,
                param: selected
            }, function() {
               LoadingModal.close();
               $scope.selectedInstance = undefined;
               $scope.previousInstance = undefined;
               $scope.showTrashInstance = false;
            }, function() {
               LoadingModal.close();
               ModalFactory.showErrorAlert('mds.error.cannotRestoreInstance', 'mds.error');
            });
        };

        /**
        * Get selected instance from trash
        */
        $scope.trashInstance = function(id) {
            LoadingModal.open();
            if($scope.selectedEntity !== null) {
                $scope.instanceEditMode = true;
                $http.get('../mds/entities/' + $scope.selectedEntity.id + '/trash/' + id)
                    .success(function (data) {
                        $scope.setVisibleIfExistFilters();
                        $scope.showTrashInstance = true;
                        $scope.previousInstance = id;
                        $scope.selectedInstance = id;
                        $scope.currentRecord = data;
                        $scope.fields = data.fields;
                        LoadingModal.close();
                    }
                );
            }
        };

        /**
        * Unselects adding or editing instance to allow user to return to entities list by modules
        */
        $scope.unselectInstance = function() {
            var prev;
            $scope.setVisibleIfExistFilters();
            if ($scope.previouslyEdited) {
                prev = $scope.previouslyEdited;
                $scope.selectEntityByClassName(prev.entityClassName, function() {
                    $scope.editInstance(prev.instanceId);
                    $scope.entityClassName = prev.entityClassName;
                    $scope.previouslyEdited = prev.previouslyEdited;
                });
            } else {
                $scope.entityAdvanced = undefined;
                if ($scope.entityClassName) {
                    $scope.selectEntityByClassName($scope.entityClassName);
                } else {
                    $scope.selectEntity($scope.tmpModuleName, $scope.tmpEntityName);
                }
                $scope.addedEntity = undefined;
                $scope.selectedInstance = undefined;
                $scope.loadedFields = undefined;
                if ($state.current.parent === "mds") {
                    $state.reload();
                } else {
                    $state.transitionTo($state.current);
                }
            }
            $scope.cancelAddRelatedForm();
            $scope.cancelEditRelatedForm();
            resizeLayout();
        };

        /**
        *
        * Saves the entity instance after the user clicks save
        *
        */
        $scope.addEntityInstance = function () {
            LoadingModal.open();

            var values = $scope.currentRecord.fields;
            angular.forEach (values, function(value, key) {
                if(value.type.typeClass === "java.lang.String" && value.value === "" && value.name !== "creator"){
                    value.value = null;
                }
                value.value = value.value === 'null' ? null : value.value;
            });

            $scope.currentRecord.$save(function() {
                $scope.unselectInstance();
                LoadingModal.close();
            }, function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.cannotAddInstance', 'mds.error', response);
            });
        };

        /**
        * Deletes an instance of the currently selected entity, with id "selected".
        */
        $scope.deleteInstance = function (selected) {
            LoadingModal.open();
            Instances.deleteInstance({
                id: $scope.selectedEntity.id,
                param: selected
            }, function() {
                $scope.unselectInstance();
                LoadingModal.close();
            }, function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.cannotDeleteInstance', 'mds.error', response);
            });
        };

        /**
        * Delete every instances of the currently selected entity.
        */
        $scope.deleteEveryInstance = function(){
            LoadingModal.open();
            Instances.deleteAllInstances({
                id:$scope.selectedEntity.id
            }, function() {
                $scope.unselectInstance();
                LoadingModal.close();
            }, function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.cannotDeleteInstance', 'mds.error', response);
            });
        };

        /**
        * Deletes every selected instances of the currently selected entity.
        */
        $scope.deleteEverySelectedInstance = function (selected) {
            LoadingModal.open();
            Instances.deleteSelectedInstances({
                id: $scope.selectedEntity.id,
                param: selected
            }, function() {
                $scope.unselectInstance();
                LoadingModal.close();
            }, function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.cannotDeleteSelectedInstances', 'mds.error', response);
            });
        };

        /**
        * Find field setting with given name.
        *
        * @param {Array} settings A array of field settings.
        * @param {string} name This value will be used to find setting.
        * @return {object} a single object which represent setting with the given name.
        */
        $scope.findSettingByName = function (settings, name) {
            return MDSUtils.find(settings, [{field: 'name', value: name}], true);
        };

        /**
        * Sets selected instance history by id
        */
        $scope.selectInstanceHistory = function (instanceId) {
            LoadingModal.open();
            History.getHistory({
                entityId: $scope.selectedEntity.id,
                instanceId: instanceId
            }, function () {
                LoadingModal.close();
                $scope.previousInstance = undefined;
                $scope.instanceId = instanceId;
            }, function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.historyRetrievalError', 'mds.error', response);
            });
        };

        $scope.backToInstance = function() {
            $scope.unselectInstanceHistory();
            $scope.editInstance($scope.selectedInstance);
            $scope.updateInstanceGridFields();
        };

        /**
        * Unselects instace history to allow user to return to instance view
        */
        $scope.unselectInstanceHistory = function() {
            // Temporary - should return to instance view
            $scope.instanceId = undefined;
            $scope.previousInstance = undefined;
            $scope.updateInstanceGridFields();
        };

        /**
        * Select view trash
        */
        $scope.showInstancesTrash = function () {
            $scope.showTrash = true;
            $scope.setHiddenFilters();
            $scope.showTrashInstance = false;
            $scope.updateInstanceGridFields($scope.modificationFields);
        };

        /**
        * Select view entity instance list
        */
        $scope.hideInstancesTrash = function () {
            $scope.showTrash = false;
            $scope.setVisibleIfExistFilters();
            $scope.showTrashInstance = false;
            $scope.updateInstanceGridFields();
        };

        /**
        * Select view entity instances trash
        */
        $scope.backInstancesTrash = function () {
            $scope.showTrash = false;
            $scope.showTrashInstance = false;
            $scope.setHiddenFilters();
            $scope.selectedInstance = undefined;
            $scope.previousInstance = undefined;
            $scope.hideInstancesTrash();
            $scope.showInstancesTrash();
            $timeout(function() {
                $scope.updateInstanceGridFields($scope.modificationFields);
            });
        };

        /**
        * Sets selected entity by module and entity name
        */
        $scope.selectEntity = function (module, entityName) {
            // get entity, fields, display fields
            $scope.dataRetrievalError = false;
            $scope.retrieveAndSetEntityData('../mds/entities/getEntity/' + module + '/' + entityName);
            $scope.showFilters = false;
        };

        /**
        * Sets selected entity by the entities className
        */
        $scope.selectEntityByClassName = function(entityClassName, callback) {
            // get entity, fields, display fields
            $scope.retrieveAndSetEntityData('../mds/entities/getEntityByClassName?entityClassName=' + entityClassName,
                callback);
        };

        $scope.setAvailableFieldsForDisplay = function() {
            var i;
            $scope.availableFieldsForDisplay = [];
            for (i = 0; i < $scope.allEntityFields.length; i += 1) {
                if (!$scope.allEntityFields[i].nonDisplayable) {
                    $scope.availableFieldsForDisplay.push($scope.allEntityFields[i]);
                }
            }
        };

        $scope.retrieveAndSetEntityData = function(entityUrl, callback) {
          $scope.lookupBy = {};
          $scope.selectedLookup = undefined;
          $scope.lookupFields = [];
          $scope.allEntityFields = [];

          LoadingModal.open();

          $http.get(entityUrl).success(function (data) {
              $scope.selectedEntity = data;

              $scope.setModuleEntity($scope.selectedEntity.module, $scope.selectedEntity.name);

              $http.get('../mds/entities/'+$scope.selectedEntity.id+'/entityFields').success(function (data) {
                   $scope.allEntityFields = data;
                   $scope.setAvailableFieldsForDisplay();

                   if ($stateParams.entityId === undefined) {
                      var hash = window.location.hash.substring(2, window.location.hash.length) + "/" + $scope.selectedEntity.id;
                      $location.path(hash);
                      $location.replace();
                      window.history.pushState(null, "", $location.absUrl());
                   }

                   Entities.getAdvancedCommited({id: $scope.selectedEntity.id}, function(data) {
                      $scope.entityAdvanced = data;
                      $rootScope.filters = [];
                      $scope.setVisibleIfExistFilters();

                      var filterableFields = $scope.entityAdvanced.browsing.filterableFields,
                          i, field, types;
                      for (i = 0; i < $scope.allEntityFields.length; i += 1) {
                          field = $scope.allEntityFields[i];

                          if ($.inArray(field.id, filterableFields) >= 0) {
                              types = $scope.filtersForField(field);

                              $rootScope.filters.push({
                                  displayName: field.basic.displayName,
                                  type: field.type.typeClass,
                                  field: field.basic.name,
                                  types: types
                              });
                          }
                      }

                      $scope.selectedFields = [];
                      for (i = 0; i < $scope.allEntityFields.length; i += 1) {
                          field = $scope.allEntityFields[i];
                          if ($.inArray(field.basic.name, $scope.entityAdvanced.userPreferences.visibleFields) !== -1) {
                              $scope.selectedFields.push(field);
                          }
                      }
                      $scope.updateInstanceGridFields();

                      if (callback) {
                          callback();
                      }

                      LoadingModal.close();
                   });
                });
            });
        };

        $scope.$on('$stateChangeSuccess', function() {
            if ($stateParams.entityId !== undefined) {
                $scope.retrieveAndSetEntityData('../mds/entities/getEntityById?entityId=' + $stateParams.entityId);
            }
        });

        $scope.getMetadata = function(field, key) {
            var i, result = '';
            if (field.metadata) {
                for (i = 0; i < field.metadata.length; i += 1) {
                    if (_.isEqual(field.metadata[i].key, key)) {
                        result = field.metadata[i].value;
                    }
                }
            }
            return result;
        };

        $scope.isRelationshipField = function(field) {
            return Boolean($scope.getMetadata(field, 'related.class'));
        };

        $scope.isDateField = function(field) {
            return field.type.typeClass === "org.joda.time.LocalDate" || field.type.typeClass === "java.time.LocalDate";
        };

        $scope.isTextArea = function (field) {
            return (field.type !== undefined && field.type.typeClass === "textArea") ? true : false;
        };

        $scope.isMapField = function(field) {
            return (field && field.type && field.type.typeClass === "java.util.Map") ? true : false;
        };

        $scope.isComboboxField = function(field) {
             return (field && field.type && field.type.typeClass === "java.util.Collection") ? true : false;
        };

        $scope.isMultiSelectCombobox = function(field) {
            var result;
            angular.forEach(field.settings, function(setting) {
                if (setting.name === "mds.form.label.allowMultipleSelections") {
                    result = setting.value;
                }
            });
            return result;
        };

        $scope.isFieldSelected = function(name) {
            var i;
            for (i = 0; i < $scope.selectedFields.length; i += 1) {
                if ($scope.selectedFields[i] && $scope.selectedFields[i].basic.name === name) {
                    return true;
                }
            }
            return false;
        };

        $scope.selectFieldByName = function(name) {
            var i, field;
            for (i = 0; i < $scope.allEntityFields.length; i += 1) {
                field = $scope.allEntityFields[i];
                if (field && field.basic.name === name) {
                    $scope.selectedFields.push(field);
                    return;
                }
            }
        };

        $scope.addFieldsForDataBrowser = function(checked) {
            var field, i, fieldsData = {
                field: '',
                action: 'ADD_ALL'
            };

            if (!checked) {
                fieldsData.action = 'REMOVE_ALL';
                $scope.selectedFields = [];
            } else {
                $scope.selectedFields = [];
                for (i = 0; i < $scope.availableFieldsForDisplay.length; i += 1) {
                    $scope.selectedFields.push(field);
                }
            }

            $http.post('../mds/entities/' + $scope.selectedEntity.id + "/preferences/fields", fieldsData)
            .error(function () {
                LoadingModal.close();
                ModalFactory.showErrorAlert('mds.preferences.error.fields');
            });
        };

        $scope.addFieldForDataBrowser = function(selected, checked) {
            var field, i, fieldsData = {
                field: selected,
                action: 'ADD'
            };

            if (!checked) {
                fieldsData.action = 'REMOVE';
                if ($scope.isFieldSelected(selected)) {
                    for (i = 0; i < $scope.selectedFields.length; i += 1) {
                        field = $scope.selectedFields[i];
                        if (field && field.basic.name === selected) {
                            $scope.selectedFields.remove(i, i);
                        }
                    }
                }
            } else {
                if (!$scope.isFieldSelected(selected)) {
                    for (i = 0; i < $scope.availableFieldsForDisplay.length; i += 1) {
                        field = $scope.allEntityFields[i];
                        if (field && field.basic.name === selected) {
                            $scope.selectedFields.push(field);
                        }
                    }
                }
            }

            $http.post('../mds/entities/' + $scope.selectedEntity.id + "/preferences/fields", fieldsData)
            .error(function () {
                LoadingModal.close();
                ModalFactory.showErrorAlert('mds.preferences.error.fields');
            });
        };

        $scope.filtersForField = function(field) {
            var type = field.type.typeClass;
            if (type === "java.lang.Boolean") {
                return ['ALL', 'YES', 'NO'];
            } else if (type === "java.util.Date" || type === "org.joda.time.DateTime" || type === "org.joda.time.LocalDate" || type === "java.time.LocalDateTime" || type === "java.time.LocalDate") {
                return ['ALL', 'TODAY', 'PAST_7_DAYS', 'THIS_MONTH', 'THIS_YEAR'];
            } else if (type === "java.util.Collection") {
                return  ['ALL'].concat($scope.getComboboxValues(field.settings));
            }
        };

        $rootScope.msgForFilter = function(filter) {
            var mdsFilterMessage = $scope.msg("mds.filter." + filter.toLowerCase());
            if (mdsFilterMessage === "[mds.filter."+filter.toLowerCase()+"]") {
                return filter;
            } else {
                return mdsFilterMessage;
            }
        };

        $scope.getLookupIds = function(lookupFields) {
            var i, ids = [];
            for (i = 0; i < lookupFields.length; i += 1) {
                ids.push(lookupFields[i].id);
            }
            return ids;
        };

        /**
        * Marks passed lookup as selected. Sets fields that belong to the given lookup and resets lookupBy object
        * used to filter instances by given values
        */
        $scope.selectLookup = function(lookup) {
            var i;

            $scope.selectedLookup = lookup;
            $scope.lookupFields = lookup.lookupFields;
            $scope.filterBy = [];
            $scope.lookupBy = {};
        };

        $scope.buildLookupFieldName = function (field) {
            if (field.relatedName !== undefined && field.relatedName !== '' && field.relatedName !== null) {
                return field.name + "." + field.relatedName;
            }
            return field.name;
        };

        /**
        * Depending on the field type, includes proper html file containing visual representation for
        * the object type. Radio input for boolean, select input for list and text input as default one.
        */
        $scope.loadInputForLookupField = function(field) {
            var value = "default", type = "field", file;

            if (field.className === "java.lang.Boolean") {
                value = "boolean";
            } else if (field.className === "java.util.Collection") {
                value = "list";
            } else if (field.className === "org.joda.time.DateTime" || field.className === "java.util.Date" || field.className === "java.time.LocalDateTime") {
                value = "datetime";
            } else if (field.className === "org.joda.time.LocalDate" || field.className === "java.time.LocalDate") {
                value = "date";
            }

            if ($scope.isRangedLookup(field)) {
                type = 'range';
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = {min: '', max: ''};
                }
            } else if ($scope.isSetLookup(field)) {
                type = 'set';
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = [];
                }
            }

            return '../mds/resources/partials/widgets/lookups/{0}-{1}.html'
                .format(type, value);
        };

        $scope.isRangedLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'RANGE');
        };

        $scope.isSetLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'SET');
        };

        $scope.isLookupFieldOfType = function(field, type) {
            var i, lookupField;
            for (i = 0; i < $scope.selectedLookup.lookupFields.length; i += 1) {
                lookupField = $scope.selectedLookup.lookupFields[i];
                if ($scope.buildLookupFieldName(lookupField) === $scope.buildLookupFieldName(field)) {
                    return lookupField.type === type;
                }
            }
        };

        $scope.addSetMember = function(set) {
            set.push({val: ''});
        };

        $scope.removeSetMember = function(set, index) {
            set.splice(index, 1);
        };

        /**
        * Hides lookup dialog and sends signal to refresh the grid with new data
        */
        $scope.filterInstancesByLookup = function() {
            if ($scope.relatedEntity === undefined) {
                $scope.showLookupDialog();
                $scope.refreshGrid();
            } else {
                $scope.showLookupRelatedInstancesDialog();
                $scope.refreshInstanceBrowserGrid();
            }
        };

        $scope.refreshGrid = function() {
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
        $rootScope.unselectEntity = function () {
            $scope.entityAdvanced = undefined;
            $scope.dataRetrievalError = false;
            innerLayout({
                spacing_closed: 30,
                east__minSize: 200,
                east__maxSize: 350
            });
            $scope.selectedEntity = undefined;
            $scope.removeIdFromUrl();
            $scope.showFilters = false;
            resizeLayout();
        };

        $scope.backToEntityList = $scope.unselectEntity;

        $rootScope.selectFilter = function(field, value, type) {
            $scope.lookupBy = {};
            $scope.selectedLookup = undefined;
            $scope.lookupFields = [];

            if (value !== "" && value !== "ALL") {
                $scope.updateFilter(field, value, type);
            } else {
                $scope.removeFilter(field);
            }
            LoadingModal.open();
            $scope.refreshGrid();
            LoadingModal.close();
        };

        $scope.updateFilter = function(field, value, type) {

            if ($scope.fieldInFilter(field)) {
                if ($scope.typeIsDate(type) || $scope.typeIsTime(type)) {
                    if ($scope.filterBy[$scope.getIndexOfField(field)].values.indexOf(value) === -1) {
                        $scope.filterBy[$scope.getIndexOfField(field)].values = [ value ];
                    } else {
                        $scope.removeFilter(field);
                    }
                } else {
                    var fieldIndex, valueIndex;
                    fieldIndex = $scope.getIndexOfField(field);
                    valueIndex = $scope.filterBy[fieldIndex].values.indexOf(value);

                    if ($scope.filterBy[fieldIndex].values.indexOf(value) === -1) {
                        $scope.filterBy[fieldIndex].values.push(value);
                    } else {
                        $scope.filterBy[fieldIndex].values.splice(valueIndex, 1);
                        if ($scope.filterBy[fieldIndex].values.length === 0) {
                            $scope.removeFilter(field);
                        }
                    }
                }
            } else {
                $scope.filterBy.push({
                    field: field,
                    values: [ value ]
                });
            }
        };

        $scope.typeIsDate = function (type) {
            return type === "java.util.Date" || type === "org.joda.time.DateTime" || type === "org.joda.time.LocalDate" || type === "java.time.LocalDateTime" || type === "java.time.LocalDate";
        };

        $scope.typeIsTime = function (type) {
            return type === "org.motechproject.commons.date.model.Time";
        };

        $scope.removeFilter = function(field) {
            if ($scope.fieldInFilter(field)) {
                $scope.filterBy.splice($scope.getIndexOfField(field), 1);
            }
        };


        $scope.fieldInFilter = function (field) {
            var pos;
            for (pos = 0; pos < $scope.filterBy.length; pos += 1) {
                if ($scope.filterBy[pos].field === field) {
                    return true;
                }
            }
            return false;
        };

        $scope.getIndexOfField = function (field) {
            var pos;
            for (pos = 0; pos < $scope.filterBy.length; pos += 1) {
                if ($scope.filterBy[pos].field === field) {
                    return pos;
                }
            }
            return -1;
        };

        $scope.exportEntityInstances = function () {
            $scope.checkboxModel.exportWithLookup = true;
            $('#exportInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportColumns = function (columns) {
            $scope.actualExportColumns = columns;
        };

        $scope.setDefaultExportColumns = function () {
            $scope.actualExportColumns = 'selected';
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportInstanceModal = function () {
            $('#exportInstanceForm').resetForm();
            $('#exportInstanceModal').modal('hide');
        };

        /**
        * Exports selected entity's instances to CSV file
        */
        $scope.exportInstance = function() {
            var selectedFieldsName = [], url, sortColumn, sortDirection;

            url = "../mds/entities/" + $scope.selectedEntity.id + "/exportInstances";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            if ($scope.actualExportColumns === 'selected') {
                angular.forEach($scope.selectedFields, function(selectedField) {
                    selectedFieldsName.push(selectedField.basic.displayName);
                });

                url = url + "&selectedFields=" + selectedFieldsName;
            }

            if ($scope.checkboxModel.exportWithOrder === true) {
                sortColumn = $('#instancesTable').getGridParam('sortname');
                sortDirection = $('#instancesTable').getGridParam('sortorder');

                url = url + "&sortColumn=" + sortColumn;
                url = url + "&sortDirection=" + sortDirection;
            }

            if ($scope.checkboxModel.exportWithLookup === true && $scope.selectedLookup !== undefined) {
                url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
                // in lookup fields the special characters may appear (for example '+' before timezone),
                // we have to encode them before passing this url
                url = url + "&fields=" + encodeURIComponent(JSON.stringify($scope.lookupBy));
            }

            $http.get(url)
            .success(function () {
                $('#exportInstanceForm').resetForm();
                $('#exportInstanceModal').modal('hide');
                window.location.replace(url);
            })
            .error(function (response) {
                LoadingModal.close();
                ModalFactory.showErrorAlertWithResponse('mds.error.exportData', 'mds.error', response);
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

        $scope.expandAll = function () {
            $scope.hidden.length = 0;
        };

        $scope.collapseAll = function () {
            angular.forEach($scope.modules, function (entities, module) {
                if ($scope.visible(module)) {
                    $scope.hidden.push(module);
                }
            });
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

        $scope.getInstanceFieldValue = function(fieldName) {
            var i, requestedField, val, field;
            if ($scope.fields) {
                for (i = 0; i < $scope.fields.length; i += 1) {
                    field = $scope.fields[i];

                    if (field.name === fieldName) {
                        requestedField = field;
                        break;
                    }
                }
            }
            if (requestedField) {
                val = requestedField.value;
            }
            return val;
        };

        /*
        * Gets validation criteria values.
        */
        $scope.getValidationCriteria = function (field, id) {
            var validationCriteria = '',
                value = $scope.getTypeSingleClassName(field.type);

            if (field.validation !== null && field.validation.criteria[id].enabled && field.validation.criteria[id].value !== null) {
                validationCriteria = field.validation.criteria[id].value;
            }
            return validationCriteria;
        };

        /**
        * Construct appropriate url according with a field type for form used to set correct
        * value of edit value property.
        */
        $scope.loadEditValueForm = function (field, isNestedField) {
            var value = angular.copy($scope.getTypeSingleClassName(field.type));

            if (value === 'boolean') {

                if (field.value === true) {
                    field.value = 'true';

                } else if (field.value === false) {
                    field.value = 'false';
                }
            } else if (value === 'combobox' && field.settings[2].value) {
                if (MDSUtils.find(field.settings, [{field: 'name', value: 'mds.form.label.allowMultipleSelections'}], true).value) {
                    value = 'combobox-multi';
                }
            } else if (value === 'string' && field.name === 'owner') {
                value = 'string-owner';
            } else if (value === 'oneToMany' && ($scope.newRelatedFields !== null || $scope.editRelatedFields !== null) && isNestedField === true) {
                value = 'oneToManyRelated';
            } else if (value === 'manyToMany' && ($scope.newRelatedFields !== null || $scope.editRelatedFields !== null) && isNestedField === true) {
                value = 'manyToManyRelated';
            } else if (value === 'manyToOne' && ($scope.newRelatedFields !== null || $scope.editRelatedFields !== null) && isNestedField === true) {
                value = 'manyToOneRelated';
            } else if (value === 'oneToone' && ($scope.newRelatedFields !== null || $scope.editRelatedFields !== null) && isNestedField === true) {
                value = 'oneToOneRelated';
            }
            return '../mds/resources/partials/widgets/field-edit-Value-{0}.html'
                          .format(value.substring(value.toLowerCase()));
        };

        /*
        * Gets type information from TypeDto object.
        */
        $scope.getTypeSingleClassName = function (type) {
            return type.displayName.substring(type.displayName.lastIndexOf('.') + 1);
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
        $scope.checkDecimalValue = function (numberInput, settings) {
            var precision = $scope.findSettingByName(settings, 'mds.form.label.precision'),
                scale = $scope.findSettingByName(settings, 'mds.form.label.scale'),
                number = parseFloat(numberInput);

            return _.isNumber(parseFloat(number))
                ? MDSUtils.validateDecimal(parseFloat(number), precision.value, scale.value)
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
            return $scope.visible(module) ? "fa-caret-down" : "fa-caret-right";
        };

        /**
        * Shows/Hides lookup dialog
        */
        $scope.showLookupDialog = function() {
            $("#lookup-dialog")
            .css({'top': ($("#lookupDialogButton").offset().top - $("#main-content").offset().top)-40,
            'left': ($("#lookupDialogButton").offset().left - $("#main-content").offset().left)-70})
            .toggle();
        };

        /**
        * Shows/Hides lookup dialog when search related instances
        */
        $scope.showLookupRelatedInstancesDialog = function() {
            $("#lookup-related-instances-dialog")
            .css({'top': ($(".instanceBrowserModal").offset().top + 50),
            'left': ($(".instanceBrowserModal").offset().left) - 30})
            .toggle();
        };

        $scope.isAutoGenerated = function (field) {
            return hasMetadata(field, 'autoGenerated', 'true');
        };

        $scope.shouldShowInputForField = function(field) {
            return !$scope.isAutoGenerated(field) && !field.nonDisplayable;
        };

        /**
        * Handles hiding lookup dialog while clicking outside the dialog
        */
        $(document).mouseup(function (e) {
            var container = $("#lookup-dialog"),
            button = $("#lookupDialogButton"),
            date_picker = $("#ui-datepicker-div");

            if (!container.is(e.target) && container.has(e.target).length === 0 &&
                !button.is(e.target) && button.has(e.target).length === 0 && container.is(":visible")) {
                if(!date_picker.is(e.target) && date_picker.has(e.target).length === 0){
                    $scope.showLookupDialog();
                }
                return;
            }

            container = $("#lookup-related-instances-dialog");
            button = $("#lookupRelatedInstanceButton");
            if (!container.is(e.target) && container.has(e.target).length === 0 &&
                !button.is(e.target) && button.has(e.target).length === 0 && container.is(":visible")) {
                $scope.showLookupRelatedInstancesDialog();
            }
        });

        $scope.getFieldName = function(displayName) {
            var result;
            angular.forEach($scope.allEntityFields, function(field) {
                if (field.basic.displayName === displayName) {
                    result = field.basic.name;
                }
            });
            return result;
        };

        $scope.updateInstanceGridFields = function(fieldsToSelect) {
            angular.forEach($("select.multiselect")[0], function(field) {
                var name = $scope.getFieldName(field.label), selected = false;

                // this fields won't be used for user preferences
                $scope.autoDisplayFields = fieldsToSelect || [];

                if (name) {
                    angular.forEach($scope.selectedFields, function(selectedField) {
                        if (selectedField.basic.name === name) {
                            selected = true;
                        }
                    });

                    if (!selected && fieldsToSelect && $.inArray(name, fieldsToSelect) !== -1) {
                        selected = true;
                    }

                    if (selected) {
                        $timeout(function() {
                            $($(".multiselect-container").find(":checkbox")).each(function() {
                                if (field.label === $.trim($(this).parent().text()) && !this.checked) {
                                    $(this).click();
                                }
                            });
                        });
                    }
                }
            });
        };

        $scope.initHistoryGrid = function() {
            $timeout(function() {
                $scope.updateInstanceGridFields($scope.modificationFields);
            });
        };

        $scope.printDateTime = function(dt) {
            return moment(dt, 'YYYY-MM-DD HH:mm ZZ').format('YYYY-MM-DD, HH:mm');
        };
        /**
        * Return available users.
        *
        * @return {Array} A array of possible users.
        */
        $scope.getUsers = function () {
            var users = [];
            angular.forEach($scope.availableUsers,
                function(value, key) {
                    this.push(value.userName);
                }, users);
            return  users;
        };

        $scope.anyValidationCriteria = function (field) {
            var exist = false, i,
            fieldValCriteriaLength;

            if (field.validation !== null && field.validation !== undefined) {
                fieldValCriteriaLength = field.validation.criteria.length;
                for (i = 0; i <= fieldValCriteriaLength - 1; i += 1) {
                    if (field.validation.criteria[i].enabled && field.validation.criteria[i].value !== null) {
                        exist = true;
                    }
                }
            }
            return exist;
        };

        $scope.printResult = function (response) {
            $scope.currentError = null;
            $scope.failRows = 0;
            $scope.successRows = 0;
            $('#errorPanel').collapse({'toggle': false});
            $('#successPanel').collapse({'toggle': false});
            if (Object.keys(response.rowErrors).length === 0) {
                $scope.successRows = Object.keys(response.newInstanceIDs).length
                                    + Object.keys(response.updatedInstanceIDs).length;
                $('#successPanel').collapse('show');
            } else {
                $scope.failRows = Object.keys(response.rowErrors).length;
                $scope.successRows = Object.keys(response.newInstanceIDs).length
                                    + Object.keys(response.updatedInstanceIDs).length;
                $scope.currentError = response.rowErrors;
                $('#errorPanel').collapse('show');
            }
            $('#importResultModal').modal('show');
        };

        $scope.importInstance = function () {
            LoadingModal.open();

            $('#importInstanceForm').ajaxSubmit({
                success: function (response) {
                    $("#instancesTable").trigger('reloadGrid');
                    $('#importInstanceForm').resetForm();
                    $('#importInstanceModal').modal('hide');
                    $scope.printResult(response);
                    LoadingModal.close();
                },
                error: function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.error.importCsv', 'mds.error', response);
                }
            });
        };

        $scope.closeImportInstanceModal = function () {
            $('#importInstanceForm').resetForm();
            $('#importInstanceModal').modal('hide');
        };

        $scope.closeImportResultModal = function () {
            $('#errorPanel').collapse('hide');
            $('#successPanel').collapse('hide');
            $('#importResultModal').modal('hide');
         };

        $scope.preselectEntity = function (entityModule, entityName) {
            $scope.$parent.selectedEntity = { module: entityModule,
                                              name: entityName };
        };

        $scope.validateRelationshipPresence = function(field) {
            if (!field.required) {
                return true;
            } else {
                return (field.value === undefined || field.value === null) ? false :
                        (field.value.constructor === Array ? field.value.length > 0 : field.value);
            }
        };

        $scope.checkForModuleConfig = function () {
            if ($stateParams.moduleName !== undefined) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: '../' + $stateParams.moduleName + '/mds-databrowser-config',
                    success: function (data) {
                        /*jslint evil:true */
                        function jsEval(src){
                            return eval(src);
                        }
                        jsEval.call($scope, data);
                    }
                });
            }
            if ($stateParams.instanceId !== undefined) {
                $scope.editInstance($stateParams.instanceId, $scope.selectedEntity.module, $scope.selectedEntity.name);
            }
        };

        $scope.checkForModuleConfig();

        $scope.relatedId = function (obj) {
            if (obj.id) {
                return obj.id;
            } else {
                return obj;
            }
        };

        $scope.saveColumnWidth = function (fieldName, width) {
            var cookieName = $scope.getTableWidthCookieName($scope.selectedEntity),
                data = $scope.getColumnsWidth();
            data[fieldName] = width;
            $.cookie(cookieName, JSON.stringify(data), { expires : 7 });
        };

        $scope.getColumnsWidth = function() {
            var cookieName = $scope.getTableWidthCookieName($scope.selectedEntity);
            if ($.cookie(cookieName)) {
               return JSON.parse($.cookie(cookieName));
            }
            return {};
        };

        $scope.getTableWidthCookieName = function(entity) {
            var username = $rootScope.username || '';
            return username + '_table.width_org.motechproject.mds.databrowser.fields.' + entity.className;
        };
    });

    /**
    * The MdsFilterCtrl controller is used on the 'Data Browser' view for the right panel.
    */
    controllers.controller('MdsFilterCtrl', function ($rootScope, $scope) {
        var filtersDate;
        innerLayout({});
    });

    /**
    * The MdsSettingsCtrl controller is used on the 'Settings' view.
    */
    controllers.controller('MdsSettingsCtrl', function ($scope, $http, Entities, MdsSettings, FileUpload, ModalFactory, LoadingModal) {
        var getExportEntities, groupByModule;

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });
        workInProgress.setActualEntity(Entities, undefined);

        getExportEntities = function() {
            var exportEntities = [];
            Entities.query(function (entities) {
                angular.forEach(entities, function(entity) {
                    var moduleName = entity.module === null ? "MDS" : entity.module;

                    exportEntities.push({
                        entityName: entity.className,
                        moduleName: moduleName,
                        includeSchema: false,
                        includeData: false,
                        canIncludeSchema: true,
                        canIncludeData: true
                    });
                });
                $scope.exportEntities = exportEntities;
                $scope.groupedExportEntities = groupByModule($scope.exportEntities);
                $scope.groupedExportEntitiesLength = $scope.groupedLength;
            });
        };

        groupByModule = function (entities) {
            var groups = {}, groupLength = 0;
            angular.forEach(entities, function (entity) {
                var group = groups[entity.moduleName];
                if (!group) {
                    groupLength = groupLength + 1;
                    group = [];
                    groups[entity.moduleName] = group;
                }
                group.push(entity);
            });
            $scope.groupedLength = groupLength;
            return groups;
        };

        $scope.settings = MdsSettings.getSettings();

        $scope.gridSizes = [10, 20, 50, 100];

        $scope.timeUnits = [
            { value: 'HOURS', label: $scope.msg('mds.dateTimeUnits.hours') },
            { value: 'DAYS', label: $scope.msg('mds.dateTimeUnits.days') },
            { value: 'WEEKS', label: $scope.msg('mds.dateTimeUnits.weeks') },
            { value: 'MONTHS', label: $scope.msg('mds.dateTimeUnits.months') },
            { value: 'YEARS', label: $scope.msg('mds.dateTimeUnits.years') }
        ];

        $scope.importId = null;
        $scope.importFile = null;
        $scope.importEntities = [];
        $scope.groupedImportEntities = {};

        $scope.exportEntities = [];
        $scope.groupedExportEntities = {};
        $scope.searchText = "";

        getExportEntities();

        /**
        * This function checking if input and select fields for time selection should be disabled.
        * They are only enabled when deleting is set to "trash" and checkbox is selected
        */
        $scope.checkTimeSelectDisable = function () {
            return $scope.settings.deleteMode !== "TRASH" || !$scope.settings.emptyTrash;
        };

        /**
        * This function checking if checkbox in UI should be disabled. It is should be enabled when deleting is set to "trash"
        */
        $scope.checkCheckboxDisable = function () {
            return $scope.settings.deleteMode !== "TRASH";
        };

        /**
        * This function it is called when we change the radio. It's used for dynamically changing availability to fields
        */
        $scope.checkDisable = function () {
            $scope.checkTimeSelectDisable();
            $scope.checkCheckboxDisable();
        };

        /**
        * Get imported file and sends it to controller. Import manifest is then constructed
        * based on returned data.
        */
        $scope.importUploadFile = function () {
            LoadingModal.open();
            FileUpload.upload($scope.importFile, '../mds/settings/importUploadFile',
            function(data) {
                $scope.importId = data.importId;
                $scope.importEntities = data.records;
                angular.forEach(data.records, function (record) {
                        record.includeSchema = record.canIncludeSchema;
                        record.includeData = record.canIncludeData;
                });
                $scope.groupedImportEntities = groupByModule($scope.importEntities);
                $scope.groupedImportEntitiesLength = $scope.groupedLength;
                LoadingModal.close();
            },
            function() {
                LoadingModal.close();
                ModalFactory.showErrorAlert('mds.import.file.error');
            });
        };

        /**
         * Callback function called when import file changes.
         */
        $scope.importFileChanged = function(file) {
            $scope.importFile = file;
            $scope.importId = null;
            $scope.importEntities = [];
            $scope.groupedImportEntities = {};
        };

        /**
         * Collects and sends import blueprint to the server.
         */
        $scope.importSelectedEntities = function () {
            var blueprint = [];
            LoadingModal.open();
            angular.forEach($scope.importEntities, function (entity) {
                blueprint.push({
                    entityName: entity.entityName,
                    includeSchema: entity.includeSchema,
                    includeData: entity.includeData
                });
            });

            $http.post('../mds/settings/import/' + $scope.importId, blueprint)
            .success(function () {
                LoadingModal.close();
                ModalFactory.showSuccessAlert('mds.import.success');
            })
            .error(function () {
                LoadingModal.close();
                ModalFactory.showErrorAlert('mds.import.error');
            });
        };

        /**
        * Sends new settings to controller
        */
        $scope.saveSettings = function () {
            LoadingModal.open();
            MdsSettings.saveSettings({}, $scope.settings,
                function () {
                    LoadingModal.close();
                    ModalFactory.showSuccessAlert('mds.dataRetention.success');
                }, function (response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('mds.dataRetention.error', 'mds.error', response);
                });
        };

        $scope.isAllEntitiesChecked = function(entities, include, canInclude) {
            var i, excludedCount = 0;
            for (i = 0; i < entities.length; i += 1) {
                if(!entities[i][canInclude]) {
                    excludedCount += 1;
                } else if (!entities[i][include]) {
                  return false;
                }
            }
            return excludedCount !== entities.length;
        };

        $scope.isAllEntitiesSchemaChecked = function(entities) {
            return $scope.isAllEntitiesChecked(entities, 'includeSchema', 'canIncludeSchema');
        };

        $scope.isAllEntitiesDataChecked = function(entities) {
            return $scope.isAllEntitiesChecked(entities, 'includeData', 'canIncludeData');
        };

        $scope.isNotAllEntitiesChecked = function(entities, include, canInclude) {
            var i, count = 0, excludedCount = 0;
            for (i = 0; i < entities.length; i += 1) {
                if (!entities[i][canInclude]) {
                    excludedCount += 1;
                } else if (entities[i][include]) {
                    count += 1;
                }
            }
            return count > 0 && count !== entities.length - excludedCount;
        };

        $scope.isNotAllEntitiesSchemaChecked = function(entities) {
            return $scope.isNotAllEntitiesChecked(entities, 'includeSchema', 'canIncludeSchema');
        };

        $scope.isNotAllEntitiesDataChecked = function(entities) {
            return $scope.isNotAllEntitiesChecked(entities, 'includeData', 'canIncludeData');
        };

        $scope.isAllEntitiesDisabled = function(entities, canInclude) {
            var i, count = 0;
            for (i = 0; i < entities.length; i += 1) {
                if (entities[i][canInclude]) {
                    return false;
                }
            }
            return true;
        };

        $scope.isAllEntitiesSchemaDisabled = function(entities) {
            return $scope.isAllEntitiesDisabled(entities, 'canIncludeSchema');
        };

        $scope.isAllEntitiesDataDisabled = function(entities) {
            return $scope.isAllEntitiesDisabled(entities, 'canIncludeData');
        };

        $scope.toggleSchemaCheck = function(entities) {
            if ($scope.isAllEntitiesSchemaChecked(entities)) {
                angular.forEach(entities, function(entity) {
                    entity.includeSchema = false;
                    entity.includeData = false;
                });
            } else {
                angular.forEach(entities, function(entity) {
                    entity.includeSchema = entity.canIncludeSchema;
                });
            }
        };

        $scope.toggleDataCheck = function(entities) {
            if ($scope.isAllEntitiesDataChecked(entities)) {
                angular.forEach(entities, function(entity) {
                    entity.includeData = false;
                });
            } else {
                angular.forEach(entities, function(entity) {
                    entity.includeData = entity.canIncludeData;
                    entity.includeSchema = entity.includeData ? entity.canIncludeSchema : entity.includeSchema;
                });
            }
        };

        /**
        * Hiding selected module entities and changing arrow icon
        * in selected table.
        */
        $scope.hideModule = function (index, tableName) {
            $("#" + tableName + ' .moduleDetails' + index).hide();
            $("#" + index + "-arrow-" + tableName).addClass("fa-caret-right");
            $("#" + index + "-arrow-" + tableName).removeClass("fa-caret-bottom");
        };

        /**
        * Showing selected module entities and changing arrow icon
        * in selected table.
        */
        $scope.showModule = function (index, tableName) {
            $("#" + tableName + ' .moduleDetails' + index).show();
            $("#" + index + "-arrow-" + tableName).addClass("fa-caret-down");
            $("#" + index + "-arrow-" + tableName).removeClass("fa-caret-right");
        };

        /**
        * Expanding and collapsing module entities and changing arrow icon
        * after clicking on arrow next to module name.
        */
        $scope.toggleModule = function (index, tableName) {
            var importExport = "exportModule";
            if ($("#" + tableName + ' .moduleDetails' + index + ":hidden").length > 0) {
                $scope.showModule(index, tableName);
            } else {
                $scope.hideModule(index, tableName);
            }
        };

        /**
        * Collapsing all modules entities and changing arrow icons
        * after clicking on button 'Collapse All'.
        */
        $scope.collapseAll = function (tableName) {
            var i, modulesLength;

            if (tableName !== 'export-module') {
                modulesLength = $scope.groupedImportEntitiesLength;
            } else {
                modulesLength = $scope.groupedExportEntitiesLength;
            }

            for (i = 0; i < modulesLength; i += 1) {
                if ($("#" + tableName + ' .moduleDetails' + i + ":hidden").length <= 0) {
                    $scope.hideModule(i, tableName);
                }
            }
        };

        /**
        * Expanding all modules entities and changing arrow icons
        * after clicking on button 'Expand All'.
        */
        $scope.expandAll = function (tableName) {
            var i, modulesLength;

            if (tableName !== 'export-module') {
                modulesLength = $scope.groupedImportEntitiesLength;
            } else {
                modulesLength = $scope.groupedExportEntitiesLength;
            }

            for (i = 0; i < modulesLength; i += 1) {
                if ($("#" + tableName + ' .moduleDetails' + i + ":hidden").length > 0) {
                    $scope.showModule(i, tableName);
                }
            }
        };
    });
}());