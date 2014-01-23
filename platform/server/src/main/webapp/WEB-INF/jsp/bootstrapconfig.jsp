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
    <link href="static/css/bootstrap-page.css" type="text/css" rel="stylesheet"/>

    <script type="text/javascript" src="static/js/jquery.js"></script>
    <script type="text/javascript" src="static/js/bootstrap-page.js"></script>

    <c:if test="${redirect}">
        <script type="text/javascript">
            $(document).ready(function() {
                setInterval(function(){attemptRedirect()}, TIMEOUT);
            });
        </script>
    </c:if>
</head>
<body class="body-startup">
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
                        <form action="" method="POST" class="form-horizontal bootstrap-config-form" name="bcform">
                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.dbUrl"/></label>
                                <div class="col-sm-6 form-inline">
                                    <input type="text" class="form-control" id="dbUrl" required name="dbUrl" ng-init="config.dbUrl = '${bootstrapConfig.dbUrl}'" ng-model="config.dbUrl"/>
                                </div>
                                <div class="col-sm-3">
                                    <span ng-hide="config.dbUrl" class="form-hint"><spring:message code="server.bootstrap.form.required"/></span>
                                </div>
                                <div id="suggestion" class="suggestion col-sm-9">
                                    <div id="dbUrlSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>: </i> ${dbUrlSuggestion}</span>
                                        <button type="button" class="btn btn-default btn-xs" onclick="setSuggestedValue('dbUrl', '${dbUrlSuggestion}')"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.dbUsername"/></label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="dbUsername" ng-init="config.dbUserName = '${bootstrapConfig.dbUsername}'" ng-model="config.dbUserName"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.dbPassword"/></label>
                                <div class="col-sm-6">
                                    <input type="password" class="form-control" name="dbPassword" ng-init="config.dbPassword = '${bootstrapConfig.dbPassword}'" ng-model="config.dbPassword"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label"><spring:message code="server.bootstrap.tenantId"/></label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="tenantId" name="tenantId" ng-init="config.tenantId = '${bootstrapConfig.tenantId}'" ng-model="config.tenantId"/>
                                </div>
                                <div id="suggestion" class="suggestion col-sm-9">
                                    <div id="tenantIdUsernameSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>#1: </i> ${tenantIdDefault}</span>
                                        <button type="button" class="btn btn-default btn-xs" onclick="setSuggestedValue('tenantId', '${tenantIdDefault}')"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                                <div id="suggestion" class="suggestion col-sm-9">
                                    <div id="tenantIdDefaultSuggestion">
                                        <span><i><spring:message code="server.suggestion"/>#2: </i> ${username}</span>
                                        <button type="button" class="btn btn-default btn-xs" onclick="setSuggestedValue('tenantId', '${username}')"><spring:message code="server.use"/></button>
                                    </div>
                                </div>
                            </div>

                            <div class="form-inline form-group">
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
                                <div class="col-sm-offset-3 col-sm-9">
                                    <input class="btn btn-primary" type="submit" name="BOOTSTRAP" ng-disabled="!config.dbUrl" value="<spring:message code="server.bootstrap.submit"/>"/>
                                    <input class="btn btn-primary" type="button" name="VERIFY" ng-disabled="!config.dbUrl" value="<spring:message code="server.bootstrap.verify"/>" onclick="verifyDbConnection()"/>
                                    <img id="loader" alt="loading" src="static/img/load.gif" style="display:none"/>
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
