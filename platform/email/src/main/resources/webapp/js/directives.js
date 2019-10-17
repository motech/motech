(function () {
    'use strict';

    var directives = angular.module('email.directives', []);

    directives.directive('emailPurgeTime', function(){
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

    directives.directive('emailAutoComplete', function () {
        return {
            restrict: 'EA',
            link: function (scope, element, attrs) {
                angular.element(element).autocomplete({
                    minLength: 2,
                    source: function (request, response) {
                       $.getJSON('../email/emails/available/?autoComplete=' + attrs.emailAutoComplete, request, function (data) {
                            response(data);
                        });
                    }
                });
            }
        };
    });

    directives.directive('emailExportDatePickerFrom', function() {
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

    directives.directive('emailJqgridSearch', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element),
                    eventType = elem.data('event-type'),
                    timeoutHnd,
                    filter = function (time) {
                        var field = elem.data('search-field'),
                            value = elem.data('search-value'),
                            type = elem.data('field-type') || 'string',
                            url = parseUri(jQuery('#' + attrs.emailJqgridSearch).jqGrid('getGridParam', 'url')),
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
                                elem.find('i').removeClass('fa-square-o').addClass('fa-check-square-o');
                            } else {
                                elem.find('i').removeClass('fa-check-square-o').addClass('fa-square-o');
                            }
                            break;
                        case 'array':
                            if (elem.children().hasClass("fa-check-square-o")) {
                                elem.children().removeClass("fa-check-square-o").addClass("fa-square-o");
                            } else if (elem.children().hasClass("fa-square-o")) {
                                elem.children().removeClass("fa-square-o").addClass("fa-check-square-o");
                                array.push(value);
                            }
                            angular.forEach(url.queryKey[field].split(','), function (val) {
                                if (angular.element('#' + val).children().hasClass("fa-check-square-o")) {
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
                            jQuery('#' + attrs.emailJqgridSearch).jqGrid('setGridParam', {
                                page: 1,
                                url: '../email/emails' + params,
                                prmNames: {
                                    sort: 'sortColumn',
                                    order: 'sortDirection'
                                }
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

    directives.directive('emailLoggingGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                try {
                    if (typeof($('#emailLoggingTable')[0].grid) !== 'undefined') {
                        return;
                    }
                }
                catch (e) {
                    return;
                }

                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../email/emails?deliveryStatus=ERROR,RECEIVED,SENT',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: 'rows'
                    },
                    colModel: [{
                        name: 'id',
                        index: 'id',
                        hidden: true,
                        key: true
                    }, {
                        name: 'direction',
                        index: 'direction',
                        hidden: true
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
                    pager: '#' + attrs.emailLoggingGrid,
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    sortname: 'deliveryTime',
                    rownumbers: true,
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
                        $("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class=''></table>");

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
                                {name:"message",index:"message", align:"left", sortable: false, classes: "text"}
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

                        $('#outsideEmailLoggingTable').children('div').css('width','100%');
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
                    }
                });
            }
        };
    });
}());
