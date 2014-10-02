package org.motechproject.mds.service;

import org.motechproject.mds.query.QueryParams;

import java.util.List;
import java.util.Map;

/**
 * This service allows executing lookups on entities given
 * their classes or class names and lookup names as Strings. Allows generic access
 * to any entity in MDS. This is just a facade and all data access goes
 * through the underlying data service.
 */
public interface MDSLookupService {

    <T> T findOne(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams);
    <T> T findOne(String entityClassName, String lookupName, Map<String, ?> lookupParams);

    <T> List<T> findMany(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams);
    <T> List<T> findMany(String entityClassName, String lookupName, Map<String, ?> lookupParams);
    <T> List<T> findMany(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams,
                         QueryParams queryParams);
    <T> List<T> findMany(String entityClassName, String lookupName, Map<String, ?> lookupParams,
                         QueryParams queryParams);

    <T> List<T> retrieveAll(Class<T> entityClass);
    <T> List<T> retrieveAll(String entityClassName);
    <T> List<T> retrieveAll(Class<T> entityClass, QueryParams queryParams);
    <T> List<T> retrieveAll(String entityClassName, QueryParams queryParams);

    long count(Class entityClass, String lookupName, Map<String, ?> lookupParams);
    long count(String entityClassName, String lookupName, Map<String, ?> lookupParams);

    long countAll(Class entityClass);
    long countAll(String entityClassName);
}
