package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@AllArgsConstructor
@Builder
public class UserGuideSpec implements Serializable {
    private Long businessId;
    private GroupType groupType;
    private RoleName role;
    private byte[] binary;
    private File file;
    private String filename;

}