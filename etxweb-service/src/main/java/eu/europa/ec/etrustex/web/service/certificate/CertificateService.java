package eu.europa.ec.etrustex.web.service.certificate;

import eu.europa.ec.etrustex.web.exchange.model.CertificateUpdateDto;
import eu.europa.ec.etrustex.web.service.validation.model.UpdateCertificateSpec;

public interface CertificateService {

    String generateCertificateUpdateLink(UpdateCertificateSpec updateCertificateSpec);
    void updateCompromisedMessages(CertificateUpdateDto certificateUpdateDto, Long userId);
    boolean isValidRedirectLink(Long groupId, String identifier, Long userId);
}
