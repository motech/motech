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
<body class="body-down" ng-controller="MotechMasterCtrl" ng-init="getLoginViewData()">
    <div class="splash login" ng-hide="ready">
        <div class="splash-logo">
            <img src="./../../static/common/img/motech-logo.gif" alt="motech-logo">
        </div>
        <div class="clearfix"></div>
        <div class="splash-loader">
            <img src="./../../static/common/img/loadingbar.gif" alt="loading">
        </div>
        <div class="clearfix"></div>
        <div class="splash-msg"></div>
        <div class="clearfix"></div>
    </div>
    <div class="clearfix"></div>
    <div ng-show="ready" class="bodywrap">
        <div class="header">
            <div class="container">
                <a ui-sref="#">
                    <div class="dashboard-logo" ng-cloak>
                        <img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="./../../static/common/img/motech-logo.gif">
                    </div>
                </a>
                <div class="hidden-xs" ng-cloak>
                    <div class="header-title">
                        {{msg('server.motechTitle')}}
                    </div>
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

        <!-- TODO: Repository and OpenID sections are repeated twice here. Would be better to extract it as a separate jsps and have it in only one place.-->

        <div class="clearfix"></div>
        <div id="content" class="container">
            <div class="row">
                <div id="main-content">
                    <div ng-if="loginViewData.error == null && loginViewData.blocked == null" id="login" class="well2 margin-center margin-before spnw55">
                        <div class="box-header" ng-cloak>
                            {{msg('security.signInUser')}}
                        </div>
                        <div class="box-content clearfix" ng-cloak>
                            <div class="well3">
                                <div ng-if="loginViewData.loginMode.repository">
                                    <form action="{{loginContextPath}}" method="POST" class="inside form-horizontal">
                                        <div class="form-group">
                                            <h4>
                                                {{msg('security.signInWithId')}}
                                                {{msg('security.motechId')}}
                                            </h4>
                                        </div>
                                        <div class="form-group margin-before2">
                                            <input element-focus class="col-sm-12 form-control" type="text" name="j_username" placeholder="{{msg('security.userName')}}" />
                                        </div>
                                        <div class="form-group">
                                            <input class="col-sm-12 form-control" type="password" name="j_password" placeholder="{{msg('security.password')}}" />
                                        </div>
                                        <div class="form-group">
                                            <input class="btn btn-primary" value="{{msg('security.signin')}}" type="submit"/>
                                            <span class="pull-right margin-before05">
                                                <a href="../../server/forgot">
                                                    "{{msg('security.signInQuestions')}}"
                                                </a>
                                            </span>
                                        </div>
                                    </form>
                                </div>
                                <div ui-if="loginViewData.loginMode.openId" ng-cloak>
                                    <div class="clearfix"></div>
                                    <form class="inside form-horizontal" action="{{loginContextPathOpenId}}" method="POST">
                                        <div class="form-group open-id">
                                            <p>{{msg('security.signInWith')}} {{loginViewData.openIdProviderName}} {{msg('security.users')}}&nbsp;&nbsp;</p>
                                            <input name="openid_identifier" type="hidden" value="{{loginViewData.openIdProviderUrl}}"/>
                                            <input class="btn btn-primary" type="submit" value="{{msg('security.signInWith')}} {{loginViewData.openIdProviderName}}"/>
                                        </div>
                                        <div class="form-group open-id">
                                            <p>{{msg('server.oneTimeToken')}}&nbsp;
                                                <a href="../../server/forgot">
                                                    {{msg('security.clickHere')}}
                                                </a>
                                            </p>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div ng-if="loginViewData.error != null || loginViewData.blocked != null" class="well2 margin-center margin-before col-sm-12" ng-cloak>
                        <div class="box-header">
                            {{msg('security.signInUnsuccessful')}}
                        </div>
                        <div class="box-content clearfix">
                            <div class="row">
                                <div class="col-md-6 inside">
                                    <div class="well3">
                                            <div class="form-group margin-before">
                                                <h4 ng-if="loginViewData.error != null" class="login-error">
                                                    {{msg('security.wrongPassword')}}
                                                </h4>
                                                <h4 ng-if="loginViewData.blocked != null" class="login-error">
                                                    {{msg('security.userBlocked')}}
                                                </h4>
                                            </div>
                                            <div class="form-group margin-before2">
                                                <h5 ng-if="loginViewData.error != null" class="login-error">
                                                    {{msg('security.didnotRecognizeMsg')}}
                                                </h5>
                                                <h5 ng-if="loginViewData.blocked != null" class="login-error">
                                                    {{msg('security.userBlockedDescription')}}
                                                </h5>
                                            </div>
                                        <div class="form-group margin-before2">
                                            <h5>
                                                {{msg('security.donotRememberMsg1')}}
                                                <a href="../../server/forgot">
                                                    {{msg('security.clickHere')}}
                                                </a>
                                                {{msg('security.donotRememberMsg2')}}
                                            </h5>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="well3">
                                        <div class="left-divider">
                                            <div ui-if="loginViewData.loginMode.repository" ng-cloak>
                                                <form class="inside form-horizontal" action="{{loginContextPath}}" method="POST">
                                                    <div class="form-group">
                                                        <h4>
                                                            {{msg('security.signInWithId')}}&nbsp;
                                                            {{msg('security.motechId')}}
                                                        </h4>
                                                    </div>
                                                    <div class="form-group margin-before2">
                                                        <input element-focus class="col-sm-12 form-control" type="text" name="j_username" placeholder="{{msg('security.userName')}}">
                                                    </div>
                                                    <div class="form-group">
                                                        <input class="col-sm-12 form-control" type="password" name="j_password" placeholder="{{msg('security.password')}}">
                                                    </div>
                                                    <div class="form-group">
                                                        <input class="btn btn-primary" type="submit" value="{{msg('security.signin')}}"/>
                                                    </div>
                                                </form>
                                            </div>
                                            <div ui-if="loginViewData.loginMode.openId" ng-cloak>
                                                <form class="inside form-horizontal" action="{{loginContextPathOpenId}}" method="POST">
                                                    <div class="form-group open-id">
                                                        <p>For ${openIdProviderName} users:&nbsp;&nbsp;</p>
                                                        <input name="openid_identifier" type="hidden" value="{{loginViewData.openIdProviderUrl}}"/>
                                                        <input class="btn btn-primary" type="submit" value="{{msg('security.signInWith')}} {{loginViewData.openIdProviderName}}"/>
                                                    </div>
                                                    <div class="form-group open-id">
                                                        <p>
                                                            {{msg('server.oneTimeToken')}}&nbsp;&nbsp;
                                                            <a href="../../server/forgot">
                                                                {{msg('security.clickHere')}}
                                                            </a>
                                                        </p>
                                                    </div>
                                                </form>
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
    </div>
</body>
</html>
