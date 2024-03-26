/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class RootLinks contains all the HATEOAS links to explore the REST api
 *
 * <p>
 *
 * It should contain all links to functionality of the REST api that can be initiated from scratch.
 * It should not contain links to functions that require previous steps (e.g., upload an attachment can be done
 * only after creating a message and adding an attachment to the message, hence neither attachment_create
 * nor attachment_upload should appear as root links).
 */
@Getter
@SuppressWarnings({"java:S1068"})
public class RootLinks {
    private final List<Link> links = new ArrayList<>();
}
