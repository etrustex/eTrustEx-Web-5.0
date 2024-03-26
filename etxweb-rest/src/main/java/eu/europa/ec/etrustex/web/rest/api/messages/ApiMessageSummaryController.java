package eu.europa.ec.etrustex.web.rest.api.messages;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.InboxListViewFilter;
import eu.europa.ec.etrustex.web.hateoas.api.message.ApiMessageSummaryLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.api.ApiMessageSummaryService;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.Ack;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RecipientStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.*;
import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.MISSING_CLIENT_PUBLIC_KEY_HEADER_MESSAGE;

@Tag(name = "Message Summary", description = "Message-Summaries API")
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class ApiMessageSummaryController {

    private final ApiMessageSummaryService apiMessageSummaryService;
    private final ApiMessageSummaryLinksHandler apiMessageSummaryLinksHandler;


    @Operation(summary = "Get unread message summaries")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = UNREAD_MESSAGE_SUMMARIES)
    @PreAuthorize("@systemsPermissionEvaluator.isSystemEntity(#entitySpec) && " +
            "@permissionEvaluator.hasPermission(principal, #entitySpec, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(InboxListViewFilter.class)
    public List<Long> get(@Valid EntitySpec entitySpec,
                                    @NotBlank @RequestHeader(CLIENT_PUBLIC_KEY_HEADER_NAME) String clientPublicKeyPem,
                                    SecurityUserDetails principal) {


        return apiMessageSummaryService.findUnreadByRecipientIdForCurrentUser(entitySpec, principal.getUser(), clientPublicKeyPem)
                .stream()
                .map(messageSummary -> messageSummary.getMessage().getId())
                .collect(Collectors.toList());
    }


    @Operation(summary = "Get message summary")
    @GetMapping(value = MESSAGE_SUMMARY)
    @PreAuthorize("@systemsPermissionEvaluator.isSystemEntity(#entitySpec) && " +
            "@permissionEvaluator.hasPermission(principal, #entitySpec, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(MessageSummaryViewFilter.class)
    public MessageSummary get(@PathVariable Long messageId,
                              @Valid EntitySpec entitySpec,
                              @RequestHeader(CLIENT_PUBLIC_KEY_HEADER_NAME) @NotBlank(message = MISSING_CLIENT_PUBLIC_KEY_HEADER_MESSAGE) String clientPublicKeyPem,
                              SecurityUserDetails principal) {

        return apiMessageSummaryLinksHandler.addLinks(
                apiMessageSummaryService.findByMessageIdAndRecipientIdForCurrentUser(
                        messageId, clientPublicKeyPem, entitySpec, principal.getUser())
        );
    }


    @Operation(summary = "Get message summary statuses")
    @GetMapping(value = MESSAGE_SUMMARY_STATUS)
    @PreAuthorize("@systemsPermissionEvaluator.isSenderSystemEntity(#messageId) && " +
            "@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<RecipientStatus> status(@PathVariable Long messageId) {
        return apiMessageSummaryService.recipientStatusByMessageId(messageId);
    }

    @Operation(summary = "Retrieve message summary retrieval ACK")
    @PutMapping(value = MESSAGE_SUMMARY_ACK)
    @PreAuthorize("@systemsPermissionEvaluator.isSystemEntity(#entitySpec) && " +
            "@permissionEvaluator.hasPermission(principal, #entitySpec, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<Void> ack(SecurityUserDetails principal,
                                    @PathVariable Long messageId,
                                    @PathVariable Ack ack,
                              @Valid EntitySpec entitySpec) {

        apiMessageSummaryService.ack(principal.getUser(), messageId, entitySpec, ack);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
