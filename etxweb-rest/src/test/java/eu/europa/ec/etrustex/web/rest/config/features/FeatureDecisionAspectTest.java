package eu.europa.ec.etrustex.web.rest.config.features;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.common.features.FeatureDecision;
import eu.europa.ec.etrustex.web.common.features.FeatureDecisionAspect;
import eu.europa.ec.etrustex.web.common.features.FeaturesEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * It is not possible to mock an enum class with Mockito. We would need to use PowerMockito or something else.
 * Thus, this test depends on Features enum containing the following values
 *       ETRUSTEX_0000("ETRUSTEX_0000", "sample task toggle feature enabled", true),
 *       ETRUSTEX_0001("ETRUSTEX_0001", "sample task toggle feature disabled", false);
 */
@ExtendWith(MockitoExtension.class)
/*
 *  Already tried with @DirtiesContext but running all tests fail
 @DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
 */
@Disabled("mockito-inline is needed to mock static classes. But using it makes test fails when running all test")
class FeatureDecisionAspectTest {
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private MethodSignature signature;

    @Mock
    private Method method;

    @Mock
    private EtrustexWebProperties etrustexWebProperties;

    @Mock
    private FeatureDecisionMock featureDecision;

    @Mock
    private FeaturesEnum featuresEnum;

    private FeatureDecisionAspect featureDecisionAspect;

    @BeforeEach
    public void init() {
        featureDecisionAspect = new FeatureDecisionAspect(etrustexWebProperties);
        given(proceedingJoinPoint.getSignature()).willReturn(signature);
        given(signature.getMethod()).willReturn(method);
        given(method.getAnnotation(FeatureDecision.class)).willReturn(featureDecision);
        given(etrustexWebProperties.getEnvironment()).willReturn("dev");
        given(featureDecision.value()).willReturn(featuresEnum);
    }

    @Test
    void should_return_OK_for_enabled_feature() throws Throwable {
        given(featuresEnum.isActive(anyString())).willReturn(true);

        featureDecisionAspect.checkFeature(proceedingJoinPoint);

        verify(proceedingJoinPoint, times(1)).proceed();
    }


    @Test
    void should_throw_for_not_enabled_feature() throws Throwable {
        given(featuresEnum.isActive(anyString())).willReturn(true);

        assertNull(featureDecisionAspect.checkFeature(proceedingJoinPoint));
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    static class FeatureDecisionMock implements FeatureDecision {
        @Override
        public Class<? extends Annotation> annotationType() {
            return FeatureDecision.class;
        }

        @Override
        public FeaturesEnum value() {
            return null;
        }
    }
}
