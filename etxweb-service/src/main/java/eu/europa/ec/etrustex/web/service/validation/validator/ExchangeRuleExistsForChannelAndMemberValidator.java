package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckExchangeRuleExistsForChannelAndMember;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@RequiredArgsConstructor
public class ExchangeRuleExistsForChannelAndMemberValidator implements ConstraintValidator<CheckExchangeRuleExistsForChannelAndMember, ExchangeRuleSpec> {
    private final ExchangeRuleRepository exchangeRuleRepository;

    @Override
    public boolean isValid(ExchangeRuleSpec spec, ConstraintValidatorContext context) {

        Optional<ExchangeRule> exchangeRuleOptional = exchangeRuleRepository.findByChannelIdAndMemberId(spec.getChannelId(), spec.getMemberId());

        if (!exchangeRuleOptional.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ValidationMessage.formatExchangeRuleExistsForChannelAndMemberErrorMessage(spec.getChannelId(), spec.getMemberId()))
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
