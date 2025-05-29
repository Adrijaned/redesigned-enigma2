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
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public User find(Long id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public User saveUser(User entity) throws NumberParseException {
        System.out.println(entity.toString());
        entity.setPhoneNumber(_PhoneNumberUtil.normalizePhoneNumber(entity.getPhoneNumber()));
        entityManager.persist(entity);
        return entity;
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) throws NumberParseException {
        String formatted = _PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        return entityManager.createQuery("select u from Users u where u.phoneNumber = :phoneNumber", User.class)
                .setParameter("phoneNumber", formatted)
                .getResultList().stream().findAny();
    }

    public List<User> list() {
        return entityManager.createQuery("select u from Users u where u.valid = true", User.class).getResultList();
    }
}
