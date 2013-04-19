'use strict';

/* Controllers */

function PatientMrsCtrl($scope, Patient, $http, $routeParams, $filter) {
    var searchMatch = function (patient, searchQuery) {
        if (!searchQuery) {
            return true;
        } else if (patient.person.firstName.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1) {
            return true;
        } else if (patient.person.lastName.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1) {
            return true;
        } else
            return patient.motechId.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
    };

    $scope.sortingOrder = 'motechId';
    $scope.reverse = false;
    $scope.filteredItems = [];
    $scope.limitPages = [10, 20, 50, 100];
    $scope.itemsPerPage = $scope.limitPages[0];
    $scope.resetItemsPagination();
    $scope.showPatientsView=true;
    $scope.selectedPatientView=true;
    $scope.patientDto = {};


    $scope.getPatient = function (motechId) {
        Patient.get( { motechId: motechId }, function (data) {
            $scope.patientDto = data;
            if (data.person.dead == true) {
                $scope.dead = "mrs.yes";
            } else {
                $scope.dead = "mrs.no";
            }
            if (data.person.birthDateEstimated == true) {
                $scope.birthDateEstimated = "mrs.yes";
            } else {
                $scope.birthDateEstimated = "mrs.no";
            }

        }, angularHandler('mrs.header.error', 'mrs.patient.error'));

        $scope.showPatientsView = !$scope.selectedPatientView;
        $scope.selectedPatientView = !$scope.selectedPatientView;
    }

    $scope.search = function () {
        unblockUI();
        $scope.filteredItems = $filter('filter')($scope.patientList, function (item) {
            if (item) {
                return searchMatch(item, $scope.query);
            }
            return false;
        });
        if ($scope.sortingOrder !== '') {
            $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sortingOrder, $scope.reverse);
        }
        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    };

    $scope.sort_by = function (newSortingOrder) {
        if ($scope.sortingOrder == newSortingOrder) {
            $scope.reverse = !$scope.reverse;
        }

        $scope.sortingOrder = newSortingOrder;

        $('th img').each(function(){
            $(this).removeClass().addClass('sorting-no');
        });

        if ($scope.sortingOrder !== '') {
            $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sortingOrder, $scope.reverse);
        }
        $scope.sortingOrderClass = $scope.sortingOrder.replace("person.","");

        if ($scope.reverse)
            $('th.'+$scope.sortingOrderClass+' img').removeClass('sorting-no').addClass('sorting-desc');
        else
            $('th.'+$scope.sortingOrderClass+' img').removeClass('sorting-no').addClass('sorting-asc');
    };

    /** Get patient(s) **/

    if ($routeParams.motechId != undefined) {
        $scope.getPatient($routeParams.motechId);
    } else {
        blockUI();
        $scope.patientList = Patient.query({}, $scope.search, angularHandler('mrs.header.error', 'mrs.patient.list.error'));
    }

    $scope.changeItemsPerPage = function(newItemsPerPage){
        $scope.itemsPerPage = newItemsPerPage;
        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    }
}

function SettingsMrsCtrl($scope, $http) {
    getMrsProviders();

    function getMrsProviders() {
        $http.get('../mrs/api/impl/adapters').success(function(data) {
            $scope.mrsProvidersList = data;
        });

        $http.get('../mrs/api/impl/adapters/default').success(function(data) {
            $scope.provider = data;
        });
    }

    $scope.changeProvider = function (provider) {
        $http.post('../mrs/api/impl/adapters', provider).success(function() {
            $scope.provider = provider;
            motechAlert('mrs.provider.success.saved', 'mrs.header.changed');
        });
    }
}

