package eu.europa.ec.etrustex.web.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FindMessageSummaryDto extends PageableDto {
    Long recipientGroupId;
    @Nullable
    Long messageId;
}
