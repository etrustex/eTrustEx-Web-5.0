package eu.europa.ec.etrustex.web.service.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect;

import java.util.UUID;

public interface RedirectService {

    String getTargetUrl(UUID id);


    /***
     * creates a permalink to attachment download
     * @param groupId the recipient group
     * @param messageId the id of the message
     * @param clientReference the Attachment client reference
     * @return the fully qualified url of the permalink
     */
    String createAttachmentPermalink(Long groupId, Long messageId, String clientReference);


    /***
     * create a permalink
     * @return the fully qualified url of the permalink
     */
    String createPermalink(Redirect redirect);

    void delete(Redirect redirect);

}
