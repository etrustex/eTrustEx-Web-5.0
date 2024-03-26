package eu.europa.ec.etrustex.web.security.abac;

import eu.europa.ec.etrustex.web.service.security.abac.PolicyRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "policies")
@PropertySource(value = "classpath:policies.yml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class PoliciesProperties {
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    List<PolicyDefinition> definitions;
    @Getter
    List<PolicyRule> policyRules;

    @PostConstruct
    private void setPolicyRules() {
        policyRules = definitions.stream()
                .map(policyDefinition ->
                        PolicyRule.builder()
                                .name(policyDefinition.getName())
                                .description(policyDefinition.getDescription())
                                .target(expressionParser.parseExpression(policyDefinition.getTarget()))
                                .condition(expressionParser.parseExpression(policyDefinition.getCondition()))
                                .build()
                ).collect(Collectors.toList());
    }
}
