package eu.europa.ec.etrustex.web.persistence.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode
@Builder
public class UserPreferences implements Serializable {

    @Builder.Default
    private int paginationSize = 10;
}
