package eu.europa.ec.etrustex.web.service.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FSUtilsTest {

    Path child = Paths.get("src/test/resources/parent/child");

    @BeforeEach
    public void setup() throws IOException {
        deleteParentAndChild();
    }

    @AfterEach
    public void cleanup() throws IOException {
        deleteParentAndChild();
    }

    @Test
    void testConstructorIsPrivate() throws NoSuchMethodException {
        testPrivateConstructor(FSUtils.class.getDeclaredConstructor());
    }

    @Test
    void should_create_parent_and_folder() throws IOException {
        FSUtils.createMissingParentFolders(child);

        assertTrue(Files.exists(child));
    }

    @Test
    void should_format_file_size() {

        assertEquals("900 bytes",FSUtils.format(900, 2));

        assertEquals("1 KB",FSUtils.format(1024, 2));

        assertEquals("1.50 KB",FSUtils.format(1536, 2));
    }

    private void deleteParentAndChild() throws IOException {
        Files.deleteIfExists(child);
        Path parent = child.getParent();
        Files.deleteIfExists(parent);
    }


}
