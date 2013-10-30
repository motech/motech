Please see the Motech wiki for further explanation of the Motech Bundle Archetypes
https://code.google.com/p/motech/wiki/MotechBundleArchetypes

Generate a minimal bundle:
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"

Generate a bundle with HTTP support (run two commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle" -Dhttp=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=http-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"

Generate a bundle with repository support (run two commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle" -Drepository=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=repository-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"

Generate a bundle with settings support (run two commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle" -Dsettings=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=settings-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"

Generate a bundle with several supports (run all commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle" -Dhttp=true -Drepository=true -Dsettings=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=http-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=repository-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=settings-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"

Use archetypes locally instead of using the Nexus repository:
git clone https://code.google.com/p/motech/
cd motech
mvn clean install
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=my-bundle-test -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="My Test Bundle"
