=====================
Browser Compatibility
=====================

Supported Browsers
==================

MOTECH project uses new technologies (HTML 5, CSS3) and new frameworks like AngularJS, Bootstrap which means that the compatibility of the browsers depends on the extent to which these frameworks support old browsers.

We officialy support IE11 and up, Chrome, Firefox, Safari and Opera. We don't support older versions of Internet Explorer due to framework compatibility, as mentioned above.

Screen Resolution
=================

Currently we tested MOTECH on screen resolution 1024 x 768 px and higher,
but the best results will be achieved using resolution 1680 x 1050 px or higher, especially for wide tables.
That means we do not support mobile and tablet devices.

Browser Settings
================

All browsers must have cookies and JavaScript enabled to use MOTECH.

Our approach to maintain compatibility with browsers
====================================================

1. Even if we declare the latest HTML 5 in MOTECH, we try not to use the latest tags, if possible.
2. Newer versions of browsers provide debugging tools directly or as a plugin. These developer tools also allow you to inspect specific HTML and alter CSS styles.
3. We do not use inline JavaScript events inside HTML markup. An example would be <button onclick="validate()">Validate</button>. This practice breaks the clean separation that should exist between markup, presentation, and behavior.