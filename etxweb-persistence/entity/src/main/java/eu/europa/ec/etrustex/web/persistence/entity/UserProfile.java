
package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_USER_PROFILE")
@IdClass(UserProfileId.class)
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonView({ ListViewFilter.class })
@ToString(onlyExplicitlyIncluded = true)
public class UserProfile implements Serializable {

    @Embedded
    @Builder.Default
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @ToString.Include
    private User user;

    @Id
    @ManyToOne
    @NotNull
    @JsonIgnore
    @ToString.Include
    private Group group;

    @Email(message="Please provide a valid email address")
    @ToString.Include
    private String alternativeEmail;

    @Builder.Default
    private boolean newMessageNotifications = false;

    @Builder.Default
    private boolean messageStatusForSenderNotifications = false;

    @Builder.Default
    private boolean retentionWarningNotifications = false;

    @Builder.Default
    private boolean alternativeEmailUsed = false;

    /*
     * Hack for jpql distinct count for Pageable
     */
    @Formula("concat(user_id, group_id)")
    @JsonIgnore
    private String concatedId;

}
