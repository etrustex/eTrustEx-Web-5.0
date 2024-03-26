package eu.europa.ec.etrustex.web.common.validation;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class PatternsTest {

    private final String[] validAddresses = new String[] {
            "simple@example.com",
            "very.common@example.com",
            "disposable.style.email.with+symbol@example.com",
            "other.email-with-hyphen@example.com",
            "fully-qualified-domain@example.com",
            "user.name+tag+sorting@example.com",
            "x@example.com",
            "example-indeed@strange-example.com",
            "test/test@test.com",
            "-starting-with-dash@test.com",
            "with.a.'singlequoteand{}brackets@valid.com",
            // admin@mailserver1 (local domain name with no TLD, although ICANN highly discourages dotless email addresses[10])
            "example@s.example",
            "\" \"@example.org",
            "\"john..doe\"@example.org",
            "mailhost!username@example.org",
            "user%example.com@example.org",
            "user-@example.org"
    };
    
    private final String[] invalidAddresses = new String[] {
            "Abc.example.com",
            "A@b@c@example.com",
            "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",
            "just\"not\"right@example.com",
            "this is\"not\\allowed@example.com",
            "this\\ still\"not\\allowed@example.com",
            ".staring-with-dot@example.com",
            "consecutive..dots@example.com",
            // 1234567890123456789012345678901234567890123456789012345678901234+x@example.com (local-part is longer than 64 characters)
            "i_like_underscore@but_its_not_allowed_in_this_part.example.com",
            "QA[icon]CHOCOLATE[icon]@test.com"
    };

    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(Patterns.class.getDeclaredConstructor());
    }

    @Test
    void should_match_all_valid_addresses() {
        Pattern validEmail = Pattern.compile(Patterns.VALID_EMAIL);
        for(String email: validAddresses) {
            Matcher matcher = validEmail.matcher(email);
            assertTrue(matcher.matches());
        }
    }

    @Test
    void should_not_match_all_invalid_addresses() {
        Pattern validEmail = Pattern.compile(Patterns.VALID_EMAIL);
        for(String email: invalidAddresses) {
            Matcher matcher = validEmail.matcher(email);
            assertFalse(matcher.matches());
        }
    }
}
