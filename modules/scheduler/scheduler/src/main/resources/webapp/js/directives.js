(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('scheduler.directives', []);

    directives.directive('sidebar', function () {
        return function (scope, element, attrs) {
            $(element).sidebar({
                position:"right"
            });
        };
    });

    directives.directive('clearForm', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).on('hidden', function () {
                    $('#' + attrs.clearForm).clearForm();
                });
            }
        };
    });


    directives.directive('schedulerGridDatePickerFrom', function() {
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

    directives.directive('schedulerGridDatePickerTo', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    startDateTextBox = angular.element('#dateTimeFrom');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        startDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                    }
                });
            }
        };
    });

    directives.directive('schedulerJqgridSearch', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element),
                    table = angular.element('#' + attrs.schedulerJqgridSearch),
                    eventType = elem.data('event-type'),
                    timeoutHnd,
                    filter = function (time) {
                        var field = elem.data('search-field'),
                            value = elem.data('search-value'),
                            type = elem.data('field-type') || 'string',
                            url = parseUri(jQuery('#' + attrs.schedulerJqgridSearch).jqGrid('getGridParam', 'url')),
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
                            jQuery('#' + attrs.schedulerJqgridSearch).jqGrid('setGridParam', {
                                page: 1,
                                url: '../scheduler/api/jobs' + params
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

    directives.directive('schedulerGrid', function($compile, $http, $templateCache, MotechScheduler) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                try {
                    if (typeof($('#schedulerTable')[0].grid) != 'undefined') return;
                }
                catch (e) {
                    return;
                }

                var elem = angular.element(element), filters, i, j, k, rows, activity, status;

                elem.jqGrid({
                    url:"../scheduler/api/jobs?name=&sortColumn=name&sortDirection=asc&activity=NOTSTARTED,ACTIVE,FINISHED&status=ERROR,BLOCKED,PAUSED,OK&timeFrom=&timeTo=",
                    datatype:"json",
                    jsonReader:{
                        repeatitems: false
                    },
                    colModel:[
                        {name:'activity',index:'activity', width: 100, align:"center"},
                        {name:'status',index:'status', width: 90, align:"center"},
                        {name:'name',index:'name', width: 400, align:"left"},
                        {name:'startDate',index:'startDate', width: 180, align:"center", sorttype:"date"},
                        {name:'endDate',index:'endDate', width: 160, algign:"center", sorttype:"date"},
                        {name:'jobType',index:'jobType', width: 100, align:"center"},
                        {name:'info',index:'info', width: 130, align:"left"}
                    ],
                    pager: '#' + attrs.schedulerGrid,
                    rownumbers: true,
                    rownumWidth: 20,
                    sortname: 'startDate',
                    viewrecords: true,
                    multiselect: false,
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
                        url:'../scheduler/api/jobs/'+row_id,
                            datatype:"json",
                            jsonReader:{
                                repeatitems: false,
                                root: 'eventInfoList',
                                records: function (obj) { return obj.length; }
                            },
                            colNames: [scope.msg('scheduler.subject'), scope.msg('scheduler.key'), scope.msg('scheduler.value')],
                            colModel: [
                                {name:scope.msg('.subject'),index:"subject", align:"center"},
                                {name:"parameters",index:"parameters", width:80, align:"center",
                                    formatter: function (array, options, data) {
                                        var div = $('<div>');
                                        $.each(array, function (key, value) {
                                            div.append($('<div>').append(key)
                                                .addClass('parameters')
                                            );
                                        });
                                        return '<div>' + div.html() + '</div>';
                                    }},
                                {name:"parameters",index:"parameters", width:80, align:"center",
                                    formatter: function (array, options, data) {
                                        var div2 = $('<div>');
                                        $.each(array, function (key, value) {
                                            div2.append($('<div>').append($('<span>').append(value))
                                                .addClass('parameters')
                                            );
                                        });
                                        return '<div>' + div2.html() + '</div>';
                                    }}
                            ],
                            rowNum:99, pager: pager_id, sortname: 'parameters', sortorder: "asc", height: '100%' });
                        jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false});
                        jQuery("#"+subgrid_table_id).jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders:[
                                {startColumnName: 'Key', numberOfColumns: 2, titleText: 'Parameters'}
                            ]
                        });

                        $('div.ui-widget-content').width('100%');
                        $('div.ui-jqgrid-bdiv').width('100%');
                        $('div.ui-jqgrid-view').width('100%');
                        $('div.ui-jqgrid-hdiv').width('100%');
                        $('table.ui-jqgrid-htable').width('100%');
                        $('table.ui-jqgrid-btable').width('100%');
                        $('div.ui-jqgrid-hbox').css({'padding-right':'0'});

                    },

                    gridComplete: function () {
                        $.ajax({
                            url: '../server/lang/locate',
                            success:  function() {},
                            async: false
                        });

                        angular.forEach(['activity', 'status', 'name', 'startDate', 'endDate', 'jobType', 'info'], function (value) {
                            elem.jqGrid('setLabel', value, scope.msg('scheduler.' + value));
                        });

                        $('#outsideSchedulerTable').children('div').width('100%');
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-bdiv').width('100%');
                        $('.ui-jqgrid-hdiv').width('100%');
                        $('div.ui-jqgrid-hbox').css({'padding-right':'0'});
                        $('.ui-jqgrid-hbox').width('100%');
                        $('.ui-jqgrid-view').width('100%');
                        $('#t_resourceTable').width('auto');
                        $('.ui-jqgrid-pager').width('100%');
                        $('#outsideSchedulerTable').children('div').each(function() {
                            $(this).find('table').width('100%');
                        });
                        rows = $("#schedulerTable").getDataIDs();
                        for (k = 0; k < rows.length; k+=1) {
                            activity = $("#schedulerTable").getCell(rows[k],"activity").toLowerCase();
                            status = $("#schedulerTable").getCell(rows[k],"status").toLowerCase();
                            if (activity !== undefined && status !== undefined) {
                                switch (activity) {
                                    case 'active':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','active',{ },'');
                                        break;
                                    case 'stopped':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','stopped',{ },'');
                                        break;
                                    case 'finished':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','finished',{ },'');
                                        break;
                                    case 'notstarted':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','notstarted',{ },'');
                                        break;
                                    default:
                                        break;
                                }
                                switch (status) {
                                    case 'ok':
                                        if (activity === 'active') {
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-check-circle icon-green"></i>','ok',{ },'');
                                        } else if (activity === 'notstarted' || activity === 'finished'){
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-clock-o icon-gold"></i>','waiting',{ },'' );
                                        } else if (activity === 'error'){
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-exclamation-triangle icon-red"></i>','error',{ },'');
                                        } else {
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-spinner fa-spin icon-green"></i>','idle',{ },'');
                                        }
                                        break;
                                    case 'paused':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-pause icon-gold"></i>','waiting',{ },'');
                                        break;
                                    case 'blocked':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-minus-circle icon-red"></i>','waiting',{ },'');
                                        break;
                                    case 'error':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i title="'+scope.msg('scheduler.' + status).toUpperCase()+'" class="fa fa-lg fa-exclamation-triangle icon-red"></i>','error',{ },'');
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                    }
                });
            }
        };
    });

}());
