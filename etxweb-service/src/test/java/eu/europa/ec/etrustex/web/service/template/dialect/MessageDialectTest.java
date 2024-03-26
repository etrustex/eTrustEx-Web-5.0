package eu.europa.ec.etrustex.web.service.template.dialect;

import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MessageDialectTest {

    private MessageDialect messageDialect;
    private static final String DIALECT_NAME = "Message Dialect";

    @BeforeEach
    public void setUp() {
        RedirectService redirectService = mock(RedirectService.class);
        this.messageDialect = new MessageDialect(redirectService);
    }

    @Test
    void should_get_processors() {

        assertThat(this.messageDialect.getProcessors(DIALECT_NAME).size()).isEqualTo(2);
    }
}
