/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.service.rest;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class RestClient {
    public boolean get(String url) {
        return get(url, createWebClientWithServerURLAndDefaultValues());
    }

    public boolean get(String url, WebClient webClient) {
        ResponseEntity<Void> result = webClient
                .get()
                .uri(URI.create(url))
                .retrieve()
                .toBodilessEntity()
                .block();

        return result != null && result.getStatusCode() == HttpStatus.OK;

    }

    public <T> boolean put(String url, T body) throws URISyntaxException {
        return put(url, body, createWebClientWithServerURLAndDefaultValues());
    }

    public <T> boolean put(String url, T body, WebClient webClient) throws URISyntaxException {
        ResponseEntity<Void> result = webClient.put()
                .uri(new URI(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .toBodilessEntity()
                .onErrorComplete()
                .block();

        return result != null && result.getStatusCode() == HttpStatus.OK;
    }

    /* Handle SSL
     * https://stackoverflow.com/questions/45418523/spring-5-webclient-using-ssl
     */
    private WebClient createWebClientWithServerURLAndDefaultValues() {
        try {
            final SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();


            HttpClient httpClient = HttpClient
                    .create()
                    .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

            return WebClient.builder()
                    .clientConnector(connector)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        } catch (SSLException e) {
            throw new EtxWebException(e);
        }
    }
}
