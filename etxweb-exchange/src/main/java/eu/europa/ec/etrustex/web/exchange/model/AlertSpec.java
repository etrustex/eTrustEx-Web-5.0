package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckEndDateAfterStartDate;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckStartDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CheckStartDate(message = START_DATE_IS_REQUIRED_ERROR_MSG)
@CheckEndDateAfterStartDate(message = END_DATE_CANNOT_PRECEDE_START_DATE_ERROR_MSG)
public class AlertSpec {

    @NotNull(message = GROUP_ID_NOT_NULL_ERROR_MSG)
//    @Size(min = 1, max = 255, message = GROUP_ID_LENGTH_ERROR_MSG)
    private Long groupId;

    @NotNull(message = TYPE_NOT_NULL_ERROR_MSG)
    private AlertType type;

    @NotNull(message = TITLE_NOT_NULL_ERROR_MSG)
    @Size(min = 1, max = 255, message = TITLE_LENGTH_ERROR_MSG)
    private String title;

    @NotNull(message = CONTENT_NOT_NULL_ERROR_MSG)
    private String content;

    private boolean isActive;

    private Date startDate;
    private Date endDate;
}
