package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.ForbiddenExtensionsGroupConfiguration;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckForbiddenExtensions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ForbiddenExtensionsValidator implements ConstraintValidator<CheckForbiddenExtensions, Object[]> {

    private final FileNameValidatorDelegate fileNameValidatorDelegate;

    private final Pattern fileExtension = Pattern.compile(Patterns.FILE_EXTENSION);

    @Override
    @Transactional
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {

        context.disableDefaultConstraintViolation();

        Pair<ForbiddenExtensionsGroupConfiguration, List<AttachmentSpec>> confAndSpecs = fileNameValidatorDelegate.getConfigurationAndAttachmentSpecs(value, ForbiddenExtensionsGroupConfiguration.class);

        if (confAndSpecs.getLeft().getForbiddenExtensions().isEmpty()) {
            return true;
        }

        Set<String> forbiddenExtensions = confAndSpecs.getLeft().getForbiddenExtensions();
        for (AttachmentSpec attachmentSpec : confAndSpecs.getRight()) {

            if (isForbidden(attachmentSpec.getName(), forbiddenExtensions)) {
                context.buildConstraintViolationWithTemplate(ValidationMessage.formatForbiddenExtensionsMessage(forbiddenExtensions)).addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private boolean isForbidden(String fileName, Set<String> forbiddenExtensions) {
        String extension = getExtension(fileName);
        return (extension != null && forbiddenExtensions.contains(extension));
    }

    private String getExtension(String fileName) {
        Matcher matcher = fileExtension.matcher(fileName);
        return matcher.matches() ? matcher.group(1).toUpperCase(Locale.ROOT) : null;
    }
}
