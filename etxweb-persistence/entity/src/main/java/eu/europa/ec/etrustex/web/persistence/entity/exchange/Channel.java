package eu.europa.ec.etrustex.web.persistence.entity.exchange;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_CHANNEL",
        indexes = { @Index(name = "IDX_CHANNEL_BUSINESS_AND_NAME", columnList = "business_id, name", unique = true) }
)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor

/*
 * Channels are used to define rules for sending and receiving messages.
 * <p>
 * Group A can send to group B iff:
 * 1) there is an ExchangeRule for channel C, member A, and exchangeMode sender or bidirectional
 * 2) there is an ExchangeRule for channel C, member B, and exchangeMode recipient or bidirectional
 *
 * Examples:
 * For EIOPA we will create:
 * 1) a channel CollectorsToSupervisor and ExchangeRules where collectors can send and supervisors can receive (or more
 * probably bidirectional)
 * 2) a channel SupervisorToInsuranceCompany and ExchangeRules where the supervisors can send and the insurance
 * companies can receive (again, these rules could be bidirectional)
 *
 * For businesses that have many users that can exchange messages freely among them we will create one channel where all
 * users have bidirectional ExchangeRules
 * </p>
 *
 */
public class Channel implements Serializable {
    @Builder.Default
    @ToString.Exclude
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_generator")
    @SequenceGenerator(name = "channel_generator", sequenceName = "CHANNEL_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView(ListViewFilter.class)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255, message = "Name should be between 1 and 255 characters long.")
    private String name;

    private String description;

    @ManyToOne
    private Group business;

    private boolean isActive;

    @Column(name="is_default")
    @JsonView(ListViewFilter.class)
    private boolean defaultChannel;

    @Enumerated(EnumType.STRING)
    @JsonView(ListViewFilter.class)
    private ExchangeMode defaultExchangeMode;
}
