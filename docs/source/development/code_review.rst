==================================
Code Review Workflow and Checklist
==================================

##########
Principles
##########

In general, we aim to have all code reviewed prior to submission, with very few – hopefully rare – exceptions
Please be conscientious about turning around reviews quickly – investing in good code review karma will pay off when it comes time for you to solicit reviews from your peers

#####################################
Exceptions to Code Review Requirement
#####################################

Urgent fix for a build break. Unless the fix is completely trivial, please remember to get a code review after submission.

########
Workflow
########

#. **Write code:** Follow the development workflow described :doc:`here <patch>` to develop code changes and submit for review
#. **Choose code reviewers:** Add your chosen code reviewers as reviewers in Gerrit. For most changes, one reviewer should be sufficient; if your change is complex and/or represents core platform functionality, it is ideal to involve more than one reviewer. If a particular individual has significant expertise in the code you are changing, it’s a good practice to include that person as a revewier. If not, it's a great practice to choose someone from a different organization to review your code, in order to facilitate cross-pollination of ideas across the project.
#. **Wait for reviews to come in:** Code reviewers should attempt to provide comments within 2 business days – in the case of time-sensitive fixes, reviews should be turned around faster. (TIP: If you aren’t seeing email notifications for code reviews assigned to you, check your junk mail folder.)
#. **Address feedback:** It should be possible to address most code review feedback prior to submission. If changes are non-trivial, create a new patch and send it out for re-review as described :doc:`here <patch>`.
#. **Track any future work identified:** For larger issues that are flagged during code review, discuss with the reviewer whether the feedback should block submission. If not, open a new Trello card to track the suggestion for a later code change.
#. **Submit:** Once reviewers have signed off, submit change. Gerrit has a button that allows you to merge directly from the site.

#########
Checklist
#########

Correctness
###########

* Does the code completely and correctly implement the design?

* Does every code change in the submission map to a ticket in Jira?

Maintainability
###############

* Does the code make sense?

* Code reuse

    * Are there any blocks of repeated code that could be encapsulated into methods?
    * Can the desired functionality be achieved by reusing any existing code?

* Does the code comply with the accepted :doc:`Coding Conventions <coding_conventions>`? (Indentations, varable/method names, bracket style, commenting, etc.)

Error Handling
##############

* Are all thrown exceptions handled properly?

* Does the code catch (or throw) general exceptions, e.g. java.lang.Exception?

* If a method could return null, does the caller check for null?

Control Flow and Structure
##########################

* Are loop termination conditions obvious and invariably achievable?

Test Coverage
#############

* Have sufficient unit tests been provided to cover the basic functionality being provided?

* Do unit tests cover all error conditions of a method call?

Documentation
#############

* Are the classes properly documented? Do they contain at least a short description of what they do?

* Is the code self-documenting? If its very cryptic what a piece of code does or why, it should either get reworked or properly documented.

* Are documentation changes required? If the change is modifying the API or behavior of already documented components, it should also contain appropriate updates to the documentation.

* Is new documentation required? In case of a new module or new functionality, it should get properly documented.
The documentation should either be included with the change or a ticket for documenting the functionality should exist. If there is no documentation and no ticket on Jira,
a ticket for adding the documentation should get created on Jira with "Inbox" set as the fix version. That ticket will get discussed during our weekly review call and scheduled accordingly.

Resources
#########

The resources below contain a number of good ideas (and some bad ones). Many of the checklist items above were borrowed from these documents, with some tweaking.

    * http://www.javacodegeeks.com/2011/06/not-doing-code-reviews-whats-your.html
    * http://scientopia.org/blogs/goodmath/2011/07/06/things-everyone-should-do-code-review/
    * http://www.perlmonks.org/?node_id=744932
    * https://wiki.openmrs.org/display/docs/Code+Review+Checklist