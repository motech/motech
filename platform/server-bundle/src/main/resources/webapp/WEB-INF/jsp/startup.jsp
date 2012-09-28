<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                        <form action="startup.jsp" method="POST" class="form-horizontal">
                            <div class="control-group">
                                <h2 class="title">Welcome to Motech - startup settings.</h2>
                            </div>
                            <div class="control-group">
                                <label class="control-label">Select Language</label>
                                <div class="controls">
                                    <c:forEach var="lang" items="${languages}">
                                        <input type="radio" value="${lang}" name="language" <c:if test="${startupSettings.language == lang}">checked</c:if> /><i class="flag flag-${lang}"></i>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label">Database URL</label>
                                <div class="controls">
                                    <input placeholder="Enter Database URL" class="input-large" name="databaseUrl" value="${startupSettings.databaseUrl}"/>
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
                                <label class="control-label">Queue URL</label>
                                <div class="controls">
                                    <input placeholder="Enter Database URL" class="input-large" name="queueUrl" value="${startupSettings.queueUrl}"/>
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
                                <label class="control-label">Scheduler URL </label>
                                <div class="controls">
                                    <input placeholder="Enter Database URL" class="input-large" name="schedulerUrl" value="${startupSettings.schedulerUrl}"/>
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
                            <c:if test="${not empty errors}">
                                <div class="alert alert-error">
                                <c:forEach var="error" items="${errors}">
                                    ${error}<br/>
                                </c:forEach>
                                </div>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>