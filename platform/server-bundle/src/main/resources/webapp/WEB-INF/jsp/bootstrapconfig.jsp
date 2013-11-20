<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>
    <script type="text/javascript">
        $(window).load(function() {
            initAngular();
        });
    </script>

</head>
<body ng-controller="MasterCtrl" class="body-startup">
<div class="bodywrap">
    <div class="nav-collapse">
        <div class="margin-before5"></div>
    </div>
    <div class="clearfix"></div>
    <div class="startup" ng-show="ready">
        <a href="."><div class="startup-logo"><img src="../server/resources/img/motech-logo.jpg" alt="motech-logo" /></div></a>
        <div class="startup-title ng-binding">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="control-group">
            <h2 class="title ng-binding">{{msg('server.welcome.startup')}}</h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form action="bootstrap.do" method="POST" class="form-horizontal bootstrap-config-form" name="bcform">
                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.dbUrl')}}</label>
                        <div class="controls">
                            <div>
                                <input type="text" class="input-xlarge" required name="dbUrl" ng-init="config.dbUrl = '${bootstrapConfig.dbUrl}'" ng-model="config.dbUrl"/>
                                <span ng-hide="config.dbUrl" class="form-hint ng-binding">{{msg('server.bootstrap.form.required')}}</span>
                            </div>
                            <div id="suggestion" class="suggestion">
                                <div id="dbUrlSuggestion">
                                    <span><i>{{msg('server.suggestion')}}: </i> ${dbUrlSuggestion}</span>
                                    <button type="button" class="btn btn-mini" ng-click="setSuggestedValue(config, 'dbUrl', '${dbUrlSuggestion}')">{{msg('server.use')}}</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.dbUsername')}}</label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="dbUsername" ng-init="config.dbUserName = '${bootstrapConfig.dbUsername}'" ng-model="config.dbUserName"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.dbPassword')}}</label>
                        <div class="controls">
                            <input type="password" class="input-xlarge" name="dbPassword" ng-init="config.dbPassword = '${bootstrapConfig.dbPassword}'" ng-model="config.dbPassword"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.tenantId')}}</label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="tenantId" ng-init="config.tenantId = '${bootstrapConfig.tenantId}'" ng-model="config.tenantId"/>
                            <div id="suggestion" class="suggestion">
                                <div id="tenantIdUsernameSuggestion">
                                    <span><i>{{msg('server.suggestion')}}#1: </i> ${tenantIdDefault}</span>
                                    <button type="button" class="btn btn-mini" ng-click="setSuggestedValue(config, 'tenantId', '${tenantIdDefault}')">{{msg('server.use')}}</button>
                                </div>
                                <div id="tenantIdDefaultSuggestion">
                                    <span><i>{{msg('server.suggestion')}}#2: </i> ${username}</span>
                                    <button type="button" class="btn btn-mini" ng-click="setSuggestedValue(config, 'tenantId', '${username}')">{{msg('server.use')}}</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.configSource')}}</label>
                        <div class="controls">
                            <label class="radio inline">
                                <input type="radio" value="file" name="configSource" <c:if test="${bootstrapConfig.configSource == 'file'}">checked=checked</c:if> />
                                {{msg('server.configSource.file')}}
                            </label>
                            <label class="radio inline">
                                <input type="radio" value="ui" name="configSource" <c:if test="${bootstrapConfig.configSource != 'file'}">checked=checked</c:if> />
                                {{msg('server.configSource.ui')}}
                            </label>
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <input class="btn btn-primary" type="submit" name="BOOTSTRAP" ng-disabled="!config.dbUrl" value="{{msg('server.bootstrap.submit')}}"/>
                            <input class="btn btn-primary" type="button" name="VERIFY" ng-disabled="!config.dbUrl" value="{{msg('server.bootstrap.verify')}}" ng-click="verifyDbConnection()"/>
                        </div>
                    </div>
                    <div class="alerts-container">
                        <c:if test="${not empty errors}">
                            <div class="alert alert-error">
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
