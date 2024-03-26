package eu.europa.ec.etrustex.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class EcasErrorControllerTest {
    @Mock
    private HttpServletRequest httpServletRequest;

    private EcasErrorController ecasErrorController;

    @BeforeEach
    public void init() {
        ecasErrorController = new EcasErrorController();
    }

    @Test
    void should_return_unauthorized() {
        assertEquals(HttpStatus.UNAUTHORIZED, ecasErrorController.handle401(httpServletRequest).getStatusCode());
    }

}
