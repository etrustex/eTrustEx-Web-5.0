package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.WindowsCompatibleFilenamesGroupConfiguration;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckWindowsCompatible;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.util.List;
import java.util.Locale;

@Component
@AllArgsConstructor()
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class WindowsCompatibleValidator implements ConstraintValidator<CheckWindowsCompatible, Object[]> {

    private final FileNameValidatorDelegate fileNameValidatorDelegate;

    @Override
    @Transactional
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {

        context.disableDefaultConstraintViolation();

        Pair<WindowsCompatibleFilenamesGroupConfiguration, List<AttachmentSpec>> confAndSpecs = fileNameValidatorDelegate.getConfigurationAndAttachmentSpecs(value, WindowsCompatibleFilenamesGroupConfiguration.class);

        if (Boolean.FALSE.equals(confAndSpecs.getLeft().isActive())) {
            return true;
        }

        for (AttachmentSpec attachmentSpec : confAndSpecs.getRight()) {
            for (String fileOrFolder : attachmentSpec.getName().split("/")) {
                if (isForbidden(fileOrFolder)) {
                    context.buildConstraintViolationWithTemplate(ValidationMessage.formatWindowsCompatibilityErrorMessage(fileOrFolder)).addConstraintViolation();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isForbidden(String fileOrFolder) {

        if (!fileOrFolder.equals(fileOrFolder.trim())) {
            return true;
        }
        for (WindowsCompatibleFilenamesGroupConfiguration.ReservedCharacters reservedCharacters : WindowsCompatibleFilenamesGroupConfiguration.ReservedCharacters.values()) {

            // this is because the backslash needs to be repeated in order to create the enum in typescript correctly
            String reservedCharacter = reservedCharacters.toString().replace("\\\\", "\\");
            if (fileOrFolder.contains(reservedCharacter)) {
                return true;
            }
        }
        for (WindowsCompatibleFilenamesGroupConfiguration.ReservedFilenames reservedFilename : WindowsCompatibleFilenamesGroupConfiguration.ReservedFilenames.values()) {
            String fileOrFolderUpperCased = fileOrFolder.toUpperCase(Locale.ROOT);
            if (reservedFilename.toString().equals(fileOrFolderUpperCased)
                    || fileOrFolderUpperCased.startsWith(reservedFilename + ".")
            ) {
                return true;
            }
        }
        return false;
    }
}
