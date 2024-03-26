package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_NEW_SERVER_CERTIFICATE")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewServerCertificate implements Serializable {
    public static final long NEW_SERVER_CERTIFICATE_ID = 1L;

    @Embedded
    @JsonIgnore
    @ToString.Exclude
    private final AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @NotNull
    private boolean ready;
    @NotNull
    private boolean firstNotificationSent;
    @NotNull
    private boolean secondNotificationSent;

    @Transient
    @EqualsAndHashCode.Exclude
    private LocalDateTime expirationDate;
    @Transient
    @EqualsAndHashCode.Exclude
    private String status;
    @Transient
    @EqualsAndHashCode.Exclude
    private String oldCertificateAlias;
}
