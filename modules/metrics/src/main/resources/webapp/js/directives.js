(function () {
    'use strict';

    var directives = angular.module('metrics.directives', []);

    directives.directive('multiselectDropdown', function () {
        return {
            restrict: 'A',
            require : 'ngModel',
            link: function (scope, element, attrs) {
                element.multiselect({
                    buttonClass : 'btn btn-default',
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

    directives.directive('double', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.keypress(function (evt) {
                    var charCode = evt.which || evt.keyCode,
                        allow = [8, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]; // char code: <Backspace> . 0 1 2 3 4 5 6 7 8 9

                    return allow.indexOf(charCode) >= 0;
                });
            }
        };
    });

   directives.directive('integer', function () {
       return {
           restrict: 'A',
           link: function (scope, element, attrs) {
               element.keypress(function (evt) {
                   var charCode = evt.which || evt.keyCode,
                       allow = [8, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]; // char code: <Backspace> 0 1 2 3 4 5 6 7 8 9

                   return allow.indexOf(charCode) >= 0;
               });
           }
       };
   });

}());
