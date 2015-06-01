package org.motechproject.mds.service.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.config.MdsConfig;
import org.motechproject.mds.domain.MigrationMapping;
import org.motechproject.mds.repository.AllMigrationMappings;
import org.motechproject.mds.service.MigrationService;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class MigrationServiceTest {

    @InjectMocks
    MigrationService migrationService = new MigrationServiceImpl();

    @Mock
    Bundle bundle;

    @Mock
    MdsConfig mdsConfig;

    @Mock
    AllMigrationMappings allMigrationMappings;

    @Mock
    InputStream inputStream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void migrationFileNameRegexTest() {
        assertTrue("V1__MOTECH-1720.sql".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
        assertTrue("V113__Test1.sql".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
        assertTrue("V1__Test1.sql".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
        assertFalse("V17__sample.".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
        assertFalse("V02__T1.sql".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
        assertFalse("M10__Mig1.sql".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
        assertFalse("V103_Test1.sql".matches(Constants.EntitiesMigration.MIGRATION_FILE_NAME_PATTERN));
    }

    @Test
    public void shouldProcessBundleAndCopyMigrationFiles() throws IOException {
        String migrationsDirectory = System.getProperty("user.home") + Constants.EntitiesMigration.MIGRATION_DIRECTORY + "/mysql/";
        File migrationFile1 = new File(migrationsDirectory + "M10013__Test1.sql");
        File migrationFile2 = new File(migrationsDirectory + "M10014__Test2.sql");
        File migrationFile3 = new File(migrationsDirectory + "M10015__Test3.sql");
        deleteFiles(new File[] {migrationFile1, migrationFile2, migrationFile3});

        assertFalse(migrationFile1.exists());
        assertFalse(migrationFile2.exists());
        assertFalse(migrationFile3.exists());

        URL url = new ClassPathResource("migration/V1__Test1.sql").getURL();
        List<String> paths = new ArrayList<>();
        paths.add("db/migration/mysql/V1__Test1.sql");
        paths.add("db/migration/mysql/V2__Test2.sql");
        paths.add("db/migration/mysql/V3__Test3.sql");
        Enumeration<String> enumeration = new Vector<>(paths).elements();

        MigrationMapping migrationInfo1 = new MigrationMapping("testModule", 1);
        MigrationMapping migrationInfo2 = new MigrationMapping("testModule", 2);
        MigrationMapping migrationInfo3 = new MigrationMapping("testModule", 3);
        MigrationMapping newMigrationInfo1 = new MigrationMapping("testModule", 1);
        MigrationMapping newMigrationInfo2 = new MigrationMapping("testModule", 2);
        MigrationMapping newMigrationInfo3 = new MigrationMapping("testModule", 3);

        newMigrationInfo1.setFlywayMigrationVersion(13);
        newMigrationInfo2.setFlywayMigrationVersion(14);
        newMigrationInfo3.setFlywayMigrationVersion(15);

        when(mdsConfig.getFlywayLocations()).thenReturn(new String[]{"db/migration/mysql"});
        when(mdsConfig.getFlywayMigrationDirectory()).thenReturn(new File(migrationsDirectory));
        when(bundle.getEntryPaths(anyString())).thenReturn(enumeration);
        when(bundle.getSymbolicName()).thenReturn("testModule");
        when(bundle.getResource(anyString())).thenReturn(url);

        when(allMigrationMappings.retrieveByModuleAndMigrationVersion("testModule", 1)).thenReturn(null);
        when(allMigrationMappings.retrieveByModuleAndMigrationVersion("testModule", 2)).thenReturn(null);
        when(allMigrationMappings.retrieveByModuleAndMigrationVersion("testModule", 3)).thenReturn(null);

        when(allMigrationMappings.create(argThat(new MigrationMappingMatcher(migrationInfo1)))).thenReturn(newMigrationInfo1);
        when(allMigrationMappings.create(argThat(new MigrationMappingMatcher(migrationInfo2)))).thenReturn(newMigrationInfo2);
        when(allMigrationMappings.create(argThat(new MigrationMappingMatcher(migrationInfo3)))).thenReturn(newMigrationInfo3);

        migrationService.processBundle(bundle);

        assertTrue(migrationFile1.exists());
        assertTrue(migrationFile2.exists());
        assertTrue(migrationFile3.exists());
        verify(allMigrationMappings, times(3)).create(Matchers.<MigrationMapping>anyObject());

        when(allMigrationMappings.retrieveByModuleAndMigrationVersion("testModule", 1)).thenReturn(migrationInfo1);
        when(allMigrationMappings.retrieveByModuleAndMigrationVersion("testModule", 2)).thenReturn(migrationInfo2);
        when(allMigrationMappings.retrieveByModuleAndMigrationVersion("testModule", 3)).thenReturn(migrationInfo3);

        migrationService.processBundle(bundle);

        verify(allMigrationMappings, times(3)).create(Matchers.<MigrationMapping>anyObject());
        deleteFiles(new File[] {migrationFile1, migrationFile2, migrationFile3});
    }

    private void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private class MigrationMappingMatcher extends ArgumentMatcher<MigrationMapping> {

        private MigrationMapping migrationMapping;

        public MigrationMappingMatcher(MigrationMapping migrationMapping) {
            super();
            this.migrationMapping = migrationMapping;
        }

        @Override
        public boolean matches(Object o) {
            if (o == null) {
                return false;
            }
            MigrationMapping mapping = (MigrationMapping) o;
            if (!StringUtils.equals(mapping.getModuleName(), migrationMapping.getModuleName())) {
                return false;
            }
            if (!mapping.getModuleMigrationVersion().equals(migrationMapping.getModuleMigrationVersion())) {
                return false;
            }
            return true;
        }
    }

}
