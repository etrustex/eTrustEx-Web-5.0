package eu.europa.ec.etrustex.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppInitializerTest {

    @Spy
    private MockApplicationBuilder springApplicationBuilder;

    private AppInitializer appInitializer;

    @BeforeEach
    public void init() {
        appInitializer = new AppInitializer();
    }

    @Test
    void should_call_source() {
        appInitializer.configure(springApplicationBuilder);
        verify(springApplicationBuilder).sources(AppInitializer.class);
    }

    private static class MockApplicationBuilder extends SpringApplicationBuilder {
    }
}
