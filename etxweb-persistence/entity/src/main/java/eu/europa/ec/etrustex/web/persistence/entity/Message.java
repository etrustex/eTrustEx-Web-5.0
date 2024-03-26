package eu.europa.ec.etrustex.web.persistence.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.SentListViewFilter;
import eu.europa.ec.etrustex.web.persistence.converter.AttachmentSpecConverter;
import eu.europa.ec.etrustex.web.persistence.converter.SymmetricKeyConverter;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_MESSAGE", indexes = {@Index(name = "idx_message_sentOn_senderGroup", columnList = "sentOn DESC, sender_group_id")})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private AuditingEntity auditingEntity = new AuditingEntity();

    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    @Transient
    @Setter(AccessLevel.NONE)
    private final List<Link> links = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_generator")
    @SequenceGenerator(name = "message_generator", sequenceName = "MESSAGE_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    @ToString.Include
    private Long id;

    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    @ToString.Include
    private String subject;

    @ManyToOne
    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    @NotNull
    @ToString.Include
    private Group senderGroup;

    @JsonView({ListViewFilter.class})
    @NotNull
    @ToString.Include
    private String senderUserName;

    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    @ToString.Include
    private Date sentOn;

    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    private Long attachmentsTotalByteLength;

    @JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
    private Integer attachmentTotalNumber;

    @Lob
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String text;

    /**
     * See {@link Status#DELIVERED}
     */
    @Enumerated(EnumType.STRING)
    @JsonView(ListViewFilter.class)
    private Status status;

    @JsonView(DetailsViewFilter.class)
    @NotNull
    private Date statusModifiedDate;

    /**
     * Field {@code attachmentSpecs} is a JSON value containing an array of serialized
     * {@link eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec AttachmentSpecs}. <p>
     * Among other things, the AttachmentSpecs contain the IV used during encryption, (the symmetric key is stored
     * in the symmetricKey field of the {@link MessageSummary MessageSummary}),
     * and thus needed for decryption
     */
    @Lob
    @Convert(converter = AttachmentSpecConverter.class)
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private List<AttachmentSpec> attachmentSpecs;

    @Lob
    @JsonView(DetailsViewFilter.class)
    private String metadataAttachmentSpec;

    /**
     * Json representation of template variables
     * In Java, Map<String, Serializable>
     */
    @Lob
    @JsonView(DetailsViewFilter.class)
    private String templateVariables;

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonView(SentListViewFilter.class)
    private final Set<MessageSummary> messageSummaries = ConcurrentHashMap.newKeySet();

    @JoinTable(name = "EW_MESSAGE_DRAFT_GROUP")
    @OneToMany(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @JsonView(SentListViewFilter.class)
    private Set<Group> draftRecipients = new HashSet<>();

    @Lob
    @Convert(converter = SymmetricKeyConverter.class)
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private SymmetricKey symmetricKey;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String iv;

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonView(SentListViewFilter.class)
    @Builder.Default
    private Set<MessageUserStatus> messageUserStatuses = ConcurrentHashMap.newKeySet();

    @JsonView(ListViewFilter.class)
    private Boolean highImportance;

    @JsonIgnore
    @Builder.Default
    private boolean updatedWithNewCertificate = false;

    /*
     * PROCESSED flag to know if the symmetric key has been successfully encrypted with the new certificate. We cannot use UPDATED_WITH_NEW_CERTIFICATE because it is used in the JpaPagingItemReader query and cannot be modified during the job
     */
    @JsonIgnore
    @Builder.Default
    private boolean processed = false;


    @JsonProperty
    @JsonView(SentListViewFilter.class)
    public Date getModifiedDate() {
        return auditingEntity.getModifiedDate();
    }

    public void setSenderUserProfile(UserProfile userProfile) {
        this.setSenderUserName(userProfile.getUser().getName());
        this.setSenderGroup(userProfile.getGroup());
    }

    public void setStatus(Status status) {
        this.status = status;
        this.statusModifiedDate = new Date();
    }

    @PrePersist
    private void prePersistStatusModifiedDate() {
        if (this.statusModifiedDate == null) {
            this.statusModifiedDate = new Date();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Message message = (Message) o;
        return id != null && Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return getClass().hashCode();
    }

    public static class MessageBuilder {

        String senderUserName;
        Group senderGroup;

        public MessageBuilder senderUserProfile(UserProfile userProfile) {
            this.senderUserName = userProfile.getUser().getName();
            this.senderGroup = userProfile.getGroup();
            return this;
        }
    }
}
