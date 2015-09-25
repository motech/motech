<!DOCTYPE html>
<html ng-app="motech-dashboard">
    <head>
        <meta charset="UTF-8">
        <title>MOTECH - Mobile Technology for Community Health</title>

        ${mainHeader}

    </head>

    <body ng-show="ready" ng-controller="MotechMasterCtrl" class="body-down" ng-init="initChangePasswordViewData()">
        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <div class="dashboard-logo">
                        <img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="../server/resources/img/motech-logo.gif">
                    </div>
                    <div class="hidden-xs hidden-sm">
                        <div class="header-title">{{msg('server.motechTitle')}}</div>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>

            <div class="clearfix"></div>
            <div class="navbar-wrapper navbar-default">
                <div class="header-nav navbar">
                    <div class="navbar-inner"></div>
                </div>
            </div>
            <div class="clearfix"></div>
            <div id="content" class="container">
                <div class="row">
                    <div id="main-content">
                        <div class="well2 margin-center margin-before spn6">
                            <div class="reset-content">
                                <div ng-if="!changePasswordViewData.changingSucceed" class="box-header">
                                     {{msg('server.reset.changeYourPassword')}}
                                </div>
                                <div ng-if="changePasswordViewData.changingSucceed" class="box-header">
                                     {{msg('server.information')}}
                                </div>
                                <div class="box-content">
                                    <div class="well3">
                                        <h5 ng-if="!changePasswordViewData.changingSucceed">{{msg('server.reset.passwordExpired')}}</h5>
                                        <form ng-if="!changePasswordViewData.changingSucceed" method="post" name="changePasswordForm"
                                            class="inside form-horizontal" ng-submit="submitChangePasswordForm()">
                                            <div class="form-group">
                                                <label class="control-label col-md-5">{{msg('server.reset.currentPassword')}}</label>
                                                <div class="form-inline col-md-6">
                                                    <input class="form-control input-auto" type="password" required
                                                        id="oldPassword" name="oldPassword" ng-model="changePasswordViewData.changePasswordForm.oldPassword" />
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="control-label col-md-5">{{msg('server.reset.newPassword')}}</label>
                                                <div class="form-inline col-md-6">
                                                    <input class="form-control input-auto" type="password" required validate-password old-password="changePasswordViewData.changePasswordForm.oldPassword"
                                                        id="password" name="newPassword" ng-model="changePasswordViewData.changePasswordForm.password" />
                                                    <span ng-show="changePasswordForm.newPassword.$error.notEqual" class="form-hint form-hint-bottom">
                                                        {{msg('server.reset.passwordCannotBeEqual')}}
                                                    </span>
                                                    <span ng-show="changePasswordForm.newPassword.$error.valid === true && changePasswordForm.newPassword.$error.notEqual === false" class="form-hint form-hint-bottom">
                                                        {{validatorMessage}}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="control-label col-md-5">{{msg('server.reset.confirmPassword')}}</label>
                                                <div class="form-inline col-md-6">
                                                    <input class="form-control input-auto" type="password" required id="passwordConfirmation" name="confirmPassword"
                                                        confirm-password="changePasswordViewData.changePasswordForm.password" ng-model="changePasswordViewData.changePasswordForm.passwordConfirmation" />
                                                    <span ng-show="changePasswordForm.confirmPassword.$error.equal === true && changePasswordForm.newPassword.$error.valid === false && changePasswordForm.newPassword.$error.notEqual === false"
                                                        class="form-hint form-hint-bottom">
                                                        {{msg('server.error.invalid.password')}}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="col-md-offset-5 col-md-4">
                                                    <input class="btn btn-primary" type="submit" value="{{msg('server.reset.changePassword')}}" ng-disabled="changePasswordForm.$invalid"/>
                                                </div>
                                                <div class="col-md-offset-5 col-md-4 margin-before">
                                                    <a class="btn btn-default" href="<%=request.getContextPath()%>/server/j_spring_security_logout">
                                                        <span class="fa fa-power-off"></span>&nbsp;{{msg("server.signOut")}}
                                                    </a>
                                                </div>
                                            </div>
                                        </form>
                                        <div ng-if="changePasswordViewData.errors != null">
                                            <div class="alert alert-danger" ng-repeat="error in changePasswordViewData.errors">
                                                {{msg(error)}}
                                            </div>
                                        </div>
                                    </div>
                                    <div ng-if="changePasswordViewData.changingSucceed" class="well3">
                                        <div>
                                            <h4>{{msg('server.reset.passwordChanged')}}</h4>
                                            <div class="form-group">
                                                <a class="btn btn-primary" href="./home">{{msg('server.continue')}}</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
