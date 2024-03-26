package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.GroupUtils;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserExportControllerTest extends AbstractControllerTest {

    @Autowired
    private GroupUtils groupUtils;

    private Group otherBusiness;

    @BeforeEach
    public void init() {
        otherBusiness = groupUtils.createBusiness(root.getId(), "" + RANDOM.nextLong(), sysAdminUserDetails);
    }

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
    }

    private void happyFlows() throws Exception {
        should_export_users(sysAdminUserDetails, abstractTestBusiness.getId());
        should_export_users(businessAdminUserDetails, abstractTestBusiness.getId());
    }

    private void securityAccessTests() throws Exception {
        should_not_export_users(businessAdminUserDetails, otherBusiness.getId());
        should_not_export_users(entityAdminUserDetails, abstractTestBusiness.getId());
        should_not_export_users(operatorUserDetails, abstractTestBusiness.getId());
    }

    void should_export_users(SecurityUserDetails userDetails, Long businessId) throws Exception {
        MvcResult result = mockMvc.perform(get(UrlTemplates.EXPORT_USERS, businessId)
                        .with(user(userDetails))
                ).andExpect(status().isOk())
                .andReturn();

        byte[] fileContent = result.getResponse().getContentAsByteArray();

        XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(new ByteArrayInputStream(fileContent)));

        assertThat(workbook.getNumberOfSheets()).isOne();
        assertThat(workbook.getSheetAt(0).getPhysicalNumberOfRows()).isNotZero();
    }

    void should_not_export_users(SecurityUserDetails userDetails, Long businessId) throws Exception {
        mockMvc.perform(get(UrlTemplates.EXPORT_USERS, businessId)
                .with(user(userDetails))
        ).andExpect(status().isForbidden());
    }
}
