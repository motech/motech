# Principles #

  * Release often – so that implementers can access new features – but not so often that we introduce undue overhead for the team or churn for implementers
  * Publish our release roadmap for consumption by implementers, donors and partners
  * As part of our planning process, attempt to call out some of the major features expected in the next two or three versions. But in the spirit of maintaining our agile planning philosophy, do not attempt to create a detailed release plan in advance.
  * Release dates provide a rallying point for targeting checkins and adding a bit of predictability to our work, but we do not need to be religiously date-driven unless there is a specific external/partner release target at stake. If an important feature misses the release deadline – or will need to sacrifice quality or completeness in order to make the deadline – we can evaluate slipping the release to accommodate.

# Release Management #

## Frequency ##
  * Release monthly, with a default release date of the **third Tuesday of every month** – this calendar will be published on our website
  * [Eventually: **Stable** releases quarterly - see below for the stable release criteria]

## Planning ##
  * Planning meetings will identify a small set of “highlight” features that are candidates for inclusion in the next several upcoming releases. Members of the motech-dev mailing list will be encouraged to provide feedback/suggestions for features to include during the Inbox Review call. This list will be published on a Roadmap page on our website, to provide coming attractions for interested parties.
  * Once monthly on the Inbox Review call, we will review the set of changes slated for the next release – this is a good time to call out any important features or critical bug fixes that are needed for the release but were not identified as part of the Roadmap exercise.
  * Eventually, we will start an implementers conference call which will follow the Inbox Review meeting and will give implementers an opportunity to provide feedback on the set of features planned for the upcoming release.

# Release Criteria #
To date, we have been relatively lax regarding QA signoff metrics for a release. Only the following criteria need to pass in order to release a build:
  * Build succeeds
  * Unit tests pass
  * Verbal signoff from development teams indicating that the needs of various implementers will be met by the release candidate

In future, we will implement stricter release criteria for quarterly (stable) releases, to ensure quality of a given release candidate. These criteria will include:
  * 100% test pass rate
  * 100% CheckStyle pass rate
  * XX% code coverage
  * Demos up-to-date, functioning
  * Integration tests have been implemented to cover major new features in the release (and they pass)
  * Sign-off on release candidate from Grameen, ThoughtWorks, USM, SolDevelo

# Branching (and Back Porting) #