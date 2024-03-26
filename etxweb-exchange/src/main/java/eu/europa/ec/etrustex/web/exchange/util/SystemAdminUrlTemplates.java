package eu.europa.ec.etrustex.web.exchange.util;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.LinkUtils;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;

/**
 * The Class UrlTemplates is used to define all the <em>PATHS</em> and the <em>PARAMETERS</em> of the REST api.
 *
 * <p>
 *
 * @author fuscoem
 * @see Rels Rels
 * @see LinkUtils LinkUtils
 */
public class SystemAdminUrlTemplates {
    public static final String API_SYSTEM_ADMIN_V_1 = "/api/system/admin/v1";
    public static final String GROUP_SYS_ADMIN = API_SYSTEM_ADMIN_V_1 + "/group/admin";
    public static final String GROUPS_SYS_ADMIN = API_SYSTEM_ADMIN_V_1 + "/groups/admin";
    public static final String GROUPS_SYS_ADMIN_SEARCH = GROUPS_SYS_ADMIN + "/search";
    public static final String USER_PROFILES = API_SYSTEM_ADMIN_V_1 + "/user-profiles";
    public static final String USER_PROFILES_SEARCH = USER_PROFILES + "/search";
    public static final String USER_PROFILE_LIST_ITEMS = USER_PROFILES + "/list-items";
    public static final String USER_PROFILE_DELETE = USER_PROFILES + "/delete";
    public static final String CERTIFICATE_UPDATE = API_SYSTEM_ADMIN_V_1 + "/certificate-update/link-to-update-certificate";
    public static final String ORPHAN_FILES = API_SYSTEM_ADMIN_V_1 + "/storage/orphan-files";
    public static final String GROUP_ORPHAN_FILES = API_SYSTEM_ADMIN_V_1 + "/groups/storage/orphan-files";
    public static final String LAUNCH_NEW_CERTIFICATE_JOB = API_SYSTEM_ADMIN_V_1 + "/new-certificate/launch-job";
    public static final String NEW_SERVER_CERTIFICATE = API_SYSTEM_ADMIN_V_1 + "/new-certificate/job-status";
    public static final String NEW_SERVER_CERTIFICATE_RESET_UPDATED_FLAG = API_SYSTEM_ADMIN_V_1 + "/new-certificate/reset-updated-flag";
    public static final String UVSCAN_TEST = API_SYSTEM_ADMIN_V_1 + "/uvscan/test";
    public static final String INTEGRATION_TEST = API_SYSTEM_ADMIN_V_1 + "/integration-test/submit-document";
    public static final String CLEAN_UP_TEST = API_SYSTEM_ADMIN_V_1 + "/clean-up/parent/{parentIdentifier}/group/{groupIdentifier}";
    public static final String DISABLE_MESSAGE_SUMMARIES = API_SYSTEM_ADMIN_V_1 + "/message-summary/disable";
    public static final String COUNT_MESSAGE_SUMMARIES_TO_DISABLE = API_SYSTEM_ADMIN_V_1 + "/message-summary/disable/count";
    public static final String LOG_LEVEL = API_SYSTEM_ADMIN_V_1 + "/log-level";
    public static final String LOG_LEVEL_SEARCH = LOG_LEVEL + "/search";
    public static final String LOG_LEVEL_UPDATE = LOG_LEVEL + "/update";
    public static final String LOG_LEVEL_RESET = LOG_LEVEL + "/reset";

    private SystemAdminUrlTemplates() {
        throw new EtxWebException("Utility class");
    }
}
