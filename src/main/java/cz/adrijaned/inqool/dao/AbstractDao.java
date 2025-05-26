package cz.adrijaned.inqool.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;

import java.util.List;

public abstract class AbstractDao<T> {

    protected EntityManager entityManager;
    private final Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity){
        entityManager.persist(entity);
        return entity;
    }

    public void delete(T entity){
        entityManager.remove(entity);
    }

    public T find(long id){
        return entityManager.find(entityClass, id);
    }

    public List<T> list(){
        entityManager.getCriteriaBuilder().createQuery(entityClass);
        CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(entityClass);
        query.select(query.from(entityClass));
        return entityManager.createQuery(query).getResultList();
    }
}