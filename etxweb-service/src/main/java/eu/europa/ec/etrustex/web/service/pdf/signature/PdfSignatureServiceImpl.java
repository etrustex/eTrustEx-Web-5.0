package eu.europa.ec.etrustex.web.service.pdf.signature;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;

@Component
public class PdfSignatureServiceImpl implements PdfSignatureService, SignatureInterface {
    @Setter
    @Getter
    private PrivateKey privateKey;
    @Setter
    @Getter
    private Certificate[] certificateChain;
    private final ValidationTimeStamp validationTimeStamp;

    private final boolean useTimeStampValidation;


    public PdfSignatureServiceImpl(EncryptionService encryptionService, EtrustexWebProperties etrustexWebProperties, ValidationTimeStamp validationTimeStamp) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
        this.validationTimeStamp = validationTimeStamp;
        this.useTimeStampValidation = etrustexWebProperties.isPdfSignatureValidateTimestamp();

        KeyStore keystore = encryptionService.getServerKeyStore();
        char[] pin = etrustexWebProperties.getServerCertificatePassword().toCharArray();

        Enumeration<String> aliases = keystore.aliases();
        String alias;
        Certificate cert = null;
        while (cert == null && aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            setPrivateKey((PrivateKey) keystore.getKey(alias, pin));
            Certificate[] certChain = keystore.getCertificateChain(alias);
            if (certChain != null) {
                setCertificateChain(certChain);
                cert = certChain[0];
                if (cert instanceof X509Certificate) {
                    ((X509Certificate) cert).checkValidity();

                    SigUtils.checkCertificateUsage((X509Certificate) cert);
                }
            }
        }

        if (cert == null) {
            throw new IOException("Could not find certificate");
        }
    }

    @Override
    public InputStream sign(ByteArrayOutputStream byteArrayOutputStream) {
        PDDocument doc = null;
        try (ByteArrayOutputStream signedByteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] contentInBytes = byteArrayOutputStream.toByteArray();
            doc = PDDocument.load(contentInBytes);

            signDetached(doc, signedByteArrayOutputStream);

            return new ByteArrayInputStream(signedByteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new EtxWebException(e);
        } finally {
            IOUtils.closeQuietly(doc);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }

    /**
     * SignatureInterface sample implementation.
     * <p>
     * This method will be called from inside of the pdfbox and create the PKCS #7 signature.
     * The given InputStream contains the bytes that are given by the byte range.
     * <p>
     * This method is for internal use only.
     * <p>
     * Use your favorite cryptographic library to implement PKCS #7 signature creation.
     * If you want to create the hash and the signature separately (e.g. to transfer only the hash
     * to an external application), read <a href="https://stackoverflow.com/questions/41767351">this
     * answer</a> or <a href="https://stackoverflow.com/questions/56867465">this answer</a>.
     *
     */
    @Override
    public byte[] sign(InputStream content) {
        try {
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            X509Certificate cert = (X509Certificate) certificateChain[0];
            ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cert));
            gen.addCertificates(new JcaCertStore(Arrays.asList(certificateChain)));
            CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
            CMSSignedData signedData = gen.generate(msg, false);

            if (useTimeStampValidation) {
                validationTimeStamp.addSignedTimeStamp(signedData);
            }

            return signedData.getEncoded();
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }


    private void signDetached(PDDocument document, OutputStream output) throws IOException {
        int accessPermissions = SigUtils.getMDPPermission(document);
        if (accessPermissions == 1) {
            throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
        }

        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setReason("eTrustEx Web. Transmission receipt");
        signature.setSignDate(Calendar.getInstance());

        if (accessPermissions == 0) {
            SigUtils.setMDPPermission(document, signature, 2);
        }

        SignatureOptions signatureOptions = new SignatureOptions();
        signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);
        document.addSignature(signature, this, signatureOptions);
        document.saveIncremental(output);
    }

}

