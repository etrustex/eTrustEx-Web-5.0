package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {


    /**
     * @return the file repository path
     */
    Path getFileUploadDir();

    Path getAbsolutePath(String relativePath);

    Path createFile(String relativePath);

    void appendChunkToFile(byte[] content, Path target);

    InputStreamResource loadFile(Path filePath);

    void delete(Path filePath);

    void deleteOrphanFiles() throws IOException;

    void deleteOrphanFiles(Path dir) throws IOException;

    void deleteOrphanFilesForGroup(Long groupId);

    void deleteOrphanFilesForGroup(Group group);
}
