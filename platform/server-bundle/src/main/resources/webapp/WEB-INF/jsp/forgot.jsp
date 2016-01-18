<!DOCTYPE html>
<html ng-app="motech-dashboard">
<head>
    <meta charset="UTF-8">
    <title>MOTECH - Mobile Technology for Community Health</title>

    <%@ include file="header.jsp" %>

    <script type="text/javascript" src="resources/js/app.js"></script>
    <script type="text/javascript" src="resources/js/services.js"></script>
    <script type="text/javascript" src="resources/js/controllers.js"></script>
</head>

<body class="body-down" >
<div class="bodywrap" ng-controller="MotechMasterCtrl" ng-init="getForgotViewData()">
    <div class="header">
        <div class="container">
            <a href=".">
                <div class="dashboard-logo hidden-xs" ng-cloak>
                    <img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="./../../static/common/img/motech-logo.gif">
                </div>
            </a>

            <div class="hidden-xs">
                <div class="header-title" ng-cloak>{{msg('server.motechTitle')}}</div>
            </div>
            <div class="clearfix"></div>
        </div>
    </div>

    <div class="clearfix"></div>
    <div class="navbar-wrapper navbar-default">
        <div class="header-nav navbar">
            <div class="navbar-inner">
            </div>
        </div>
    </div>

    <div class="clearfix"></div>
    <div id="content" class="container">
        <div class="row">
            <div id="main-content" ng-if="forgotViewData.emailGetter">
                <div class="well2 margin-center margin-before spnw6" ng-if="forgotViewData.loginMode.repository">
                    <div class="box-header" ng-cloak>{{msg('security.resetInstructions')}}</div>
                    <div class="box-content" ng-cloak>
                        <form class="inside form-horizontal" method="post" ng-submit="sendReset()">
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
                    <div class="box-header" ng-cloak>{{msg('security.oneTimeToken')}}</div>
                    <div class="box-content" ng-cloak>
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
                    <div class="box-header" ng-cloak>{{msg('security.forgotPassword')}}</div>
                    <div class="box-content well3" ng-cloak>
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
