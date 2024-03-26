package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockRole;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;


    @BeforeEach
    public void init() {
        roleRepository.save(mockRole(RoleName.OPERATOR));
        roleRepository.save(mockRole(RoleName.GROUP_ADMIN));
    }

    @Test
    void should_find_by_name() {
        assertThat(this.roleRepository.findByName(RoleName.OPERATOR)).isNotNull();
    }
}
