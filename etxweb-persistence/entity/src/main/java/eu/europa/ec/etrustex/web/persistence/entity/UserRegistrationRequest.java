package eu.europa.ec.etrustex.web.persistence.entity;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserRegistrationRequestId.class)
@Getter
@Setter
@Table(name = "EW_USER_REGISTRATION_REQUEST")
public class UserRegistrationRequest implements Serializable {

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @ManyToOne
    @NotNull
    private User user;

    @Id
    @ManyToOne
    @NotNull
    private Group group;
    private boolean isOperator;
    private boolean isAdmin;

}
