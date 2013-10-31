Please see the Motech wiki for further explanation of the Motech Bundle Archetypes
https://code.google.com/p/motech/wiki/MotechBundleArchetypes

Generate a minimal bundle:
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"

Generate a bundle with HTTP support (run two commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module" -Dhttp=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=http-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"

Generate a bundle with repository support (run two commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module" -Drepository=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=repository-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"
* note: this would be suport for a new, module-specific repository, not MRS or other existing repository. 

Generate a bundle with settings support (run two commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module" -Dsettings=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=settings-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"

Generate a bundle with lots of added support (run all commands from the same directory):
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module" -Dhttp=true -Drepository=true -Dsettings=true
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=http-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=repository-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=settings-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"

Use archetypes locally instead of using the Nexus repository:
git clone https://code.google.com/p/motech/
cd motech
mvn clean install
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=archetype-test-module -Dpackage=archetype.test -Dversion=0.1-SNAPSHOT -DbundleName="Archetype Test Module"
