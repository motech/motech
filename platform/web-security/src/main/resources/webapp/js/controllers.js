(function () {
    'use strict';

    var webSecurityModule = angular.module('motech-web-security');

    webSecurityModule.controller('UserCtrl', function ($scope, Roles, Users, $http) {
           $scope.user = {
               externalId : "",
               userName: "",
               password: "",
               email: "",
               roles: [],
               active: true,
               openId: "",
               generatePassword:false
           };
           $scope.confirmPassword="";
           $scope.propertyUserName="";
           $scope.pasPassword=true;
           $scope.pwdNameValidate=true;
           $scope.addUserView=true;
           $scope.editUserView=true;
           $scope.showUsersView=true;
           $scope.currentPage=0;
           $scope.pageSize=15;
           $scope.selectedItem='';
           $scope.deleteU=false;

           $scope.isFormValid = function() {
               if($scope.user.generatePassword) {
                    if($scope.createUserForm.userName.$valid && $scope.createUserForm.email.$valid) {
                        return false;
                    } else {
                        return true;
                    }
               } else {
                    return $scope.createUserForm.$valid;
               }
           };

           $scope.roleList = Roles.query();

           $scope.userList = Users.query();

           $scope.selectedRole = -1;

           $scope.loginMode = function() {
               $http.get('../websecurity/api/users/loginmode').
                   success(function(data){
                       return data==="openid" ? false : true;
                   });
           };

           $scope.activeRole = function(roleName) {
                 if ($scope.user.roles.indexOf(roleName)===-1) {
                    $scope.user.roles.push(roleName);
                 } else {
                    $scope.user.roles.removeObject(roleName);
                 }
           };

           $scope.getClass = function(roleName) {
                if ($scope.user.roles.indexOf(roleName)!==-1){
                    return "btn btn-success";
                } else {
                    return "btn";
                }
           };

           $scope.saveUser = function() {
               $http.post('../websecurity/api/users/create', $scope.user).
                    success(function(){
                        motechAlert('security.create.user.saved', 'security.create');
                        $scope.userList = Users.query();
                        $scope.showUsersView=!$scope.addUserView;
                        $scope.addUserView=!$scope.addUserView;
                    }).
                    error(function(response) {
                        handleResponse('server.error', 'security.create.user.error', response);
                        if (response && response.startsWith('key:security.sendEmailException')) {
                            $scope.userList = Users.query();
                            $scope.showUsersView=!$scope.addUserView;
                            $scope.addUserView=!$scope.addUserView;
                        }
                    });
           };


           $scope.numberOfPages = function(){
               return Math.ceil($scope.userList.length/$scope.pageSize);
           };

           $scope.changeCurrentPage = function(page) {
               $scope.currentPage=page;
           };

           $scope.getUser = function(user)  {
               $scope.successfulMessage='';
               $scope.failureMessage='';
               $scope.deleteU=false;
               $http.post('../websecurity/api/users/getuser', user.userName).success(function(data) {
                       $scope.user = data;
                       $scope.user.password='';
                       $scope.confirmPassword="";
               });
               $scope.showUsersView=!$scope.editUserView;
               $scope.editUserView=!$scope.editUserView;
           };

           $scope.updateUser = function(){
               $http.post('../websecurity/api/users/update', $scope.user).
                   success(function(){motechAlert('security.update.user.saved', 'security.update');
                       $scope.userList = Users.query();
                       $scope.showUsersView=!$scope.editUserView;
                       $scope.editUserView=!$scope.editUserView;
                       $scope.$emit('module.list.refresh');
                   }).error(angularHandler('server.error', 'security.update.user.error'));
           };

           $scope.deleteUser = function() {
               $http.post('../websecurity/api/users/delete', $scope.user).
                    success(function(){
                        motechAlert('security.delete.user.saved', 'security.deleted');
                        $scope.showUsersView=!$scope.editUserView;
                        $scope.editUserView=!$scope.editUserView;
                        $scope.userList = Users.query();
                    }).error(function(){motechAlert('security.delete.user.error', 'server.error');});
           };

           $scope.resetValues = function() {
                $scope.user = {
                    externalId : "",
                    userName: "",
                    password: "",
                    email: "",
                    roles: [],
                    active: true,
                    openId: "",
                    generatePassword:false
                };
                $scope.confirmPassword="";
           };

           $scope.hasValue = function(prop) {
               return $scope.user.hasOwnProperty(prop) && $scope.user[prop] !== '' && $scope.user[prop] !== undefined;
           };

           $scope.hasPassword = function(password){
                return $scope[password] !== '' && $scope[password] !== undefined;
           };

           $scope.cssPassword = function() {
               var msg = 'control-group';
                  if ($scope.user.password!==$scope.confirmPassword || (!$scope.hasValue('password') && $scope.editUserView)) {
                     msg = msg.concat(' server.error');
                  }
               return msg;
           };

           $scope.cssClass = function(prop, pass) {
               var msg = 'control-group';
               if (!$scope.hasValue(prop)) {
                    msg = msg.concat(' server.error');
               }
               return msg;
           };

           $scope.addUser=function() {
               $scope.resetValues();
               $scope.showUsersView=!$scope.addUserView;
               $scope.addUserView=!$scope.addUserView;
           };

           $scope.cancelAddUser=function() {
               $scope.showUsersView=!$scope.addUserView;
               $scope.addUserView=!$scope.addUserView;
           };

           $scope.cancelEditUser=function() {
               $scope.showUsersView=!$scope.editUserView;
               $scope.editUserView=!$scope.editUserView;
          };
    });

    webSecurityModule.controller('RolePermissionCtrl', function ($scope, Roles, Permissions, $http) {
           $scope.role = {
                roleName : '',
                originalRoleName:'',
                permissionNames : [],
                deletable : false
           };
           $scope.permission = {};
           $scope.addingPermission=false;
           $scope.addRoleView=true;
           $scope.pwdNameValidate=true;
           $scope.successfulMessage='';
           $scope.currentPage=0;
           $scope.pageSize=15;
           $scope.addOrEdit="";
           $scope.roleList = Roles.query();
           $scope.permissionList = [];

           Permissions.query(function(data) {
               $scope.permissionList = data;
           });

           $scope.numberOfPages=function(){
               return Math.ceil($scope.roleList.length/$scope.pageSize);
           };

           $scope.changeCurrentPage = function(page) {
               $scope.currentPage=page;
           };

           $scope.uniquePermissionList = function(list) {
               var newArr = [],
               listLen = list.length,
               found, x, y;
               for (x = 0; x < listLen; x+=1) {
                   found = undefined;
                   for (y = 0; y < newArr.length; y+=1) {
                       if (list[x].bundleName === newArr[y].bundleName) {
                           found = true;
                           break;
                       }
                   }
                   if (!found) {
                       newArr.push(list[x]);
                   }
               }
               return newArr;
           };

           $scope.addPermission = function(permissionName) {
               if ($scope.role.permissionNames.indexOf(permissionName)===-1) {
                   $scope.role.permissionNames.push(permissionName);
               } else {
                   $scope.role.permissionNames.removeObject(permissionName);
               }
           };

            $scope.saveRole = function() {
                if ($scope.addOrEdit==="add") {
                    $scope.role.deletable = true;
                    $http.post('../websecurity/api/web-api/roles/create', $scope.role).
                       success(function() {
                       motechAlert('security.create.role.saved', 'security.create');
                       $scope.roleList=Roles.query();
                       $scope.addRoleView=!$scope.addRoleView;
                       }).
                       error(function(){motechAlert('security.create.role.error', 'server.error');});
                } else {
                    $http.post('../websecurity/api/web-api/roles/update', $scope.role).
                       success(function() {
                       motechAlert('security.update.role.saved', 'security.update');
                       $scope.roleList=Roles.query();
                       $scope.addRoleView=!$scope.addRoleView;
                       $scope.$emit('module.list.refresh');
                       }).
                       error(function(){motechAlert('security.update.role.error', 'server.error');});
                }
            };

            $scope.getRole = function(role)  {
                $scope.addOrEdit = "edit";
                $http.post('../websecurity/api/web-api/roles/getrole', role.roleName).success(function(data) {
                       $scope.role = data;
                       $scope.role.originalRoleName=role.roleName;
                });
                $scope.addRoleView=!$scope.addRoleView;
                $scope.deleteR = false;
            };

            $scope.deleteRole = function() {
                $http.post('../websecurity/api/web-api/roles/delete', $scope.role).
                success(function(){
                    motechAlert('security.delete.role.saved', 'security.deleted');
                    $scope.addRoleView=!$scope.addRoleView;
                    $scope.roleList = Roles.query();
                }).error(function(response){
                    handleResponse('server.error', 'security.delete.role.error', response);
                });
            };

            $scope.addRole = function() {
                $scope.role = {
                        roleName : '',
                        originalRoleName:'',
                        permissionNames : [],
                        deletable : false
                };
                $scope.addOrEdit = "add";
                $scope.addRoleView=!$scope.addRoleView;
            };

            $scope.startAddingPermission = function() {
                $scope.permission = {};
                $scope.addingPermission = true;
            };

            $scope.cancelAddingPermission = function() {
                $scope.addingPermission = false;
            };

            $scope.permissionHasValue = function(prop) {
                return $scope.permission.hasOwnProperty(prop) && $scope.permission[prop] !== '' && $scope.permission[prop] !== undefined;
            };

            $scope.savePermission = function() {
                $scope.addingPermission = false;
                Permissions.save($scope.permission, function() {
                   $("#permissionSaveSuccessMsg").css('visibility', 'visible').animate({opacity:0}, 5000);
                   Permissions.query(function(data) {
                        $scope.permissionList = data;
                   });
                }, angularHandler('server.error', 'security.create.permission.error'));
            };

            $scope.deletePermission = function(permission) {
                motechConfirm('security.confirm.permissionDelete', "security.confirm", function (val) {
                    if (val) {
                        permission.$delete(function() {
                           Permissions.query(function(data) {
                                $scope.permissionList = data;
                           });
                        }, angularHandler('server.error', 'security.delete.permission.error'));
                    }
                });
            };

            $scope.cancelRole=function() {
                $scope.addRoleView=!$scope.addRoleView;
            };

            $scope.hasValue = function(prop) {
                return $scope.role.hasOwnProperty(prop) && $scope.role[prop] !== '' && $scope.role[prop] !== undefined;
            };

            $scope.cssClass = function(prop) {
                var msg = 'control-group';

                if (!$scope.hasValue(prop)) {
                    msg = msg.concat(' server.error');
                }

                return msg;
            };

            $scope.isChecked = function(permissionName){
                  return $scope.role.permissionNames.indexOf(permissionName)===-1 ? false : true;
            };
    });

    webSecurityModule.controller('ProfileCtrl', function ($scope, Users, $http, $routeParams) {
        if ($routeParams.username !== undefined) {
            $http.post('../websecurity/api/users/getuser', $routeParams.username).
                success(function(data) {
                    $scope.userName = data.userName;
                    $scope.email = data.email;
                });
        }

        $scope.cssPassword = function() {
            var msg = 'control-group';

            if ($scope.hasValue('newPassword') && $scope.newPassword !== $scope.confirmPassword) {
                msg = msg.concat(' server.error');
            }

            return msg;
        };

        $scope.cssClass = function (prop) {
            var msg = 'control-group';

            if (!$scope.hasValue(prop)) {
                 msg = msg.concat(' server.error');
            }

            return msg;
        };

        $scope.hasValue = function(prop) {
            return $scope.hasOwnProperty(prop) && $scope[prop] !== '' && $scope[prop] !== undefined;
        };

        $scope.changeEmail = function () {
            $http.post('../websecurity/api/users/' + $scope.userName + '/change/email', $scope.email).
                success(alertHandler('security.update.email.saved', 'security.update')).
                error(angularHandler('server.error', 'security.update.email.error'));
        };

        $scope.changePassword = function () {
            $http.post('../websecurity/api/users/' + $scope.userName + '/change/password', [$scope.oldPassword, $scope.newPassword]).
                success(function () {
                    motechAlert('security.update.userPass.saved', 'security.update');
                    delete $scope.user.oldPassword;
                    delete $scope.user.newPassword;
                    delete $scope.confirmPassword;
                }).error(alertHandler('security.update.userPass.error', 'server.error'));
        };
    });

    webSecurityModule.controller('DynamicCtrl', function ($scope, Users, Permissions, Dynamic) {
        $scope.users = Users.query();
        $scope.permissions = Permissions.query();
        $scope.config = Dynamic.get();

        $scope.createNewRule = function () {
            var selector;

            if (!$scope.config.securityRules) {
                $scope.config.securityRules = [];
            }

            $scope.config.securityRules.push({
                pattern: 'ANY',
                protocol: 'HTTP',
                priority: 0,
                origin: 'SYSTEM_PLATFORM',
                supportedSchemes: ['NO_SECURITY'],
                permissionAccess: [],
                userAccess: [],
                methodsRequired: ['ANY'],
                version: $scope.msg('server.version'),
                active: false
            });

            selector = '#rule-{0}'.format($scope.config.securityRules.length - 1);

            angular.element(selector).livequery(function () {
                var elem = angular.element(selector + ' .panel-title');

                elem.click();
                elem.expire();

                angular.element('body').animate({
                    scrollTop: $(selector).offset().top
                }, 2000);
            });
        };

        $scope.toggleMethods = function (rule, item, param) {
            if (_.isBoolean(param) && param) {
                rule.methodsRequired = [item];
            } else {
                if (param) {
                   rule.methodsRequired.removeObject(param);
                }

                if (rule.methodsRequired.indexOf(item) === -1) {
                    rule.methodsRequired.push(item);
                } else {
                    rule.methodsRequired.removeObject(item);
                }
            }
        };

        $scope.toggleSchemas = function (rule, item, param) {
            if (_.isBoolean(param) && param) {
                rule.supportedSchemes = [item];
            } else {
                if (param) {
                   rule.supportedSchemes.removeObject(param);
                }

                if (rule.supportedSchemes.indexOf(item) === -1) {
                    rule.supportedSchemes.push(item);
                } else {
                    rule.supportedSchemes.removeObject(item);
                }
            }
        };

        $scope.toggleActive = function (rule) {
            rule.active = !rule.active;
        };

        $scope.validateRules = function () {
            var exp = true;

            angular.forEach($scope.config.securityRules, function (rule) {
                exp = exp && $scope.validateRule(rule);
            });

            return exp;
        };

        $scope.validateRule = function (rule) {
            return rule.pattern && rule.priority >= 0;
        };

        $scope.removeRule = function (idx) {
            motechConfirm('security.warning.removeRule', 'securtiy.warning', function (val) {
                if (val) {
                    $scope.safeApply(function () {
                        $scope.config.securityRules.remove(idx);
                    });
                }
            });
        };

        $scope.save = function () {
            $scope.config.$save(
                angularHandler('security.success', 'security.success.save'),
                angularHandler('security.error', 'security.error.save')
            );
        };

        $scope.cancel = function () {
            motechConfirm('security.warning.cancel', 'securtiy.warning', function (val) {
                if (val) {
                    $scope.config = Dynamic.get();
                }
            });
        };

    });
}());
