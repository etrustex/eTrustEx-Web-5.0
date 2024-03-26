package eu.europa.ec.etrustex.web.exchange.util;

import eu.europa.ec.etrustex.web.util.UTFStringsUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static eu.europa.ec.etrustex.web.util.UTFStringsUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UTFStringsUtilsTest {

    String oneByteChar = "a";
    String endWithNoSpaces = "αβγδεζηθικλμνξοπρςτυφχψω";
    String twoBytesChar = "α";
    String threeBytesChar = "€";
    String fourBytesChar = "𩸽";
    String reverserStripChars = new StringBuffer(STRIP_CHARS).reverse().toString();

    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(UTFStringsUtils.class.getDeclaredConstructor());
    }

    @Test
    void check_lengths() {
        assertThat(oneByteChar).hasSize(1);
        assertThat(twoBytesChar).hasSize(1);
        assertThat(threeBytesChar).hasSize(1);
        assertThat(fourBytesChar).hasSize(2); // the string length returns the number of chars required to save the string
        assertThat(ELLIPSIS.getBytes(StandardCharsets.UTF_8)).hasSize(3);

        assertThat(getByteLength(oneByteChar)).isOne();
        assertThat(getByteLength(twoBytesChar)).isEqualTo(2);
        assertThat(getByteLength(threeBytesChar)).isEqualTo(3);
        assertThat(getByteLength(fourBytesChar)).isEqualTo(4);

    }

    @Test
    void should_remove_spaces_and_punctuation() {
        String expected = "aString";
        String noise = "    " + reverserStripChars + " ;  ";
        String s = expected + noise + fourBytesChar;
        int byteLength = s.getBytes(StandardCharsets.UTF_8).length;

        assertThat(abbreviateToByteLength(s, byteLength - 1)).isEqualTo(expected + ELLIPSIS);

        s = expected + noise + threeBytesChar;
        byteLength = s.getBytes(StandardCharsets.UTF_8).length;
        assertThat(abbreviateToByteLength(s, byteLength - 1)).isEqualTo(expected + ELLIPSIS);

        s = expected + noise + twoBytesChar;
        byteLength = s.getBytes(StandardCharsets.UTF_8).length;
        assertThat(abbreviateToByteLength(s, byteLength - 1)).isEqualTo(expected + ELLIPSIS);

        s = expected + noise + oneByteChar;
        byteLength = s.getBytes(StandardCharsets.UTF_8).length;
        assertThat(abbreviateToByteLength(s, byteLength - 1)).isEqualTo(expected + ELLIPSIS);
    }

    @Test
    void should_keep_a_short_string_unaltered() {
        assertThat(abbreviateToByteLength(endWithNoSpaces, getByteLength(endWithNoSpaces))).isEqualTo(endWithNoSpaces);
    }

    @Test
    void should_remove_characters() {
        int length = endWithNoSpaces.length();
        int byteLength = getByteLength(endWithNoSpaces);
        assertThat(byteLength).isEqualTo(2 * length);

        String abbreviated_1 = abbreviateToByteLength(endWithNoSpaces, byteLength - 1);
        String abbreviated_2 = abbreviateToByteLength(endWithNoSpaces, byteLength - 2);
        String abbreviated_3 = abbreviateToByteLength(endWithNoSpaces, byteLength - 3);
        String abbreviated_4 = abbreviateToByteLength(endWithNoSpaces + fourBytesChar, byteLength + 3);

        assertThat(abbreviated_1).isEqualTo(endWithNoSpaces.substring(0, endWithNoSpaces.length() - 2) + ELLIPSIS);
        assertThat(abbreviated_2).isEqualTo(endWithNoSpaces.substring(0, endWithNoSpaces.length() - 3) + ELLIPSIS);
        assertThat(abbreviated_3).isEqualTo(endWithNoSpaces.substring(0, endWithNoSpaces.length() - 3) + ELLIPSIS);
        assertThat(abbreviated_4).isEqualTo(endWithNoSpaces + ELLIPSIS);
    }
}
