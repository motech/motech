======================================
Demo: MOTECH Data Services Bulk Import
======================================

As of MOTECH 0.25, it is now possible to bulk import MDS entity instances in CSV format. This short tutorial describes the process. For this tutorial, we'll use the :std:ref:`CMS Lite module <cms-lite-module>`, which has a simple data model and represents a real-world use case where bulk upload is useful.

First let's take a look at CMS Lite in the data browser, in particular the StringContent entity. StringContent has four user-supplied fields: value, language, name, and metadata (a key-value map). The former three fields are required.

    .. image:: img/cms_lite_schema.png
        :scale: 100 %
        :alt: MDS Bulk Import Demo - CMS Lite data model
        :align: center

Now let's create a CSV file that defines a few entities for bulk import. You may use your text editor of choice, although for large data sets it's going to be easiest to use a spreadsheet tool like LibreOffice or MS Excel. Here's a sample with a header row and four entities:

    value,language,name,metadata

    Ciao,Italian,hello,A:1

    Bonjour,French,hello,B:2

    Hola,Spanish,hello,D:4

    Goodbye,English,goodbye,E:5

Note that it's not necessary to set many of the entity's fields in your CSV file (e.g. owner, creationDate, createdBy, modifiedBy, etc.); these will be set by the system. If you include the id field and it matches an existing entity's ID, the import will be handled as an update to the existing row.

Save the file in .csv format, and you're ready to go. Now let's upload the file. In the MDS Data Browser, navigate once again to StringContent, and click on Import CSV:

    .. image:: img/import_csv.png
        :scale: 100 %
        :alt: MDS Bulk Import Demo - Import CSV button
        :align: center

Browse to the location of your saved CSV file and select it. Et voila, your new entities will appear in the data browser:

    .. image:: img/imported_entities.png
        :scale: 100 %
        :alt: MDS Bulk Import Demo - Imported entities
        :align: center
