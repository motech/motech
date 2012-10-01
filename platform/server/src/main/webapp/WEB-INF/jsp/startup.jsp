<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>MOTECH - Mobile Technology for Community Health</title>
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-responsive.css">
    <link rel="stylesheet" type="text/css" href="resources/css/index.css" />

    <script src="resources/lib/jquery/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.form.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery-ui.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.alerts.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.i18n.properties-min-1.0.9.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.tools.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.blockUI.js" type="text/javascript"></script>

    <script src="resources/lib/bootstrap/bootstrap-modal.js"></script>

    <script src="resources/js/startup.js"></script>
</head>
<body>
<div class="bodywrap">
    <div class="header">
        <div class="container">
            <div class="logo"></div>
            <div class="header-title">Mobile Technology for Community Health</div>
            <div class="clearfix"></div>
        </div>
    </div>
    <div class="clearfix"></div>
    <div id="content" class="container-fluid">
        <div class="row-fluid">
            <div id="main-content">
                <div>
                    <div class="form-panel">
                        <form:form commandName="startupSettings" method="POST" class="form-horizontal">
                            <div class="control-group">
                                <h2 class="title">Welcome to Motech - startup settings.</h2>
                            </div>
                            <div class="control-group">
                                <form:label path="language" class="control-label">Select Language</form:label>
                                <div class="controls">
                                    <c:forEach var="lang" items="${languages}">
                                        <form:radiobutton path="language" value="${lang}"/><i class="flag flag-${lang}"></i>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="control-group">
                                <form:label path="databaseUrl" class="control-label">Database URL</form:label>
                                <div class="controls">
                                    <form:input path="databaseUrl" placeholder="Enter Database URL" class="input-large" />
                                    <c:if test="${ not empty suggestions.databaseUrls }">
                                        <div id="database.urls">
                                        <c:forEach var="url" items="${suggestions.databaseUrls}" varStatus="status">
                                            <div id="database.url.${status.count}">
                                                <span><i>Suggestion #${status.count}: </i>${url}</span>
                                                <button type="button" class="btn btn-mini">Use</button>
                                            </div>
                                        </c:forEach>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="control-group">
                                <form:label path="queueUrl" class="control-label">Queue URL</form:label>
                                <div class="controls">
                                    <form:input path="queueUrl" placeholder="Enter Database URL" class="input-large" />
                                    <c:if test="${ not empty suggestions.queueUrls }">
                                        <div id="queue.urls">
                                        <c:forEach var="url" items="${suggestions.queueUrls}" varStatus="status">
                                            <div id="queue.url.${status.count}">
                                                <span><i>Suggestion #${status.count}: </i>${url}</span>
                                                <button type="button" class="btn btn-mini">Use</button>
                                            </div>
                                        </c:forEach>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="control-group">
                                <form:label path="schedulerUrl" class="control-label">Scheduler URL </form:label>
                                <div class="controls">
                                    <form:input path="schedulerUrl" placeholder="Enter Database URL" class="input-large" />
                                    <c:if test="${ not empty suggestions.schedulerUrls }">
                                        <div id="scheduler.urls">
                                        <c:forEach var="url" items="${suggestions.schedulerUrls}" varStatus="status">
                                            <div id="scheduler.url.${status.count}">
                                                <span><i>Suggestion #${status.count}: </i>${url}</span>
                                                <button type="button" class="btn btn-mini">Use</button>
                                            </div>
                                        </c:forEach>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="control-group">
                                <div class="controls">
                                    <input class="btn btn-primary" type="submit" name="SUBMIT" value="Submit"/>
                                    <input class="btn" type="submit" name="START" value="Submit and start"/>
                                </div>
                            </div>
                            <form:errors path="*" element="div" cssClass="alert alert-error"/>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>