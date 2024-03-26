package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateSpec {

    @NotNull(message = GROUP_ID_NOT_NULL_ERROR_MSG)
    private Long groupId;

    @NotNull(message = TYPE_NOT_NULL_ERROR_MSG)
    private TemplateType type;

    @NotNull(message = CONTENT_NOT_NULL_ERROR_MSG)
    private String content;

    private boolean useDefault;
}
