=====================
Browser Compatibility
=====================

Supported Browsers
==================

MOTECH project uses new technologies (HTML 5, CSS3) and new frameworks like `AngularJS <https://angularjs.org/>`_, `Bootstrap <http://getbootstrap.com/>`_
which means that the compatibility of the browsers depends on the extent to which these frameworks support old browsers.

Specifically, we support the latest versions of the following browsers and platforms.

.. |v| image:: img/checkmark.png
.. |x| image:: img/x.png

.. csv-table::
    :header: "Platform", "IE 11+", "Chrome", "Firefox", "Safari", "Opera"
    :widths: 25, 20, 20, 20, 20, 20

    "Windows", |v| Supported, |v| Supported, |v| Supported, |x| Not Supported, |v| Supported
    "Mac OS X", |x| Not Supported, |v| Supported, |v| Supported, |v| Supported, |v| Supported
    "Linux", |x| Not Supported, |v| Supported, |v| Supported, |x| Not Supported, |v| Supported


Unofficially, MOTECH should look and behave well enough in Chromium for Linux,
though it is not officially supported.

Internet Explorer Compatibility
===============================

Bootstrap is built to work best in the latest desktop browsers, meaning older browsers might display differently styled, though fully functional, renderings of certain components.

AngularJS 1.3 has dropped support for IE8. Read more about it on this `blog <https://blog.angularjs.org/2013/12/angularjs-13-new-release-approaches.html>`_.

The AngularJS project currently supports and will attempt to fix bugs for IE9 and above. Their continuous integration server runs all the tests against IE9, IE10, and IE11. See `Travis CI <https://travis-ci.org/angular/angular.js>`_ and `ci.angularjs.org <https://ci.angularjs.org>`_.

However, MOTECH supports IE11 and above. We don't support older versions of Internet Explorer due to framework compatibility, as mentioned above.

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