package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.common.features.FeatureDecision;
import eu.europa.ec.etrustex.web.common.features.FeaturesEnum;
import eu.europa.ec.etrustex.web.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.UVSCAN_TEST;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UVScanTestController {

    private final StorageService storageService;

    @FeatureDecision(FeaturesEnum.ETRUSTEX_7060)
    @PostMapping(UVSCAN_TEST)
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    public List<String> uvScanTest() throws EtxWebException {
        List<String> result = new ArrayList<>();
        Path targetDir = storageService.getFileUploadDir();

        log.debug("In UVScanTestController ");

        try (Stream<Path> files = Files.find(targetDir, 999, (p, bfa) -> bfa.isRegularFile())) {
            files
                    //  Uncomment to scan all. For the moment just one
                    //                .forEach(path -> result.add(scanFile(path)));
                    .findFirst()
                    .ifPresent(path -> result.add(scanFile(path)));
        } catch (IOException e) {
            throw new EtxWebException(String.format("Error reading files to scan from %s", targetDir), e);
        }

        log.debug("Returning UVScanTestController " + result);

        return result;
    }

    private String scanFile(Path path) {
        StringBuilder scanResult = new StringBuilder();
        String command = "/ec/software/bin/uvscan " + path.toString() /* + "--DAT=" + virusdb_dir */;
        Process process;
        Instant start = Instant.now();

        try {
            process = Runtime.getRuntime().exec(command);

            try (final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String processOutputLine;
                while ((processOutputLine = br.readLine()) != null) {
                    scanResult.append(processOutputLine).append(" - ");
                }

                try {
                    process.waitFor();

                    scanResult.append(process.exitValue()).append(" - ");

                    long elapsedTime = Duration.between(start, Instant.now()).toMillis();

                    scanResult.append(String.format("%n%n Scanned file %1$s. Size %2$s", path.getFileName(), Files.size(path)));
                    scanResult.append(String.format("%n Time %s ms.", elapsedTime));
                } finally {
                    process.destroy();
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new EtxWebException(String.format("Interrupted command %s on file %s", command, path.getFileName()));
        } catch (IOException e) {
            throw new EtxWebException(String.format("Exception reading file %s", path.getFileName()), e);
        }


        log.debug("uvscan finished: " + scanResult);
        return scanResult.toString();
    }
}
