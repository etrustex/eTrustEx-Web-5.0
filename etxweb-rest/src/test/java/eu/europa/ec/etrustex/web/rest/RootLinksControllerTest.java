package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import eu.europa.ec.etrustex.web.hateoas.LinksConverter;
import eu.europa.ec.etrustex.web.hateoas.RootLinksHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RootLinksControllerTest {

    @Mock
    private LinksConverter linksConverter;
    @Mock
    private RootLinksHandler rootLinksHandler = new RootLinksHandler(linksConverter);

    private RootLinksController rootLinksController;

    @BeforeEach
    public void init() {
        rootLinksController = new RootLinksController(rootLinksHandler);
    }

    @Test
    void should_add_the_links() {
        rootLinksController.get();
        verify(rootLinksHandler).addLinks(any(RootLinks.class));
    }
}
