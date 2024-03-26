package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_REDIRECT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 50)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Redirect implements Serializable {
    protected static final String MESSAGES_BOX = "#/mbox/";
    protected static final String INBOX_URL = MESSAGES_BOX + "%s/inbox/";
    protected static final String SENT_URL = MESSAGES_BOX + "%s/sent/";

    @Embedded
    @ToString.Exclude
    private final AuditingEntity auditingEntity = new AuditingEntity();

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID id;

    @NotNull
    private Long groupId;

    public abstract String getTargetPath();
}

