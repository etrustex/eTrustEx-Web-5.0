package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160", "squid:S107"})
public class UserListItem implements Serializable {
    private String ecasId;
    private String name;
    private String euLoginEmailAddress;
    private String alternativeEmail;
    private Boolean alternativeEmailUsed;
    private Long groupId;
    private String groupIdentifier;
    private String groupName;
    private GroupType groupType;
    private String businessIdentifier;
    private String businessName;
    private Date createdDate;
    private String createdBy;
    private Date modifiedDate;
    private String modifiedBy;
    private Boolean newMessageNotification;
    private Boolean statusNotification;
    private Boolean retentionWarningNotification;
    private Boolean status;
    private Boolean isOperator;
    private Boolean isAdmin;
    private final List<RoleName> roleNames = new ArrayList<>();

   public UserListItem(String ecasId, String name, String euLoginEmailAddress, String alternativeEmail, Boolean alternativeEmailUsed, Long groupId, String groupIdentifier, String groupName, GroupType groupType, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy, Boolean newMessageNotification, Boolean statusNotification, Boolean retentionWarningNotification, Boolean status) {
        this.ecasId = ecasId;
        this.name = name;
        this.euLoginEmailAddress = euLoginEmailAddress;
        this.alternativeEmail = alternativeEmail;
        this.alternativeEmailUsed = alternativeEmailUsed;
        this.groupId = groupId;
        this.groupIdentifier = groupIdentifier;
        this.groupName = groupName;
        this.groupType = groupType;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.newMessageNotification = newMessageNotification;
        this.statusNotification = statusNotification;
        this.retentionWarningNotification = retentionWarningNotification;
        this.status = status;
    }

    public UserListItem(String ecasId, String name, String euLoginEmailAddress, String alternativeEmail, Boolean alternativeEmailUsed, Long groupId, String groupIdentifier, String groupName, GroupType groupType, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy, Boolean newMessageNotification, Boolean statusNotification, Boolean retentionWarningNotification, Boolean status, Boolean isOperator, Boolean isAdmin) {
        this.ecasId = ecasId;
        this.name = name;
        this.euLoginEmailAddress = euLoginEmailAddress;
        this.alternativeEmail = alternativeEmail;
        this.alternativeEmailUsed = alternativeEmailUsed;
        this.groupId = groupId;
        this.groupIdentifier = groupIdentifier;
        this.groupName = groupName;
        this.groupType = groupType;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.newMessageNotification = newMessageNotification;
        this.statusNotification = statusNotification;
        this.retentionWarningNotification = retentionWarningNotification;
        this.status = status;
        this.isOperator = isOperator;
        this.isAdmin = isAdmin;
    }

    public UserListItem(String ecasId, String name, Long groupId, String groupIdentifier, String groupName, GroupType groupType, String businessIdentifier, String businessName, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy) {
        this.ecasId = ecasId;
        this.name = name;
        this.groupId = groupId;
        this.groupIdentifier = groupIdentifier;
        this.groupName = groupName;
        this.groupType = groupType;
        this.businessIdentifier = businessIdentifier;
        this.businessName = businessName;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
    }
}
