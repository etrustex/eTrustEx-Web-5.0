package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.rest.system.admin.*;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static eu.europa.ec.etrustex.web.util.exchange.model.Rels.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class SysAdminLinksHandler  {
    private final LinksConverter linksConverter;
    private final RootLinksHandler rootLinksHandler;

    @SuppressWarnings("ConstantConditions")
    public RootLinks addLinks(RootLinks rootLinks) {
        try {
            rootLinksHandler.addLinks(rootLinks);
            rootLinks.getLinks()
                    .addAll(
                            Arrays.asList(
                                    linksConverter.convert(linkTo(methodOn(StorageController.class).deleteOrphanFiles()).withRel(DELETE_ORPHAN_FILES.toString())),
                                    linksConverter.convert(linkTo(methodOn(StorageController.class).deleteOrphanFiles(null)).withRel(DELETE_GROUP_ORPHAN_FILES.toString())),

                                    linksConverter.convert(linkTo(methodOn(NewServerCertificateController.class).launchJob()).withRel(NEW_CERTIFICATE_JOB.toString())),
                                    linksConverter.convert(linkTo(methodOn(NewServerCertificateController.class).resetUpdatedFlag()).withRel(NEW_CERTIFICATE_RESET_UPDATED_FLAG.toString())),
                                    linksConverter.convert(linkTo(methodOn(NewServerCertificateController.class).newServerCertificate()).withRel(NEW_CERTIFICATE.toString())),

                                    linksConverter.convert(linkTo(methodOn(UVScanTestController.class).uvScanTest()).withRel(UVSCAN_TEST.toString())),
                                    linksConverter.convert(linkTo(methodOn(TestCleanUpController.class).cleanUpAfterTests(null, null)).withRel(TESTS_CLEAN_UP_DELETE.toString())),


                                    linksConverter.convert(linkTo(methodOn(SysAdminGroupController.class).get()).withRel(ROOT_GROUP_GET.toString())),

                                    linksConverter.convert(linkTo(methodOn(SysAdminGroupController.class).get(null, null, null, null, null)).withRel(GROUPS_ADMIN_GET.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminGroupController.class).search(null)).withRel(GROUPS_ADMIN_SEARCH.toString())),

                                    linksConverter.convert(linkTo(methodOn(SysAdminUsersController.class).preSearchUsers(null, null)).withRel(SYS_ADMIN_PROFILES_SEARCH.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminUsersController.class).getSysAdminListItems(null, null, null, null, null, null, null, null)).withRel(SYS_ADMIN_LIST_ITEMS_GET.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminUsersController.class).create(null)).withRel(SYS_ADMIN_PROFILE_CREATE.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminUsersController.class).update(null)).withRel(SYS_ADMIN_PROFILE_UPDATE.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminUsersController.class).delete(null)).withRel(SYS_ADMIN_PROFILE_DELETE.toString())),

                                    linksConverter.convert(linkTo(methodOn(SysAdminMessageSummaryController.class).disable(null)).withRel(MESSAGE_SUMMARY_DISABLE.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminMessageSummaryController.class).countMessageSummariesToDisable(null)).withRel(COUNT_MESSAGE_SUMMARIES_TO_DISABLE.toString())),

                                    linksConverter.convert(linkTo(methodOn(SysAdminLogsController.class).get(null, null, null, null, null)).withRel(LOG_LEVEL_SEARCH.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminLogsController.class).update(null, null)).withRel(LOG_LEVEL_UPDATE.toString())),
                                    linksConverter.convert(linkTo(methodOn(SysAdminLogsController.class).reset()).withRel(LOG_LEVEL_RESET.toString()))
                            )
                    );
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
        return rootLinks;
    }
}
