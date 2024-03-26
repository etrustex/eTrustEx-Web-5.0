package eu.europa.ec.etrustex.web.service.redirect;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.AttachmentDownloadRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.RedirectRepository;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedirectServiceImpl implements RedirectService {
    private final RedirectRepository redirectRepository;
    private final EtrustexWebProperties etrustexWebProperties;
    private final AttachmentService attachmentService;


    @Override
    public String getTargetUrl(UUID id) {
        return redirectRepository.findById(id)
                .map(Redirect::getTargetPath)
                .map(this::addApplicationUrl)
                .orElseThrow(() -> new EtxWebException("Cannot get redirect url for Redirect with id: " + id));
    }

    @Override
    public String createAttachmentPermalink(Long groupId, Long messageId, String clientReference) {
        Attachment attachment = attachmentService.findByClientReferenceAndMessageId(clientReference, messageId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EtxWebException(String.format("Cannot find attachment with referenceId %s for message %s", clientReference, messageId.toString())));

        Redirect redirect = AttachmentDownloadRedirect
                .builder()
                .groupId(groupId)
                .messageId(messageId)
                .attachmentId(attachment.getId())
                .build();
        return getPermalink(redirectRepository.save(redirect));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createPermalink(Redirect redirect) {
        return getPermalink(redirectRepository.save(redirect));
    }

    @Override
    public void delete(Redirect redirect) {
        redirectRepository.delete(redirect);

    }


    private String getPermalink(Redirect redirect) {
        String redirectUrl = etrustexWebProperties.getRedirectUrl();

        return redirectUrl.endsWith("/")
                ? redirectUrl + redirect.getId()
                : redirectUrl + "/" + redirect.getId();
    }

    private String addApplicationUrl(String targetPath) {
        return etrustexWebProperties.getApplicationUrl() + targetPath;
    }
}
