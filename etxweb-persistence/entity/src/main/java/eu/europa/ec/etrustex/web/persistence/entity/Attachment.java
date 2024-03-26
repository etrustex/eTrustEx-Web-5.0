package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_ATTACHMENT")
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment implements Serializable {
    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private AuditingEntity auditingEntity = new AuditingEntity();

    @JsonView(ListViewFilter.class)
    @Transient
    @Setter(AccessLevel.NONE)
    private final List<Link> links = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_generator")
    @SequenceGenerator(name = "attachment_generator", sequenceName = "ATTACHMENT_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Size(max = 255, message = "clientReference should be at most 255 characters long.")
    private String clientReference;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Message message;

    /* the path in the server storage */
    @JsonIgnore
    @Size(min = 1, max = 255, message = "serverStorePath should be at between 1 and 255 characters long.")
    private String serverStorePath;

    @Transient
    private String encryptedContentChecksum;
}
