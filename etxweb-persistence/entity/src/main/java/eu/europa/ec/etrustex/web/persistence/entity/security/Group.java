package eu.europa.ec.etrustex.web.persistence.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.ExchangeRuleViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.BUSINESS;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "EW_GROUP")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(onlyExplicitlyIncluded = true)
public class Group implements Serializable {
    @Transient
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private final List<Link> links = new ArrayList<>();

    @Builder.Default
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_generator")
    @SequenceGenerator(name = "group_generator", sequenceName = "GROUP_SEQ", allocationSize = 1)
    @JsonView({ListViewFilter.class, ExchangeRuleViewFilter.class, MessageSummaryViewFilter.class})
    @ToString.Include
    private Long id;

    @JsonView({ListViewFilter.class, ExchangeRuleViewFilter.class, MessageSummaryViewFilter.class})
    @ToString.Include
    private String identifier;

    @Column(length = 100)
    @JsonView({ListViewFilter.class, ExchangeRuleViewFilter.class, MessageSummaryViewFilter.class})
    @NotNull
    @Size(min = 1, max = 100, message = "name should be at between 1 and 100 characters long.")
    @ToString.Include
    private String name;

    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private GroupType type;

    @JsonView(ExchangeRuleViewFilter.class)
    private String description;

    @Column(length = 2559)
    @ToString.Exclude
    private String newMessageNotificationEmailAddresses;
    @Column(length = 2559)
    @ToString.Exclude
    private String registrationRequestNotificationEmailAddresses;
    @Column(length = 2559)
    @ToString.Exclude
    private String statusNotificationEmailAddress;

    @Column(length = 2559)
    @ToString.Exclude
    private String retentionWarningNotificationEmailAddresses;

    @ToString.Exclude
    @Builder.Default
    private boolean individualStatusNotifications = false;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    @JsonView({ListViewFilter.class, ExchangeRuleViewFilter.class})
    private boolean isSystem = false;

    @Column(length = 2559)
    @ToString.Exclude
    private String systemEndpoint;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @ToString.Exclude
    private Group parent;

    @OneToOne(cascade = {CascadeType.REMOVE})
    @ToString.Exclude
    private RecipientPreferences recipientPreferences;

    @ManyToOne
    @ToString.Exclude
    private SenderPreferences senderPreferences;

    @ToString.Exclude
    private Boolean additionalAttachmentActions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "EW_GROUP_TEMPLATE",
            joinColumns = @JoinColumn(name = "GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "TEMPLATE_ID"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Template> templates;

    @Builder.Default
    private boolean isPendingDeletion = false;

    private Date removedDate;

    @JsonIgnore
    public Group getBusinessOrRoot() {
        if ((type.equals(ENTITY) || type.equals(BUSINESS)) && parent == null) {
            throw new EtxWebException(String.format("Group has type %s and parent is null", ENTITY));
        }
        return type == ENTITY ? parent.getBusinessOrRoot() : this;
    }

    @JsonProperty
    @ToString.Include
    public Long getBusinessId() {
        return getBusinessOrRoot().getId();
    }

    @JsonProperty
    @ToString.Include
    public String getBusinessIdentifier() {
        return getBusinessOrRoot().getIdentifier();
    }
}
