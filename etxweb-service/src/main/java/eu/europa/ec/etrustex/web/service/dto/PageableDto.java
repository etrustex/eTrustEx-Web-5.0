package eu.europa.ec.etrustex.web.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PageableDto {
    Pageable pageable;
    @Nullable
    String filterBy;
    @Nullable
    String filterValue;

    public Map<String, String> filters() {
        Map<String, String> filters = new HashMap<>();

        if (StringUtils.isNotBlank(filterBy) && StringUtils.isNotBlank(filterValue)) {
            String[] filterBys = Arrays.stream(filterBy.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            String[] filterValues = Arrays.stream(filterValue.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);

            if (filterBys.length != filterValues.length) {
                throw new IllegalStateException(String.format("filterBy length (%s) does not match filterValue length (%s)!", filterBy, filterValue));
            }

            for (int i=0 ; i<filterBys.length ; i++) {
                filters.put(filterBys[i], filterValues[i]);
            }
        }

        return filters;
    }
}
