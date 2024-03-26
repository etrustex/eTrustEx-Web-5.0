/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.chunkprocessor;

import eu.europa.ec.etrustex.web.util.crypto.Md;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"java:S106", "java:S6437", "java:S100"})
class ChunkProcessorTest {
    private static final int CHUNK_SIZE = 1024*24; // 24 KB
    private static final Path filesDirectory = Paths.get("src","test","resources", "files");
    private MessageDigest messageDigest;
    @TempDir
    private static Path tmpDir;


    @BeforeEach
    void setUp() {
        messageDigest = Md.getSha512MdInstance();
    }

    @SuppressWarnings({"java:S2095", "resource"})
    @Test
    void should_process_file_smaller_than_chunk_size() throws IOException {
        Files.list(filesDirectory)
                .filter(Files::isRegularFile)
                .forEach(this::processFile);

    }

    @Test
    void should_throw() throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream("foo".getBytes())) {
            ChunkProcessor chunkProcessor = new ChunkProcessor(
                    (chunk, bytesProcessed) -> {
                        throw new EtxWebException("Ex");
                    }, CHUNK_SIZE);

            assertThrows(EtxWebException.class, () -> chunkProcessor.process(inputStream));
        }
    }

    private void processFile(Path source) {
        try {
            byte[] originalBytes = Files.readAllBytes(source);
            String originalMD = DatatypeConverter.printHexBinary(messageDigest.digest(originalBytes)).toUpperCase();

            Path tmpFile = Files.createFile(tmpDir.resolve(source.getFileName() + ".tmp"));

            byte[] processedBytes = writeFileInChunksWithChunkProcessor(source, tmpFile);
            String processedMD = DatatypeConverter.printHexBinary(messageDigest.digest(processedBytes)).toUpperCase();

            assertEquals(originalMD, processedMD);
        } catch (IOException e) {
            throw new EtxWebException(e);
        }
    }

    @SuppressWarnings({"java:S1144"})
    private byte[] writeFileInChunksWithChunkProcessor(Path source, Path tmpFile) throws IOException {
        InputStream inputStream = Files.newInputStream(source);

        ChunkProcessor chunkProcessor = new ChunkProcessor(
                (chunk, bytesProcessed) -> appendToTempFile(tmpFile, chunk),
                CHUNK_SIZE);

        chunkProcessor.process(inputStream);

        return Files.readAllBytes(tmpFile);
    }

    private void appendToTempFile(Path tmpFile, byte[] chunk) {
        try {
            Files.write(tmpFile, chunk, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new EtxWebException("error writing temp file", e);
        }
    }
}