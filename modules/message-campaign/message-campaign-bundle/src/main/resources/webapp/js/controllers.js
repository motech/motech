function CampaignsCtrl($scope, Campaigns) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.campaigns = Campaigns.query();
    });
}

function EnrollmentsCtrl($scope, $routeParams, Enrollments) {

    $scope.campaignName = $routeParams.campaignName;

    $scope.$on('$viewContentLoaded', function () {

        var createOrUpdateEnrollementUrl = "../messagecampaign/enrollments/" + $scope.campaignName + "/users";
        var getEnrollementsUrl = "../messagecampaign/enrollments/users?campaignName=" + $scope.campaignName;
        var deleteEnrollementUrl = "../messagecampaign/enrollments/" + $scope.campaignName + "/users/";

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
                {name:'externalId', index:'externalId', sortable:false, editable:true},
                {name:'edit', formatter:'actions',
                    formatoptions:{keys:true, editbutton:true, delbutton:false, url:createOrUpdateEnrollementUrl, mtype:"POST" }},
                {name:'delete', formatter:'actions',
                    formatoptions:{editbutton:false, delbutton:true, delOptions:{
                        url:deleteEnrollementUrl,
                        mtype:"DELETE",
                        reloadAfterSubmit:true,
                        onclickSubmit: function(colModel, postdata) {
                            var rowdata = jQuery('#enrollmentsTable').getRowData(postdata);
                            colModel.url = colModel.url + encodeURIComponent(rowdata.externalId);
                        }
                    }}}
            ],
            autowidth:true,
            height: "auto",
            multiselect:true
        });

        jQuery("#deleteEnrollments").click(function () {
            var grid = jQuery("#enrollmentsTable");
            var rowIds = grid.jqGrid('getGridParam', 'selarrrow');
            if (rowIds.length > 0) {
                for (i = 0; rowIds[i]; i++) {
                    var refresh = (i == rowIds.length - 1);
                    var rowdata = grid.jqGrid("getRowData", rowIds[i]);
                    jQuery.ajax({
                        type:"DELETE",
                        url:deleteEnrollementUrl + rowdata.externalId,
                        success: function() {
                            if (refresh)
                                jQuery("#enrollmentsTable").trigger('reloadGrid');
                        }
                    });
                }
            }
        });

        jQuery("#addEnrollment").click(function () {
            var rowId = Math.round(Math.random()*10000);
            jQuery("#enrollmentsTable").jqGrid('addRowData', rowId, {});
            jQuery(jQuery("#enrollmentsTable").jqGrid('getInd', rowId, true)).find('.ui-inline-edit').click();
        });
    });
}
