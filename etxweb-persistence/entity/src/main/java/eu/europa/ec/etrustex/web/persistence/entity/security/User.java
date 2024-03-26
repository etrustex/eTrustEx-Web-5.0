package eu.europa.ec.etrustex.web.persistence.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import eu.europa.ec.etrustex.web.persistence.entity.UserPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "EW_USER")
@ToString(onlyExplicitlyIncluded = true)
public class User implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(ecasId, user.ecasId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ecasId);
    }

    @Builder.Default
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Include
    private Long id;

    @Column(length = 50)
    @NotNull
    @Size(min = 1, max = 50, message = "ecasId should be at between 1 and 10 characters long.")
    @ToString.Include
    private String ecasId;

    @NotNull
    @Size(min = 1, max = 255, message = "name should be at between 1 and 255 characters long.")
    @ToString.Include
    private String name;

    @Column(name = "EULOGIN_EMAIL_ADDRESS")
    @Email(message="Please provide a valid email address")
    @ToString.Include
    private String euLoginEmailAddress;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonIgnore
    private Set<UserProfile> userProfiles = ConcurrentHashMap.newKeySet();

    @Embedded
    @Builder.Default
    private UserPreferences userPreferences = new UserPreferences();

    public User(String ecasId) {
        this.ecasId = ecasId;
    }
}
