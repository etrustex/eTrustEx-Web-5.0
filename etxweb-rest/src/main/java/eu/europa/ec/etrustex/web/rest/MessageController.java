package eu.europa.ec.etrustex.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.SentDetailsViewFilter;
import eu.europa.ec.etrustex.web.common.exchange.view.SentListViewFilter;
import eu.europa.ec.etrustex.web.hateoas.MessageLinksHandler;
import eu.europa.ec.etrustex.web.hateoas.SentMessageLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.StatusService;
import eu.europa.ec.etrustex.web.service.dto.FindMessageDto;
import eu.europa.ec.etrustex.web.service.pagination.MessagePage;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.UpdateMessageRequestSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;
import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;

@RestController
@AllArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;
    private final MessageLinksHandler messageLinksHandler;
    private final SentMessageLinksHandler sentMessageLinksHandler;
    private final UserProfileService userProfileService;
    private final StatusService statusService;


    @PostMapping(value = MESSAGES)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #senderEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @JsonView(DetailsViewFilter.class)
    public Message create(@RequestParam Long senderEntityId, SecurityUserDetails principal) {
        log.trace("about to create a new message");
        UserProfile userProfile = userProfileService.findUserProfileByUserAndGroupId(principal.getUser(), senderEntityId);

        Message message = messageService.create(userProfile);
        log.info("Message has been created id: {}", message.getId());
        return messageLinksHandler.addLinks(message);
    }

    @PutMapping(value = MESSAGE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #sendMessageRequestSpec, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> update(SecurityUserDetails principal,
                                          @PathVariable Long messageId,
                                          @Valid @RequestBody SendMessageRequestSpec sendMessageRequestSpec) {
        log.info("about to update message id: {}", messageId);
        messageService.update(messageId, sendMessageRequestSpec, principal.getUser().getName());

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PutMapping(value = DRAFT_MESSAGE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #updateMessageRequestSpec, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> draft(
            SecurityUserDetails principal,
            @PathVariable Long messageId,
            @Valid @RequestBody UpdateMessageRequestSpec updateMessageRequestSpec) {

        messageService.update(messageId, updateMessageRequestSpec, principal.getUser().getName());

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping(value = MESSAGE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #senderEntityId, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(SentDetailsViewFilter.class)
    public Message get(@PathVariable Long messageId,
                       @RequestParam Long senderEntityId,
                       @RequestHeader(CLIENT_PUBLIC_KEY_HEADER_NAME) String clientPublicKeyPem,
                       SecurityUserDetails userDetails) {

        return sentMessageLinksHandler.addLinks(messageLinksHandler.addLinks(messageService.getMessage(messageId, senderEntityId, clientPublicKeyPem, userDetails.getUser())));
    }

    @GetMapping(value = MESSAGES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(SentListViewFilter.class)
    public MessagePage get(
            @RequestParam Long senderEntityId,
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

        Sort sort = Sort.by(direction, StringUtils.isNotEmpty(sortBy) ? sortBy : "sentOn");

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        FindMessageDto findMessageDto = FindMessageDto.builder()
                .senderGroupId(senderEntityId)
                .messageId(messageId)
                .pageable(pageRequest)
                .filterBy(filterBy)
                .filterValue(filterValue)
                .build();

        MessagePage messagesPage = messageService.getMessagesForUser(findMessageDto, userDetails.getUser());
        messagesPage.forEach(messageLinksHandler::addLinks);

        return messagesPage;
    }

    @GetMapping(value = COUNT_UNREAD_SENT_MESSAGES)
    public ResponseEntity<Integer> countUnreadSent(@RequestParam Long senderEntityId, SecurityUserDetails userDetails) {
        return ResponseEntity.ok(messageService.countUnreadSent(senderEntityId, userDetails.getUser()));
    }

    @DeleteMapping(value = MESSAGE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Void> delete(SecurityUserDetails principal,
                                       @PathVariable Long messageId) {
        messageService.delete(messageId);
        log.info("Message with id: {} has been deleted by userId: {}", messageId, principal.getUser().getEcasId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = MESSAGES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<Boolean> markRead(@RequestBody Long[] messageIds, SecurityUserDetails userDetails) {
        statusService.markMessageRead(messageIds, userDetails.getUser());
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping(value = MESSAGE_IS_READY_TO_SEND)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<Boolean> isReadyToSend(@PathVariable Long messageId) {

        return new ResponseEntity<>(messageService.isReadyToSend(messageId), HttpStatus.OK);
    }
}
