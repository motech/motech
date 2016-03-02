package org.motechproject.mds.service;

import org.motechproject.mds.query.QueryParams;

import java.util.List;
import java.util.Map;

/**
 * This service allows executing lookups on entities given
 * their classes or class names and lookup names as Strings. Allows generic access
 * to any entity in MDS. This is just a facade and all data access goes
 * through the underlying data service. EUDE can be identified either by their fully qualified class name
 * (eg: "org.motechproject.mds.entity.Patient") or by their entity name (eg: "Patient")
 */
public interface MDSLookupService {

    /**
     * Retrieves and executes single-return lookup for the given entity class,
     * lookup name and parameters. It will fail, if lookup parameters do not match the
     * parameters specified in the lookup or if the lookup of given name does not exist for
     * the retrieved entity. It will also throw {@link org.motechproject.mds.exception.lookup.SingleResultFromLookupExpectedException}
     * in case lookup returns a collection of instances, rather than single instance.
     *
     * @param entityClass entity class
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @param <T> entity class
     * @return Single instance, retrieved using given lookup criteria
     */
    <T> T findOne(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams);

    /**
     * Retrieves and executes single-return lookup for the given entity class name,
     * lookup name and parameters. It will fail, if lookup parameters do not match the
     * parameters specified in the lookup or if the lookup of given name does not exist for
     * the retrieved entity. It will also throw {@link org.motechproject.mds.exception.lookup.SingleResultFromLookupExpectedException}
     * in case lookup returns a collection of instances, rather than single instance.
     *
     * @param entityClassName entity class name
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @param <T> entity class
     * @return Single instance, retrieved using given lookup criteria
     */
    <T> T findOne(String entityClassName, String lookupName, Map<String, ?> lookupParams);

    /**
     * Retrieves and executes multi-return lookup for the given entity class,
     * lookup name and parameters. It will fail, if lookup parameters do not match the
     * parameters specified in the lookup or if the lookup of given name does not exist for
     * the retrieved entity.
     *
     * @param entityClass entity class
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @param <T> entity class
     * @return collection of instances, retrieved using given lookup criteria
     */
    <T> List<T> findMany(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams);

    /**
     * Retrieves and executes multi-return lookup for the given entity class name,
     * lookup name and parameters. It will fail, if lookup parameters do not match the
     * parameters specified in the lookup or if the lookup of given name does not exist for
     * the retrieved entity.
     *
     * @param entityClassName entity class name
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @param <T> entity class
     * @return collection of instances, retrieved using given lookup criteria
     */
    <T> List<T> findMany(String entityClassName, String lookupName, Map<String, ?> lookupParams);

    /**
     * Retrieves and executes multi-return lookup for the given entity class,
     * lookup name and parameters. It will fail, if lookup parameters do not match the
     * parameters specified in the lookup or if the lookup of given name does not exist for
     * the retrieved entity. This version additionally allows to use query parameters,
     * to adjust retrieved instances (eg. limit their number).
     *
     * @param entityClass entity class
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @param queryParams parameters to use, retrieving the instances
     * @param <T> entity class
     * @return collection of instances, retrieved using given lookup criteria
     */
    <T> List<T> findMany(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams,
                         QueryParams queryParams);

    /**
     * Retrieves and executes multi-return lookup for the given entity class name,
     * lookup name and parameters. It will fail, if lookup parameters do not match the
     * parameters specified in the lookup or if the lookup of given name does not exist for
     * the retrieved entity. This version additionally allows to use query parameters,
     * to adjust retrieved instances (eg. limit their number).
     *
     * @param entityClassName entity class name
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @param queryParams parameters to use, retrieving the instances
     * @param <T> entity class
     * @return collection of instances, retrieved using given lookup criteria
     */
    <T> List<T> findMany(String entityClassName, String lookupName, Map<String, ?> lookupParams,
                         QueryParams queryParams);

    /**
     * Retrieves all instances for the given entity class.
     *
     * @param entityClass entity class
     * @param <T> entity class
     * @return a list of all instances for the given entity
     */
    <T> List<T> retrieveAll(Class<T> entityClass);

    /**
     * Retrieves all instances for the given entity class name.
     *
     * @param entityClassName entity class name
     * @param <T> entity class
     * @return a list of all instances for the given entity
     */
    <T> List<T> retrieveAll(String entityClassName);

    /**
     * Retrieves all instances for the given entity class name. This version additionally
     * allows to use query parameters, to adjust retrieved instances (eg. limit their number).
     *
     * @param entityClass entity class
     * @param <T> entity class
     * @return a list of all instances for the given entity
     */
    <T> List<T> retrieveAll(Class<T> entityClass, QueryParams queryParams);

    /**
     * Retrieves all instances for the given entity class name. This version additionally
     * allows to use query parameters, to adjust retrieved instances (eg. limit their number).
     *
     * @param entityClassName entity class name
     * @param <T> entity class
     * @return a list of all instances for the given entity
     */
    <T> List<T> retrieveAll(String entityClassName, QueryParams queryParams);

    /**
     * Retrieves a total number of instances, that match the specified lookup parameters,
     * for the given lookup and entity. This will fail if specified lookup parameters do not match
     * the lookup definition or if the lookup of given name is not specified for the given entity.
     *
     * @param entityClass entity class
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @return number of instances
     */
    long count(Class entityClass, String lookupName, Map<String, ?> lookupParams);

    /**
     * Retrieves a total number of instances, that match the specified lookup parameters,
     * for the given lookup and entity class name. This will fail if specified lookup parameters do not match
     * the lookup definition or if the lookup of given name is not specified for the given entity.
     *
     * @param entityClassName entity class name
     * @param lookupName name of the lookup from entity
     * @param lookupParams parameters to use, when executing the lookup
     * @return number of instances
     */
    long count(String entityClassName, String lookupName, Map<String, ?> lookupParams);

    /**
     * Retrieves a total number of instances, for the given entity class.
     *
     * @param entityClass entity class
     * @return number of instances
     */
    long countAll(Class entityClass);

    /**
     * Retrieves a total number of instances, for the given entity class name.
     *
     * @param entityClassName entity class name
     * @return number of instances
     */
    long countAll(String entityClassName);
}
