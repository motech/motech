package org.motechproject.mds;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.domain.ClassMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.List;

/**
 * The <code>PersistanceClassLoader</code> class is a wrapper for {@link ClassLoader} and its main
 * purpose is to save and get classes definition from mds database.
 */
@Component
public class PersistanceClassLoader extends ClassLoader {
    private PersistenceManagerFactory persistenceManagerFactory;

    public PersistanceClassLoader() {
        super(PersistanceClassLoader.class.getClassLoader());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // load from parent
        Class<?> clazz = findLoadedClass(name);

        if (null == clazz) {
            try {
                clazz = findSystemClass(name);
            } catch (ClassNotFoundException e) {
                // the class could not be found in system
            }

            if (null == clazz) {
                Query query = getPersistenceManager().newQuery(ClassMapping.class);
                query.setFilter("className == name");
                query.declareParameters("java.lang.String name");
                query.setUnique(true);

                ClassMapping mapping = (ClassMapping) query.execute(name);

                if (null == mapping) {
                    throw new ClassNotFoundException(name);
                }

                clazz = defineClass(mapping);
                createEntityInfrastructure(clazz);
            }
        }

        return clazz;
    }

    public void saveClass(String className, byte[] codebyte) {
        ClassMapping mapping = new ClassMapping();
        mapping.setClassName(className);
        mapping.setBytecode(codebyte);

        getPersistenceManager().makePersistent(mapping);
    }

    public Class<?> defineClass(ClassMapping mapping) {
        return defineClass(mapping.getClassName(), mapping.getBytecode(), 0, mapping.getLength());
    }

    private void createEntityInfrastructure(Class<?> clazz) {
        List<ClassMapping> mappings = EntityInfrastructureBuilder.create(this, clazz);

        if (CollectionUtils.isNotEmpty(mappings)) {
            for (ClassMapping mapping : mappings) {
                defineClass(mapping);
            }
        }
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    protected PersistenceManager getPersistenceManager() {
        return null != persistenceManagerFactory
                ? persistenceManagerFactory.getPersistenceManager()
                : null;
    }

}
