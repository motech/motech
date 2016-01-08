===================================
Managing External OSGi dependencies
===================================

OSGi dependencies
=================

All MOTECH modules run in a `Felix <http://felix.apache.org/>`_ OSGi framework instance embedded within
the platform war. Because of this all libraries you make use of in your module should be packaged as actual OSGi
bundles. Since everything that is (theoretically) needed to make a library ready for OSGi is adding a few entries in the
**META-INF/MANIFEST.MF** many popular java libraries are already OSGi enabled by default. However this might not be
the case for all the libraries you would wish to use. While you are urged to use libraries that are OSGi bundles, it is
still possible to use non OSGi compatible libraries. This document will take you through the process of evaluating a given
dependency and using it in MOTECH.

Recognizing whether the library is an OSGi bundle
=================================================

First of all you should check whether the library itself is an OSGi dependency. An easy way to do this is to open its jar
file using any of the tools that allow browsing zip files (jars are actually zip files after all). After opening the jar,
check the **META_INF/MANIFEST.MF** file for presence of OSGi related entries such as: Bundle-SybmolicName, Bundle-Version,
Import-Package, Export-Package, etc. If they are present it means that the library supports OSGi.

.. note::

    Always check the source library for OSGi support before doing anything else, like looking for alternatives
    or OSGifying it yourself.


Finding an existing OSGi version of the dependency
==================================================

If your dependency is not an OSGi bundle at source, don't worry, it's possible that someone has already OSGified the
bundle for you. You can search for an OSGified version using Google (or any other search engine). There are however a few particular
repositories excelling in providing OSGi versions of common libraries:

* **Spring EBR** - http://ebr.springsource.com/repository/app/ - while the future is not certain for this project, the
    Enterprise Bundle Repository remains a good source of OSGi bundles. This repository is proxied through our Nexus.

* **ServiceMix Bundles** - http://servicemix.apache.org/developers/source/bundles-source.html - this repository is a good place
    to search for existing OSGi compatible versions of popular java libraries. The bundled versions from this repository go to maven
    central.

* **Pax Tipi** - https://ops4j1.jira.com/wiki/display/PAXTIPI/Pax+Tipi - OPS4J's answer to this problem. Pax Tipi is an umbrella project
    for third-party artifacts repackaged as OSGi bundles. These OSGi bundles are all available from Maven Central with groupId org.ops4j.pax.tipi.

If none of these resources provide the OSGi version of the library you are looking for, it seems you will have to get your
hands dirty and follow the good old principle - if you want something done, do it yourself.

Creating an OSGi-ready version of the dependency
================================================

Creating an OSGi version of given library is not an awfully complicated process. All you have to do is to create a version with
the correct **META-INF/MANIFEST.MF** file. There are tools that can help you with this and you are not required to have
access to the library code in order to accomplish this task. In MOTECH we use the `Felix Bundle Plugin for Maven <http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html>`_
for creating bundles. A dependency to OSGify should be added as a maven module in our `External OSGi Bundles repository <https://github.com/motech/external-osgi-bundles>`_. Everything
you should need to place there is the pom file with the correct configuration for creating the bundle. The following code is an example of such a
pom.xml file for creating a bundle from org.example.example-artifact, version 1.0:

.. code-block:: xml

    <?xml version="1.0" encoding="UTF-8"?>

    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

        <modelVersion>4.0.0</modelVersion>

        <properties>
            <example.version>1.0<example.version>
        </properties>

        <parent>
            <groupId>org.motechproject</groupId>
            <artifactId>external-osgi-bundles</artifactId>
            <version>1.0.8</version>
        </parent>

        <!-- We prefix the groupId for our bundles with org.motechproject -->
        <groupId>org.motechproject.org.example</groupId>
        <artifactId>example-artifact</artifactId>
        <!-- The release tag property is important. It allows us to update the version of the bundle
             without making changes to the base version -->
        <version>${example.version}-${release.tag}</version>


        <!-- The library we are OSGifying has to be declared as a dependency -->
        <dependencies>
            <dependency>
                <groupId>org.example</groupId>
                <artifactId>example-artifact</artifactId>
                <version>${example.version}</version>
                <scope>provided</scope>
            </dependency>
        </dependencies>

        <build>
            <plugins>
                <!-- This will make Felix scan the library for imports -->
                <plugin>
                    <artifactId>maven-dependency-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>unpack-sources</id>
                            <goals>
                                <goal>unpack</goal>
                            </goals>
                            <phase>package</phase>
                            <configuration>
                                <outputDirectory>${project.build.directory}/sources</outputDirectory>
                                <artifactItems>
                                    <artifactItem>
                                        <groupId>org.example</groupId>
                                        <artifactId>example-artifact</artifactId>
                                        <version>${example.version}</version>
                                        <classifier>sources</classifier>
                                    </artifactItem>
                                </artifactItems>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>

                <!-- This configuration will tell the Felix bundle plugin to generate
                     the bundle for the library. The original library will be embedded in the newly
                     created bundle jar -->
                <plugin>
                    <groupId>org.apache.felix</groupId>
                    <artifactId>maven-bundle-plugin</artifactId>
                    <version>2.3.4</version>
                    <extensions>true</extensions>
                    <configuration>
                        <instructions>
                            <!-- All library packages that are supposed to be exposed must be declared as exports -->
                            <Export-Package>
                                org.example;version=${project.version},
                                org.example.subpackage;version=${project.version}
                            </Export-Package>
                            <!-- You can specify additional imports that were not found by Felix -->
                            <Import-Package>
                                hidden.import,
                                *
                            </Import-Package>
                            <!-- Bundle metadata for the newly created bundle -->
                            <Bundle-SymbolicName>${project.artifactId}</Bundle-SymbolicName>
                            <Bundle-Vendor>Example.com</Bundle-Vendor>
                            <!-- We embed the original library -->
                            <Embed-Dependency>example-artifact;inline=true</Embed-Dependency>
                            <Embed-Transitive>true</Embed-Transitive>
                        </instructions>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </project>

It is important to note that by adding the release tag to the version of the bundle, we allow ourselves to make updates
i.e. add an export we forgot about, without changing the base version. The value of the release tag is in the main pom
of the external OSGi bundles repository.

Releasing a new version of external OSGi dependencies
=====================================================

For making a release of the external bundles, three steps are required:

#. Update the version in the parent pom. You can use the following command, where X is the new old number increased by one.

    .. code-block:: bash

        mvn versions:set -DnewVersion=1.0.X

#. Increment the release tag in the parent pom. For example if its value is r30, change it to r31

#. Trigger the external-osgi-bundles build on Jenkins, it should trigger automatically when new commits come in
 to the repository.

After the repository gets updated, the release tag values defined in parent poms for the MOTECH platform and MOTECH
modules must be updated in order to use the new versions.

.. note::

    We would prefer to keep the number of bundles we maintain to a minimum. So please only commit additional bundles when it's
    necessary and you are absolutely sure the library is not already an OSGi bundle itself and there are no existing OSGi compatible
    versions. Also, if you have knowledge that one of the dependencies we maintain was OSGified at source, please let us know,
    so that we can get rid off the burden of maintaining it. Remember that in an ideal world, the external-osgi-bundles repository
    would not exist.