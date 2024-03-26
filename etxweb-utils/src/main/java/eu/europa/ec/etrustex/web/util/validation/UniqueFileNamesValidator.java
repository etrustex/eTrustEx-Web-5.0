package eu.europa.ec.etrustex.web.util.validation;

import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageRequestSpecWithAttachments;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class UniqueFileNamesValidator implements ConstraintValidator<CheckUniqueFileNames, MessageRequestSpecWithAttachments> {
    @Override
    public boolean isValid(MessageRequestSpecWithAttachments value, ConstraintValidatorContext context) {
        if (value.getAttachmentSpecs() != null) {
            Set<String> names = new HashSet<>();
            for (AttachmentSpec attachmentSpec : value.getAttachmentSpecs()) {
                if (names.contains(attachmentSpec.getName())) {
                    return false;
                } else {
                    names.add(attachmentSpec.getName());
                }
            }
        }

        return true;
    }
}
