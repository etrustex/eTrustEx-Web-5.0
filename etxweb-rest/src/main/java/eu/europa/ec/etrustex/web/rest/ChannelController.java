package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.common.DateFormatters;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.service.ChannelService;
import eu.europa.ec.etrustex.web.service.ExportService;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;


@RestController
@AllArgsConstructor
@Slf4j
public class ChannelController {

    private final ChannelService channelService;
    private final ExportService exportService;

    @GetMapping(value = CHANNELS)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public RestResponsePage<Channel> get(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam String businessId // Must be String to avoid clash in ChannelEnforcement
    ) {
        Sort.Direction direction = Sort.Direction.fromString(StringUtils.isNotEmpty(sortOrder) ? sortOrder : Sort.Direction.ASC.toString());
        Sort.Order order = new Sort.Order(direction, StringUtils.isNotEmpty(sortBy) ? sortBy : "name").ignoreCase();

        Sort sort = Sort.by(order);

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Channel> channelPage = channelService.findByBusinessIdAndName(Long.valueOf(businessId), filterValue, pageRequest);
        return new RestResponsePage<>(channelPage.getContent(), channelPage.getPageable(), channelPage.getTotalElements());
    }

    @GetMapping(value = CHANNELS_BY_GROUP)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<Channel> get(@RequestParam Long businessId, @RequestParam Long entityId) {
        return channelService.findByBusinessIdAndEntityId(businessId, entityId);
    }

    @GetMapping(value = CHANNELS_SEARCH)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<SearchItem> find(@RequestParam Long businessId,
                                 @RequestParam String name) {
        return channelService.findByBusinessIdAndName(businessId, name);
    }

    @GetMapping(value = CHANNEL)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Channel get(
            @PathVariable Long channelId
    ) {
        return channelService.findById(channelId);
    }

    @PostMapping(value = CHANNELS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelSpec, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public Channel create(@Valid @RequestBody ChannelSpec channelSpec) {
        return channelService.create(channelSpec);
    }

    @PostMapping(value = IS_VALID_CHANNEL)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelSpec, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public ResponseEntity<Boolean> validate(@Valid @RequestBody ChannelSpec channelSpec) {
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PutMapping(value = CHANNEL)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelSpec, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> update(@PathVariable Long channelId, @Valid @RequestBody ChannelSpec channelSpec) {
        channelService.update(channelId, channelSpec);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @DeleteMapping(value = CHANNEL)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable Long channelId) {
        channelService.delete(channelId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(EXPORT_CHANNELS)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.exchange.model.ChannelExportItem), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<InputStreamResource> exportChannels(@PathVariable Long groupId) {
        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename("export-channels-" + DateFormatters.getYearMonthDayFormatter().format(new Date()) + ".xlsx")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        InputStream inputStream = exportService.exportChannelsAndParticipants(groupId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

}
