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
                    center__showOverflowOnHover: false,
                    west__size: 300,
                    west__minSize: 160,
                    west__maxSize: 360,
                    useStateCookie: true,
                    cookie__keys: "north.size,north.isClosed,south.size,south.isClosed,west.size,west.isClosed,east.size,east.isClosed",
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_closed: '<span><i class="fa fa-caret-right button"></i></span>',
                    togglerContent_open: '<span><i class="fa fa-caret-left"></i></span>',
                    togglerAlign_closed: "top", // align to top of resizer
                    south__togglerAlign_closed: "bottom",
                    south__togglerContent_closed: '<span><i class="fa fa-caret-up button"></i></span>',
                    south__togglerContent_open: '',
                    togglerAlign_open: "top",
                    togglerLength_open: 0, // NONE - using custom togglers INSIDE west-pane
                    togglerLength_closed: 35,
                    south__togglerLength_closed: 30,
                    slideTrigger_open: "click",  // default
                    initClosed: false,
                    onresize_start: function () {
                        $('#inner-center').trigger("change");
                        return false;
                    },
                    south__initClosed: false,
                    defaults: {
                        enableCursorHotkey: false
                    }
                };

                scope.outerLayout = elm.layout( scope.outerLayoutOptions);
                scope.outerLayout.sizePane('north', scope.showDashboardLogo.changeHeight());

                // BIND events to hard-coded buttons
                scope.outerLayout.addCloseBtn( "#tbarCloseSouth", "south" );
                scope.outerLayout.addCloseBtn( "#tbarCloseWest", "west" );

                $('#tabbuttons').live({
                    mouseover: function() {
                        scope.outerLayout.addOpenBtn( '#tabbuttons li a', "west" );
                        $('#tabbuttons li a').attr({"title" : ''});
                    }
                });

                $('.ui-layout-resizer').live({
                    mouseover: function() {
                        $('.ui-layout-toggler-closed').attr({"title" : scope.msg('server.jqlayout.openPane')});
                        $('.ui-layout-button-close').attr({"title" : scope.msg('server.jqlayout.closePane')});
                        $('.ui-layout-resizer-closed').attr({"title" : scope.msg('server.jqlayout.slide')});
                        $('.ui-layout-resizer-open').attr({"title" : scope.msg('server.jqlayout.resize')});
                    }
                });
                $('.ui-layout-toggler').live({
                    mouseover: function() {
                        $('.ui-layout-toggler-closed').attr({"title" : scope.msg('server.jqlayout.openPane')});
                        $('.ui-layout-button-close').attr({"title" : scope.msg('server.jqlayout.closePane')});
                        $('.ui-layout-resizer-closed').attr({"title" : scope.msg('server.jqlayout.slide')});
                    }
                });
                $('.ui-layout-toggler-closed .button').hover( function() {
                    $('.ui-layout-toggler-closed').attr({"title" : scope.msg('server.jqlayout.openPane')});
                });
                $('.ui-layout-button-close').hover( function() {
                    $('.ui-layout-button-close').attr({"title" : scope.msg('server.jqlayout.closePane')});
                });
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
                $('.inner-center .ui-layout-content').animate({
                    scrollTop: 0
                }, 800);

                return false;
            });
        };
    });

    widgetModule.directive('goToEnd', function () {
        return function (scope, element, attrs) {
            $(element).click(function () {
                $('.inner-center .ui-layout-content').animate({
                    scrollTop: $(".inner-center .ui-layout-content .tab-content").height()
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

    widgetModule.directive('serverTime', function ($http) {
        return function (scope, element, attributes) {
            var localTime, serverTime, calculatedDate, recalculatedDate, diff,
            formatPattern = 'YYYY-MM-DD HH:mm',
            setTime = function () {
                if (diff !== undefined && !isNaN(diff)) {
                    localTime = new Date();

                    // Recalculate the difference in seconds between the server date and the client date
                    recalculatedDate = localTime.getTime() - (parseInt(diff * 1000, 10));
                    $(element).text(moment(parseInt(recalculatedDate, 10)).format(formatPattern));
                } else {
                    $(element).text(' ? ');
                }
            },
            getServerTime = function() {
                $http.post('gettime').success( function(time, status) {
                     if (status === 200) {
                         localTime = new Date();
                         serverTime = new Date(moment(parseInt(time, 10)));
                         $(element).text(moment(new Date(moment(parseInt(time, 10)))).format(formatPattern));

                         // Calculate the difference  in seconds between the server date and the client date
                         diff = parseInt((localTime.getTime() / 1000) - (serverTime.getTime() / 1000), 10);
                         calculatedDate = new Date(localTime.getTime() + diff);

                         $(element).text(moment(calculatedDate).format(formatPattern));

                     } else {
                         setTime();
                     }
                });
            };

            getServerTime();
            setInterval(function () {
                setTime();
            }, 60000);
        };
    });

    widgetModule.directive('serverUpTime', function ($http) {
        return function (scope, element, attributes) {
            var currentDate, serverStartTime,
            setUpTime = function () {
                if (serverStartTime !== undefined && !isNaN(serverStartTime)) {
                    $(element).text(moment(parseInt(serverStartTime, 10)).fromNow());
                } else {
                    $(element).text(' ? ');
                }
            },
            getUptime = function() {
                $http.post('getUptime').success( function(data, status) {
                    if (status === 200) {
                        $(element).text(moment(parseInt(data, 10)).fromNow());

                        // Store server start time
                        serverStartTime = data;

                    } else {
                        setUpTime();
                    }
                });
            };

            getUptime();
            setInterval(function () {
                setUpTime();
            }, 60000);
        };
    });

    widgetModule.directive('serverNodeName', function ($http) {
        return function (scope, element, attributes) {
            var getNodeName = function() {
                $http.post('getNodeName').success( function(data, status) {
                    if (status === 200) {
                       $(element).text(data);
                    } else {
                       $(element).text(scope.msg('server.error.unknown'));
                    }
                });
            };

            getNodeName();
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

    widgetModule.directive('validatePassword', function($http) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                scope.validatorMessage = '';
                function validateUserPassword(userPassword) {
                    var msg;
                    if (userPassword !== undefined && userPassword !== '') {
                        $http.post('../websecurity/api/users/checkPassword', userPassword).success(function() {
                            ctrl.$setValidity('valid', true);
                        }).error(function(response) {
                            msg = parseResponse(response, "server.error");
                            if (msg.literal === true) {
                                scope.validatorMessage = msg.value;
                            } else {
                                scope.validatorMessage = jQuery.i18n.prop.apply(null, [msg.value].concat(msg.params));
                            }
                            ctrl.$setValidity('valid', false);
                        });
                    }
                    return userPassword;
                }

                ctrl.$parsers.unshift(function(viewValue) {
                    return validateUserPassword(viewValue);
                });
                ctrl.$formatters.unshift(function(modelPassword) {
                    return validateUserPassword(modelPassword);
                });
            }
        };
    });

    widgetModule.directive('confirmPassword', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {

                function validateEqual(confirmPassword, userPassword) {
                    if (confirmPassword === userPassword) {
                        ctrl.$setValidity('equal', true);
                    } else {
                        ctrl.$setValidity('equal', false);
                    }
                    return confirmPassword;
                }

                scope.$watch(attrs.confirmPassword, function(userViewPassword) {
                    validateEqual(ctrl.$viewValue, userViewPassword);
                });

                ctrl.$parsers.unshift(function(viewValue) {
                    return validateEqual(viewValue, scope.$eval(attrs.confirmPassword));
                });

                ctrl.$formatters.unshift(function(modelPassword) {
                    return validateEqual(modelPassword, scope.$eval(attrs.confirmPassword));
                });
            }
        };
    });

    widgetModule.directive('oldPassword', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {

                function validateNotEqual(userNewPassword, userOldPassword) {
                    if (userNewPassword !== userOldPassword) {
                        ctrl.$setValidity('notEqual', true);
                        return userNewPassword;
                    } else {
                        ctrl.$setValidity('notEqual', false);
                        return undefined;
                    }
                }

                scope.$watch(attrs.oldPassword, function(userViewPassword) {
                    validateNotEqual(ctrl.$viewValue, userViewPassword);
                });

                ctrl.$parsers.unshift(function(viewValue) {
                    return validateNotEqual(viewValue, scope.$eval(attrs.oldPassword));
                });

                ctrl.$formatters.unshift(function(modelPassword) {
                    return validateNotEqual(modelPassword, scope.$eval(attrs.oldPassword));
                });
            }
        };
    });

    widgetModule.directive('visitedConfirmInput', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                var elm = angular.element(element),
                typingTimer;
                elm.on('keyup', function () {
                    scope.isConfirmPasswordDirty = false;
                    clearTimeout(typingTimer);
                    typingTimer = setTimeout( function() {
                        scope.$apply(function () {
                            scope.isConfirmPasswordDirty = true;
                        });
                    }, 750);
                });

                elm.on("blur", function() {
                    scope.$apply(function () {
                        scope.isConfirmPasswordDirty = true;
                    });
                });
            }
        };
    });
    
    // code for date-picker
    
    widgetModule.directive('datePicker', function() {
        return {
            restrict: 'A',
            scope: {},
            link: function(scope, element, attrs) {
               var elem = angular.element(element),
                   otherDateTextBox = {},
                   curId = attrs.id,
                   curIdLength = curId.length,
                   otherId = '',
                   isStartDate = false;
                     
                if(curId.substr(curIdLength-2,2) === 'To') {
                    otherId = curId.slice(0,curIdLength - 2) + 'From';
                }
                else {
                    otherId = curId.slice(0,curIdLength - 4) + 'To';
                    isStartDate = true;
                }
                otherDateTextBox = angular.element('#' + otherId);

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        if(isStartDate) {
                            otherDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                        }
                        else {
                            otherDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                        }
                        if(curId === "messagesDateTimeFrom") {
                            scope.setDateTimeFilter(selectedDateTime, null);
                        }
                        else if(curId === "messagesDateTimeTo") {
                            scope.setDateTimeFilter(null, selectedDateTime);
                        }
                    },
                    onChangeMonthYear: function (year, month, inst) {
                        var curDate = $(this).datepicker("getDate");
                        if (curDate === null) {
                            return;
                        }
                        if (curDate.getFullYear() !== year || curDate.getMonth() !== month - 1) {
                            curDate.setYear(year);
                            curDate.setMonth(month - 1);
                            $(this).datepicker("setDate", curDate);
                        }
                        if(curId === "messagesDateTimeFrom") {
                            scope.setDateTimeFilter(curDate, null);
                        }
                        else if(curId === "messagesDateTimeTo") {
                            scope.setDateTimeFilter(null, curDate);
                        }
                    },
                    onClose: function () {
                        var viewValue = $(this).val();
                        if (viewValue === '') {
                            if(isStartDate) {
                                otherDateTextBox.datetimepicker('option', 'minDate', null);
                            }
                            else {
                                otherDateTextBox.datetimepicker('option', 'maxDate', null);
                            }
                        } else {
                            if(isStartDate) {
                                otherDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                            }
                            else {
                                otherDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                            }
                        }
                        if(curId === "messagesDateTimeFrom") {
                            scope.setDateTimeFilter(viewValue, null);
                        }
                        else if(curId === "messagesDateTimeTo") {
                            scope.setDateTimeFilter(null, viewValue);
                        }
                    }
                });
            }
        };
    });
    
    widgetModule.directive('messagesDatePicker', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
               var elem = angular.element(element),
                   otherDateTextBox = {},
                   curId = attrs.id,
                   curIdLength = curId.length,
                   otherId = '',
                   isStartDate = false;
                     
                if(curId.substr(curIdLength-2,2) === 'To') {
                    otherId = curId.slice(0,curIdLength - 2) + 'From';
                }
                else {
                    otherId = curId.slice(0,curIdLength - 4) + 'To';
                    isStartDate = true;
                }
                otherDateTextBox = angular.element('#' + otherId);

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        if(isStartDate) {
                            otherDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                        }
                        else {
                            otherDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                        }
                        if(curId === "messagesDateTimeFrom") {
                            scope.setDateTimeFilter(selectedDateTime, null);
                        }
                        else if(curId === "messagesDateTimeTo") {
                            scope.setDateTimeFilter(null, selectedDateTime);
                        }
                    },
                    onChangeMonthYear: function (year, month, inst) {
                        var curDate = $(this).datepicker("getDate");
                        if (curDate === null) {
                            return;
                        }
                        if (curDate.getFullYear() !== year || curDate.getMonth() !== month - 1) {
                            curDate.setYear(year);
                            curDate.setMonth(month - 1);
                            $(this).datepicker("setDate", curDate);
                        }
                        if(curId === "messagesDateTimeFrom") {
                            scope.setDateTimeFilter(curDate, null);
                        }
                        else if(curId === "messagesDateTimeTo") {
                            scope.setDateTimeFilter(null, curDate);
                        }
                    },
                    onClose: function () {
                        var viewValue = $(this).val();
                        if (viewValue === '') {
                            if(isStartDate) {
                                otherDateTextBox.datetimepicker('option', 'minDate', null);
                            }
                            else {
                                otherDateTextBox.datetimepicker('option', 'maxDate', null);
                            }
                        } else {
                            if(isStartDate) {
                                otherDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                            }
                            else {
                                otherDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                            }
                        }
                        if(curId === "messagesDateTimeFrom") {
                            scope.setDateTimeFilter(viewValue, null);
                        }
                        else if(curId === "messagesDateTimeTo") {
                            scope.setDateTimeFilter(null, viewValue);
                        }
                    }
                });
            }
        };
    });
}());
