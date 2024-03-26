package eu.europa.ec.etrustex.web.persistence.entity.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EW_ROLE")
@Builder
public class Role implements Serializable {
    @Embedded
    @ToString.Exclude
    private final AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
    @SequenceGenerator(name = "role_generator", sequenceName = "ROLE_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(length = 20)
    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleName name;

    private String description;
}
