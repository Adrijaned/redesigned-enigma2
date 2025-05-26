package cz.adrijaned.inqool.dao;

import cz.adrijaned.inqool.entities.SurfaceType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public class SurfaceTypeDao extends AbstractDao<SurfaceType> {

    protected SurfaceTypeDao() {
        super(SurfaceType.class);
    }

    @PersistenceContext
    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public SurfaceType save(SurfaceType entity) {
        return super.save(entity);
    }

    @Override
    public List<SurfaceType> list() {
        return entityManager.createQuery("select st from SurfaceType st where st.valid = true", SurfaceType.class).getResultList();
    }
}
