package eu.europa.ec.etrustex.web.persistence.entity.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SuppressWarnings({"java:S106", "java:S100", "java:S6437"})
@ExtendWith(MockitoExtension.class)
class UserTest {
    private static final String ECAS_ID = "ecasID";

    @Test
    void should_assign_ecasId_and_name() {

        User user = new User(ECAS_ID);

        assertEquals(ECAS_ID, user.getEcasId());
    }

    @Test
    void equals() {
        assertNotEquals(new User(ECAS_ID), new GrantedAuthority());
        assertNotEquals(new User(ECAS_ID), new User("foo"));
        assertEquals(new User(ECAS_ID), new User(ECAS_ID));
    }

}
