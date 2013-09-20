import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PackageCycles implements EnforcerRule {

    @Override
    public void execute(EnforcerRuleHelper enforcerRuleHelper) throws EnforcerRuleException {
        Log log = enforcerRuleHelper.getLog();

        try {
            MavenProject mavenProject = (MavenProject)enforcerRuleHelper.evaluate("${project}");
            File targetDir = new File((String)enforcerRuleHelper.evaluate("${project.build.directory}"));
            File classesDir = new File(targetDir, "classes");

            if (classesDir.exists()) {
                JDepend jDepend = new JDepend();
                jDepend.addDirectory(classesDir.getAbsolutePath());
                Collection<JavaPackage> packages = jDepend.analyze();

                if (jDepend.containsCycles()) {
                    for (JavaPackage javaPackage : packages) {
                        List<JavaPackage> cycle = new LinkedList<>();
                        if (javaPackage.collectCycle(cycle)) {
                            log.error("At least one cycle detected in package list originating from \"" + javaPackage.getName() + "\"");
                            for (JavaPackage p : cycle) {
                                log.error("    -> " + p.getName());
                            }
                        }
                    }

                    throw new EnforcerRuleException("Package cycles detected");
                }
            } else if (!classesDir.exists()) {
                log.warn("Skipping JDepend analisis. Target: " + classesDir.getAbsolutePath() + " does not exists");
            }
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Cannot evaluate expression. " + e.getMessage());
        } catch (IOException e) {
            throw new EnforcerRuleException("IO exception. " + e.getMessage());
        }
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule enforcerRule) {
        return false;
    }

    @Override
    public String getCacheId() {
        return null;
    }
}
