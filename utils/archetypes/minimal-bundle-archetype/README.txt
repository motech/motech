# vanilla minimal archetype
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=minimal-bundle-test -Dpackage=archetype.test.pack -Dversion=0.1-SNAPSHOT -DbundleName="Test Minimal"

# minimal archetype plus http
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=minimal-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=http-bundle-test -Dpackage=archetype.test.pack -Dversion=0.1-SNAPSHOT -DbundleName="Test HTTP" -Dhttp=true

mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.motechproject -DarchetypeArtifactId=http-bundle-archetype -DarchetypeVersion=0.22-SNAPSHOT -DgroupId=archetype-test -DartifactId=http-bundle-test -Dpackage=archetype.test.pack -Dversion=0.1-SNAPSHOT -DbundleName="Test HTTP" -Dhttp=true

