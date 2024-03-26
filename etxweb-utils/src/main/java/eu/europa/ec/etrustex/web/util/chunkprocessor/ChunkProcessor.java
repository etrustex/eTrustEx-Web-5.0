/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.chunkprocessor;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@RequiredArgsConstructor
public class ChunkProcessor {
    private final ChunkProcessorFn chunkProcessorFn;
    private final int chunkSize;


    public long process(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[chunkSize];
        int bytesRead;
        long bytesProcessed = 0;

        try {
            while ((bytesRead = IOUtils.read(inputStream, buffer)) > 0) {
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                chunkProcessorFn.process(chunk, bytesProcessed);
                bytesProcessed += bytesRead;
            }

            return bytesProcessed;
        } catch (IOException e) {
            throw new EtxWebException("Cannot read chunk", e);
        } finally {
            inputStream.close();
        }
    }
}
