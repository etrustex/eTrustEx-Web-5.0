package eu.europa.ec.etrustex.web.persistence.entity.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.cia.Availability;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.cia.Integrity;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_RECIPIENT_PREFERENCES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonView(ListViewFilter.class)
@ToString
public class RecipientPreferences implements Serializable {

    @Transient
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private final List<Link> links = new ArrayList<>();

    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipient_preferences_generator")
    @SequenceGenerator(name = "recipient_preferences_generator", sequenceName = "RECIPIENT_PREFERENCES_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Lob
    private String publicKey;
    private String publicKeyFileName;
    private String publicKeyHashValue;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Confidentiality confidentiality = Confidentiality.PUBLIC;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Integrity integrity = Integrity.MODERATE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.MODERATE;
}

