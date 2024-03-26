package eu.europa.ec.etrustex.web.persistence.entity.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.common.exchange.view.ExchangeRuleViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.AuditingEntity;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EW_EXCHANGE_RULE")
@IdClass(ExchangeRuleId.class)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExchangeRule {

    @Embedded
    @Builder.Default
    @ToString.Exclude
    @JsonView(ExchangeRuleViewFilter.class)
    private AuditingEntity auditingEntity = new AuditingEntity();


    @Id
    @ManyToOne(fetch= FetchType.LAZY)
    @JsonIgnore
    private Channel channel;

    @Id
    @ManyToOne(fetch= FetchType.LAZY)
    @JsonView(ExchangeRuleViewFilter.class)
    private Group member;

    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonView(ExchangeRuleViewFilter.class)
    private ExchangeMode exchangeMode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRule)) return false;
        ExchangeRule that = (ExchangeRule) o;
        return Objects.equals(getMember(), that.getMember());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMember());
    }
}
