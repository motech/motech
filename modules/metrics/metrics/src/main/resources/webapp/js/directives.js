(function () {
    'use strict';

    var metricsModule = angular.module('motech-platform-metrics');

    metricsModule.directive('multiselectDropdown', function () {
        return {
            restrict: 'A',
            require : 'ngModel',
            link: function (scope, element, attrs) {
                element.multiselect({
                    buttonClass : 'btn',
                    buttonWidth : 'auto',
                    buttonContainer : '<div class="btn-group" />',
                    maxHeight : false,
                    buttonText : function(options) {
                        if (options.length === 0) {
                            return scope.msg('metrics.backend.select');
                        } else {
                            return options.length + ' ' + scope.msg('metrics.backend.selected');
                        }
                    },

                    onChange: function (optionElement, checked) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.attr('selected', 'selected');
                        }
                        element.change();
                    }

               });

               scope.$watch(function () {
                   return element[0].length;
               }, function () {
                   element.multiselect('rebuild');
               });

               scope.$watch(attrs.ngModel, function () {
                   element.multiselect('refresh');
               });
            }
        };
    });

}());
