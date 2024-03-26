package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.common.DbStringListsSeparators;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckEmailList;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;


public class EmailListValidator implements ConstraintValidator<CheckEmailList, String> {

    Pattern pattern = null;
    int limit;
    String separator = null;

    @Override
    public void initialize(CheckEmailList constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern());
        this.limit = constraintAnnotation.limit();
        this.separator = DbStringListsSeparators.DB_STRING_LIST_SEPARATOR.toString();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        String [] asArray = value.split(separator);
        if (this.limit > 0 && asArray.length > this.limit) {
            return false;
        }

        if (asArray.length != new HashSet<>(Arrays.asList(asArray)).size()) {
            return false;
        }

        return Arrays.stream(asArray)
                .map(emailAddress -> emailAddress.length() <= 255 && pattern.matcher(emailAddress).matches())
                .reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .orElse(true);
    }
}
