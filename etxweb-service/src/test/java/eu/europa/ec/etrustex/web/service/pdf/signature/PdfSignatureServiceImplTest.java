/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.service.pdf.signature;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@TestPropertySource("classpath:application-test.properties")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100", "java:S6437", "java:S2068"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive & password, is compromised. */
class PdfSignatureServiceImplTest {
    private static final String SERVER_CERTIFICATE_PATH = "server-cert.p12";
    private static final String SERVER_CERTIFICATE_TYPE = "PKCS12";

    private static final String SERVER_CERTIFICATE_PASSWORD = "test123";
    @TempDir
    File temporaryFolder;
    @Value("${etrustexweb.pdf-signature-validate-timestamp}")
    private boolean isPdfSignatureValidateTimestamp;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private EtrustexWebProperties etrustexWebProperties;
    @Mock
    private ValidationTimeStamp validationTimeStamp;
    private PdfSignatureService pdfSignatureService;

    @BeforeEach
    public void init() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
        KeyStore keystore = getKeyStore();
        given(etrustexWebProperties.isPdfSignatureValidateTimestamp()).willReturn(isPdfSignatureValidateTimestamp);
        given(encryptionService.getServerKeyStore()).willReturn(keystore);
        given(etrustexWebProperties.getServerCertificatePassword()).willReturn(SERVER_CERTIFICATE_PASSWORD);
        pdfSignatureService = new PdfSignatureServiceImpl(encryptionService, etrustexWebProperties, validationTimeStamp);
    }

    @Test
    void should_sign_byteArrayOutputStream() throws IOException, CertificateException, OperatorCreationException, CMSException {
        File pdf = new File("src/test/resources/pdf/receipt.pdf");

        try (PDDocument document = PDDocument.load(pdf);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            document.save(byteArrayOutputStream);

            InputStream inputStream = pdfSignatureService.sign(byteArrayOutputStream);
            File signedFile = new File(temporaryFolder, "signed.pdf");
            FileUtils.copyInputStreamToFile(inputStream, signedFile);

            assertTrue(validateSignature(signedFile));
        }
    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        Resource serverCertificatePath = new ClassPathResource(SERVER_CERTIFICATE_PATH);
        KeyStore keystore = KeyStore.getInstance(SERVER_CERTIFICATE_TYPE);
        keystore.load(serverCertificatePath.getInputStream(), SERVER_CERTIFICATE_PASSWORD.toCharArray());

        return keystore;
    }

    private boolean validateSignature(File signedFile) throws CMSException, IOException, CertificateException, OperatorCreationException {
        boolean isValidSignature = false;
        PDDocument signedPdf = PDDocument.load(signedFile);
        PDSignature signature = getLastRelevantSignature(signedPdf);
        assert signature != null;
        byte[] signatureContent = signature.getContents(Files.newInputStream(signedFile.toPath()));
        byte[] signedContent = signature.getSignedContent(Files.newInputStream(signedFile.toPath()));

        CMSProcessable cmsProcessableInputStream = new CMSProcessableByteArray(signedContent);
        CMSSignedData cmsSignedData = new CMSSignedData(cmsProcessableInputStream, signatureContent);
        SignerInformationStore signerInformationStore = cmsSignedData.getSignerInfos();
        Collection<SignerInformation> signers = signerInformationStore.getSigners();
        Store<X509CertificateHolder> certs = cmsSignedData.getCertificates();

        for (SignerInformation signerInformation : signers) {
            @SuppressWarnings("unchecked")
            Collection<X509CertificateHolder> certificates = certs.getMatches(signerInformation.getSID());
            Iterator<X509CertificateHolder> certIt = certificates.iterator();
            X509CertificateHolder signerCertificate = certIt.next();
            isValidSignature = signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().build(signerCertificate));
        }

        return isValidSignature;
    }

    private PDSignature getLastRelevantSignature(PDDocument document) throws IOException {
        Comparator<PDSignature> comparatorByOffset = Comparator.comparing(sig -> sig.getByteRange()[1]);

        Optional<PDSignature> optLastSignature = document.getSignatureDictionaries().stream().max(comparatorByOffset);
        if (optLastSignature.isPresent()) {
            PDSignature lastSignature = optLastSignature.get();
            COSBase type = lastSignature.getCOSObject().getItem(COSName.TYPE);
            if (type == null || COSName.SIG.equals(type) || COSName.DOC_TIME_STAMP.equals(type)) {
                return lastSignature;
            }
        }
        return null;
    }
}