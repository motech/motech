function CampaignsCtrl($scope, Campaigns) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.campaigns = Campaigns.query();
    });
}

function EnrollmentsCtrl($scope, $routeParams, Enrollments) {

    $scope.campaignName = $routeParams.campaignName;

    jQuery(window).bind('resize', function() {
        jQuery("#enrollmentsTable").jqGrid('setGridWidth', getPanelWidth());
    }).trigger('resize');

    $scope.$on('$viewContentLoaded', function () {

        var createOrUpdateEnrollementUrl = "../messagecampaign/enrollments/" + $scope.campaignName + "/users";
        var getEnrollementsUrl = "../messagecampaign/enrollments/users?campaignName=" + $scope.campaignName;
        var deleteEnrollementUrl = "../messagecampaign/enrollments/" + $scope.campaignName + "/users/";
        var newRowPrefix = "NEW_ROW_";

        jQuery("#enrollmentsTable").jqGrid({
            caption:"Enrollments for Campaign - " + $scope.campaignName,
            url:getEnrollementsUrl,
            datatype:"json",
            jsonReader:{
                root:"enrollments",
                id:"0",
                repeatitems:false
            },
            colNames:['Enrollment ID', 'ID', 'Edit', 'Delete'],
            colModel:[
                {name:'enrollmentId', index:'enrollmentId', hidden:true, editable:true},
                {
                    name:'externalId',
                    index:'externalId',
                    sortable:false,
                    editable:true,
                    editrules:{
                        custom:true,
                        custom_func:validateExternalId
                    }
                },
                {
                    name:'edit',
                    formatter:'actions',
                    formatoptions:{
                        keys:true,
                        editbutton:true,
                        delbutton:false,
                        url:createOrUpdateEnrollementUrl,
                        mtype:"POST",
                        onSuccess:function () {
                            jQuery("#enrollmentsTable").trigger('reloadGrid');
                        },
                        afterRestore:function (rowId) {
                            var grid = jQuery("#enrollmentsTable");
                            var externalId = grid.jqGrid('getRowData', rowId).externalId;
                            if (!externalId) {
                                grid.jqGrid('delRowData', rowId);
                            }
                        }
                    }
                },
                {
                    name:'delete',
                    formatter:deleteFormatter
                }
            ],
            width:getPanelWidth(),
            height:"auto",
            multiselect:true
        });

        function validateExternalId(value, columnName) {
            if (value.length > 0) {
                return [true, ""];
            }
            return [false, "msgCampaign.enrollment.emptyExternalId"];
        }

        function deleteFormatter(cellvalue, options, rowObject) {
            return "" +
                "<div style='margin-left:8px;'>" +
                "<div " +
                "class='ui-pg-div ui-inline-del'" +
                "onmouseout='jQuery(this).removeClass(\"ui-state-hover\");'" +
                "onmouseover='jQuery(this).addClass(\"ui-state-hover\");'" +
                "onclick='deleteRows([\"" + options.rowId + "\"]);'" +
                "style='float:left;margin-left:5px;'" +
                "title='Delete selected row'>" +
                "<span class='ui-icon ui-icon-trash'></span>" +
                "</div>" +
                "</div>";
        }

        jQuery("#deleteEnrollments").click(function () {
            var grid = jQuery("#enrollmentsTable");
            var rowIds = grid.jqGrid('getGridParam', 'selarrrow');
            deleteRows(rowIds);
        });

        this.deleteRows = function (rowIds) {
            if (rowIds.length == 0) {
                motechAlert("msgCampaign.enrollment.noUserSelected", "msgCampaign.enrollment.invalidAction");
                return;
            }
            motechConfirm("msgCampaign.enrollment.deleteConfirmMsg", "msgCampaign.enrollment.deleteConfirmTitle",
                function (response) {
                    if (!response) {
                        return;
                    }
                    var grid = jQuery("#enrollmentsTable");
                    for (var i = 0; i < rowIds.length; i++) {
                        var refresh = (i == rowIds.length - 1);
                        var rowId = rowIds[i];
                        if (isNewRow(rowId)) {
                            grid.jqGrid('delRowData', rowId);
                            continue;
                        }
                        var rowData = grid.jqGrid("getRowData", rowId);
                        jQuery.ajax({
                            type:"DELETE",
                            url:deleteEnrollementUrl + rowData.externalId,
                            success:function () {
                                if (refresh)
                                    jQuery("#enrollmentsTable").trigger('reloadGrid');
                            }
                        });
                    }
                });
        }

        jQuery("#addEnrollment").click(function () {
            var grid = jQuery("#enrollmentsTable");
            var rowIds = grid.jqGrid('getDataIDs');
            for (var i = 0; i < rowIds.length; i++) {
                if (isNewRow(rowIds[i])) {
                    motechAlert("msgCampaign.enrollment.unsavedEnrollement", "msgCampaign.enrollment.invalidAction");
                    return;
                }
            }
            var rowId = newRowPrefix + Math.round(Math.random() * 10000);
            grid.jqGrid('addRowData', rowId, {});
            var row = jQuery(grid.jqGrid('getInd', rowId, true));
            row.find('.ui-inline-edit').click();
            row.find('input').focus();
        });

        var isNewRow = function (rowId) {
            return rowId.startsWith(newRowPrefix);
        };

        $.extend($.jgrid, {
            info_dialog:function (caption, content, c_b, modalopt) {
                setTimeout(function () {
                    motechAlert(content.trim(), "msgCampaign.enrollment.invalidAction");
                }, 0)

            }
        });
    });
    function getPanelWidth() {
        return document.getElementById("main-content").offsetWidth-20;
    }
}
