package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.service.util.FSUtils.createMissingParentFolders;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.BUSINESS;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;


@Service
@Slf4j
@Getter
public class StorageServiceImpl implements StorageService {
    private final AttachmentRepository attachmentRepository;
    private final GroupRepository groupRepository;

    private final Path fileUploadDir;

    private final FileVisitor<Path> deleteOrphansVisitor = new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult
        preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            if (dir.getFileName().toString().startsWith(".")) {
                log.debug("Skipping directory: " + dir.getFileName());
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            log.debug("Checking existence of: " + fileUploadDir.relativize(file));
            if (!file.toString().endsWith(".gitignore") && !attachmentRepository.existsByServerStorePath(fileUploadDir.relativize(file).toString())) {
                log.info("Delete orphan file: " + file);
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (isEmpty(dir)) {
                log.info("Delete empty directory: " + dir);
                FileUtils.deleteDirectory(dir.toFile());
            }
            return FileVisitResult.CONTINUE;
        }

        private boolean isEmpty(Path path) throws IOException {
            if (Files.isDirectory(path)) {
                try (Stream<Path> entries = Files.list(path)) {
                    return !entries.findFirst().isPresent();
                }
            }
            return false;
        }
    };

    public StorageServiceImpl(AttachmentRepository attachmentRepository, GroupRepository groupRepository, @Value("${etrustexweb.file-upload-dir}") String fileUploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.groupRepository = groupRepository;
        this.fileUploadDir = Paths.get(fileUploadDir);
    }

    @Override
    public Path getAbsolutePath(String relativePath) {
        return fileUploadDir.resolve(relativePath);
    }

    @Override
    public Path createFile(String relativePath) {
        log.info("About to create file " + relativePath);
        Path target = getAbsolutePath(relativePath);
        log.info("Resolved path " + target);

        try {
            assert (!target.toFile().exists());
            createMissingParentFolders(target.getParent());
            Files.createFile(target);
        } catch (Exception e) {
            log.error("Cannot create attachment path {} starting from relativePath {}!", target, relativePath);
            throw new EtxWebException("Cannot save attachment!", e);
        }

        log.info("File saved to " + target);

        return target;
    }

    @Override
    public void appendChunkToFile(byte[] content, Path target) {
        log.info("About to append chunk to file" + target);

        try {
            FileUtils.writeByteArrayToFile(target.toFile(), content, true);
        } catch (IOException e) {
            throw new EtxWebException("Cannot write chunk", e);
        }
    }

    @Override
    public InputStreamResource loadFile(Path filePath) {
        try {
            return new InputStreamResource(new BufferedInputStream(Files.newInputStream(filePath)));
        } catch (Exception e) {
            throw new EtxWebException("Error getting file inputStream", e);
        }
    }

    @Override
    public void delete(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new EtxWebException(String.format("Cannot delete file with path %s", filePath), e);
        }
    }

    @Override
    public void deleteOrphanFiles() throws IOException {
        Files.walkFileTree(fileUploadDir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, deleteOrphansVisitor);
    }

    @Override
    public void deleteOrphanFiles(Path dir) throws IOException {
        Files.walkFileTree(dir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, deleteOrphansVisitor);
    }

    @Override
    public void deleteOrphanFilesForGroup(Long groupId) {
        Group businessOrEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new EtxWebException(String.format("Cannot find group with id: %s", groupId)));

        deleteOrphanFilesForGroup(businessOrEntity);
    }

    @Override
    public void deleteOrphanFilesForGroup(Group group) {
        Path dir = getFileUploadDir();

        try {
            if (group.getType().equals(ENTITY)) {
                deleteOrphanFiles(dir.resolve(group.getParent().getIdentifier()).resolve(group.getIdentifier()));
            } else if (group.getType().equals(BUSINESS)) {
                deleteOrphanFiles(dir.resolve(group.getIdentifier()));
            } else {
                throw new IllegalStateException("Trying to delete orphan files for group type " + group.getType() + " not allowed");
            }
        } catch (IOException ioe) {
            throw new EtxWebException("Error trying to delete orphan files for group " + group.getIdentifier(), ioe);
        }
    }
}
