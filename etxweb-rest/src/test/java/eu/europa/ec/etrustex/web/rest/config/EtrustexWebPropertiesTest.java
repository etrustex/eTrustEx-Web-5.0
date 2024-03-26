package eu.europa.ec.etrustex.web.rest.config;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = EtrustexWebProperties.class)
class EtrustexWebPropertiesTest {
    @Autowired
    EtrustexWebProperties etrustexWebProperties;

    private static final Path FILE_DIR_PATH = Paths.get(System.getProperty("java.io.tmpdir"), "fs-repo");


    @Test
    void should_return_file_upload_dir_path() {
        assertEquals(FILE_DIR_PATH, etrustexWebProperties.getFileUploadDirPath());
    }

    @Test
    void should_get_environment() {
        assertTrue(etrustexWebProperties.isDevEnvironment());
        assertFalse(etrustexWebProperties.isTestEnvironment());
        assertFalse(etrustexWebProperties.isAccEnvironment());
        assertFalse(etrustexWebProperties.isProdEnvironment());
    }
}
