package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_ALERT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Alert implements Serializable {
    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private AuditingEntity auditingEntity = new AuditingEntity();

    @JsonView(ListViewFilter.class)
    @Transient
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private final List<Link> links = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_generator")
    @SequenceGenerator(name = "alert_generator", sequenceName = "ALERT_SEQ", allocationSize = 1)
    private Long id;

    @OneToOne
    private Group group;

    private boolean isActive;

    private Date startDate;
    private Date endDate;

    private String title;

    @Enumerated(EnumType.STRING)
    private AlertType type;

    @Lob
    private String content;

    @OneToMany(mappedBy = "alert", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private Set<AlertUserStatus> alertUserStatuses = ConcurrentHashMap.newKeySet();

}
