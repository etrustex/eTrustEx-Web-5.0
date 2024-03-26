package eu.europa.ec.etrustex.web.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@SuppressWarnings({"squid:S1068", "squid:S2160"})
public class FindMessageDto extends PageableDto {
    Long senderGroupId;
    @Nullable
    Long messageId;
}
