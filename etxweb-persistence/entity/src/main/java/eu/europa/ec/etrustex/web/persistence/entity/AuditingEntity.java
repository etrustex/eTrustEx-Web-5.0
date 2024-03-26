package eu.europa.ec.etrustex.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.ExchangeRuleViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;


/**
 * The columns annotated with @CreatedBy and @LastModifiedBy are populated with the name of the principal that created or last modified the entity. The information is pulled from SecurityContext‘s Authentication instance. If you want to customize values that are set to the annotated fields, you can implement AuditorAware<T> interface:
 *<code>public class AuditorAwareImpl implements AuditorAware<String> {...}</code>
 * <p>
 * In order to configure the app to use AuditorAwareImpl to look up the current principal, declare a bean of AuditorAware type initialized with an instance of AuditorAwareImpl and specify the bean's name as the auditorAwareRef parameter's value in @EnableJpaAuditing:
 * <code>
 * @EnableJpaAuditing(auditorAwareRef="auditorProvider") public class PersistenceConfig {
 * <p>
 * //...
 * @Bean AuditorAware<String> auditorProvider() {
 * return new AuditorAwareImpl();
 * }
 * <p>
 * //...
 * <p>
 * }
 * </code>
 */

@Embeddable
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@JsonView({ListViewFilter.class, ExchangeRuleViewFilter.class})
public class AuditingEntity implements Serializable {
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @LastModifiedDate
    private Date modifiedDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;
}
