package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.common.DbStringListsSeparators;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckNoRepetitions;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;


public class NoRepetitionsValidator implements ConstraintValidator<CheckNoRepetitions, String> {

    String separator = DbStringListsSeparators.DB_STRING_LIST_SEPARATOR.toString();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        String[] asArray = value.split(separator);
        return asArray.length == new HashSet<>(Arrays.asList(asArray)).size();
    }
}
