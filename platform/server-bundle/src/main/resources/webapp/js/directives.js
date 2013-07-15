(function () {
    'use strict';

    var widgetModule = angular.module('motech-widgets', []);

    widgetModule.directive('bsPopover', function() {
        return function(scope, element, attrs) {
            $(element).popover({
                content: function () {
                    return attrs.bsPopover;
                }
            });
        };
    });

    widgetModule.directive('goToTop', function () {
        return function (scope, element, attrs) {
            $(element).click(function () {
                $('body, html').animate({
                    scrollTop: 0
                }, 800);

                return false;
            });
        };
    });

    widgetModule.directive('goToEnd', function () {
        return function (scope, element, attrs) {
            $(element).click(function () {
                $('body, html').animate({
                    scrollTop: $(document).height()
                }, 800);

                return false;
            });
        };
    });

    widgetModule.directive('ngDateTimePicker', function () {
        return function (scope, element, attributes) {
            $(element).datetimepicker();
        };
    });

    widgetModule.directive('serverTime', function () {
        return function (scope, element, attributes) {
            var getTime = function() {
                $.post('gettime', function(time) {
                     $(element).text(time);
                });
            };

            getTime();
            window.setInterval(getTime, 60000);
        };
    });

    widgetModule.directive('serverUpTime', function () {
        return function (scope, element, attributes) {
            var getUptime = function() {
                $.post('getUptime', function(time) {
                     $(element).text(moment(time).fromNow());
                });
            };

            getUptime();
            window.setInterval(getUptime, 60000);
        };
    });

    widgetModule.directive('motechModules', function ($compile, $timeout, $http, $templateCache) {
        var templateLoader;

        return {
            restrict: 'E',
            replace : true,
            transclude: true,
            compile: function (tElement, tAttrs, scope) {
                var url = '../server/resources/partials/motech-modules.html',

                templateLoader = $http.get(url, {cache: $templateCache})
                    .success(function (html) {
                        tElement.html(html);
                    });

                return function (scope, element, attrs) {
                    templateLoader.then(function () {
                        element.html($compile(tElement.html())(scope));
                    });
                };
            }
        };
    });

}());
