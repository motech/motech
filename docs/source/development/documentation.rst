=======================
Authoring Documentation
=======================

This document provides information specific to setting up and authoring Sphinx documentation for MOTECH. The `Sphinx Documentation Guide <http://sphinx-doc.org/contents.html>`_ is also a good resource.

Each MOTECH repository contains a *docs* directory populated with reStructured Text (reST) files. These files are built by Sphinx and hosted at http://readthedocs.org. A Sphinx plugin, called Javasphinx, builds Javadoc for the MOTECH codebase.

Installing Sphinx and Javasphinx
================================

If you are working on MOTECH documentation, it is helpful to build the docs locally and review changes in advance of checkin. The steps below will get your environment configured to build Sphinx docs.

Install python utils

.. code-block:: bash

    sudo apt-get install python-setuptools python-dev python-pip

Install javasphinx and motechjavasphinx

.. code-block:: bash

    sudo apt-get install libxslt-dev libxml2-dev zlib1g-dev
    cd docs
    sudo pip install -r requirements.txt

Building Docs
=============
ReadTheDocs.org automatically builds Sphinx projects, but it is a good idea to build your documents and make sure they render correctly before you push your changes up to the code repository.

To build Sphinx docs, go to the docs directory and type:

.. code-block:: bash

    make html

You can then use a web browser to view docs/build/html/index.html and make sure everything looks good.

Authoring and Submitting New Documents
======================================

To create a new documentation topic, simply create a file with extension *.rst* under the docs directory. Create a table-of-contents entry for your new doc in the appropriate index.rst file. The `reStructuredText Primer <http://sphinx-doc.org/rest.html>`_ is good to have handy as you write your doc.

When your document is ready for review, follow the instructions for :doc:`creating and submitting a patch <patch>` to submit it for code review.