<!DOCTYPE html>
<html ng-app="motech-dashboard">
    <head>
        <meta charset="UTF-8">
        <title>MOTECH - Mobile Technology for Community Health</title>

        <%@ include file="faviconPage.jsp" %>

        <link rel="stylesheet" type="text/css" href="../../static/common/css/motech.css">

        <script type="text/javascript" src="../../static/common/js/motech.min.js"></script>
        <script type="text/javascript" src="resources/js/app.js"></script>
        <script type="text/javascript" src="resources/js/services.js"></script>
        <script type="text/javascript" src="resources/js/controllers.js"></script>

    </head>

    <body ng-show="ready" ng-controller="MotechMasterCtrl" class="body-down" ng-init="getResetViewData()">
        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href=".">
                        <div class="dashboard-logo" ng-cloak>
                            <img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="./../../static/common/img/motech-logo.gif">
                        </div>
                    </a>
                    <div class="hidden-xs" ng-cloak>
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
                        <div class="well2 margin-center margin-before spnw5">
                            <div class="reset-content" ng-cloak>
                                <div class="box-header">
                                     {{msg('server.reset.resetYourPassword')}}
                                </div>
                                <div class="box-content">
                                    <div class="well3" ng-if="resetViewData.invalidToken == false && resetViewData.resetSucceed == false">
                                        <div ng-if="resetViewData.errors != null">
                                            <div class="login-error" ng-repeat="error in resetViewData.errors">
                                                {{msg(error)}}
                                            </div>
                                        </div>
                                        <form method="post" class="reset-password-form" ng-submit="submitResetPasswordForm()" name="resetPasswordForm">
                                            <input type="hidden" id="token" name="token" value="{{resetViewData.resetForm.token}}" />

                                            <div class="form-group">
                                                <h4>{{msg('server.reset.enterNewPassword')}}</h4>
                                            </div>
                                            <div class="form-group">
                                                <label>{{msg('server.reset.password')}}</label>
                                                <input class="col-md-12 form-control" type="password" required validate-password
                                                    id="password" name="password" ng-model="resetViewData.resetForm.password" />
                                                <span ng-show="resetPasswordForm.password.$error.valid === true" class="form-hint form-hint-bottom">
                                                    {{validatorMessage}}
                                                </span>
                                            </div>
                                            <div class="form-group">
                                                <label>{{msg('server.reset.confirmPassword')}}</label>
                                                <input class="col-md-12 form-control" type="password" confirm-password="resetViewData.resetForm.password" required visited-confirm-input
                                                    id="passwordConfirmation" name="passwordConfirmation" ng-model="resetViewData.resetForm.passwordConfirmation" />
                                                <span ng-show="isConfirmPasswordDirty && resetPasswordForm.passwordConfirmation.$dirty && resetPasswordForm.passwordConfirmation.$error.equal === true && resetPasswordForm.password.$error.valid === false"
                                                    class="form-hint form-hint-bottom">
                                                    {{msg('server.error.invalid.password')}}
                                                </span>
                                            </div>
                                            <div class="form-group margin-before ">
                                                <input ng-disabled="resetPasswordForm.$invalid" class="btn btn-primary margin-before" type="submit" value="{{msg('server.reset.changePassword')}}" />
                                            </div>
                                        </form>
                                    </div>
                                    <div class="well3" ng-if="resetViewData.resetSucceed == true">
                                        <div ng-if="resetViewData.errors != null">
                                            <div class="login-error" ng-repeat="error in resetViewData.errors">
                                                {{msg('error')}}
                                            </div>
                                        </div>
                                        <div ng-if"resetViewData.errors == null">
                                            <p>{{msg('server.reset.resetSuccess')}}</p>
                                            <p><a href="./login">{{msg('server.login')}}</a></p>
                                        </div>
                                    </div>
                                    <div class="well3" ng-if="resetViewData.invalidToken == true">
                                        {{msg('server.reset.invalidToken')}}
                                    </div>
                                    <div class="clearfix"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
