package eu.europa.ec.etrustex.web.persistence.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "EW_GRANTED_AUTHORITY")
public class GrantedAuthority {
    @Embedded
    @JsonIgnore
    private final AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "granted_authority_generator")
    @SequenceGenerator(name = "granted_authority_generator", sequenceName = "GRANTED_AUTHORITY_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @NotNull
    private User user;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @NotNull
    private Group group;

    @ManyToOne
    @NotNull
    private Role role;

    @NotNull
    private boolean enabled = true;

    @Builder
    public GrantedAuthority(User user, Group group, Role role) {
        super();
        this.user = user;
        this.group = group;
        this.role = role;
    }

    @Override
    public String toString() {
        return "GrantedAuthority{" +
                "user=" + user.getEcasId() +
                ", group=" + (group == null ? "" : group.getIdentifier()) +
                ", role=" + role.getName() +
                '}';
    }
}
