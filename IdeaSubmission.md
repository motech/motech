# Introduction #

This document aims to describe how new bugs and ideas are introduced for inclusion in the MOTECH codebase. The process is quite simple, but may not be obvious for those new to the project.


# Details #
## Project Management Basics ##

We use [Trello](https://trello.com/motechproject) for project management. Our work is organized among several different Trello boards.

The first two boards are open to submissions from everyone involved in the project:

  * [Bugs](https://trello.com/board/bugs/4f998b99bce6f4507520fb65) - the Bugs board tracks bugs that are found during development. These cards are reviewed during the weekly Inbox Review meeting.
  * [Inbox](https://trello.com/board/inbox/4f998b3cbce6f4507520e157) - anyone may submit a new idea for consideration to the Inbox board. These cards are reviewed during the weekly Inbox Review meeting. If approved, they are moved to the Current Development board, or to the Planning board if spec work is required.

Ideas enter the Current Development, Roadmap and Planning boards via Inbox Review and Planning meetings. MOTECH devs should not create cards on these boards:

  * [Current Development](https://trello.com/board/current-development/4f998bf4bce6f45075216d26) - this board tracks current work. The For Current Release list represents our immediate product backlog - this is where developers should look for new work when they free up. If For Current Release _and_ the Review Queue are empty, developers are welcome to choose items from the Backlog. Items flow to Current Development from Bugs, Inbox, and Planning.
  * [Epics](https://trello.com/b/5kcpNTPG/epics) - we will update shortly

The following diagram shows how ideas usually flow through the system:

http://wiki.motech.googlecode.com/git/TrelloFlow.PNG

## Inbox Review Meeting ##
The Inbox Review meeting occurs on Skype, every Tuesday at 9am PST. During this meeting, we review new cards on the Inbox and Bugs boards, and move them to Current Development if approved (or Planning if a spec is needed). If an idea requires further clarification, we may defer to discussion on the mailing list.

If you have submitted a new card for consideration, please join the call or send a note to the mailing list providing any necessary justification or clarification.

## Module Contributions ##
Ideas for new modules should be submitted to the Inbox board, under the Module Contributions list. Please include a spec for your module (either in the card description, or include a link), and provide any further clarification on the mailing list if needed. Please see the [New Module Checklist](NewModule.md) for a discussion of what should be covered in the module spec.