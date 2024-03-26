package eu.europa.ec.etrustex.web.service.signature;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


/*
  Test class to verify compliance to the jws standard for the signature done with json
  Usage:
 */
@ExtendWith(MockitoExtension.class)
class SignatureComplianceTest {
    private JWSVerifier verifier;
    private SignedJWT signedJWT;
    private String signature; // eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvZXVzZW5kd2ViLmVjLmV1cm9wYS5ldSIsInN1YiI6InBheWxvYWQgdG8gc2lnbiIsImV4cCI6MTY2MzE2NTA3OH0.ElZKl01HQP2wF9CI0Ivb87bAs--ml-wwGV2K7qvVVw8by4lopeij-0NLc4QS1D3O1fUY8CDVEkstBz3D_VZqVZQR-cXskng4t5DMqFfyaCelvDIj7vESApn6w8JCpxm246bjY9OwUY6EmqAHrW6FY2S6OTwwZmALQuX7TeR-y7BX8rga9QzwJRxu7KQENFTNVjTub6iDtbhM4N5p1fdd9FNtmsPIFPtBrtG4W-YOEtSAcASpBHuteIUDty3ie6tCj2elFIWwzMX4BBzxWhh3IbWoYItn2ywUX4sXPyqyvvj4LrR-YFc4poRscDftdbLdp50yyIlwv5TNNJQF6Hwxzw


    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, JOSEException, ParseException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(2048);

        KeyPair kp = keyGenerator.genKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        JWSSigner signer = new RSASSASigner(privateKey);

        String issuer = "https://etrustexweb.ec.europa.eu";
        String payloadToSign = "payload to sign";
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(payloadToSign)
                .issuer(issuer)
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(signer);

        signature = signedJWT.serialize();

        verifier = new RSASSAVerifier(publicKey);

        assertEquals(issuer, signedJWT.getJWTClaimsSet().getIssuer());
        assertEquals(payloadToSign, signedJWT.getJWTClaimsSet().getSubject());
        assertTrue(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
    }

    @Test
    void should_pass_validation() throws ParseException, JOSEException {
        signedJWT = SignedJWT.parse(signature);

        assertTrue(signedJWT.verify(verifier));
    }

    @Test
    void should_fail_validation() throws ParseException, JOSEException {
        String tampered = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJo.ElZKl01HQP2wF9CI0Ivb87bAs--ml-wwGV2K7qvVVw8by4lopeij-0NLc4QS1D3O1fUY8CDVEkstBz3D_VZqVZQR-cXskng4t5DMqFfyaCelvDIj7vESApn6w8JCpxm246bjY9OwUY6EmqAHrW6FY2S6OTwwZmALQuX7TeR-y7BX8rga9QzwJRxu7KQENFTNVjTub6iDtbhM4N5p1fdd9FNtmsPIFPtBrtG4W-YOEtSAcASpBHuteIUDty3ie6tCj2elFIWwzMX4BBzxWhh3IbWoYItn2ywUX4sXPyqyvvj4LrR-YFc4poRscDftdbLdp50yyIlwv5TNNJQF6Hwxzw";
        signedJWT = SignedJWT.parse(tampered);

        assertFalse(signedJWT.verify(verifier));
    }
}
