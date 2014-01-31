(function () {
    'use strict';

    var emailModule = angular.module('motech-email');

    emailModule.directive('innerlayout', function() {
        return {
            restrict: 'EA',
            link: function(scope, elm, attrs) {
                var eastSelector;
                /*
                * Define options for inner layout
                */
                scope.innerLayoutOptions = {
                    name: 'innerLayout',
                    resizable: true,
                    slidable: true,
                    closable: true,
                    east__paneSelector: "#inner-east",
                    center__paneSelector: "#inner-center",
                    east__spacing_open: 6,
                    spacing_closed: 30,
                    east__size: 300,
                    east__minSize: 200,
                    east__maxSize: 350,
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_open: '',
                    togglerContent_closed: '<div><i class="icon-caret-left button"></i></div>',
                    autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
                    noRoom: true,
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top",
                    togglerLength_open: 0,
                    togglerLength_closed: 35,
                    togglerTip_open: "Close This Pane",
                    togglerTip_closed: "Open This Pane",
                    east__initClosed: true,
                    initHidden: false
                };

                // create the page-layout, which will ALSO create the tabs-wrapper child-layout
                scope.innerLayout = elm.layout(scope.innerLayoutOptions);

                // BIND events to hard-coded buttons
                scope.innerLayout.addCloseBtn( "#tbarCloseEast", "east" );
            }
        };
    });

    emailModule.directive('richTextEditor', function() {
        return {
            restrict : "A",
            require : 'ngModel',
            link : function(scope, element, attrs, ctrl) {
                var textarea = element.find('.textarea');

                textarea.livequery(function() {
                    var editor;

                    $(this).wysihtml5({
                        "image": false,
                        "color": false,
                        "link": false,
                        'events': {
                            'change': function() {
                                scope.$apply(function() {
                                    ctrl.$setViewValue(editor.getValue());
                                });
                            },
                            'focus': function() {
                                $('.wysihtml5-sandbox').contents().on("keyup", "body", function() {
                                    scope.$apply(function() {
                                        ctrl.$setViewValue(editor.getValue());
                                    });
                                });
                            }
                        }
                    });

                    editor = $(this).data('wysihtml5').editor;

                    // model -> view
                    ctrl.$render = function() {
                        textarea.html(ctrl.$viewValue);
                        editor.setValue(ctrl.$viewValue);
                    };

                    // load init value from DOM
                    ctrl.$render();
                });
            }
        };
    }).directive('purgeTime', function(){
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, modelCtrl) {
                modelCtrl.$parsers.push(function (inputValue) {
                    if (inputValue === undefined) {
                        return '';
                    }

                    var transformedInput = inputValue.toLowerCase().replace(/[a-z]+$/, '');

                    if (transformedInput !== inputValue) {
                        modelCtrl.$setViewValue(transformedInput);
                        modelCtrl.$render();
                    }

                    return transformedInput;
                });
            }
        };
    });

    emailModule.directive('gridDatePickerFrom', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    endDateTextBox = angular.element('#dateTimeTo');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    maxDate: +0,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        endDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                    }
                });
            }
        };
    });

    emailModule.directive('gridDatePickerTo', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    startDateTextBox = angular.element('#dateTimeFrom');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    maxDate: +0,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        startDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                    }
                });
            }
        };
    });

    emailModule.directive('autoComplete', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                angular.element(element).autocomplete({
                    minLength: 2,
                    source: function (request, response) {
                       $.getJSON('../email/emails/available/?autoComplete=' + attrs.autoComplete, request, function (data) {
                            response(data);
                        });
                    }
                });
            }
        };
    });

    emailModule.directive('exportDatePickerFrom', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    endDateTextBox = angular.element('#monthPicker'),
                    datepickerParent = angular.element('#ui-datepicker-div').parent();


                elem.datepicker({
                    dateFormat: "mm-yy",
                    changeMonth: true,
                    changeYear: true,
                    showButtonPanel: true,
                    maxDate: +0,
                    onClose: function(dateText, inst) {
                        var month, year;
                        month = $(".ui-datepicker-month :selected").val();
                        year = $(".ui-datepicker-year :selected").val();
                        $(this).datepicker('setDate', new Date(year, month, 1));
                        $('#ui-datepicker-div').removeClass('nodays');
                        datepickerParent.append(angular.element('#ui-datepicker-div'));
                    },
                    beforeShow: function(input, inst) {
                        var dateString, options;
                        dateString = $(this).val();
                        options = {};
                        if (dateString.length > 0) {
                            options.defaultDate = $.datepicker.parseDate("dd-" + $(this).datepicker("option", "dateFormat"), "01-" + dateString);
                        }
                        if ($.contains(angular.element('#exportEmailLogModal'), angular.element('#ui-datepicker-div'))) {
                            datepickerParent.append(angular.element('#ui-datepicker-div'));
                        } else {
                            angular.element('#exportEmailLogModal').append(angular.element('#ui-datepicker-div'));
                        }
                        if ($(input).hasClass('nodays')) {
                                $('#ui-datepicker-div').addClass('nodays');
                            } else {
                                $('#ui-datepicker-div').removeClass('nodays');
                                $(this).datepicker('option', 'dateFormat', 'yy-mm');
                            }
                        return options;
                    }
                });
            }
        };
    });

    emailModule.directive('jqgridSearch', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element),
                    table = angular.element('#' + attrs.jqgridSearch),
                    eventType = elem.data('event-type'),
                    timeoutHnd,
                    filter = function (time) {
                        var field = elem.data('search-field'),
                            value = elem.data('search-value'),
                            type = elem.data('field-type') || 'string',
                            url = parseUri(jQuery('#' + attrs.jqgridSearch).jqGrid('getGridParam', 'url')),
                            query = {},
                            params = '?',
                            array = [],
                            prop;

                        for (prop in url.queryKey) {
                            if (prop !== field) {
                                query[prop] = url.queryKey[prop];
                            }
                        }

                        switch (type) {
                        case 'boolean':
                            query[field] = url.queryKey[field].toLowerCase() !== 'true';

                            if (query[field]) {
                                elem.find('i').removeClass('icon-ban-circle').addClass('icon-ok');
                            } else {
                                elem.find('i').removeClass('icon-ok').addClass('icon-ban-circle');
                            }
                            break;
                        case 'array':
                            if (elem.children().hasClass("icon-ok")) {
                                elem.children().removeClass("icon-ok").addClass("icon-ban-circle");
                            } else if (elem.children().hasClass("icon-ban-circle")) {
                                elem.children().removeClass("icon-ban-circle").addClass("icon-ok");
                                array.push(value);
                            }
                            angular.forEach(url.queryKey[field].split(','), function (val) {
                                if (angular.element('#' + val).children().hasClass("icon-ok")) {
                                    array.push(val);
                                }
                            });

                            query[field] = array.join(',');
                            break;
                        default:
                            query[field] = elem.val();
                        }

                        for (prop in query) {
                            params += prop + '=' + query[prop] + '&';
                        }

                        params = params.slice(0, params.length - 1);

                        if (timeoutHnd) {
                            clearTimeout(timeoutHnd);
                        }

                        timeoutHnd = setTimeout(function () {
                            jQuery('#' + attrs.jqgridSearch).jqGrid('setGridParam', {
                                url: '../email/emails' + params
                            }).trigger('reloadGrid');
                        }, time || 0);
                    };

                switch (eventType) {
                case 'keyup':
                    elem.keyup(function () {
                        filter(500);
                    });
                    break;
                case 'change':
                    elem.change(filter);
                    break;
                default:
                    elem.click(filter);
                }
            }
        };
    });

    emailModule.directive('emailloggingGrid', function($compile, $http, $templateCache) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../email/emails?deliveryStatus=ERROR,RECEIVED,SENT',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: 'rows'
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colModel: [{
                        name: 'direction',
                        index: 'direction',
                        hidden: true,
                        width: 100
                    }, {
                        name: 'deliveryStatus',
                        index: 'deliveryStatus',
                        align: 'center',
                        width: 155
                    }, {
                        name: 'toAddress',
                        index: 'toAddress',
                        width: 200
                    },{
                        name: 'fromAddress',
                        index: 'fromAddress',
                        width: 200
                    }, {
                        name: 'subject',
                        index: 'subject',
                        width: 250
                    }, {
                        name: 'deliveryTime',
                        index: 'deliveryTime',
                        align: 'center',
                        width: 200
                    }],

                    pager: '#' + attrs.emailloggingGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'deliveryTime',
                    sortorder: 'asc',
                    viewrecords: true,
                    subGrid: true,
                    subGridOptions: {
                        "plusicon" : "ui-icon-triangle-1-e",
                        "minusicon" : "ui-icon-triangle-1-s",
                        "openicon" : "ui-icon-arrowreturn-1-e"
                    },
                    subGridRowExpanded: function(subgrid_id, row_id) {
                        var subgrid_table_id, pager_id;
                        subgrid_table_id = subgrid_id+"_t";
                        pager_id = "p_"+subgrid_table_id;
                        $("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class=''></table>");

                        jQuery("#"+subgrid_table_id).jqGrid({
                            url:'../email/emails/' +row_id,
                            datatype:"json",
                            jsonReader:{
                                repeatitems: false,
                                root:  'rows'
                            },
                            viewrecords: true,
                            colNames: ['Message'],
                            colModel: [
                                {name:"message",index:"message", align:"left", sortable: false}
                            ],
                            rowNum:1,
                            pager: pager_id,
                            sortname: 'message',
                            sortorder: "asc",
                            height: '100%'
                        });
                        jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false});

                        $('div.ui-widget-content').width('100%');
                        $('div.ui-jqgrid-bdiv').width('100%');
                        $('div.ui-jqgrid-view').width('100%');
                        $('div.ui-jqgrid-hdiv').width('auto');
                        $('table.ui-jqgrid-htable').width('100%');
                        $('table.ui-jqgrid-btable').width('100%');
                        $('div.ui-jqgrid-hbox').css({'padding-right':'0'});

                    },
                    loadComplete : function(array) {
                        angular.forEach(['direction', 'deliveryStatus', 'toAddress', 'fromAddress', 'subject', 'deliveryTime','message'], function (value) {
                            elem.jqGrid('setLabel', value, scope.msg('email.logging.' + value));
                            if (array.rows[0] !== undefined && !array.rows[0].hasOwnProperty(value)) {
                                elem.jqGrid('hideCol', value);
                                if ('message' === value) {
                                    elem.hideCol('subgrid');
                                }
                            }
                        });

                        $('#outsideEmailLoggingTable').children('div').width('100%');
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-bdiv').width('100%');
                        $('.ui-jqgrid-hdiv').width('auto');
                        $('.ui-jqgrid-hbox').width('100%');
                        $('.ui-jqgrid-view').width('100%');
                        $('#t_emailLoggingTable').width('auto');
                        $('.ui-jqgrid-pager').width('100%');
                        $('#outsideEmailLoggingTable').children('div').each(function() {
                            $('table', this).width('100%');
                            $(this).find('#emailLoggingTable').width('100%');
                            $(this).find('table').width('100%');
                       });
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    }
                });
            }
        };
    });
}());
