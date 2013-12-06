(function () {
    'use strict';

    var webSecurityModule = angular.module('motech-web-security');

    webSecurityModule.directive('roleNameValidate', function(){
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                    var names = [], i;
                    for (i=0; i<scope.roleList.length; i+=1) {
                        names.push(scope.roleList[i].roleName);
                    }
                    if(scope.addOrEdit === "edit"){
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

    webSecurityModule.directive('permNameValidate', function(){
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

    webSecurityModule.directive('userNameValidate', function(){
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

    webSecurityModule.directive('confirmPassword', function(){
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {

                function validateEqual(confirmPassword, userPassword) {
                    if (confirmPassword === userPassword) {
                        ctrl.$setValidity('equal', true);
                        return confirmPassword;
                    } else {
                        ctrl.$setValidity('equal', false);
                        return undefined;
                    }
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

    webSecurityModule.directive('expandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                var elem = angular.element(element),
                    target = angular.element(attr.href);

                target.livequery(function () {
                    angular.element(this).on({
                        'show.bs.collapse': function () {
                            elem.find('i')
                                .removeClass('icon-chevron-right')
                                .addClass('icon-chevron-down');
                        },
                        'hide.bs.collapse': function () {
                            elem.find('i')
                                .removeClass('icon-chevron-down')
                                .addClass('icon-chevron-right');
                        }
                    });

                    target.expire();
                });
            }
        };
    });

}());