function ManagePatientMrsCtrl($scope, Patient, $routeParams, $location, $http) {
    $scope.patientDto = new Patient();
    $scope.motechIdValidate=true;
    $scope.hideMotechId=true
    $scope.inProgress = false;
    var reqList = {};
    var typingTimer;
    var doneTypingInterval = 3000;

    function clearReqList() {
        reqList['motechId'] = false;
        reqList['firstName'] = false;
        reqList['middleName'] = false;
        reqList['lastName'] = false;
        reqList['preferredName'] = false;
        reqList['address'] = false;
        reqList['gender'] = false;
        reqList['dateOfBirth'] = false;
        reqList['facilityId'] = false;
    }


    $http.get('../mrs/api/patients/req').success(function(data) {
        clearReqList();
        for(var i=0;i<data.length;i++)
        {
             reqList[data[i]] = true;
        }
    });

    $('#inputMotechId').keyup(function(){
        if ($routeParams.motechId == undefined) {
            $scope.inProgress = true;
            clearTimeout(typingTimer);
            if ($('#inputMotechId').val) {
                typingTimer = setTimeout(checkIfPatientExist, doneTypingInterval);
            }
        }
    });

    $('#inputMotechId').keydown(function(){
        if ($routeParams.motechId == undefined) {
            $scope.inProgress = true;
        }
        if ($scope.patientDto.motechId.length > 0) {
            $scope.hideMotechId = false;
        }
        else {
            $scope.hideMotechId = true;
        }
    });

    if ($routeParams.motechId) {
        $scope.patientDto = Patient.get( { motechId: $routeParams.motechId }, function () {
            $scope.inProgress = false;
            $('#inputMotechId').prop('readonly', true);
        }, angularHandler('mrs.header.error', 'mrs.patient.error'));
    } else {
        $scope.inProgress = false;
    }

    $scope.save = function() {
        blockUI();

        if ($routeParams.motechId) {
            $scope.patientDto.$update({motechId: $routeParams.motechId}, function(data) {
                var alertTxt = "mrs.success.saved"
                if ($location.path().indexOf("Attributes") != -1) {
                    alertTxt = "mrs.success.saved.attributes";
                }

                unblockUI();
                motechAlert('mrs.success.saved', alertTxt);
                $location.path('/patients/' + $scope.patientDto.motechId);
            }, angularHandler('mrs.header.error', 'mrs.error.saved'));
        } else {
            $scope.patientDto.$save({}, function(data) {
                unblockUI();
                motechAlert('mrs.success.saved', 'mrs.header.saved');
                $location.path('/patients/' + $scope.patientDto.motechId);
            }, angularHandler('mrs.header.error', 'mrs.error.saved'));
        }
    }

    function checkIfPatientExist () {
        if ($routeParams.motechId == undefined) {
            $scope.motechIdValidate = true;
            Patient.get( { motechId: $scope.patientDto.motechId }, function (data) {
                if (data == "") return $scope.motechIdValidate = true;
                else $scope.motechIdValidate = false;
            });
            $scope.inProgress = false;
        }
    }

    $scope.validateForm = function() {
        return !($scope.form.$invalid || !$scope.motechIdValidate || $scope.inProgress);
    }

    $scope.validateFilterForm = function () {
        var isPass = true, i;

        for (i = 0; i < $scope.patientDto.person.attributes.length; i += 1) {
            if (!$scope.patientDto.person.attributes[i].name || !$scope.patientDto.person.attributes[i].value) {
                isPass = false;
            }
        }

        return isPass;
    };

    $scope.addAttribute = function() {
        $scope.patientDto.person.attributes.push({});
    }

    $scope.removeAttribute = function(attribute) {
           $scope.patientDto.person.attributes.removeObject(attribute);
    }

    $scope.cssClass = function(prop, option) {
        var msg = 'control-group';

        if ($scope.isRequired(prop) && !$scope.hasValue(prop, option)) {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.hasValue = function(prop, option) {
        if (!$scope.patientDto) {
            return true;
        }

        switch(option)
        {
            case '1':
                return $scope.patientDto.hasOwnProperty(prop) && $scope.patientDto[prop] != undefined;
            case '2':
                if($scope.patientDto.person && $scope.patientDto.person.hasOwnProperty(prop) && $scope.patientDto.person[prop] != undefined) {
                    if($scope.patientDto.person[prop] != "")
                        return true;
                }
                return false;
            case '3':
                if($scope.patientDto.facility && $scope.patientDto.facility.hasOwnProperty(prop) && $scope.patientDto.facility[prop] != undefined) {
                    if($scope.patientDto.facility[prop] != "")
                        return true;
                }
                return false;
            default:
                break;
        }
    }

     $scope.isRequired = function(prop) {
        return reqList[prop];
    }

    $scope.cancel = function() {
        var path = '/patients/';
        if ($routeParams.motechId) {
            path += $routeParams.motechId
        }
        $location.path(path);
    }

}
