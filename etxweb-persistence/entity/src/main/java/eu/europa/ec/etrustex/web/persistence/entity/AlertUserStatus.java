package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_ALERT_USER_STATUS")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(ListViewFilter.class)
public class AlertUserStatus implements Serializable {

    public enum AlertUserStatusType {
        UNREAD, READ
    }

    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_user_status_generator")
    @SequenceGenerator(name = "alert_user_status_generator", sequenceName = "ALERT_USER_STATUS_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Alert alert;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User user;

    @NotNull
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @NotNull
    @Builder.Default
    private AlertUserStatusType status = AlertUserStatusType.UNREAD;

}
