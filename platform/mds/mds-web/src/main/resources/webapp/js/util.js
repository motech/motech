(function () {

    'use strict';

    var utils = angular.module('data-services.utils', []);

    utils.factory('MDSUtils', function () {
        return {
            /**
            * Convert the given string to camelCase.
            *
            * @param {string} str A string to convert.
            * @return {string} new string in camelCase.
            */
            camelCase: function (str) {
                return (str || '').replace(/(?:^\w|[A-Z]|\b\w)/g, function (letter, index) {
                    return index === 0 ? letter.toLowerCase() : letter.toUpperCase();
                }).replace(/\s+/g, '');
            },

            /**
            * Find element(s) in the given array which pass certain predicates. Each predicate
            * should contain information about the property name (the field property) and the value
            * that it should have (the value property). If no arguments are given to this function,
            * it will return an empty array. If unique parameter is set to true and no objects were
            * found then an empty object will be returned.
            *
            * @param {Array} [data] An object array. By default it is set to empty array.
            * @param {Array} [predicates] An array of predicates which must be met by an element
            *                             from data param. By default it is set to empty array.
            * @param {boolean} [unique] if true then method will returns single element; otherwise
            *                           return array of elements. By default it is set to false.
            * @param {boolean} [caseInsensitive] if true, the search for elements will be case insensitive.
            *                           If left undefined, it will default to false (case sensitive search).
            * @param {$scope.msg} [label] A function which returns appropriate label for a key.
            * @return {object[]|object} the single element (if unique param is set to true) or
            *                           array of elements which passed predicates.
            */
            find: function () {
                var data = _.isArray(arguments[0]) ? arguments[0] : [],
                    predicates = _.isArray(arguments[1]) ? arguments[1] : [],
                    unique = arguments[2] !== undefined ? arguments[2] : false,
                    caseInsensitive = arguments[3] !== undefined ? arguments[3] : false,
                    label = _.isFunction(arguments[4]) ? arguments[4] : undefined,
                    found = [],
                    isTrue,
                    fields,
                    field,
                    value;

                _.each(data, function (element) {
                    isTrue = predicates.length > 0 ? true : false;

                    _.each(predicates, function (predicate) {
                        fields = predicate.field.split('.');
                        field = element;
                        value = predicate.value;

                        _.each(fields, function (f) {
                            field = field[f];

                            if (_.isUndefined(field)) {
                                field = {};
                            }
                        });

                        if (label !== undefined) {
                            isTrue = isTrue
                                && (_.isEqual(field, value) || _.isEqual(label(field), value));
                        } else {
                            if (caseInsensitive) {
                                isTrue = isTrue && _.isEqual(field !== undefined && field !== null ? field.toLowerCase() : field,
                                    value !== undefined && value !== null ? value.toLowerCase() : value);
                            } else {
                                isTrue = isTrue && _.isEqual(field, value);
                            }
                        }
                    });

                    if (isTrue) {
                        found.push(element);
                    }
                });

                return unique ? (found.length > 0 ? found[0] : {}) : found;
            },

            /** Returns a uuid value generated in the backend
             * 
             * @return {string} uuid value in string format
             */
            generateUUID: function ($http) {
                var uuid;
                $.ajax({
                    method : "GET",
                    url : "../mds/instances/generateUUID",
                    async : false,
                    success : function(value) {
                       uuid = value;
                    }
                });
                return uuid;
            },
            
            /**
            * Check if the given number has appropriate precision and scale.
            *
            * @param {number} number The number to validate.
            * @param {number} [precision] The total number of digits. By default it is set to 9.
            * @param {number} [scale] The number of digits after decimal point. By default it is
            *                         set to 2.
            * @return {boolean} true if number has appropriate precision and scale; otherwise false.
            */
            validateDecimal: function (number) {
                var precision = _.isNumber(arguments[1]) ? Math.floor(Math.abs(arguments[1])) : 9,
                    scale = _.isNumber(arguments[2]) ? Math.floor(Math.abs(arguments[2])) : 2,
                    toString = _.isNumber(number) ? number.toString() : '',
                    dot = toString.indexOf('.'),
                    length = toString.length - (dot === -1 ? 0 : 1),
                    array = dot === -1 ? [toString, ''] : toString.split('.');

                return _.isNumber(number) && length <= precision && array[1].length <= scale;
            },

            validateRegexp: function (viewValue, regexp) {
                return regexp.test(viewValue);
            },

            validateMaxLength: function (viewValue, maxLength) {
                return maxLength < viewValue;
            },

            validateMinLength: function (viewValue, minLength) {
                return minLength > viewValue;
            },

            validateMax: function (viewValue, max) {
                return max <= viewValue;
            },

            validateMin: function (viewValue, min) {
                return min > viewValue;
            },

            validateInSet: function (viewValue, inset) {
                var result,
                insetParameters = inset.split(' ');
                if($.isArray(insetParameters)) {
                    $.each(insetParameters, function (i, val) {
                        if (parseFloat(val) === parseFloat(viewValue)) {
                            result = true;
                        } else {
                            result = false;
                        }
                        return (!result);
                    });
                } else {
                    result = false;
                }
                return !result;
            },

            validateOutSet: function (viewValue, outset) {
                var result,
                outsetParameters = outset.split(' ');
                if($.isArray(outsetParameters)) {
                    $.each(outsetParameters, function (i, val) {
                        if (parseFloat(val) === parseFloat(viewValue)) {
                            result = true;
                        } else {
                            result = false;
                        }
                        return (!result);
                    });
                } else {
                    result = false;
                }
                return result;
            },

            setCustomOperatorFunctions: function(scope) {
              scope.customOperators = {
                   "": "mds.form.operator.none",
                   "<": "mds.operator.lt",
                   "<=": "mds.operator.lteq",
                   ">": "mds.operator.gt",
                   ">=": "mds.operator.gteq",
                   "==": "mds.operator.eq",
                   "!=": "mds.operator.neq",
                   "equalsIgnoreCase()": "mds.operator.eq.ci",
                   "matches()": "mds.operator.matches",
                   "matches((?i))": "mds.operator.matches.ci",
                   "startsWith()": "mds.operator.starts",
                   "endsWith()": "mds.operator.ends"
               };

               scope.getOperatorMsg = function(operator) {
                   if (operator) {
                       return scope.msg(scope.customOperators[operator]);
                   } else {
                       return scope.msg("mds.form.operator.none");
                   }
               };
            },
            
            moveItems : function($scope, index) {
                var tmp;
                tmp = $scope.browsingDisplayed[index];
                $scope.browsingDisplayed[index] = $scope.browsingDisplayed[index - 1];
                $scope.browsingDisplayed[index - 1] = tmp;
            },
            
            draftForFields : function($scope, array, selected, $timeout) {
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
            },
            
            canMoveUpOrDown : function($scope, isDown) {
                var items = $('.target-item.browsing'),
                            wasLastSelected = true,
                            ret = false;
                if (items.filter('.selected').size() === 0) {
                    return false;
                }

                if(isDown) {
                    items = $(items.get().reverse());
                }
                items.each(function() {
                    var isThisSelected = $(this).hasClass('selected');
                    if (!wasLastSelected && isThisSelected) {
                        ret = true;
                    }
                    wasLastSelected = isThisSelected;
                });
                return ret;
            },
            
            isEnabled : function(viewValue, field, criterion, criterionNum) {
                var enabled = false;
                if (field.validation !== null && criterion !== '' && (field.validation.criteria[criterionNum].enabled && viewValue !== null && viewValue !== undefined && viewValue !== '')) {
                    enabled = true;
                }
                return enabled;
            },
            
            collapseOrExpandAll : function($scope, tableName, toCollapse) {
                var i, modulesLength;

                if (tableName !== 'export-module') {
                    modulesLength = $scope.groupedImportEntitiesLength;
                } else {
                    modulesLength = $scope.groupedExportEntitiesLength;
                }
                
                for (i = 0; i < modulesLength; i += 1) {
                    if (!toCollapse && $("#" + tableName + ' .moduleDetails' + i + ":hidden").length > 0) {
                        $scope.showModule(i, tableName);
                    }
                    
                    else if (toCollapse && $("#" + tableName + ' .moduleDetails' + i + ":hidden").length <= 0) {
                        $scope.hideModule(i, tableName);
                    }
                }
            }
        };
    });

}());
