<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>
    <script src="../server/resources/js/startup.js" type="text/javascript"></script>

    <script type="text/javascript">
        $(window).load(function() {
            initAngular();
        });
    </script>

</head>
<body ng-controller="MasterCtrl" class="body-startup">
<div class="bodywrap">
    <div class="startup" ng-show="ready">
        <a href="."><div class="startup-logo"><img src="../server/resources/img/motech-logo.jpg" alt="motech-logo" /></div></a>
        <div class="startup-title ng-binding">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="control-group">
            <h2 class="title ng-binding"><fmt:message key="welcome.startup" bundle="${bundle}"/></h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form action="startup.do" method="POST" class="form-horizontal">
                    <div class="control-group">
                        <label class="control-label"><fmt:message key="select.language" bundle="${bundle}"/></label>
                        <div class="controls">
                            <c:forEach var="lang" items="${languages}">
                                <input ng-click="setUserLang('${lang}', true)" type="radio" value="${lang}" name="language" <c:if test="${startupSettings.language == lang}">checked</c:if> /><i class="flag flag-${lang} label-flag-radio"></i>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><fmt:message key="enter.queueUrl" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="text" class="input-large" name="queueUrl" value="${startupSettings.queueUrl}"/>
                            <c:if test="${ not empty suggestions.queueUrls }">
                                <div id="queue.urls" class="queue-urls">
                                <c:forEach var="url" items="${suggestions.queueUrls}" varStatus="status">
                                    <div id="queue.url.${status.count}">
                                        <span><fmt:message key="suggestion" bundle="${bundle}"/> #${status.count}: ${url}</span>
                                        <button type="button" class="btn btn-mini"><fmt:message key="use" bundle="${bundle}"/></button>
                                    </div>
                                </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label"><fmt:message key="enter.schedulerUrl" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="text" class="input-large" name="schedulerUrl" value="${startupSettings.schedulerUrl}"/>
                            <c:if test="${ not empty suggestions.schedulerUrls }">
                                <div id="scheduler.urls">
                                <c:forEach var="url" items="${suggestions.schedulerUrls}" varStatus="status">
                                    <div id="scheduler.url.${status.count}">
                                        <span><i><fmt:message key="suggestion" bundle="${bundle}"/> #${status.count}: </i>${url}</span>
                                        <button type="button" class="btn btn-mini"><fmt:message key="use" bundle="${bundle}"/></button>
                                    </div>
                                </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="control-group">
                         <label class="control-label"><fmt:message key="select.loginMode" bundle="${bundle}"/></label>
                         <div class="controls">
                             <input type="radio" value="repository" name="loginMode" ng-click="securityMode = 'repository'" ng-checked="securityMode == 'repository'"><span>{{msg('repository')}}</span>
                             <input type="radio" value="openid" name="loginMode" ng-click="securityMode = 'openid'" ng-checked="securityMode == 'openid'"/><span>{{msg('openId')}}</span>
                         </div>
                     </div>
                    <div ng-show="securityMode=='repository'" class="control-group">
                        <label class="control-label"><fmt:message key="enter.adminLogin" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="text" class="input-large" name="adminLogin" value="${startupSettings.adminLogin}"/>
                        </div>
                    </div>
                    <div ng-show="securityMode=='repository'" class="control-group">
                        <label class="control-label"><fmt:message key="enter.adminPassword" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="password" class="input-large" name="adminPassword" value="${startupSettings.adminPassword}"/>
                        </div>
                    </div>
                    <div ng-show="securityMode=='repository'" class="control-group">
                        <label class="control-label"><fmt:message key="enter.adminComfirmPassword" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="password" class="input-large" name="adminConfirmPassword" value="${startupSettings.adminConfirmPassword}"/>
                        </div>
                    </div>
                     <div ng-show="securityMode=='repository'" class="control-group">
                        <label class="control-label"><fmt:message key="enter.adminEmail" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="email" class="input-large" name="adminEmail" value="${startupSettings.adminEmail}"/>
                        </div>
                     </div>
                     <div ng-show="securityMode=='openid'" class="control-group">
                         <label class="control-label"><fmt:message key="enter.providerName" bundle="${bundle}"/></label>
                         <div class="controls">
                             <input type="text" class="input-large" name="providerName" value="${startupSettings.providerName}"/>
                         </div>
                     </div>
                     <div ng-show="securityMode=='openid'" class="control-group">
                          <label class="control-label"><fmt:message key="enter.providerUrl" bundle="${bundle}"/></label>
                          <div class="controls">
                              <input type="text" class="input-large" name="providerUrl" value="${startupSettings.providerUrl}"/>
                          </div>
                     </div>
                    <div class="control-group">
                        <div class="controls">
                            <input class="btn btn-primary" type="submit" name="START" value="<fmt:message key="submit" bundle="${bundle}"/>"/>
                        </div>
                    </div>
                    <c:if test="${not empty errors}">
                        <div class="alert alert-error">
                        <c:forEach var="error" items="${errors}">
                            <fmt:message key="${error}" bundle="${bundle}"/><br/>
                        </c:forEach>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
