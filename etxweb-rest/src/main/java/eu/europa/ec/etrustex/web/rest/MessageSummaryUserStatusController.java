package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService;
import eu.europa.ec.etrustex.web.service.dto.PageableDto;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class MessageSummaryUserStatusController {
    private final MessageSummaryUserStatusService messageSummaryUserStatusService;

    @GetMapping(value = UrlTemplates.MESSAGE_MONITORING)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public RestResponsePage<MessageSummaryUserStatusItem> get(
            @RequestParam Long recipientEntityId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String filterValue,
            SecurityUserDetails userDetails
    ) {
        Sort.Direction direction = Sort.Direction.fromString(StringUtils.isNotEmpty(sortOrder) ? sortOrder : Sort.Direction.DESC.toString());

        Sort sort = Sort.by(direction, StringUtils.isNotEmpty(sortBy) ? sortBy : "auditingEntity.modifiedDate");

        PageableDto pageDto = PageableDto.builder()
                .pageable(PageRequest.of(page, size, sort))
                .filterBy(filterBy)
                .filterValue(filterValue)
                .build();

        Page<MessageSummaryUserStatusItem> pageResult = messageSummaryUserStatusService.findReadMessages(recipientEntityId, pageDto);
        return new RestResponsePage<>(pageResult.getContent(), pageResult.getPageable(), pageResult.getTotalElements());
    }
}
