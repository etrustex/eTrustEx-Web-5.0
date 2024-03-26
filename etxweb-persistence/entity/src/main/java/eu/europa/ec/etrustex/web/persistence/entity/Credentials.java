package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.EncryptedPassword;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_CREDENTIALS")
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credentials implements Serializable {
    @Embedded
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private final AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentials_generator")
    @SequenceGenerator(name = "credentials_generator", sequenceName = "CREDENTIALS_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long credentialsId;

    @NotNull
    @Size(min = 1, max = 255, message = "userName should be between 1 and 255 characters long.")
    private String userName;

    @Embedded
    @Builder.Default
    @Valid
    private EncryptedPassword encryptedPassword = new EncryptedPassword();
}

