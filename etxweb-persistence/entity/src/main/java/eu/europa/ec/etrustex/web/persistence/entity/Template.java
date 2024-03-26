package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_TEMPLATE")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template implements Serializable {
    @Embedded
    @JsonIgnore
    @ToString.Exclude
    private final AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template_generator")
    @SequenceGenerator(name = "template_generator", sequenceName = "TEMPLATES_SEQ", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Lob
    private String content;

    @ManyToMany(mappedBy = "templates")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Group> groups;
}
