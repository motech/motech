<!DOCTYPE html>
<html ng-app="motech-dashboard">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>MOTECH - Mobile Technology for Community Health</title>

    ${mainHeader}
</head>

<body class="body-down" >
<div class="bodywrap" ng-controller="MasterCtrl" ng-init="getForgotViewData()">
    <div class="header">
        <div class="container">
            <a href=".">
                <div class="dashboard-logo hidden-xs" ng-show="showDashboardLogo.showDashboard"><img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="../server/resources/img/motech-logo.jpg"></div>
            </a>

            <div class="navbar-collapse hidden-xs">
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
            <div id="main-content" ng-if="forgotViewData.emailGetter">
                <div class="well2 margin-center margin-before spnw6" ng-if="forgotViewData.loginMode.repository">
                    <div class="box-header">{{msg('security.resetInstructions')}}</div>
                    <div class="box-content">
                        <form class="inside" method="post" ng-submit="sendReset()">
                            <div class="well3">
                                <div class="form-group">
                                    <h4>{{msg('security.enterEmailQuestions')}}</h4>
                                </div>
                                <div class="form-group">
                                    <p>{{msg('security.enterEmailMsg')}}</p>
                                </div>
                                <div class="form-group">
                                    <label>{{msg('security.enterEmail')}}</label>
                                    <input class="col-sm-12 form-control" ng-model="forgotViewData.email" name="email" type="email">
                                </div>
                                <div class="form-group">
                                    <input class="btn btn-primary" type="submit" value="{{msg('security.sendReset')}}"/>
                                    <a href="." class="pull-right margin-before05" />{{msg('security.back')}}</a>
                                </div>
                            </div>
                        </form>
                        <div class="clearfix"></div>
                    </div>
                </div>
                <div class="well2 margin-center spn5"  ng-if="forgotViewData.loginMode.openId">
                    <div class="box-header">{{msg('security.oneTimeToken')}}</div>
                    <div class="box-content">
                         <form class="inside" method="post" ng-submit="sendReset()">
                             <div class="well3">
                                 <div class="form-group">
                                     <p>{{msg('security.enterEmailMsgToken')}}</p>
                                 </div>
                                 <div class="form-group">
                                     <label>{{msg('security.enterEmail')}}</label>
                                     <input class="col-sm-12 form-control" ng-model="forgotViewData.email" type="email" id="email" name="email">
                                 </div>
                                 <div class="form-group">
                                     <input class="btn btn-primary" type="submit" value="{{msg('security.sendOneTimeToken')}}"/>
                                 </div>
                             </div>
                         </form>
                         <div class="clearfix"></div>
                    </div>
                </div>
            </div>
            <div id="main-content" ng-if="forgotViewData.processed">
                <div class="well2 margin-center margin-before spnw5">
                    <div class="box-header">{{msg('security.forgotPassword')}}</div>
                    <div class="box-content well3">
                        <div class="form-group" ng-if="!error">
                            <p>{{msg('security.tokenSent')}}</p>
                        </div>
                        <div class="login-error" ng-if="error">
                            <h4>{{msg('security.tokenSendError')}}</h4>
                        </div>
                        <div class="form-group login-error" ng-if="error">
                            <label>{{msg(error)}}</label>
                        </div>
                        <div class="form-group login-error" ng-if="error">
                            <a href="."><input type="button" class="btn btn-primary" value="{{msg('security.back')}}" /></a>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
