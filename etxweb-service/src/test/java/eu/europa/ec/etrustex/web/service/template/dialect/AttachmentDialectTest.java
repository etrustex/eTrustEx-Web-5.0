package eu.europa.ec.etrustex.web.service.template.dialect;

import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AttachmentDialectTest {
    @Mock
    private RedirectService redirectService;
    private AttachmentDialect attachmentDialect;

    private static final String DIALECT_NAME = "Attachment Dialect";

    @BeforeEach
    public void setUp() {
        this.attachmentDialect = new AttachmentDialect(redirectService);
    }

    @Test
    void should_get_processors() {

        assertThat(this.attachmentDialect.getProcessors(DIALECT_NAME).size()).isEqualTo(2);
    }
}
