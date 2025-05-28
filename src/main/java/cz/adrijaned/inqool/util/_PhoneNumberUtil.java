package cz.adrijaned.inqool.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class _PhoneNumberUtil {
    public static String normalizePhoneNumber(String phoneNumber) throws NumberParseException {
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber parsed = numberUtil.parse(phoneNumber, "+420");
        if (!numberUtil.isValidNumber(parsed)) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "");
        }
        return numberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
