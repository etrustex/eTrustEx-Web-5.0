package eu.europa.ec.etrustex.web.rest.api.messages;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.hateoas.api.message.ApiMessageLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.MESSAGE;
import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.MESSAGES;

@Tag(name = "Message", description = "Messages API")
@RestController
@AllArgsConstructor
@Slf4j
public class ApiMessageController {
    private final MessageService messageService;
    private final ApiMessageLinksHandler apiMessageLinksHandler;
    private final UserProfileService userProfileService;

    @PostMapping(value = MESSAGES)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@systemsPermissionEvaluator.isSystemEntity(#entitySpec) && " +
            "@permissionEvaluator.hasPermission(principal, #entitySpec, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @JsonView(DetailsViewFilter.class)
    public Message create(@RequestBody @Valid EntitySpec entitySpec, SecurityUserDetails principal) {
        UserProfile userProfile = userProfileService.findByUserAndGroupIdentifierAndBusinessIdentifier(principal.getUser(), entitySpec);

        return apiMessageLinksHandler.addLinks(messageService.create(userProfile));
    }

    @PutMapping(value = MESSAGE)
    @PreAuthorize("@systemsPermissionEvaluator.isSenderSystemEntity(#messageId) && " +
            "@permissionEvaluator.hasPermission(principal, #sendMessageRequestSpec, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> update(SecurityUserDetails principal,
                                          @PathVariable Long messageId,
                                          @RequestBody @Valid SendMessageRequestSpec sendMessageRequestSpec) {

        messageService.update(messageId, sendMessageRequestSpec, principal.getUser().getName());

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }
}
