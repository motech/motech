<%@ page language="java" pageEncoding="UTF-8"%>
<%
java.util.jar.Manifest manifest = new java.util.jar.Manifest();
manifest.read(pageContext.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
java.util.jar.Attributes attributes = manifest.getMainAttributes();
org.motechproject.server.osgi.OsgiFrameworkService osgiService = org.motechproject.server.osgi.OsgiListener.getOsgiService();
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>MOTECH - Mobile Technology for Community Health</title>

    <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="css/bootstrap-responsive.css">
    <link rel="stylesheet" type="text/css" href="css/index.css" />
</head>
<body>

<div class="bodywrap">
    <div class="header">
        <div class="container">
            <div class="logo"></div>
            <div class="header-title">Mobile Technology for Community Health</div>
            <div class="top-menu">
                <div class="navbar">
                    <ul class="nav">
                        <li><strong>Server up time: </strong>459 days 11:23:43</li>
                        <li>|</li>
                        <li><a href=""><strong>Login </strong></a></li>
                        <li>|</li>

                        <li class="dropdown">
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                                <i class="flag flag-en"></i> English
                                <span class="caret"></span>
                            </a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="#"><i class="flag flag-pl"></i> Polski</a></li>
                            <li><a href="#"><i class="flag flag-us"></i> US-English</a></li>
                            <li><a href="#"><i class="flag flag-fr"></i> French</a></li>
                            <li><a href="#"><i class="flag flag-de"></i> Deutsch</a></li>
                        </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="clearfix"></div>

    <div class="header-nav navbar">
        <div class="navbar-inner navbar-inner-bg">
            <ul class="nav" role="navigation">
                <li class="current"><a  role="menu"  href="#/welcome">Home</a></li>
                <li><a>|</a></li>
                <li><a role="menu" href="#/trees">Motech</a></li>
                <li><a>|</a></li>
                <li><a role="menu" href="#">Project</a></li>
                <li><a>|</a></li>
                <li><a role="menu" href="#">Community</a></li>
            </ul>
        </div>
    </div>
    </div>
    <div class="clearfix"></div>

    <div id="content" class="container-fluid">
        <div class="row-fluid">

            <div id="side-nav" class="span2 well">
                <ul class="nav nav-tabs nav-stacked">
                    <li class="nav-header">VIEW</li>
                    <li><a href="#">Metrics</a></li>
                    <li><a href="#">DB Viewer</a></li>
                    <li><a href="#">Batch Tasks</a></li>
                    <li><a href="#">Settings</a></li>
                    <li class="divider"></li>

                    <li class="nav-header">MODULES</li>
                    <li><a href="#">Alerts</a></li>
                    <li><a href="#">Appointments</a></li>
                    <li><a href="#">CMS Life</a></li>
                    <li><a href="#">Decision Tree</a></li>
                    <li><a href="#">IVR</a></li>
                    <li class="active"><a href="#">Message Campaign</a></li>
                </ul>
            </div>

            <div id="main-content" class="span10">
                <div >
                    <div class="top1">
                        <ul class="breadcrumb">
                            <li><a href="#">Modules</a> <span class="divider">/</span></li>
                            <li class="active">Message Campaign</li>
                        </ul>
                    </div>

                    <div class="tabbable"> <!-- Only required for left/right tabs -->
                        <ul class="nav nav-tabs">
                            <li class="active"><a href="#tab1" data-toggle="tab">Settings</a></li>
                            <li><a href="#tab2" data-toggle="tab">Create</a></li>
                            <li><a href="#tab2" data-toggle="tab">Campaigns</a></li>
                            <li><a href="#tab2" data-toggle="tab">Subscriptions</a></li>
                        </ul>

                        <div class="tab-content">
                            <div class="tab-pane active" id="tab1">
                                <p>I'm in section Settings.</p>
                            </div>
                            <div class="tab-pane" id="tab2">
                                <p>Howdy, I'm in section Create.</p>
                            </div>
                            <div class="tab-pane" id="tab3">
                                <p>Howdy, I'm in section Campaigns.</p>
                            </div>
                            <div class="tab-pane" id="tab4">
                                <p>Howdy, I'm in section Subscriptions.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

</div>

<hr/>
<b>Generated at:</b> <%= new java.util.Date() %>
</body>
</html>
