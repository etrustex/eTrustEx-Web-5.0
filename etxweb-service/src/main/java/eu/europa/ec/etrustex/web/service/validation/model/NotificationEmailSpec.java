package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckEmptyBccAndCcEmail;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.BCC_OR_CC_EMAIL_IS_REQUIRED_ERROR_MSG;


@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@CheckEmptyBccAndCcEmail(message = BCC_OR_CC_EMAIL_IS_REQUIRED_ERROR_MSG, groups = {PostAuthorization.class})
@ToString
public class NotificationEmailSpec {
    public static final String SUBJECT_BLANK_ERROR_MSG = "The subject cannot be empty";
    public static final String BODY_BLANK_ERROR_MSG = "The body cannot be empty";

    public static final String BUSINESS_BLANK_ERROR_MSG = "The business cannot be empty";

    private String to;
    private HashSet<String> cc;
    private HashSet<String> bcc;

    @NotBlank(message = SUBJECT_BLANK_ERROR_MSG)
    private String subject;

    @NotBlank(message = BODY_BLANK_ERROR_MSG)
    private String body;

    @NotNull(message = BUSINESS_BLANK_ERROR_MSG)
    private Long businessId;

    /**
     * The priority ("X-Priority" header) of the message.
     * typically between 1 (highest) and 5 (lowest)
     * See org.springframework.mail.javamail.MimeMessageHelper#setPriority(int)
     */
    private int priority;

    private Long groupId;
    private String groupIdentifier;
    private boolean isUserRegistration;
}
