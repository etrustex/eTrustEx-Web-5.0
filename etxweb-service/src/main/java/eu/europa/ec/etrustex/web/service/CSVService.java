package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.service.validation.model.BulkExchangeRuleSpec;
import eu.europa.ec.etrustex.web.service.validation.model.BulkUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;

import java.util.List;

public interface CSVService {

    List<BulkUserProfileSpec> parseCSVFileToUserProfile(byte[] fileContent, Long businessId);
    List<GroupSpec> parseCSVFileToGroupSpecs(byte[] fileContent, Long businessId);
    List<BulkExchangeRuleSpec> parseCSVFileToBulkExchangeRuleSpecs(byte[] fileContent, Long businessId);
    List<ExchangeRule> parseCSVFileToExchangeRules(byte[] fileContent, Long businessId, Channel channel);
}
