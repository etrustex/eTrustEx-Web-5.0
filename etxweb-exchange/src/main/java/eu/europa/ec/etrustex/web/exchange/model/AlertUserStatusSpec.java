package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertUserStatusSpec {

    private Long groupId;
    private Long userId;
    private Long alertId;
    private String status;

}
