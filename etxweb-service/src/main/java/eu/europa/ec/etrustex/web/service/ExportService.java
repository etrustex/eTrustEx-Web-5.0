package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;

import java.io.InputStream;

public interface ExportService {
    InputStream exportUsersAndFunctionalMailboxes(Long businessId);

    InputStream exportUsersByEntity(Long entityId);

    String getExportUsersFilename(GroupType groupType);

    InputStream exportChannelsAndParticipants(Long businessId);
}
