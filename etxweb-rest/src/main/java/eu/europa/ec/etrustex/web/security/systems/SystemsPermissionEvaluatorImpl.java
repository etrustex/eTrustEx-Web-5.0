/*
 * Copyright (c) 2018-2024. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.security.systems;

import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("systemsPermissionEvaluator")
@RequiredArgsConstructor
public class SystemsPermissionEvaluatorImpl implements SystemsPermissionEvaluator {
    private final GroupService groupService;
    private final MessageService messageService;


    @Override
    public boolean isSystemEntity(EntitySpec entitySpec) {
        return groupService.findByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier()).isSystem();
    }

    @Override
    public boolean isSenderSystemEntity(Long messageId) {
        return messageService.findById(messageId).getSenderGroup().isSystem();
    }
}
