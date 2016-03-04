package org.motechproject.admin.bundles;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Holds all important information about POM.
 */
public class PomInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(PomInformation.class);

    private PomInformation parentPomInformation;

    private Parent parent;
    private List<Dependency> dependencies;
    private Properties properties;
    private List<RemoteRepository> repositories;

    public void parsePom(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            parsePom(fileInputStream);
        } catch (IOException ex) {
            LOGGER.error("Error while reading POM file", ex);
        }
    }

    public void parsePom(InputStream inputStream) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();

            Model model = reader.read(inputStream);

            if (dependencies == null) {
                dependencies = new LinkedList<>();
            }
            if (repositories == null) {
                repositories = new LinkedList<>();
            }

            parent = model.getParent();
            setPropertiesFromModel(this, model);

            for (org.apache.maven.model.Dependency dependency : model.getDependencies()) {
                if (!"test".equalsIgnoreCase(dependency.getScope())) {
                    dependencies.add(new Dependency(new DefaultArtifact(
                            dependency.getGroupId(),
                            dependency.getArtifactId(),
                            dependency.getClassifier(),
                            "jar",
                            dependency.getVersion()
                    ), JavaScopes.RUNTIME));
                }
            }

            for (Repository remoteRepository : model.getRepositories()) {
                repositories.add(new RemoteRepository(remoteRepository.getId(), "default", remoteRepository.getUrl()));
            }
        } catch (Exception ex) {
            LOGGER.error("Error while reading POM file", ex);
        }
    }

    public void parseParentPom(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            parseParentPom(fileInputStream);
        } catch (IOException ex) {
            LOGGER.error("Error while reading parent POM file", ex);
        }
    }

    public void parseParentPom(InputStream inputStream) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();

            Model model = reader.read(inputStream);

            PomInformation parentPom = new PomInformation();

            setPropertiesFromModel(parentPom, model);
            parentPom.setParent(model.getParent());

            this.parentPomInformation = parentPom;
        } catch (Exception ex) {
            LOGGER.error("Error while reading parent POM file", ex);
        } 
    }

    /**
     * Sets properties from parsed pom file to the pomInformation object.
     * Additionally this method adds project.version, project.artifactId,
     * project.groupID as properties if the suitable tags are used in the pom file.
     *
     * @param pomInformation the information about parsed pom file
     * @param model the model from parsed pom file
     */
    private void setPropertiesFromModel(PomInformation pomInformation, Model model) {
        Properties propertiesFromModel = model.getProperties();

        if (model.getVersion() != null) {
            propertiesFromModel.put("project.version", model.getVersion());
        }

        if (model.getArtifactId() != null) {
            propertiesFromModel.put("project.artifactId", model.getArtifactId());
        }

        if (model.getGroupId() != null) {
            propertiesFromModel.put("project.groupId", model.getGroupId());
        }

        pomInformation.setProperties(propertiesFromModel);
    }

    public PomInformation getParentPomInformation() {
        return parentPomInformation;
    }

    public void setParentPomInformation(PomInformation parentPomInformation) {
        this.parentPomInformation = parentPomInformation;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<RemoteRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RemoteRepository> repositories) {
        this.repositories = repositories;
    }
}
