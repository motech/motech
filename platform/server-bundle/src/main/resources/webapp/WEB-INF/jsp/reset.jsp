<!DOCTYPE html>
<html ng-app="motech-dashboard">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>MOTECH - Mobile Technology for Community Health</title>

        ${mainHeader}

    </head>

    <body ng-show="ready" ng-controller="MasterCtrl" class="body-down" ng-init="getResetViewData()">
        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href="."><div class="dashboard-logo"><img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="../server/resources/img/motech-logo.jpg"></div></a>
                    <div class="hidden-xs hidden-sm">
                        <div class="header-title">{{msg('server.motechTitle')}}</div>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>

            <div class="clearfix"></div>
            <div class="navbar-wrapper navbar-default">
                <div class="header-nav navbar">
                    <div class="navbar-inner navbar-inner-bg">
                    </div>
                </div>
            </div>
            <div class="clearfix"></div>
            <div id="content" class="container">
                <div class="row">
                    <div id="main-content">
                        <div class="well2 margin-center margin-before spnw5">
                            <div class="reset-content">
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
                                        <form method="post" class="reset-password-form" ng-submit="submitResetPasswordForm()">
                                            <input type="hidden" id="token" name="token" value="{{resetViewData.resetForm.token}}" />

                                            <div class="form-group">
                                                <h4>{{msg('server.reset.enterNewPassword')}}</h4>
                                            </div>
                                            <div class="form-group">
                                                <label>{{msg('server.reset.password')}}</label>
                                                <input class="col-md-12 form-control" type="password"
                                                    id="password" name="password" ng-model="resetViewData.resetForm.password" />
                                            </div>
                                            <div class="form-group">
                                                <label>{{msg('server.reset.confirmPassword')}}</label>
                                                <input class="col-md-12 form-control" type="password"
                                                    id="passwordConfirmation" name="passwordConfirmation" ng-model="resetViewData.resetForm.passwordConfirmation" />
                                            </div>
                                            <div class="form-group">
                                                <input class="btn btn-primary" type="submit" value="{{msg('server.reset.changePassword')}}" />
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
