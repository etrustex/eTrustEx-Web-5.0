package eu.europa.ec.etrustex.web.service.pdf.signature;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.ContentResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.tsp.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Component
@Slf4j
public class TSAClient {
    private final MessageDigest digest;
    private final boolean useProxy;
    private URL url;
    private String proxyHost;
    private  Integer proxyPort;
    private  String proxyUser;
    private  String proxyPassword;

    private static final Random RANDOM = new SecureRandom();

    public TSAClient(EtrustexWebProperties etrustexWebProperties) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance("SHA-256");
        this.url = etrustexWebProperties.getTsaUrl();
        this.proxyHost = etrustexWebProperties.getProxyHost();
        this.proxyPort = etrustexWebProperties.getProxyPort();
        this.proxyUser = etrustexWebProperties.getProxyUser();
        this.proxyPassword = etrustexWebProperties.getProxyPassword();
        this.useProxy = StringUtils.isNotBlank(proxyHost) && proxyPort != null && StringUtils.isNotBlank(proxyUser) && StringUtils.isNotBlank(proxyPassword);
    }

    /**
     * @return the time stamp token
     * @throws IOException if there was an error with the connection or data from the TSA server,
     *                     or if the time stamp response could not be validated
     */
    public TimeStampToken getTimeStampToken(byte[] content) throws IOException {
        digest.reset();
        byte[] hash = digest.digest(content);

        int nonce = RANDOM.nextInt();

        TimeStampRequestGenerator tsaGenerator = new TimeStampRequestGenerator();
        tsaGenerator.setCertReq(true);
        ASN1ObjectIdentifier oid = getHashObjectIdentifier(digest.getAlgorithm());
        TimeStampRequest request = tsaGenerator.generate(oid, hash, BigInteger.valueOf(nonce));

        byte[] tsaResponse = getTSAResponse(request.getEncoded());

        TimeStampResponse response;
        try {
            response = new TimeStampResponse(tsaResponse);
            response.validate(request);
        } catch (TSPException e) {
            throw new EtxWebException(e);
        }

        TimeStampToken timeStampToken = response.getTimeStampToken();
        if (timeStampToken == null) {
            throw new EtxWebException("Response from " + url + " does not have a time stamp token, status: "
                    + response.getStatus() + " (" + response.getStatusString() + ")");
        }

        return timeStampToken;
    }
    /**
     * gets response data for the given encoded TimeStampRequest data
     * throws Exception if a connection to the TSA cannot be established
     *
     * OptionalGetWithoutIsPresent suppressed because it is already checked in constructor (useProxy)
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private byte[] getTSAResponse(byte[] request)  {

        try (CloseableHttpClient httpClient = useProxy
                ? HttpClients.custom().setRoutePlanner(proxyRoutePlanner()).build()
                : HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(url.toString());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/timestamp-query");

            ByteArrayEntity entity = new ByteArrayEntity(request, ContentType.APPLICATION_PDF);

            httpPost.setEntity(entity);
            HttpClientResponseHandler<Content> responseHandler = new ContentResponseHandler();
            final Content responseBody = httpClient.execute(httpPost, responseHandler);
            log.debug("Retrieved response from TSA.");

            return responseBody.asBytes();
        } catch (IOException e) {
            throw new EtxWebException(e);
        }

    }

    private DefaultProxyRoutePlanner proxyRoutePlanner() {
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);

        log.debug("Using proxy. Host: {}, Port: {}", proxyHost, proxyPort);

        return new DefaultProxyRoutePlanner(proxy);
    }

    /*
     * returns the ASN.1 OID of the given hash algorithm
     */
    private ASN1ObjectIdentifier getHashObjectIdentifier(String algorithm) {
        switch (algorithm) {
            case "MD2":
                return new ASN1ObjectIdentifier(PKCSObjectIdentifiers.md2.getId());
            case "MD5":
                return new ASN1ObjectIdentifier(PKCSObjectIdentifiers.md5.getId());
            case "SHA-1":
                return new ASN1ObjectIdentifier(OIWObjectIdentifiers.idSHA1.getId());
            case "SHA-224":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha224.getId());
            case "SHA-256":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha256.getId());
            case "SHA-384":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha384.getId());
            case "SHA-512":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha512.getId());
            default:
                return new ASN1ObjectIdentifier(algorithm);
        }
    }
}
