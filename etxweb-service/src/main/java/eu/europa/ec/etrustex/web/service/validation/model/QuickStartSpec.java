package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.REQUIRED_CHANNEL;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.REQUIRED_GROUP;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuickStartSpec {
    @NotNull(message = REQUIRED_GROUP)
    private GroupSpec groupSpec;

    @NotNull(message = REQUIRED_CHANNEL)
    private ChannelSpec channelSpec;

    private List<UserProfileSpec> existingUsers;

    private List<UserProfileSpec> newUsers;

    private List<RoleName> roleNames;

    private boolean newMessageNotification;
    private boolean statusNotification;
    private boolean retentionWarningNotification;
}
