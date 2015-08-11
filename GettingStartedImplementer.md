# Introduction #

Add your content here.


# Extending MOTECH #
## Extending MOTECH without custom code ##
TODO: discussion of tasks

Future: customizing data models with Seuss

## Creating a Custom Module ##
> MOTECH can be easily customized by writing extension plugins. Using the MOTECH Maven archetype will help you to get started faster. Below are two command line usage examples for creating a module named 'childcare'.

Running the commands will create a new sub-directory with the project name specified. For the example, we would generate a folder called motech-childcare in the current directory

**Generating Archetype from Nexus Repository**
```
mvn archetype:generate -DarchetypeRepository=http://nexus.motechproject.org/content/repositories/releases -DarchetypeGroupId=org.motechproject.maven.archetypes -DarchetypeArtifactId=maven-archetype-motech-module  -DarchetypeVersion=0.21.1  -DgroupId=org.motechproject -DartifactId=motech-childcare -Dversion=0.1-SNAPSHOT
```

**Generating Archetype from Local Repository**
```
mvn archetype:generate -DarchetypeRepository=file://~/.m2 -DarchetypeGroupId=org.motechproject.maven.archetypes -DarchetypeArtifactId=maven-archetype-motech-module -DarchetypeVersion=0.20-SNAPSHOT -DgroupId=org.motechproject -DartifactId=motech-childcare -Dversion=0.1-SNAPSHOT
```

### Custom UI ###
Any custom module can present its own UI panel, similar to the existing platform modules. If the implementer starts with the Maven archetype referenced above, it will generate a directory structure with a web controller placeholder as a starting point - implementers can then customize to add their own UI.

### Extending MOTECH's Security Model ###
Implementers may also extend MOTECH's security model with additional roles/permissions that would restrict the usage of the implementation module. See how Message Campaign defines roles [here](https://code.google.com/p/motech/source/browse/modules/message-campaign/message-campaign/src/main/resources/roles.json).

# Hosting MOTECH #
TODO: link to hosting page on MOTECH website