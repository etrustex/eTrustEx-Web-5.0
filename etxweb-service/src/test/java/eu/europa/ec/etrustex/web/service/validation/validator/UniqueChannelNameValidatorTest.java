package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_BUSINESS_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UniqueChannelNameValidatorTest {
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ConstraintValidatorContext context;

    private UniqueChannelNameValidator uniqueChannelNameValidator;

    @BeforeEach
    public void init() {
        uniqueChannelNameValidator = new UniqueChannelNameValidator(channelRepository);
    }

    @Test
    void should_validate() {
        ChannelSpec channelSpec = ChannelSpec.builder().businessId(TEST_BUSINESS_ID).name("TEST_CHANNEL_NAME").build();

        given(channelRepository.existsByBusinessIdAndName(anyLong(), anyString())).willReturn(true);
        assertFalse(uniqueChannelNameValidator.isValid(channelSpec, context));

        given(channelRepository.existsByBusinessIdAndName(anyLong(), anyString())).willReturn(false);
        assertTrue(uniqueChannelNameValidator.isValid(channelSpec, context));
    }
}
