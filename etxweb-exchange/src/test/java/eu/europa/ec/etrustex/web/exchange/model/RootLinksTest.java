package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RootLinksTest {
    @Test
    void should_initialize_the_empty_list_or_links() {
        RootLinks rootLinks = new RootLinks();

        assertEquals(Collections.emptyList(), rootLinks.getLinks());
    }
}
