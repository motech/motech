========================
Design and UI Guidelines
========================

This section of the documentation intends to outline common patterns that are used in the MOTECH platform, along with examples of working code that can be used to as templates for developers contributing to the MOTECH system.

Writing Style
=============
The following are conventions for writing messages and instructional text in MOTECH. We want the messages in the system to feel consistent, which means that messages should use similar structure.

More text will be added.

Typography
==========
Since the MOTECH UI is built off the Bootstrap framework, it shares the same typographical features. The base MOTECH font in 'Helvetica Neue' 14px.

Iconography
-----------
The MOTECH UI uses the Font Awesome icon libaray and implements icons using CSS psudo-elements so that the HTML templates in MOTECH are clean, semantic, and easy to update from a single location.

MOTECH Logo
-----------
The only custom design asset in the MOTECH platform is the MOTECH logo. A SVG version of the :download:`MOTECH logo is available here, <motech_logo.svg>` incase you need to do something with it.

Markup Guidelines
=================
To keep the MOTECH UI consistent, our goal is to keep the HTML markup as simple and semantic as possible. This means that all markup tags should be kept as concise as possible, and container elements should be avoided where possible.

Extra markup needed for interactive elements that are rendered with Javascript should be added in Javascript, and wrapped in an Angular directive.

Headings
--------
Recommendations for heading structure will be added.

Hyperlinks
----------
Ideally all hyperlinks should be accessiable, meaning that the first word in the link text is the subject of the link. Links such as 'stop' or 'click here' are not accessiable because a screen reader might loose the context of the link.

Examples of accessiable link text would be: 'Alert module stop' or 'Alert module add data'.

An important note is while this text should be here for non-sighted users, this can be hidden for sighted users.

Buttons
-------
Buttons should only be used to represent an action that will make a change rather than a link that will navigate a user to a different section of the website.

When adding icons to buttons, don't add extra markup for the button icon, instead use a css class that adds a psudo-element.::

    <button class="icon icon-add">Create New Configuration</button>

Dropdowns
---------
More text will go here.

Accordions
----------
More text will go here.

Navigation Layout
=================
More text will go here.

System Messasges
================
There are four types of messages that can show up on the MOTECH UI: Information, Warnings, Alerts, and Errors. The difference between these items is how assertive the message from the system needs to be. Errors and Alerts are the most assertive, and both should launch a modal window forcing the user to take action.

Information Messages
--------------------
More text will go here.

Warnings
--------
More text will go here.

Alerts
------
More text will go here.

Errors
------
More text will go here.

Page Layout
===========
More text will go here.

Forms
=====
More text will go here.

Inline Forms
------------
More text will go here.

Pop-Up Forms
------------
More text will go here.

Lists
=====
More text will go here.

Collapsible Lists
-----------------
More text will go here.

Data Grids
==========
More text will go here.

Tasks Workflow
==============
More text will go here.


