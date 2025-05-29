package cz.adrijaned.inqool.dao;

import com.google.i18n.phonenumbers.NumberParseException;
import cz.adrijaned.inqool.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserDaoTest {
    @Autowired
    UserDao userDao;

    @Test
    public void testUserDao() throws NumberParseException {
        User u1 = new User("+420 605 477 976");
        u1.setName("Pepa");
        User u2 = new User("+420 605 477 977");
        u2.setName("Josef");
        User u3 = new User("+420 605 477 978");
        u3.setName("Pepík");
        u3.setValid(false);
        userDao.saveUser(u1);
        userDao.saveUser(u2);
        userDao.saveUser(u3);
        Optional<User> p1 = userDao.findByPhoneNumber("605477977");
        Optional<User> p2 = userDao.findByPhoneNumber("605477978");
        Optional<User> p3 = userDao.findByPhoneNumber("605477979");
        assertThrows(NumberParseException.class, () -> userDao.findByPhoneNumber("123456789"));
        assertTrue(p1.isPresent());
        assertTrue(p2.isPresent());
        assertFalse(p3.isPresent());
        assertEquals(u2, p1.get());
        assertEquals(u3, p2.get());
        List<User> users = userDao.list();
        assertEquals(2, users.size());
    }
}
