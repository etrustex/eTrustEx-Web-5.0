package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_USER_GUIDE")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGuide implements Serializable {
    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_guide_generator")
    @SequenceGenerator(name = "user_guide_generator", sequenceName = "USER_GUIDE_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView(ListViewFilter.class)
    private Long id;

    @ManyToOne
    private Group business;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GroupType groupType;

    @ManyToOne
    @NotNull
    private Role role;

    @Lob
    @ToString.Exclude
    @NotNull
    private byte[] binary;

    @NotNull
    private String filename;
}
