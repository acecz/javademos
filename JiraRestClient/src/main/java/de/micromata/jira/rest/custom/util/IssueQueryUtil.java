package de.micromata.jira.rest.custom.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.micromata.jira.rest.JiraRestClient;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlConstants;
import de.micromata.jira.rest.core.jql.JqlSearchBean;
import de.micromata.jira.rest.core.jql.SortOrder;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.IssueStatus;

public class IssueQueryUtil {
    public static List<IssueSimplePO> devOngoingBugs(JiraRestClient restClient) throws Exception {
        Set<String> devs = FileUtil.developers().stream().map(s -> "\"" + s + "\"").collect(Collectors.toSet());
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.ISSUE_TYPE, EOperator.EQUALS, JqlConstants.ISSUETYPE_BUG).and()
                .addCondition(EField.STATUS, EOperator.IN, JqlConstants.STATUS_OPEN, JqlConstants.STATUS_REOPENED,
                        JqlConstants.STATUS_IN_PROGRESS)
                .and().addCondition(EField.ASSIGNEE, EOperator.IN, devs.toArray(new String[devs.size()])).and()
                .addCondition(EField.PRIORITY, EOperator.IN, "P1", "P2", "P3").orderBy(SortOrder.ASC, EField.PRIORITY);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.ISSUE_TYPE, EField.PRIORITY, EField.SUMMARY,
                EField.ASSIGNEE, EField.TIME_ORIGINAL_ESTIMATE, EField.AGGREGATE_TIME_ORIGINAL_ESTIMATE, EField.OWNER,
                EField.STATUS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        return jqlSearchResult.getIssues().stream().map(ModelUtil::simpleIssue).collect(Collectors.toList());
    }

    public static List<IssueSimplePO> releaseTasks(JiraRestClient restClient, List<String> taskKeys) throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.ISSUE_KEY, EOperator.IN, taskKeys.toArray(new String[taskKeys.size()]))
                .orderBy(SortOrder.ASC, EField.PRIORITY);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.ISSUE_TYPE, EField.PRIORITY, EField.SUMMARY,
                EField.ASSIGNEE, EField.TIME_ORIGINAL_ESTIMATE, EField.AGGREGATE_TIME_ORIGINAL_ESTIMATE, EField.OWNER,
                EField.STATUS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        return jqlSearchResult.getIssues().stream().map(ModelUtil::simpleIssue).collect(Collectors.toList());
    }
}
