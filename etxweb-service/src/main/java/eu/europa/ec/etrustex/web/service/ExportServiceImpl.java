package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.DateFormatters;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.ChannelExportItem;
import eu.europa.ec.etrustex.web.exchange.model.UserExportItem;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static eu.europa.ec.etrustex.web.common.DbStringListsSeparators.DB_STRING_LIST_SEPARATOR;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportServiceImpl implements ExportService {

    private static final String NOT_APPLICABLE = "N.A.";
    private final UserProfileRepository userProfileRepository;
    private final GroupRepository groupRepository;

    private final ExchangeRuleRepository exchangeRuleRepository;

    @Override
    @Transactional
    public InputStream exportUsersAndFunctionalMailboxes(Long businessId) {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Users and functional mailboxes");

        exportUsers(sheet, workbook, businessId, GroupType.BUSINESS);
        exportFunctionalMailboxes(sheet, workbook, businessId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        autoSizeColumns(sheet);

        try {
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }
    @Override
    @Transactional
    public InputStream exportUsersByEntity(Long entityId){
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Users");
        exportUsers(sheet, workbook, entityId, GroupType.ENTITY);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        autoSizeColumns(sheet);

        try {
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    @Transactional
    public InputStream exportChannelsAndParticipants(Long businessId){
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Channels and Participants");
        exportChannels(sheet, workbook, businessId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        autoSizeColumns(sheet);

        try {
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public String getExportUsersFilename(GroupType groupType) {
        if(groupType.equals(GroupType.BUSINESS)){
            return "export-users-fm-" + DateFormatters.getYearMonthDayFormatter().format(new Date()) + ".xlsx";
        }else{
            return "export-users-" + DateFormatters.getYearMonthDayFormatter().format(new Date()) + ".xlsx";
        }
    }

    private void exportChannels(Sheet sheet, Workbook workbook, Long businessId) {
        CellStyle headerCellStyle = getHeaderCellStyle(workbook);

        createChannelsHeader(sheet, headerCellStyle);

        CellStyle bodyCellStyle = getBodyCellStyle(workbook);

        exchangeRuleRepository.exportMembersByBusinessId(businessId)
                .forEach(channelExportItem -> addChannelItem(channelExportItem, bodyCellStyle, sheet));
    }

    private void exportUsers(Sheet sheet, Workbook workbook, Long groupId, GroupType groupType) {
        CellStyle headerCellStyle = getHeaderCellStyle(workbook);

        createUsersHeader(sheet, headerCellStyle, groupType);

        CellStyle bodyCellStyle = getBodyCellStyle(workbook);

        if(groupType.equals(GroupType.BUSINESS)){
            userProfileRepository.exportByBusinessId(groupId)
                    .forEach(userExportItem -> addUserItem(userExportItem, bodyCellStyle, sheet, groupType));
        }else{
            userProfileRepository.exportByEntityId(groupId)
                    .forEach(userExportItem -> addUserItem(userExportItem, bodyCellStyle, sheet, groupType));
        }
    }

    private void exportFunctionalMailboxes(Sheet sheet, Workbook workbook, Long businessId) {

        CellStyle headerCellStyle = getHeaderCellStyle(workbook);
        createUsersHeader(sheet, headerCellStyle, GroupType.BUSINESS);

        CellStyle bodyCellStyle = getBodyCellStyle(workbook);

        groupRepository.findByParentId(businessId)
                .forEach(group -> {

                    Map<String, Pair<Boolean, Boolean>> functionalEmails = new HashMap<>();

                    if (StringUtils.isNotEmpty(group.getNewMessageNotificationEmailAddresses())) {
                        Arrays.asList(group.getNewMessageNotificationEmailAddresses().split(DB_STRING_LIST_SEPARATOR.toString()))
                                .forEach(newMessageNotificationEmail -> functionalEmails.put(newMessageNotificationEmail, Pair.of(true, false)));
                    }
                    if (StringUtils.isNotEmpty(group.getStatusNotificationEmailAddress())) {
                        String statusChangeNotificationEmail = group.getStatusNotificationEmailAddress();
                        if (functionalEmails.containsKey(statusChangeNotificationEmail)) {
                            functionalEmails.put(statusChangeNotificationEmail, Pair.of(true, true));
                        } else {
                            functionalEmails.put(statusChangeNotificationEmail, Pair.of(false, true));
                        }
                    }

                    functionalEmails.forEach((s, booleanBooleanPair) -> {
                        log.trace("Email {} -newMessage: {} - status: {}", s, booleanBooleanPair.getLeft(), booleanBooleanPair.getRight());
                        UserExportItem userExportItem = UserExportItem.builder()
                                .entityIdentifier(group.getIdentifier())
                                .entityName(group.getName())
                                .name("Functional mailbox")
                                .roleName(null)
                                .euLoginId("")
                                .alternativeEmail(s)
                                .messageNotification(booleanBooleanPair.getLeft())
                                .statusNotification(booleanBooleanPair.getRight())
                                .build();

                        addUserItem(userExportItem, bodyCellStyle, sheet, GroupType.BUSINESS);

                    });
                });
    }

    private void addChannelItem(ChannelExportItem channelExportItem, CellStyle cellStyle, Sheet sheet) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        addCell(row, cellStyle, channelExportItem.getChannelName());
        addCell(row, cellStyle, channelExportItem.getEntityName());
        addCell(row, cellStyle, channelExportItem.getEntityDisplayName());
        addCell(row, cellStyle, channelExportItem.getEntityRole().toString());
    }

    private void addUserItem(UserExportItem userExportItem, CellStyle cellStyle, Sheet sheet, GroupType groupType) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        if(groupType.equals(GroupType.BUSINESS)){
            addCell(row, cellStyle, userExportItem.getEntityIdentifier());
            addCell(row, cellStyle, userExportItem.getEntityName());
        }
        addCell(row, cellStyle, userExportItem.getName());
        addCell(row, cellStyle, getRole(userExportItem));
        addCell(row, cellStyle, getEuLogin(userExportItem));
        addCell(row, cellStyle, getEuLoginEmail(userExportItem));
        addCell(row, cellStyle, userExportItem.getAlternativeEmail());
        addCell(row, cellStyle, getUseAlternativeEmail(userExportItem));
        addCell(row, cellStyle, getMessageNotification(userExportItem));
        addCell(row, cellStyle, getStatusNotification(userExportItem));
        addCell(row, cellStyle, getRetentionWarningNotification(userExportItem));
        addCell(row, cellStyle, getStatus(userExportItem));
    }

    private void createChannelsHeader(Sheet sheet, CellStyle cellStyle){
        Row row = sheet.createRow(0);

        // lock the header row
        sheet.createFreezePane(0, 1);

        addCell(row, cellStyle, "Channel name");
        addCell(row, cellStyle, "Entity identifier");
        addCell(row, cellStyle, "Entity name");
        addCell(row, cellStyle, "Entity Role");
    }

    private void createUsersHeader(Sheet sheet, CellStyle cellStyle, GroupType groupType) {
        Row row = sheet.createRow(0);

        // lock the header row
        sheet.createFreezePane(0, 1);

        if(groupType.equals(GroupType.BUSINESS)){
            addCell(row, cellStyle, "Entity identifier");
            addCell(row, cellStyle, "Entity name");
        }
        addCell(row, cellStyle, "User name");
        addCell(row, cellStyle, "User role");
        addCell(row, cellStyle, "EU Login ID");
        addCell(row, cellStyle, "EU Login email address");
        addCell(row, cellStyle, "Alternative email address");
        addCell(row, cellStyle, "Use Alternative email");
        addCell(row, cellStyle, "Message notification");
        addCell(row, cellStyle, "Status notification");
        addCell(row, cellStyle, "Retention warning notification");
        addCell(row, cellStyle, "User status");
    }

    private void addCell(Row row, CellStyle cellStyle, @Nullable Object value) {
        int lastCellNum = row.getLastCellNum() >= 0 ? row.getLastCellNum() : 0;
        Cell cell = row.createCell(lastCellNum);
        cell.setCellStyle(cellStyle);
        if (value != null) {
            cell.setCellValue(value.toString());
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);

        return createStyle(workbook, font);
    }

    private CellStyle getBodyCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(false);
        font.setFontHeightInPoints((short) 12);

        return createStyle(workbook, font);
    }

    private CellStyle createStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        return style;
    }

    private void autoSizeColumns(Sheet sheet) {
        int column = 0;
        Cell cell;
        do {
            cell = sheet.getRow(0).getCell(column);
            sheet.autoSizeColumn(column++);

        } while (cell != null);
    }

    private String getRole(UserExportItem userExportItem) {
        RoleName roleName = userExportItem.getRoleName();

        String returnValue = NOT_APPLICABLE;

        GroupType groupType = userExportItem.getGroupType();
        if (GroupType.BUSINESS.equals(groupType) && RoleName.GROUP_ADMIN.equals(roleName)) {
            returnValue = "BUSINESS ADMINISTRATOR";
        }
        if (GroupType.ENTITY.equals(groupType)) {
            if (RoleName.GROUP_ADMIN.equals(roleName)) {
                returnValue = "ENTITY ADMINISTRATOR";
            } else if (RoleName.OPERATOR.equals(roleName)) {
                returnValue = roleName.toString();
            }
        }
        return returnValue;
    }

    private String getMessageNotification(UserExportItem userExportItem) {
        return getNotificationValue(userExportItem.getRoleName(), userExportItem.getMessageNotification());

    }

    private String getStatusNotification(UserExportItem userExportItem) {
        return getNotificationValue(userExportItem.getRoleName(), userExportItem.getStatusNotification());
    }

    private String getRetentionWarningNotification(UserExportItem userExportItem) {
        return getNotificationValue(userExportItem.getRoleName(), userExportItem.getRetentionWarningNotification());
    }

    private String getNotificationValue(RoleName roleName, Boolean setValue) {
        if (RoleName.GROUP_ADMIN.equals(roleName)) {
            return NOT_APPLICABLE;
        }
        if (setValue == null) {
            setValue = false;
        }

        return setValue.toString();
    }

    private String getEuLogin(UserExportItem userExportItem) {
        return StringUtils.isNotEmpty(userExportItem.getEuLoginId()) ? userExportItem.getEuLoginId() : NOT_APPLICABLE;
    }

    private String getStatus(UserExportItem userExportItem) {
        if (userExportItem.getStatus() == null) {
            return NOT_APPLICABLE;
        } else {
            return Boolean.TRUE.equals(userExportItem.getStatus()) ? "Active" : "Inactive";
        }
    }

    private String getEuLoginEmail(UserExportItem userExportItem) {
        return StringUtils.isNotEmpty(userExportItem.getEuLoginId()) ? userExportItem.getEuLoginEmail() : NOT_APPLICABLE;
    }

    private String getUseAlternativeEmail(UserExportItem userExportItem) {
        return StringUtils.isNotEmpty(userExportItem.getEuLoginId()) ? userExportItem.getUseAlternativeEmail().toString() : NOT_APPLICABLE;
    }

}

