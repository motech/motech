package org.motechproject.mds.service.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.config.MdsConfig;
import org.motechproject.mds.domain.MigrationMapping;
import org.motechproject.mds.repository.AllMigrationMappings;
import org.motechproject.mds.service.MigrationService;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Default implementation of {@link org.motechproject.mds.service.MigrationService} interface.
 */
@Service
public class MigrationServiceImpl implements MigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationServiceImpl.class);

    @Autowired
    private AllMigrationMappings allMigrationMappings;

    @Autowired
    private MdsConfig mdsConfig;

    @Override
    public void processBundle(Bundle bundle) throws IOException {
        LOGGER.debug("Starting to process {} bundle", bundle.getSymbolicName());
        String flywayLocation = mdsConfig.getFlywayLocations();

        if (bundle.getEntryPaths(flywayLocation) == null) {
            return;
        }

        Collection<String> migrationFiles = Collections2.filter(
                Collections.list(bundle.getEntryPaths(flywayLocation)),
                new Predicate<String>() {
                    @Override
                    public boolean apply(String o) {
                        return o.substring(o.lastIndexOf('/') + 1).matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN);
                    }
                }
        );

        if (CollectionUtils.isNotEmpty(migrationFiles)) {
            LOGGER.debug("Bundle {} contains {} migrations files", bundle.getSymbolicName(), migrationFiles.size());
            File migrationDirectory = mdsConfig.getFlywayMigrationDirectory();

            //numerically order the migration files before executing them
            List<String> migrationFls = new ArrayList(migrationFiles);
            Collections.sort(migrationFls, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return getMigrationsVersion(s1).compareTo(getMigrationsVersion(s2));
                }
            });

            for (String resourcePath : migrationFls) {
                Integer migrationVersion = getMigrationsVersion(resourcePath);
                MigrationMapping migrationInfo = allMigrationMappings.retrieveByModuleAndMigrationVersion(bundle.getSymbolicName(),
                        migrationVersion);

                //we must copy migration file
                if (migrationInfo == null) {
                    migrationInfo = new MigrationMapping(bundle.getSymbolicName(), migrationVersion);
                    migrationInfo = allMigrationMappings.create(migrationInfo);
                    String orginalFileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
                    String newFileName = generateFlywayMigrationFileName(migrationInfo, orginalFileName);

                    try (InputStream inputStream = bundle.getResource(resourcePath).openStream()) {
                        LOGGER.debug("Creating new migration file with name {}, for {} bundle", newFileName, bundle.getSymbolicName());
                        File migrationFile = new File(migrationDirectory.getAbsolutePath(), newFileName);
                        Files.createDirectories(migrationDirectory.toPath());
                        Files.copy(inputStream, migrationFile.toPath());
                    }
                }
            }
        }
    }

    private String generateFlywayMigrationFileName(MigrationMapping migrationMapping, String orginalFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.EntitiesMigration.ENTITY_MIGRATIONS_PREFIX).append(migrationMapping.getFlywayMigrationVersion()
                + Constants.EntitiesMigration.MIGRATION_VERSION_OFFSET);
        stringBuilder.append("__");
        stringBuilder.append(orginalFileName.substring(orginalFileName.indexOf('_') + 2));
        return stringBuilder.toString();
    }

    private Integer getMigrationsVersion(String resourcePath) {
        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        return Integer.valueOf(fileName.substring(1, fileName.indexOf('_')));
    }

}
