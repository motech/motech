<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>MOTECH - Mobile Technology for Community Health</title>

    ${mainHeader}

    <script type="text/javascript">
        $(window).load(function() {
            initAngular();
        });
    </script>

</head>
<body ng-controller="MasterCtrl" class="body-startup">
<div class="bodywrap">
    <div class="navbar-collapse hidden-xs">
        <div class="margin-before5"></div>
    </div>
    <div class="clearfix"></div>
    <div class="startup" ng-show="ready">
        <a href="."><div class="startup-logo"><img src="../server/resources/img/motech-logo.jpg" alt="motech-logo" /></div></a>
        <div class="startup-title ng-binding">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="form-group">
                <h2 class="title ng-binding">{{msg('server.welcome.startup')}}</h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form action="bootstrap.do" method="POST" class="form-horizontal bootstrap-config-form" name="bcform">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">{{msg('server.bootstrap.dbUrl')}}</label>
                        <div class="col-sm-6 form-inline">
                            <input type="text" class="form-control" required name="dbUrl" ng-init="config.dbUrl = '${bootstrapConfig.dbUrl}'" ng-model="config.dbUrl"/>
                        </div>
                        <div class="col-sm-3">
                            <span ng-hide="config.dbUrl" class="form-hint ng-binding">{{msg('server.bootstrap.form.required')}}</span>
                        </div>
                        <div id="suggestion" class="suggestion col-sm-9">
                            <div id="dbUrlSuggestion">
                                <span><i>{{msg('server.suggestion')}}: </i> ${dbUrlSuggestion}</span>
                                <button type="button" class="btn btn-default btn-xs" ng-click="setSuggestedValue(config, 'dbUrl', '${dbUrlSuggestion}')">{{msg('server.use')}}</button>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">{{msg('server.bootstrap.dbUsername')}}</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" name="dbUsername" ng-init="config.dbUserName = '${bootstrapConfig.dbUsername}'" ng-model="config.dbUserName"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label">{{msg('server.bootstrap.dbPassword')}}</label>
                        <div class="col-sm-6">
                            <input type="password" class="form-control" name="dbPassword" ng-init="config.dbPassword = '${bootstrapConfig.dbPassword}'" ng-model="config.dbPassword"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label">{{msg('server.bootstrap.tenantId')}}</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" name="tenantId" ng-init="config.tenantId = '${bootstrapConfig.tenantId}'" ng-model="config.tenantId"/>
                        </div>
                        <div id="suggestion" class="suggestion col-sm-9">
                            <div id="tenantIdUsernameSuggestion">
                                <span><i>{{msg('server.suggestion')}}#1: </i> ${tenantIdDefault}</span>
                                <button type="button" class="btn btn-default btn-xs" ng-click="setSuggestedValue(config, 'tenantId', '${tenantIdDefault}')">{{msg('server.use')}}</button>
                            </div>
                        </div>
                        <div id="suggestion" class="suggestion col-sm-9">
                            <div id="tenantIdDefaultSuggestion">
                                <span><i>{{msg('server.suggestion')}}#2: </i> ${username}</span>
                                <button type="button" class="btn btn-default btn-xs" ng-click="setSuggestedValue(config, 'tenantId', '${username}')">{{msg('server.use')}}</button>
                            </div>
                        </div>
                    </div>

                    <div class="form-inline form-group">
                        <label class="col-sm-3 control-label">{{msg('server.bootstrap.configSource')}}</label>
                        <div class="col-sm-9">
                            <label class="radio-inline">
                                <input type="radio" value="file" name="configSource" <c:if test="${bootstrapConfig.configSource == 'file'}">checked=checked</c:if> />
                                {{msg('server.configSource.file')}}
                            </label>
                            <label class="radio-inline">
                                <input type="radio" value="ui" name="configSource" <c:if test="${bootstrapConfig.configSource != 'file'}">checked=checked</c:if> />
                                {{msg('server.configSource.ui')}}
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9">
                            <input class="btn btn-primary" type="submit" name="BOOTSTRAP" ng-disabled="!config.dbUrl" value="{{msg('server.bootstrap.submit')}}"/>
                            <input class="btn btn-primary" type="button" name="VERIFY" ng-disabled="!config.dbUrl" value="{{msg('server.bootstrap.verify')}}" ng-click="verifyDbConnection()"/>
                        </div>
                    </div>
                    <div class="alerts-container">
                        <c:if test="${not empty errors}">
                            <div class="alert alert-danger">
                                <c:forEach var="error" items="${errors}">
                                    {{msg('${error}')}}   <br/>
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
