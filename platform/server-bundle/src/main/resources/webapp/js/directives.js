(function () {
    'use strict';

    var widgetModule = angular.module('motech-widgets', []);

    widgetModule.directive('layout', function() {
        return {
            link: function(scope, elm, attrs) {
                var northSelector, southSelector, westSelector, westSouthSelector, $Container;
                /*
                * Define options for outer layout
                */
                scope.outerLayoutOptions = {
                    name: 'outerLayout',
                    resizable: true,
                    slidable: true,
                    closable: true,
                    north__closable: false,
                    north__paneSelector: "#outer-north",
                    west__paneSelector: "#outer-west",
                    center__paneSelector: "#outer-center",
                    south__paneSelector: "#outer-south",
                    south__spacing_open: 0,
                    south__spacing_closed: 8,
                    south__minSize: 30,
                    south__size: 30,
                    north__spacing_open: 0,
                    west__spacing_open: 6,
                    spacing_closed: 30,
                    north__spacing_closed: 0,
                    north__minSize: 40,
                    center__showOverflowOnHover: true,
                    west__size: 350,
                    useStateCookie: true,
                    cookie__keys: "north.size,north.isClosed,south.size,south.isClosed,west.size,west.isClosed,east.size,east.isClosed",
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_closed: '<span><i class="icon-caret-right button"></i></span>',
                    togglerContent_open: '<span><i class="icon-caret-left"></i></span>',
                    togglerAlign_closed: "top", // align to top of resizer
                    south__togglerAlign_closed: "bottom",
                    south__togglerContent_closed: '<span><i class="icon-caret-up button"></i></span>',
                    south__togglerContent_open: '',
                    togglerAlign_open: "top",
                    togglerLength_open: 0, // NONE - using custom togglers INSIDE west-pane
                    togglerLength_closed: 35,
                    south__togglerLength_closed: 30,
                    togglerTip_open: "Close This Pane",
                    togglerTip_closed: "Open This Pane",
                    slideTrigger_open: "click",  // default
                    initClosed: false,
                    south__initClosed: false
                };

                scope.outerLayout = elm.layout( scope.outerLayoutOptions);
                scope.outerLayout.sizePane('north', scope.showDashboardLogo.changeHeight());

                // BIND events to hard-coded buttons
                scope.outerLayout.addCloseBtn( "#tbarCloseSouth", "south" );
                scope.outerLayout.addCloseBtn( "#tbarCloseWest", "west" );

            }
        };
    });

    widgetModule.directive('overflowChangePanel', function () {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    $(element).find('.overflowChange').livequery(function () {
                        $(this).on({
                            mouseover: function (e) {
                                if (!e.target.classList.contains("overflowChange")) {
                                    $(element).parent().parent().css('overflow', 'visible');
                                    $(element).parent().parent().css('z-index', '10');
                                }
                            },
                            mouseout: function (e) {
                                if (!$(this).hasClass("open")) {
                                    $(element).parent().parent().css('overflow', 'hidden');
                                    $(element).parent().parent().css('z-index', '0');
                                }
                            }
                        });
                    });
                }
            };
        });

    widgetModule.directive('bsPopover', function() {
        return function(scope, element, attrs) {
            $(element).popover({
                content: function () {
                    return attrs.bsPopover;
                }
            });
        };
    });

    widgetModule.directive('elementFocus', function() {
        return function (scope, element, atts) {
            element[0].focus();
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
