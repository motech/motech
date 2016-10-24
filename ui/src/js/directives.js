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

                $('#tabbuttons').on({
                    mouseover: function() {
                        scope.outerLayout.addOpenBtn( '#tabbuttons li a', "west" );
                        $('#tabbuttons li a').attr({"title" : ''});
                    }
                });

                $('.ui-layout-resizer').on({
                    mouseover: function() {
                        $('.ui-layout-toggler-closed').attr({"title" : scope.msg('server.jqlayout.openPane')});
                        $('.ui-layout-button-close').attr({"title" : scope.msg('server.jqlayout.closePane')});
                        $('.ui-layout-resizer-closed').attr({"title" : scope.msg('server.jqlayout.slide')});
                        $('.ui-layout-resizer-open').attr({"title" : scope.msg('server.jqlayout.resize')});
                    }
                });
                $('.ui-layout-toggler').on({
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

    /**
    * Add a time picker (without date) to an element.
    */
    widgetModule.directive('timePicker', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var isReadOnly = scope.$eval(attr.ngReadonly);
                if(!isReadOnly) {
                    angular.element(element).timepicker({
                        onSelect: function (timeTex) {
                            scope.safeApply(function () {
                                ngModel.$setViewValue(timeTex);
                            });
                        }
                    });
                }
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

    widgetModule.factory('StatusService', ["$http", function($http) {
        var promise = null;
        var getData = function() {
            if (promise == null) {
                promise = $http.get('getStatus');
            }
            return promise;
        };

        return {
            getData: getData
        };
    }]);

    widgetModule.directive('serverTime', ['$http', 'StatusService', function ($http, StatusService) {
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
                StatusService.getData().success( function(data, status) {
                     if (status === 200) {
                         localTime = new Date();
                         serverTime = new Date(moment(parseInt(data[0].time, 10)));
                         $(element).text(moment(new Date(moment(parseInt(data[0].time, 10)))).format(formatPattern));

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
    }]);

    widgetModule.directive('serverUpTime', ['$http', 'StatusService', function ($http, StatusService) {
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
                StatusService.getData().success( function(data, status) {
                    if (status === 200) {
                        $(element).text(moment(parseInt(data[0].uptime, 10)).fromNow());

                        // Store server start time
                        serverStartTime = data[0].uptime;

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
    }]);

    widgetModule.directive('serverNodeName', ['$http', 'StatusService', function ($http, StatusService) {
        return function (scope, element, attributes) {
            var getNodeName = function() {
                StatusService.getData().success( function(data, status) {
                    if (status === 200) {
                       $(element).text(data[0].nodeName);
                    } else {
                       $(element).text(scope.msg('server.error.unknown'));
                    }
                });
            };

            getNodeName();
        };
    }]);

    widgetModule.directive('inboundChannelActive', ['$http', 'StatusService', function ($http, StatusService) {
        return function (scope, element, attributes) {
            var isActivemqActive = function() {
                StatusService.getData().success( function(data, status) {
                    if (status === 200) {
                        var glyphicon = $('<span>').addClass('glyphicon').attr('aria-hidden', 'true');
                        if (data[0].inboundChannelActive === true) {
                            glyphicon.addClass('glyphicon-ok');
                        } else {
                            glyphicon.addClass('glyphicon-remove');
                        }
                        glyphicon.appendTo(element);
                    } else {
                       $(element).text(scope.msg('server.error.unknown'));
                    }
                });
            };

            isActivemqActive();
        };
    }]);

    widgetModule.directive('motechModules', ['$compile', '$timeout', '$http', '$templateCache', function ($compile, $timeout, $http, $templateCache) {
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
    }]);

    widgetModule.directive('validatePassword', ['$http', function($http) {
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
    }]);

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

    widgetModule.directive('periodAmount', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                var elem = angular.element(element),
                periodSliders = $("#period-slider > div"),
                periodSlider = $("#period-slider"),
                parent = elem.parent(),
                started = false,
                openPeriodModal,
                closePeriodModal,
                year = '0',
                month = '0',
                week = '0',
                day = '0',
                hour = '0',
                minute = '0',
                second = '0',
                sliderMax = {
                    year: 10,
                    month: 24,
                    week: 55,
                    day: 365,
                    hour: 125,
                    minute: 360,
                    second: 360
                },
                compileValueInputs = function (year, month, week, day, hour, minute, second) {
                    var valueInputs = [
                        year.toString( 10 ),
                        month.toString( 10 ),
                        week.toString( 10 ),
                        day.toString( 10 ),
                        hour.toString( 10 ),
                        minute.toString( 10 ),
                        second.toString( 10 )
                    ],
                    valueInputsName = ['Y', 'M', 'W', 'D', 'H', 'M', 'S'];

                    $.each( valueInputs, function( nr, val ) {
                        if (nr < 4 && val !== '0') {
                            valueInputs[ nr ] = val + valueInputsName[ nr ];
                        }
                        if ( (valueInputsName[ nr ] === 'H' || valueInputsName[ nr ] === 'M' || valueInputsName[ nr ] === 'S' ) &&  val !== '0' && nr > 3 ) {
                            valueInputs[ nr ] = val + valueInputsName[ nr ];
                            if (valueInputs[ 4 ].indexOf('T') === -1 && valueInputs[ 5 ].indexOf('T') === -1 && valueInputs[ 6 ].indexOf('T') === -1) {
                                valueInputs[ nr ] = 'T' + val + valueInputsName[ nr ];
                            }
                        }
                        if ( val === '0' ) {
                            valueInputs[ nr ] = '';
                        }
                    });
                    return 'P' + valueInputs.join( "" ).toUpperCase();
                },
                refreshPeriod = function () {
                    var fieldId = elem.attr('mds-field-id'),
                    year = periodSlider.children( "#period-year" ).slider( "value" ),
                    month = periodSlider.children( "#period-month" ).slider( "value" ),
                    week = periodSlider.children( "#period-week" ).slider( "value" ),
                    day = periodSlider.children( "#period-day" ).slider( "value" ),
                    hour = periodSlider.children( "#period-hour" ).slider( "value" ),
                    minute = periodSlider.children( "#period-minute" ).slider( "value" ),
                    second = periodSlider.children( "#period-second" ).slider( "value" ),

                    valueFromInputs = compileValueInputs(year, month, week, day, hour, minute, second);

                    periodSlider.children( "#amount-period-year" ).val( year );
                    periodSlider.children( "#amount-period-month" ).val( month );
                    periodSlider.children( "#amount-period-week" ).val( week );
                    periodSlider.children( "#amount-period-day" ).val( day );
                    periodSlider.children( "#amount-period-hour" ).val( hour );
                    periodSlider.children( "#amount-period-minute" ).val( minute );
                    periodSlider.children( "#amount-period-second" ).val( second );
                    scope.$apply(function () {
                        elem.val( valueFromInputs );
                    });
                    scope.$apply(function() {
                        ctrl.$setViewValue(valueFromInputs);
                    });
                    element.focus();
                    element.focusout();
                },
                setParsingPeriod = function () {
                    if (!started) {
                        periodSliders = $("#period-slider > div");
                        periodSlider = $("#period-slider");
                        periodSliders.each(function(index) {
                            var getValueSettings, valueName = (this.id);
                            valueName = valueName.substring(valueName.lastIndexOf('-') + 1);
                            getValueSettings = function (param1, param2) {
                                var result, resultVal = '';
                                $.each( param1, function( key, value) {
                                    if (key === param2){
                                        result = true;
                                        resultVal = value;
                                    } else {
                                        result = false;
                                    }
                                return (!result);
                                });
                            return resultVal;
                            };

                            $( this ).empty().slider({
                                value: getValueSettings([year, month, week, day, hour, minute, second], valueName),
                                range: "min",
                                min: 0,
                                max: getValueSettings(sliderMax, valueName),
                                animate: true,
                                orientation: "horizontal",
                                slide: refreshPeriod,
                                change: refreshPeriod
                            });
                            periodSlider.children( "#amount-period-" + valueName ).val( $( this ).slider( "value" ) );
                        });
                        started = true;
                    }
                    var valueElement = elem.val(), valueDate, valueTime, fieldId = elem.attr('mds-field-id'),
                    checkValue = function (param) {
                        if(isNaN(param) || param === null || param === '' || param === undefined) {
                            param = '0';
                            return param;
                        } else {
                            return param;
                        }
                    },
                    parseDate = function (valueDate) {
                        if (valueDate.indexOf('Y') !== -1) {
                            year = checkValue(valueDate.slice(0, valueDate.indexOf('Y')).toString( 10 ));
                            valueDate = valueDate.substring(valueDate.indexOf('Y') + 1, valueDate.length);
                        } else {
                            year = '0';
                        }
                        if (valueDate.indexOf('M') !== -1) {
                            month = checkValue(valueDate.slice(0, valueDate.indexOf('M')).toString( 10 ));
                            valueDate = valueDate.substring(valueDate.indexOf('M') + 1, valueDate.length);
                        } else {
                            month = '0';
                        }
                        if (valueDate.indexOf('W') !== -1) {
                            week = checkValue(valueDate.slice(0, valueDate.indexOf('W')).toString( 10 ));
                            valueDate = valueDate.substring(valueDate.indexOf('W') + 1, valueDate.length);
                        } else {
                            week = '0';
                        }
                        if (valueDate.indexOf('D') !== -1) {
                            day = checkValue(valueDate.slice(0, valueDate.indexOf('D')).toString( 10 ));
                        } else {
                            day = '0';
                        }
                    },
                    parseTime = function (valueTime) {
                        if (valueTime.indexOf('H') !== -1) {
                            hour = checkValue(valueTime.slice(0, valueTime.indexOf('H')));
                            valueTime = valueTime.substring(valueTime.indexOf('H') + 1, valueTime.length);
                        } else {
                            hour = '0';
                        }
                        if (valueTime.indexOf('M') !== -1) {
                            minute = checkValue(valueTime.slice(0, valueTime.indexOf('M')));
                            valueTime = valueTime.substring(valueTime.indexOf('M') + 1, valueTime.length);
                        } else {
                            minute = '0';
                        }
                        if (valueTime.indexOf('S') !== -1) {
                            second = checkValue(valueTime.slice(0, valueTime.indexOf('S')));
                            valueTime = valueTime.substring(valueTime.indexOf('S') + 1, valueTime.length);
                        } else {
                            second = '0';
                        }
                    };

                    if (valueElement.indexOf('T') > 0) {
                        valueTime = valueElement.slice(valueElement.indexOf('T') + 1, valueElement.length);
                        parseTime(valueTime);
                        valueDate = valueElement.slice(1, valueElement.indexOf('T'));
                        parseDate(valueDate);
                    } else {
                        valueDate = valueElement.slice(1, valueElement.length);
                        parseDate(valueDate);
                        hour = '0'; minute = '0'; second = '0';
                    }

                    periodSlider.children( "#amount-period-year" ).val( year );
                    periodSlider.children( "#amount-period-month" ).val( month );
                    periodSlider.children( "#amount-period-week" ).val( week );
                    periodSlider.children( "#amount-period-day" ).val( day );
                    periodSlider.children( "#amount-period-hour" ).val( hour );
                    periodSlider.children( "#amount-period-minute" ).val( minute );
                    periodSlider.children( "#amount-period-second" ).val( second );

                    periodSlider.children( "#period-year" ).slider("value", year);
                    periodSlider.children( "#period-month" ).slider( "value", month);
                    periodSlider.children( "#period-week" ).slider( "value", week);
                    periodSlider.children( "#period-day" ).slider( "value", day);
                    periodSlider.children( "#period-hour" ).slider( "value", hour);
                    periodSlider.children( "#period-minute" ).slider( "value", minute);
                    periodSlider.children( "#period-second" ).slider( "value", second );
                };

                elem.parent().find('.period-modal-opener').on('click', function() {
                    setParsingPeriod();
                    $("#periodModal").modal('show');
                });

            }
        };
    });

    widgetModule.directive('periodValidity', function() {
        var PERIOD_REGEXP = /^P([0-9]+Y|)?([0-9]+M|)?([0-9]+W|)?([0-9]+D)?(T([0-9]+H)?([0-9]+M)([0-9]+S)|T([0-9]+H)?([0-9]+M)?([0-9]+S)|T([0-9]+H)?([0-9]+M)([0-9]+S)?|T([0-9]+H)([0-9]+M)([0-9]+S)|T([0-9]+H)([0-9]+M)?([0-9]+S)?|T([0-9]+H)?([0-9]+M)([0-9]+S)?|T([0-9]+H)([0-9]+M)([0-9]+S)|T([0-9]+H)([0-9]+M)([0-9]+S))?$/;
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue === '' || PERIOD_REGEXP.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('period', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('period', false);
                        return undefined;
                    }
                });
            }
        };
    });

    widgetModule.directive('periodModal', ['$compile', '$timeout', '$http', '$templateCache', function ($compile, $timeout, $http, $templateCache) {
        var templateLoader;

        return {
            restrict: 'E',
            replace : true,
            transclude: true,
            compile: function (tElement, tAttrs, scope) {
                var url = '../server/resources/partials/period-modal.html',

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
    }]);

    // code for date-picker
    
    widgetModule.directive('datePicker', function() {
        return {
            restrict: 'A',
            scope: {
                min: "=?",
                max: "=?",
                parsed: "=?"
            },
            link: function(scope, element, attrs, ngModel) {
                element.datetimepicker({
                    dateFormat: 'yy-mm-dd',
                    timeFormat: 'HH:mm:ss',
                    changeMonth: true,
                    changeYear: true,
                    beforeShow: function() {
                        if (scope.min) {
                            var parts = scope.min.split(' ');
                            element.datetimepicker('option', 'minDate', parts[0]);
                        }
                        if (scope.max) {
                            var parts = scope.max.split(' ');
                            element.datetimepicker('option', 'maxDate', parts[0]);
                        }
                    },
                    onSelect: function() {
                        scope.$apply(function() {
                            scope.parsed = moment(element.datetimepicker('getDate')).format("YYYY-MM-DDTHH:mm:ssZZ");
                        });
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

    widgetModule.directive('motechFileUpload', ['$compile', '$timeout', '$http', '$templateCache', function ($compile, $timeout, $http, $templateCache) {
        var templateLoader;

        return {
            restrict: 'E',
            replace : true,
            transclude: false,
            compile: function (tElement, tAttrs, scope) {
                var url = '../server/resources/partials/motech-file-upload.html',

                templateLoader = $http.get(url, {cache: $templateCache})
                    .success(function (html) {
                        tElement.html(html);
                    });

                return function (scope, element, attrs) {
                    templateLoader.then(function () {
                        var $input = element.find("#fileInput");
                        if (attrs.accept) {
                            $input.attr('accept', attrs.accept);
                        }
                        element.html($compile(tElement.html())(scope));
                    });
                };
            }
        };
    }]);
}());
