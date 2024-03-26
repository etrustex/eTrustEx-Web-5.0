package eu.europa.ec.etrustex.web.persistence.entity.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class GrantedAuthorityTest {

    @Test
    void check_to_string_format() {
        User user = new User();
        user.setEcasId("ecasId");

        Group group = new Group();
        group.setIdentifier("groupId");

        Role role = Role.builder().name(RoleName.OPERATOR).build();

        GrantedAuthority grantedAuthority = new GrantedAuthority(user, group, role);
        String expected = "GrantedAuthority{" +
                "user=" + user.getEcasId() +
                ", group=" + group.getIdentifier() +
                ", role=" + role.getName() +
                '}';

        assertEquals(expected, grantedAuthority.toString());


        grantedAuthority = new GrantedAuthority(user, null, role);
        expected = "GrantedAuthority{" +
                "user=" + user.getEcasId() +
                ", group=" +
                ", role=" + role.getName() +
                '}';

        assertEquals(expected, grantedAuthority.toString());
    }
}
