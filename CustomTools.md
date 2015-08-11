# Enforcer custom rules #

Documentation of custom rules for maven-enforcer-plugin.

**Rule: Package cycles detection**

**implementation class:**
> PackageCycles
**parameters:**
> _exclusionPackagesFile_ - path to the file in which there is a list of packages that are excluded from cycles searching. Packages name could be either comma, semicolon or new line separated. If the cycle is found and one of it's packages can be found in exclusion file, build will not fail, but only warn about the cycle.
> > Values: String, path to file,
> > Default: none


> _failBuild_ - boolean parameter that indicates if found cycle should fail a build or only warn about it.
> > Values: true or false,
> > Default: true

# PMD custom rules #

Documentation of custom rules for maven-pmd-plugin.

**Rule: Commented out lines of code**

**implementation class:**

> CommentedOutCode
**parameters:**
> > _classificationThreshold_ - sensitivity of classifying the source fragment as a commented java code.
> > > Values: from 0 to 1,
> > > Default: 0.95


> _skipCheckSequence_ - when placed at the beginning of the comment, this sequence skips checking.
> > Values: String,
> > Default: ##


> _skipJavaDocs_ - skip checking within JavaDocs comments.
> > Values: true or false,
> > Default: true