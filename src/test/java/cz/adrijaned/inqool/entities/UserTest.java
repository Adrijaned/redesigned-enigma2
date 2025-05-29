package cz.adrijaned.inqool.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @Test
    public void testConstructor() {
        User u = new User("+420123456789");
        assertEquals("+420123456789", u.getPhoneNumber());
    }
}
