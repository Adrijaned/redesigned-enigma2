package cz.adrijaned.inqool.dao;

import cz.adrijaned.inqool.entities.Court;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public class CourtDao extends AbstractDao<Court> {

    protected CourtDao() {
        super(Court.class);
    }

    @PersistenceContext
    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Court save(Court entity) {
        return super.save(entity);
    }

    @Override
    public List<Court> list() {
        return entityManager.createQuery("select c from Court c where c.valid = true", Court.class).getResultList();
    }
}
