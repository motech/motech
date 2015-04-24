package org.motechproject.mdsmigration.java;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is an abstract class for java migrations.
 *
 * Flyway-core does not see java migration files inside motech-platform-dataservices, because of that
 * we need this class here to invoke by reflections the real implementation of migrations.
 */
public abstract class AbstractMDSMigration implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws ClassNotFoundException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Class<?> clazz = Class.forName(getMigrationImplClassName());
        Object instance = clazz.newInstance();
        Method method = clazz.getMethod("migrate", JdbcTemplate.class);
        method.invoke(instance, jdbcTemplate);
    }

    /**
     * Returns the class name of migration.
     *
     * @return the class name of migration
     */
    public abstract String getMigrationImplClassName();
}
