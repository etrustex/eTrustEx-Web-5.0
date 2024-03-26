package eu.europa.ec.etrustex.web.persistence.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.InboxListViewFilter;
import eu.europa.ec.etrustex.web.common.exchange.view.SentListViewFilter;
import eu.europa.ec.etrustex.web.persistence.converter.SymmetricKeyConverter;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.cia.Integrity;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_MESSAGE_SUMMARY")
@IdClass(MessageSummaryId.class)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class MessageSummary implements Serializable {
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
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonView({InboxListViewFilter.class, MessageSummaryViewFilter.class})
    @ToString.Include
    private Message message;

    @Id
    @ManyToOne
    @JsonView({SentListViewFilter.class,MessageSummaryViewFilter.class})
    @ToString.Include
    private Group recipient;

    @Size(max = 255, message = "clientReference should be at most 255 characters long.")
    @ToString.Include
    private String clientReference;

    /**
     * {@link Status#DELIVERED}: is available in the receiver's inbox
     * {@link Status#READ}: has been read by the receiver
     * {@link Status#FAILED}: has failed
     */
    @Enumerated(EnumType.STRING)
    @JsonView(ListViewFilter.class)
    private Status status;

    @Lob
    @Convert(converter = SymmetricKeyConverter.class)
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private SymmetricKey symmetricKey;

    @JsonIgnore
    @Builder.Default
    private boolean updatedWithNewCertificate = false;

    /*
     * PROCESSED flag to know if the symmetric key has been successfully encrypted with the new certificate. We cannot use UPDATED_WITH_NEW_CERTIFICATE because it is used in the JpaPagingItemReader query and cannot be modified during the job
     */
    @JsonIgnore
    @Builder.Default
    private boolean processed = false;

    @Enumerated(EnumType.STRING)
    @JsonView(ListViewFilter.class)
    private Confidentiality confidentiality;

    @Enumerated(EnumType.STRING)
    @JsonView(ListViewFilter.class)
    private Integrity integrity;

    @OneToMany(mappedBy = "messageSummary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonView(InboxListViewFilter.class)
    private final Set<MessageSummaryUserStatus> messageSummaryUserStatuses = ConcurrentHashMap.newKeySet();

    @Lob
    @JsonView(ListViewFilter.class)
    public String signature;

    @JsonView(DetailsViewFilter.class)
    private Date statusModifiedDate;

    private String publicKeyHashValue;
    private boolean isValidPublicKey;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private boolean disabledByRetentionPolicy = false;

    @Builder.Default
    private boolean isNotifiedOfRetentionPolicy = false;

    @Formula("concat(message_id, recipient_id)")
    @JsonIgnore
    private String concatedId;

    @PrePersist
    private void addToSummaries() {
        this.message.getMessageSummaries().add(this);
    }

    @PreRemove
    private void removeFromSummaries() {
        this.message.getMessageSummaries().remove(this);
    }

}
