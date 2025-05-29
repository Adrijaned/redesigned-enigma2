package cz.adrijaned.inqool.util;

import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class _PhoneNumberUtilTest {
    @Test
    public void testNormalizePhoneNUmber() throws NumberParseException {
            assertEquals("+420605477976", _PhoneNumberUtil.normalizePhoneNumber("605477976"));
            assertEquals("+420605477976", _PhoneNumberUtil.normalizePhoneNumber("420 605 477 976"));
            assertThrows(NumberParseException.class, () -> _PhoneNumberUtil.normalizePhoneNumber("THISISATEST"));
            assertThrows(NumberParseException.class, () -> _PhoneNumberUtil.normalizePhoneNumber("123456789"));
    }
}
