<!DOCTYPE html>
<html ng-app="motech-dashboard">
    <head>
        <meta charset="UTF-8">
        <title>MOTECH - Mobile Technology for Community Health</title>

        <link rel="apple-touch-icon" sizes="57x57" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-57x57.png">
        <link rel="apple-touch-icon" sizes="60x60" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-60x60.png">
        <link rel="apple-touch-icon" sizes="72x72" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-72x72.png">
        <link rel="apple-touch-icon" sizes="76x76" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-76x76.png">
        <link rel="apple-touch-icon" sizes="114x114" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-114x114.png">
        <link rel="apple-touch-icon" sizes="120x120" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-120x120.png">
        <link rel="apple-touch-icon" sizes="144x144" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-144x144.png">
        <link rel="apple-touch-icon" sizes="152x152" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-152x152.png">
        <link rel="apple-touch-icon" sizes="180x180" href="<%=request.getContextPath()%>/../static/img/apple-touch-icon-180x180.png">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/../static/img/favicon-32x32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/../static/img/android-chrome-192x192.png" sizes="192x192">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/../static/img/favicon-96x96.png" sizes="96x96">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/../static/img/favicon-16x16.png" sizes="16x16">
        <link rel="manifest" href="<%=request.getContextPath()%>/../manifest.json">
        <meta name="msapplication-TileColor" content="#da532c">
        <meta name="msapplication-TileImage" content="<%=request.getContextPath()%>/../static/img/mstile-144x144.png">
        <meta name="theme-color" content="#ffffff">

        ${mainHeader}

    </head>

    <body ng-show="ready" ng-controller="MotechMasterCtrl" class="body-down" ng-init="getResetViewData()">
        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href=".">
                        <div class="dashboard-logo" ng-cloak>
                            <img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="./../../static/img/motech-logo.gif">
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
