<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>MOTECH - Mobile Technology for Community Health</title>

    <link href="static/css/angular-ui.css" type="text/css" rel="stylesheet"/>
    <link href="static/css/jquery-ui.min.css" type="text/css" rel="stylesheet"/>
    <link href="static/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="static/css/bootstrap-theme.min.css" type="text/css" rel="stylesheet"/>
    <link href="static/css/bootstrap-page.css" type="text/css" rel="stylesheet"/>

    <script type="text/javascript" src="static/js/jquery.js"></script>
    <script type="text/javascript" src="static/js/angular.min.js"></script>
    <script type="text/javascript" src="static/js/bootstrap-page.js"></script>
    <script type="text/javascript">
        var bootstrapApp = angular.module('bootstrapApp',[]);
    </script>
    <c:if test="${redirect}">
        <script type="text/javascript">
            $(document).ready(function() {
                setInterval(function(){attemptRedirect()}, TIMEOUT);
            });
        </script>
    </c:if>
</head>
<body class="body-startup" ng-app="bootstrapApp">
<div class="bodywrap">
    <div class="navbar-collapse hidden-xs">
        <div class="margin-before5"></div>
    </div>
    <div class="clearfix"></div>
    <div class="startup">
        <a href="."><div class="startup-logo"><img src="static/img/motech-logo.jpg" alt="motech-logo" /></div></a>
        <div class="startup-title">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="form-group">
                <c:choose>
                    <c:when test="${redirect}">
                        <h2 class="title"><spring:message code="server.bootstrap.loading"/></h2>
                    </c:when>
                    <c:otherwise>
                        <h2 class="title"><spring:message code="server.welcome.startup"/></h2>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="clearfix"></div>
        <c:choose>
            <c:when test="${redirect}">
                <div class="text-center margin-before loadingbar">
                    <img src="static/img/loadingbar.gif" alt="loading"/>
                </div>
            </c:when>
            <c:otherwise>
                <div class="startup-form">
                    <div class="diver">
                        <form action="#" method="POST" class="form-horizontal bootstrap-config-form col-sm-12" name="bcform">
                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.couchDbUrl"/></label>
                                <div class="col-sm-6 form-inline" ng-class="{ 'has-error' : bcform.couchDbUrl.$invalid }">
                                    <input type="text" class="form-control" id="couchDbUrl" ng-required="true" name="couchDbUrl" ng-model="config.couchDbUrl"/>
                                </div>
                                <div class="col-sm-3">
                                    <span ng-show="bcform.couchDbUrl.$error.required && !bcform.couchDbUrl.$pristine"" class="form-hint"><spring:message code="server.bootstrap.form.required"/></span>
                                </div>
                                <div class="suggestion col-sm-9">
                                    <div id="couchDbUrlSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>: </i> ${couchDbUrlSuggestion}</span>
                                        <button type="button" class="btn btn-default btn-xs" ng-click="config.couchDbUrl='${couchDbUrlSuggestion}'"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.couchDbUsername"/></label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="couchDbUsername" ng-model="config.dbUserName"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.couchDbPassword"/></label>
                                <div class="col-sm-6">
                                    <input type="password" class="form-control" name="couchDbPassword" ng-model="config.couchDbPassword"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.sqlUrl"/></label>
                                <div class="col-sm-6 form-inline" ng-class="{ 'has-error' : bcform.sqlUrl.$invalid }">
                                    <input type="text" class="form-control" id="sqlUrl" ng-required="true" name="sqlUrl" ng-model="config.sqlUrl"/>
                                </div>
                                <div class="col-sm-3">
                                    <span ng-show="bcform.sqlUrl.$error.required && !bcform.sqlUrl.$pristine" class="form-hint"><spring:message code="server.bootstrap.form.required"/></span>
                                </div>
                                <div class="suggestion col-sm-9">
                                    <div id="sqlUrlSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>: </i> ${sqlUrlSuggestion}</span>
                                        <button type="button" class="btn btn-default btn-xs" ng-click="config.sqlUrl='${sqlUrlSuggestion}'"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.sqlUsername"/></label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="sqlUsername" ng-model="config.sqlUserName"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.sqlPassword"/></label>
                                <div class="col-sm-6">
                                    <input type="password" class="form-control" name="sqlPassword" ng-model="config.sqlPassword"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.tenantId"/></label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="tenantId" name="tenantId" ng-model="config.tenantId"/>
                                </div>
                                <div class="suggestion col-sm-9">
                                    <div id="tenantIdUsernameSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>#1: </i> ${tenantIdDefault}</span>
                                        <button type="button" class="btn btn-default btn-xs" ng-click="config.tenantId='${tenantIdDefault}'"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                                <div class="suggestion col-sm-9">
                                    <div id="tenantIdDefaultSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>#2: </i> ${username}</span>
                                        <button type="button" class="btn btn-default btn-xs" ng-click="config.tenantId='${username}'"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                            </div>

                            <div class="form-inline form-group primary-bg">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.configSource"/></label>
                                <div class="col-sm-9">
                                    <label class="radio-inline">
                                        <input type="radio" value="file" name="configSource" <c:if test="${bootstrapConfig.configSource == 'file'}">checked=checked</c:if> />
                                        <spring:message code="server.configSource.file"/>
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" value="ui" name="configSource" <c:if test="${bootstrapConfig.configSource != 'file'}">checked=checked</c:if> />
                                        <spring:message code="server.configSource.ui"/>
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label"><img id="loader" alt="loading" src="static/img/load.gif" style="display:none"/></label>
                                <div class="col-sm-9">
                                    <input class="btn btn-primary" type="button" name="VERIFY" ng-disabled="bcform.couchDbUrl.$error.required" value="<spring:message code="server.bootstrap.verify"/>" onclick="verifyDbConnection()"/>
                                    <input class="btn btn-success" type="submit" name="BOOTSTRAP" ng-disabled="bcform.sqlUrl.$error.required || bcform.couchDbUrl.$error.required" value="<spring:message code="server.bootstrap.submit"/>"/>
                                </div>
                            </div>
                            <div class="alerts-container">
                                <c:if test="${not empty errors}">
                                    <div class="alert alert-danger">
                                        <c:forEach var="error" items="${errors}">
                                            <spring:message code="${error}"/>   <br/>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <div class="alert alert-success" id="verify-info" style="display:none">
                                    <spring:message code="server.bootstrap.verify.success"/>
                                </div>
                                <div class="alert alert-danger" id="verify-alert" style="display:none">
                                    <spring:message code="server.bootstrap.verify.error"/>
                                </div>
                                <div class="alert alert-danger" id="verify-error" style="display:none"></div>
                            </div>
                        </form>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
