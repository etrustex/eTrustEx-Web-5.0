package eu.europa.ec.etrustex.web.persistence.entity.security;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupTest {

    @Test
    void should_return_business_for_entity() {
        Group root = mockGroup("ROOT", "ROOT", null, GroupType.ROOT);
        Group business = mockGroup("parent_id", "parent_name", root, GroupType.BUSINESS);
        Group entity = mockGroup(TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, business, GroupType.ENTITY);
        Group entityWithoutParent = mockGroup(TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, null, GroupType.ENTITY);

        assertEquals(business, entity.getBusinessOrRoot());
        assertEquals(business, business.getBusinessOrRoot());
        assertEquals(root, root.getBusinessOrRoot());
        assertThrows(EtxWebException.class, entityWithoutParent::getBusinessOrRoot);
    }
}
