package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.UniqueChannelName;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueChannelNameValidator implements ConstraintValidator<UniqueChannelName, ChannelSpec> {

    private final ChannelRepository channelRepository;

    @Override
    public boolean isValid(ChannelSpec channelSpec, ConstraintValidatorContext context) {
        return !channelRepository.existsByBusinessIdAndName(channelSpec.getBusinessId(), channelSpec.getName());
    }
}
