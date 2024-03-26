package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;



@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class ConfidentialityAndPublicKeyValidatorTest {
    ConfidentialityAndPublicKeyValidator confidentialityAndPublicKeyValidator = new ConfidentialityAndPublicKeyValidator();

    String validPublicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl\n" +
            "0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmF\n" +
            "YWz/3F80fK8ujgoU10+OtuT0XpS3NE4EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNj\n" +
            "w3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pT\n" +
            "Y9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9\n" +
            "QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdw\n" +
            "PQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";
    String invalidPublicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl\n" +
            "0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmF\n" +
            "YWz/3F80fK8ujgoU10+OtuT0XpS3EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNj\n" +
            "w3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pT\n" +
            "Y9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9\n" +
            "QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdw\n" +
            "PQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";

    @Test
    void should_pass_validation_with_public_confidentiality_and_empty_or_null_public_key() {
        RecipientPreferencesSpec nullPubKeyRecipientPreferencesSpec = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.PUBLIC)
                .build();

        RecipientPreferencesSpec emptyPubKeyRecipientPreferencesSpec = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.PUBLIC)
                .publicKey("   ")
                .build();

        assertTrue(confidentialityAndPublicKeyValidator.isValid(nullPubKeyRecipientPreferencesSpec, null));
        assertTrue(confidentialityAndPublicKeyValidator.isValid(emptyPubKeyRecipientPreferencesSpec, null));
    }

    @Test
    void should_pass_with_valid_public_key() {
        RecipientPreferencesSpec publicWithValidKey = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.PUBLIC)
                .publicKey(validPublicKey)
                .build();

        RecipientPreferencesSpec limitedHigWithValidPubKey = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.LIMITED_HIGH)
                .publicKey(validPublicKey)
                .build();

        assertTrue(confidentialityAndPublicKeyValidator.isValid(publicWithValidKey, null));
        assertTrue(confidentialityAndPublicKeyValidator.isValid(limitedHigWithValidPubKey, null));
    }

    @Test
    void should_fail_with_blank_public_key() {
        RecipientPreferencesSpec limitedHigWithInvalidPubKey = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.LIMITED_HIGH)
                .publicKey("")
                .build();

        assertFalse(confidentialityAndPublicKeyValidator.isValid(limitedHigWithInvalidPubKey, null));
    }

    @Test
    void should_fail_with_invalid_public_key() {
        RecipientPreferencesSpec publicWithInvalidKey = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.PUBLIC)
                .publicKey(invalidPublicKey)
                .build();

        RecipientPreferencesSpec limitedHigWithInvalidPubKey = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.LIMITED_HIGH)
                .publicKey(invalidPublicKey)
                .build();

        assertFalse(confidentialityAndPublicKeyValidator.isValid(publicWithInvalidKey, null));
        assertFalse(confidentialityAndPublicKeyValidator.isValid(limitedHigWithInvalidPubKey, null));
    }
}
