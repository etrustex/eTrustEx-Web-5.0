package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RootLinksHandlerTest {
    @Mock
    private LinksConverter linksConverter;

    @Test
    void should_add_the_root_links() {
        RootLinks rootLinks = new RootLinks();
        RootLinksHandler rootLinksHandler = new RootLinksHandler(linksConverter);

        rootLinksHandler.addLinks(rootLinks);

        assertThat(rootLinks.getLinks()).isNotEmpty();
    }
}
