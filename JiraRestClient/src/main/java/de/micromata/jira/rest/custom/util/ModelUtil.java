package de.micromata.jira.rest.custom.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.IssueBean;
import de.micromata.jira.rest.core.domain.ResolutionBean;
import de.micromata.jira.rest.core.domain.UserBean;
import de.micromata.jira.rest.core.domain.WorklogItemBean;
import de.micromata.jira.rest.core.domain.customFields.UserSelectBean;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.util.StringUtil;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.WorklogSimplePO;

public class ModelUtil {

    public static IssueSimplePO simpleIssue(IssueBean issue) {
        IssueSimplePO po = new IssueSimplePO();
        po.setKey(issue.getKey());
        po.setId(issue.getId());
        po.setIssueType(issue.getFields().getIssuetype().getName());
        po.setPriority(issue.getFields().getPriority().getName());
        po.setSelfUrl(issue.getSelf());
        po.setStatus(issue.getFields().getStatus().getName());
        po.setSummary(issue.getFields().getSummary());
        UserBean assignee = issue.getFields().getAssignee();
        if (assignee != null) {
            po.setAssignee(issue.getFields().getAssignee().getName().trim());
        } else {
            po.setAssignee(Const.ANONYMOUS_USER);
        }
        String dueDateStr = issue.getFields().getDuedate();
        if (dueDateStr != null) {
            po.setDueDate(LocalDate.parse(dueDateStr, Const.YEAR2DAY_FMT));
        }
        Optional<UserBean> owner = issue.getFields().getCustomFields().stream()
                .filter(u -> EField.OWNER.getField().equals(u.getId())).map(u -> ((UserSelectBean) u).getUsers().get(0))
                .findFirst();
        if (owner.isPresent()) {
            po.setOwner(owner.get().getName().trim());
        } else {
            po.setOwner(Const.ANONYMOUS_USER);
        }
        Integer timeEstimate = issue.getFields().getTimeoriginalestimate();
        if (timeEstimate != null) {
            po.setEstHour(timeEstimate / 3600D);
        } else {
            po.setEstHour(0D);
        }
        Integer aggrTimeEstimate = issue.getFields().getAggregatetimeoriginalestimate();
        if (aggrTimeEstimate != null) {
            po.setSumEstHour(aggrTimeEstimate / 3600D);
        } else {
            po.setSumEstHour(0D);
        }
        ResolutionBean resolution = issue.getFields().getResolution();
        if (resolution != null) {
            po.setResolution(resolution.getName());
        }
        String resolutionDate = issue.getFields().getResolutiondate();
        if (resolutionDate != null) {
            po.setResolutionDate(LocalDateTime.parse(resolutionDate, Const.YAER2MS_TZ_FMT).toLocalDate());
        }
        String createdTimeStr = issue.getFields().getCreated();
        if (createdTimeStr != null) {
            po.setCreatedDate(LocalDateTime.parse(createdTimeStr, Const.YAER2MS_TZ_FMT).toLocalDate());
        }
        IssueBean parent = issue.getFields().getParent();
        if (parent != null) {
            po.setParentKey(parent.getKey());
        }
        return po;
    }

    public static WorklogSimplePO simpleWorklogItem(String issueKey, WorklogItemBean item) {
        WorklogSimplePO po = new WorklogSimplePO();
        po.setIssueKey(issueKey);
        po.setWorkDate(LocalDate.parse(item.getStarted(), Const.YAER2MS_TZ_FMT));
        po.setTimeSpentHours(item.getTimeSpentSeconds() / 3600D);
        po.setUserId(item.getAuthor().getName().trim());
        po.setUserName(item.getAuthor().getDisplayName());
        po.setWorkDesc(StringUtil.filterSpecialChar(item.getComment()));
        return po;
    }
}
