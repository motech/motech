package org.motechproject.mds.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class GenericRepository<T> {
    private SessionFactory sessionFactory;
    private Class<T> type;

    protected GenericRepository() {
    }

    public GenericRepository(SessionFactory sessionFactory, Class<T> type) {
        this.sessionFactory = sessionFactory;
        this.type = type;
    }

    public T save(T o) {
        int id = (Integer) getSession().save(o);
        return get(id);
    }

    public T get(int id) {
        return (T) getSession().get(type, id);
    }

    public List<T> list() {
        return (List<T>) getSession().createCriteria(type).list();
    }

    public void update( T o) {
        getSession().update(o);
    }

    public void delete(T o) {
        getSession().delete(o);
    }

    public List<T> findAll(String field, Object param) {
        return getSession().createCriteria(type).add(Restrictions.eq(field, param)).list();
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public T findBy(String propertyName, Object value) {
        return (T) this.sessionFactory.getCurrentSession().createCriteria(type).add(
                Restrictions.eq(propertyName, value)).uniqueResult();
    }
}
