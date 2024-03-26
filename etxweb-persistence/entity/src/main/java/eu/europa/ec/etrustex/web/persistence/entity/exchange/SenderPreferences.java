package eu.europa.ec.etrustex.web.persistence.entity.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.etrustex.web.util.crypto.legacy.EncryptionImplementation;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_SENDER_PREFERENCES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SenderPreferences implements Serializable {

    @Embedded
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sender_preferences_generator")
    @SequenceGenerator(name = "sender_preferences_generator", sequenceName = "SENDER_PREFERENCES_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @NotNull
    private EncryptionImplementation encryptionImplementation = EncryptionImplementation.STANDARD;
}
