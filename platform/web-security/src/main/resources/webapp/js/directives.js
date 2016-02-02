(function () {
    'use strict';

    var directives = angular.module('webSecurity.directives', []);

    directives.directive('roleNameValidate', function(){
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var names = [], i;
                    for (i=0; i<scope.roleList.length; i+=1) {
                        names.push(scope.roleList[i].roleName);
                    }
                    if(scope.isEdit === true){
                        names.removeObject(scope.role.originalRoleName);
                    }
                    if(names.indexOf(viewValue)===-1) {
                        scope.pwdNameValidate=true;
                        ctrl.$setValidity('pwd', true);
                        return viewValue;
                    } else {
                        scope.pwdNameValidate=false;
                        ctrl.$setValidity('pwd', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('permNameValidate', function(){
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var names = [], i;
                    for (i=0; i<scope.permissionList.length; i+=1) {
                        names.push(scope.permissionList[i].permissionName);
                    }
                    if(names.indexOf(viewValue)===-1) {
                        scope.pwdNameValidate=true;
                        ctrl.$setValidity('pwd', true);
                        return viewValue;
                    } else {
                        scope.pwdNameValidate=false;
                        ctrl.$setValidity('pwd', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('userNameValidate', function(){
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var names = [], i;
                    for (i=0; i<scope.userList.length; i+=1) {
                        names.push(scope.userList[i].userName);
                    }
                    if(names.indexOf(viewValue)===-1) {
                        scope.pwdNameValidate=true;
                        ctrl.$setValidity('pwd', true);
                        return viewValue;
                    } else {
                        scope.pwdNameValidate=false;
                        ctrl.$setValidity('pwd', false);
                        return viewValue;
                    }
                });
            }
        };
    });

    directives.directive('websecurityExpandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var target = angular.element($('#dynamic-rules'));

                target.on('show.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('i.fa-caret-right')
                        .removeClass('fa-caret-right')
                        .addClass('fa-caret-down');
                });

                target.on('hide.bs.collapse', function (e) {
                    $(e.target).siblings('.panel-heading').find('i.fa-caret-down')
                        .removeClass('fa-caret-down')
                        .addClass('fa-caret-right');
                });
            }
        };
    });

    directives.directive('select2Init', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl, ngModel) {
                var elm = angular.element(element);
                elm.select2('val', attrs.ngModel);
                scope.$watch(attrs.ngModel, function (newVal, oldVal) {
                    if (newVal !== oldVal) {
                        elm.select2('val', newVal);
                    }
                });
            }
        };
    });

}());
