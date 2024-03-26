package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import eu.europa.ec.etrustex.web.hateoas.SysAdminLinksHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.API_SYSTEM_ADMIN_V_1;

@RestController
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@RequiredArgsConstructor
public class SysAdminLinksController {
    private final SysAdminLinksHandler adminLinksHandler;

    @GetMapping(API_SYSTEM_ADMIN_V_1)
    public RootLinks get() {
        return adminLinksHandler.addLinks(new RootLinks());
    }
}
