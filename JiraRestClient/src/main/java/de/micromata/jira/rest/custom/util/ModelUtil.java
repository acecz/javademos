package de.micromata.jira.rest.custom.util;

import java.time.LocalDate;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.IssueBean;
import de.micromata.jira.rest.core.domain.WorklogItemBean;
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
        return po;
    }

    public static WorklogSimplePO simpleWorklogItem(String issueKey, WorklogItemBean item) {
        WorklogSimplePO po = new WorklogSimplePO();
        po.setIssueKey(issueKey);
        po.setWorkDate(LocalDate.parse(item.getStarted(), Const.YAER2MS_TZ_FMT));
        po.setTimeSpentHours(item.getTimeSpentSeconds() / 3600D);
        po.setUserId(item.getAuthor().getName());
        po.setUserName(item.getAuthor().getDisplayName());
        return po;
    }
}
