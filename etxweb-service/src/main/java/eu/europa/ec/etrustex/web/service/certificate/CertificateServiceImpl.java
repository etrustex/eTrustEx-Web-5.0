package eu.europa.ec.etrustex.web.service.certificate;

import eu.europa.ec.etrustex.web.exchange.model.CertificateUpdateDto;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.CertificateUpdateRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.CertificateUpdateRedirectRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.service.validation.model.UpdateCertificateSpec;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.crypto.Rsa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {
    private final UserService userService;
    private final GroupService groupService;
    private final RedirectService redirectService;
    private final MessageSummaryRepository messageSummaryRepository;
    private final EncryptionService encryptionService;
    private final CertificateUpdateRedirectRepository certificateUpdateRedirectRepository;
    @Override
    public String generateCertificateUpdateLink(UpdateCertificateSpec updateCertificateSpec) {
        User user = userService.findByEcasId(updateCertificateSpec.getEuLoginId());
        Group group = groupService.findById(updateCertificateSpec.getEntityId());
        return redirectService.createPermalink(CertificateUpdateRedirect.builder()
                .groupId(updateCertificateSpec.getEntityId())
                .groupIdentifier(group.getIdentifier())
                .userId(user.getId())
                .build()
        );
    }

    @Override
    @Transactional
    public void updateCompromisedMessages(CertificateUpdateDto certificateUpdateDto, Long userId) {
        log.info("Updating compromised certificate for messages matching " + certificateUpdateDto.getHashedPublicKey());
        List<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndAndPublicKeyHashValue(certificateUpdateDto.getRecipientEntityId(), certificateUpdateDto.getHashedPublicKey());
        log.info(messageSummaries.size() + " messages found");

        byte[] randomBitsB64 = encryptionService.decryptWithServerPrivateKey(certificateUpdateDto.getRandomBits());
        SecretKey key = new SecretKeySpec(randomBitsB64, "AES");
        String privateKey = Aes.gcmDecrypt(key, certificateUpdateDto.getIv(), certificateUpdateDto.getEncryptedPrivateKey());
        PrivateKey pk = Rsa.toPrivateKey(privateKey);

        messageSummaries.forEach(messageSummary -> {
            byte[] decryptedRandomBits = Rsa.decrypt(pk, messageSummary.getSymmetricKey().getRandomBits());
            String newEncryptedRandomBits = Rsa.encrypt(messageSummary.getRecipient().getRecipientPreferences().getPublicKey(), decryptedRandomBits);

            messageSummary.getSymmetricKey().setRandomBits(newEncryptedRandomBits);
            messageSummary.setPublicKeyHashValue(messageSummary.getRecipient().getRecipientPreferences().getPublicKeyHashValue());
            messageSummary.setValidPublicKey(true);

            messageSummaryRepository.save(messageSummary);
        });

        log.info("Finishing updating compromised certificate");
        certificateUpdateRedirectRepository.deleteByGroupIdAndAndUserId(certificateUpdateDto.getRecipientEntityId(), userId);
    }

    @Override
    public boolean isValidRedirectLink(Long groupId, String identifier, Long userId) {
        return certificateUpdateRedirectRepository.existsByGroupIdAndGroupIdentifierAndUserId(groupId, identifier, userId);
    }

}
