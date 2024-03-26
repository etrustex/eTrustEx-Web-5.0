package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LinksConverterTest {
    @Mock
    private EtrustexWebProperties etrustexWebProperties;

    private LinksConverter linksConverter;

    @BeforeEach
    public void init() {
        linksConverter = new LinksConverterImpl(etrustexWebProperties);
    }

    @Test
    void should_convert_from_hateoas_link() {
        Link hateoasLink = Link.of("anHRef");
        given(etrustexWebProperties.getTargetServerContextRoot()).willReturn(null);
        assertEquals(hateoasLink.getHref(), linksConverter.convert(hateoasLink).getHref());
    }

    @Test
    void should_convert_from_custom_link() {
        eu.europa.ec.etrustex.web.util.exchange.model.Link jsonViewAnnotatedLink = eu.europa.ec.etrustex.web.util.exchange.model.Link.of("anotherHRef");
        assertEquals(jsonViewAnnotatedLink.getHref(), linksConverter.convert(jsonViewAnnotatedLink).getHref());
    }

    @Test
    void should_remove_path_if_target_url_is_different() {
        Link hateoasLink = Link.of("anHRef");
        given(etrustexWebProperties.getContextPath()).willReturn("/");
        given(etrustexWebProperties.getTargetServerContextRoot()).willReturn("/extra/path");
        assertEquals(hateoasLink.getHref(), linksConverter.convert(hateoasLink).getHref());
    }
}
