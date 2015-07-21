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
    <link rel="manifest" href="./../../manifest.json">
    <meta name="msapplication-TileColor" content="#da532c">
    <meta name="msapplication-TileImage" content="<%=request.getContextPath()%>/../static/img/mstile-144x144.png">
    <meta name="theme-color" content="#ffffff">

    ${mainHeader}
</head>

<body class="body-down" >
<div class="bodywrap" ng-controller="MotechMasterCtrl" ng-init="getForgotViewData()">
    <div class="header">
        <div class="container">
            <a href=".">
                <div class="dashboard-logo hidden-xs" ng-cloak>
                    <img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="./../../static/img/motech-logo.gif">
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
