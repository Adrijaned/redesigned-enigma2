package cz.adrijaned.inqool.dao;

import com.google.i18n.phonenumbers.NumberParseException;
import cz.adrijaned.inqool.entities.User;
import cz.adrijaned.inqool.util._PhoneNumberUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public class UserDao extends AbstractDao<User> {

    protected UserDao() {
        super(User.class);
    }

    @PersistenceContext
    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public User save(User entity) {
        return super.save(entity);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) throws NumberParseException {
        String formatted = _PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        return entityManager.createQuery("select u from User u where u.phoneNumber = :phoneNumber", User.class)
                .setParameter("phoneNumber", formatted)
                .getResultStream().findAny();
    }

    @Override
    public List<User> list() {
        return entityManager.createQuery("select u from User u where u.valid = true", User.class).getResultList();
    }
}
