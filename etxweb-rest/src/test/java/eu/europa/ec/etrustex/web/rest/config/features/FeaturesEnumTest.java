package eu.europa.ec.etrustex.web.rest.config.features;

import eu.europa.ec.etrustex.web.common.features.FeaturesEnum;
import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.common.features.FeaturesEnum.Environment.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeaturesEnumTest {

    @Test
    void isActive() {
        FeaturesEnum etrustex0000 = FeaturesEnum.ETRUSTEX_0000;
        FeaturesEnum etrustex0001 = FeaturesEnum.ETRUSTEX_0001;

        assertTrue(etrustex0000.isActive(DEV.toString()));
        assertTrue(etrustex0000.isActive(TEST.toString()));
        assertTrue(etrustex0000.isActive(ACC.toString()));
        assertTrue(etrustex0000.isActive(PROD.toString()));

        assertFalse(etrustex0001.isActive(DEV.toString()));
        assertFalse(etrustex0001.isActive(TEST.toString()));
        assertFalse(etrustex0001.isActive(ACC.toString()));
        assertFalse(etrustex0001.isActive(PROD.toString()));
    }
}
