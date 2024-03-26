package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class StorageServiceImplTest {
    @TempDir
    public Path fileUploadDir;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private GroupRepository groupRepository;
    private StorageService storageService;

    @BeforeEach
    public void setUp() {
        this.storageService = new StorageServiceImpl(attachmentRepository, groupRepository, fileUploadDir.toString());
    }

    @Test
    void deleteOrphanFiles() throws IOException {
        Path existingGroupFolder = Files.createDirectory(fileUploadDir.resolve(TEST_ENTITY_IDENTIFIER));
        Path nonExistingGroupFolder = Files.createDirectory(fileUploadDir.resolve("FOO_PARTY"));
        Path tempFile = Files.createTempFile(nonExistingGroupFolder, "just_to_ensure_non_empty_folder", ".is_deleted");

        Path existingMessageFolder = Files.createDirectory(existingGroupFolder.resolve("1"));

        Path nonExistingMessageFolder = Files.createDirectory(existingGroupFolder.resolve("2"));

        Path startingWithDotFolder = Files.createDirectory(fileUploadDir.resolve(".snapshot"));
        Files.createFile(startingWithDotFolder.resolve("aFile"));

        Path uuidMessageFolder = Files.createDirectory(existingGroupFolder.resolve("123e4567-e89b-12d3-a456-556642440000"));

        Path existingAttachmentFile = Files.createFile(existingMessageFolder.resolve("1"));

        Path uuidAttachmentFile = Files.createFile(existingMessageFolder.resolve("123e4567-e89b-12d3-a456-556642440000"));

        Path nonExistingAttachmentFile = Files.createFile(existingMessageFolder.resolve("2"));

        given(attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(tempFile).toString())).willReturn(false);
        given(attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(existingAttachmentFile).toString())).willReturn(true);
        given(attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(uuidAttachmentFile).toString())).willReturn(false);
        given(attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(nonExistingAttachmentFile).toString())).willReturn(false);

        storageService.deleteOrphanFiles();

        assertTrue(Files.exists(existingGroupFolder));
        assertFalse(Files.exists(nonExistingGroupFolder));

        assertTrue(Files.exists(existingMessageFolder));
        assertFalse(Files.exists(nonExistingMessageFolder));
        assertFalse(Files.exists(uuidMessageFolder));

        assertTrue(Files.exists(existingAttachmentFile));
        assertFalse(Files.exists(nonExistingAttachmentFile));
        assertFalse(Files.exists(uuidAttachmentFile));

        assertTrue(Files.exists(startingWithDotFolder));
    }

    @Test
    void should_get_path() {
        assertEquals(fileUploadDir, storageService.getAbsolutePath(fileUploadDir.toString()));
    }

    @Test
    void should_create_file() {
        assertTrue(storageService.createFile("foo").toFile().exists());
    }

    @Test
    void should_throw_on_create_file_if_exists() {
        assertThrows(AssertionError.class, () -> storageService.createFile(".."));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void should_throw_on_create_file_if_IOE() {
        assertThrows(EtxWebException.class, () -> storageService.createFile("/foo/bar"));
    }


    @Test
    void should_throw_if_cannot_append_and_checksum() {
        Path target = Paths.get(fileUploadDir.toString(), "..");

        byte[] fooBytes = "foo".getBytes();
        assertThrows(EtxWebException.class, () -> storageService.appendChunkToFile(fooBytes, target));
    }


    @Test
    void should_load_file() throws IOException {
        Path filePath = Files.createTempFile(fileUploadDir, "foo", ".bar");

        InputStreamResource inputStreamResource = storageService.loadFile(filePath);

        assertTrue(inputStreamResource.exists());
    }

    @Test
    void should_throw_if_file_does_not_exist_for_load_file() {
        Path fooPath = Paths.get("foo");
        assertThrows(EtxWebException.class, () -> storageService.loadFile(fooPath));
    }

    @Test
    void should_throw_if_file_does_not_exist_for_delete() {
        Path parentPath = Paths.get("..");
        assertThrows(EtxWebException.class, () -> storageService.delete(parentPath));
    }

    @Test
    void should_delete_orphan_files_for_entity() throws IOException {
        Group entity = mockGroup(GroupType.ENTITY, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME);

        Path existingBusinessFolder = Files.createDirectory(fileUploadDir.resolve(entity.getBusinessIdentifier()));
        Path existingGroupFolder = Files.createDirectory(existingBusinessFolder.resolve(entity.getIdentifier()));
        Path existingMessageFolder = Files.createDirectory(existingGroupFolder.resolve("1"));
        Path existingAttachmentFile = Files.createFile(existingMessageFolder.resolve("1"));

        given(attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(existingAttachmentFile).toString())).willReturn(false);

        storageService.deleteOrphanFilesForGroup(entity);

        assertFalse(Files.exists(existingGroupFolder));
        assertTrue(Files.exists(existingBusinessFolder));
    }

    @Test
    void should_delete_orphan_files_for_business() throws IOException {
        Group business = mockGroup(GroupType.BUSINESS, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME);

        Path existingBusinessFolder = Files.createDirectory(fileUploadDir.resolve(business.getBusinessIdentifier()));
        Path existingGroupFolder = Files.createDirectory(existingBusinessFolder.resolve("RANDOM_ENTITY"));
        Path existingMessageFolder = Files.createDirectory(existingGroupFolder.resolve("1"));
        Path existingAttachmentFile = Files.createFile(existingMessageFolder.resolve("1"));

        given(attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(existingAttachmentFile).toString())).willReturn(false);

        storageService.deleteOrphanFilesForGroup(business);

        assertFalse(Files.exists(existingGroupFolder));
        assertFalse(Files.exists(existingBusinessFolder));
    }

    @Test
    void should_throw_delete_orphan_files_for_root() {
        Group root = mockGroup(GroupType.ROOT, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME);
        assertThrows(IllegalStateException.class, () -> storageService.deleteOrphanFilesForGroup(root));
    }

    @Test
    void should_throw_delete_orphan_files_if_IOE_thrown() throws IOException {
        Group entity = mockGroup(GroupType.ENTITY, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME);
        assertThrows(EtxWebException.class, () -> storageService.deleteOrphanFilesForGroup(entity));
    }
}
