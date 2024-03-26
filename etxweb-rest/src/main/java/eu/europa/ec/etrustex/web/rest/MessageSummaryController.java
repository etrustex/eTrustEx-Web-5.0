package eu.europa.ec.etrustex.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.InboxDetailsViewFilter;
import eu.europa.ec.etrustex.web.common.exchange.view.InboxListViewFilter;
import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.hateoas.MessageSummaryLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.StatusService;
import eu.europa.ec.etrustex.web.service.dto.FindMessageSummaryDto;
import eu.europa.ec.etrustex.web.service.pagination.MessageSummaryPage;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.COUNT_UNREAD_MESSAGE_SUMMARIES;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.MESSAGE_SUMMARY;
import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;

@RestController
@AllArgsConstructor
@Slf4j
public class MessageSummaryController {

    private final MessageSummaryService messageSummaryService;
    private final MessageSummaryLinksHandler messageSummaryLinksHandler;
    private final StatusService statusService;

    @Operation(summary = "Get the Inbox messages")
    @ResponseStatus(HttpStatus.OK)
    @JsonView(InboxListViewFilter.class)
    @GetMapping(value = UrlTemplates.MESSAGE_SUMMARIES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public MessageSummaryPage get(
            @RequestParam Long recipientEntityId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long messageId,
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String filterValue,
            SecurityUserDetails userDetails
    ) {
        Sort.Direction direction = Sort.Direction.fromString(StringUtils.isNotEmpty(sortOrder) ? sortOrder : Sort.Direction.DESC.toString());

        Sort sort = Sort.by(direction, StringUtils.isNotEmpty(sortBy) ? sortBy : "message.sentOn");

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        FindMessageSummaryDto findMessageSummaryDto = FindMessageSummaryDto.builder()
                .recipientGroupId(recipientEntityId)
                .messageId(messageId)
                .pageable(pageRequest)
                .filterBy(filterBy)
                .filterValue(filterValue)
                .build();

        MessageSummaryPage messageSummaryPage = messageSummaryService.findByRecipientForUser(findMessageSummaryDto, userDetails.getUser());
        messageSummaryPage.forEach(messageSummaryLinksHandler::addLinks);

        return messageSummaryPage;
    }

    @JsonView(InboxDetailsViewFilter.class)
    @GetMapping(value = MESSAGE_SUMMARY)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientEntityId, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public MessageSummary get(@PathVariable Long messageId,
                              @RequestParam Long recipientEntityId,
                              @RequestHeader(CLIENT_PUBLIC_KEY_HEADER_NAME) String clientPublicKeyPem,
                              SecurityUserDetails userDetails) {
        log.debug("MessageSummaryController. Find MessageSummary by message id : " + messageId);

        return messageSummaryLinksHandler.addLinks(messageSummaryService.findByMessageIdAndRecipientIdForCurrentUser(messageId, clientPublicKeyPem, recipientEntityId, userDetails.getUser()));
    }

    @GetMapping(value = COUNT_UNREAD_MESSAGE_SUMMARIES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<Integer> countUnread(@RequestParam Long recipientEntityId, @RequestParam(required = false) Long to, SecurityUserDetails userDetails) {
        return ResponseEntity.ok(messageSummaryService.countUnreadMessages(to, recipientEntityId, userDetails.getUser()));
    }

    @PutMapping(value = UrlTemplates.MESSAGE_SUMMARIES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Boolean> markRead(@RequestBody Long[] messageIds, @RequestParam Long recipientEntityId, SecurityUserDetails userDetails) {
        statusService.markMessageSummaryRead(messageIds, recipientEntityId, userDetails.getUser());
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping(value = UrlTemplates.MESSAGE_SUMMARY_LIST_ITEMS)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public RestResponsePage<MessageSummaryListItem> getMessagesDisplay(
            @RequestParam Long businessId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String filterValue
    ) {

        Sort.Direction direction = Sort.Direction.fromString(StringUtils.isNotEmpty(sortOrder) ? sortOrder : Sort.Direction.DESC.toString());

        Sort sort = Sort.by(direction, StringUtils.isNotEmpty(sortBy) ? sortBy : "auditingEntity.createdDate");

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return new RestResponsePage<>(messageSummaryService.findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(businessId, filterValue, pageRequest));

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = UrlTemplates.MESSAGE_SUMMARY_SEARCH_ITEM)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<SearchItem> search(
            @RequestParam Long businessId,
            @RequestParam String messageIdOrSubject) {

        return messageSummaryService.search(businessId, messageIdOrSubject);

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = UrlTemplates.MESSAGE_SUMMARY_UPDATE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Void> updateActiveStatus(
            @RequestParam Long businessId,
            @RequestParam Long messageId,
            @RequestParam String recipientIdentifier,
            @RequestParam Boolean isActive) {

        messageSummaryService.setActive(messageId, recipientIdentifier, isActive);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = UrlTemplates.MESSAGE_SUMMARIES_UPDATE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Void> updateMultipleActiveStatus(
            @RequestParam Long businessId,
            @RequestBody List<MessageSummaryListItem> messageSummaryListItems,
            @RequestParam Boolean activate) {
        messageSummaryService.activateOrInactivateMessageSummaries(messageSummaryListItems, activate);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
