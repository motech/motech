(function () {
    'use strict';

    angular.module('commcare').directive('autosave', function() {

        return {
            restrict: 'A',
            priority: 0,
            link: function(scope, element, attrs) {
                element.on(attrs.autosave, function(e) {
                    scope.saveSettings(element);
                });
            }
        };
    });

    angular.module('commcare').directive('switch', function() {

        return {
            restrict: 'A',
            priority: 1,
            link: function(scope, element, attrs) {

                var property, deregister;

                property = $(attrs['switch']).attr('ng-model');
                element.on('switch-change', function(e, data) {
                    scope.settings[property.split('.')[1]] = data.value;
                });

                deregister = scope.$watch(property, function(value) {
                    if (value !== undefined) {
                        element.bootstrapSwitch();
                        deregister();
                    }
                });
            }
        };
    });

}());
