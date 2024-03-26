package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration;

import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EW_GROUP_CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = {"GROUP_ID", "DTYPE"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 50)
@SuperBuilder
public abstract class GroupConfiguration<T> {
    public static final String URL_TEMPLATE = UrlTemplates.GROUP_CONFIGURATION + "/";

    @Transient
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private final List<Link> links = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_configuration_generator")
    @SequenceGenerator(name = "group_configuration_generator", sequenceName = "GROUP_CONFIGURATION_SEQ", allocationSize = 1)
    protected Long id;
    @NotNull
    @ManyToOne
    protected Group group;
    @Embedded
    @Builder.Default
    @ToString.Exclude
    private AuditingEntity auditingEntity = new AuditingEntity();
    @Column(insertable = false, updatable = false)
    private String dtype;

    private boolean active;

    public abstract void setValue(T value);

    public abstract Class<?> specClass();

}
