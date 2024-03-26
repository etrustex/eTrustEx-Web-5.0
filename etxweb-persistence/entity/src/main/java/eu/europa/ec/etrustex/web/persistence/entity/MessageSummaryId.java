package eu.europa.ec.etrustex.web.persistence.entity;

import lombok.*;

import java.io.Serializable;

@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MessageSummaryId implements Serializable {
    private Long recipient;
    private Long message;

}
