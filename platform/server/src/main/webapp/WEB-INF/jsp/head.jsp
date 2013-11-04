<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>MOTECH - Mobile Technology for Community Health</title>

<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jquery-ui-1.9.1-redmond.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/angular-ui.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/bootstrap.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/bootstrap-responsive.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/font-awesome/css/font-awesome.min.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/bootstrap-fileupload.min.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jquery-ui-min.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/tagsinput/jquery.tagsinput.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/timepicker/jquery-ui-timepicker-addon.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jquery-cron/jquery-gentleSelect.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jquery-cron/jquery-cron.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jquery-sidebar.css"/>' />
<%--<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jqGrid/ui.jqgrid.css"/>' />--%>
<%--<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/jqGrid/ui.jqgrid.override.css"/>' />--%>
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/index.css"/>' />
<%--<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/bootstrap-notify.css"/>' />--%>
<%--<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/bootstrap-switch.css"/>' />--%>
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/alert-blackgloss.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="/static/css/select2.css"/>' />

<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.js"/>'></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.form.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery-ui.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.alerts.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.i18n.properties-min-1.0.9.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.tools.min.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.blockUI.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.caret.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.sidebar.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.livequery.min.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery/jquery.select2.js"/>' ></script>

<script type="text/javascript" src='<c:url value="/static/lib/angular/angular.min.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/angular/angular-resource.min.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/angular/angular-cookies.min.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/angular/angular-bootstrap.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/angular/angular-ui.min.js"/>' ></script>

<script type="text/javascript" src='<c:url value="/static/lib/bootstrap/bootstrap.min.js"/>' ></script>
<%--<script type="text/javascript" src='<c:url value="/static/lib/bootstrap/bootstrap-fileupload.min.js"/>'></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/lib/bootstrap/bootstrap-notify.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/lib/bootstrap/bootstrap-switch.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/lib/bootstrap/bootstrap-multiselect.js"/>' ></script>--%>

<script type="text/javascript" src='<c:url value="/static/lib/tagsinput/jquery.tagsinput.js"/>' ></script>

<script type="text/javascript" src='<c:url value="/static/lib/timepicker/jquery-ui-sliderAccess.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/timepicker/jquery-ui-timepicker-addon.js"/>' ></script>

<script type="text/javascript" src='<c:url value="/static/lib/jquery-cron/jquery-gentleSelect.js"/>' ></script>
<script type="text/javascript" src='<c:url value="/static/lib/jquery-cron/jquery-cron.js "/>' ></script>

<%--<script type="text/javascript" src='<c:url value="/static/lib/moment/moment.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/lib/moment/langs.js"/>' ></script>--%>

<%--<script type="text/javascript" src='<c:url value="/static/lib/parseuri/parseuri.js"/>' ></script>--%>

<%--<script type="text/javascript" src='<c:url value="/static/lib/underscore/underscore.js"/>' ></script>--%>

<%--<script type="text/javascript" src='<c:url value="/static/js/app.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/js/services.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/js/util.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/js/common.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/js/localization.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/js/directives.js"/>' ></script>--%>
<%--<script type="text/javascript" src='<c:url value="/static/js/controllers.js"/>' ></script>--%>
<script type="text/javascript" src='<c:url value="/static/js/dashboard.js"/>' ></script>
<%--<script type="text/javascript" src='<c:url value="/static/js/browser-detect.js"/>' ></script>--%>
