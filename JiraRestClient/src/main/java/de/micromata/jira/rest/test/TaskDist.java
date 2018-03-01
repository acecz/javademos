package de.micromata.jira.rest.test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.domain.UserBean;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlSearchBean;
import de.micromata.jira.rest.core.jql.SortOrder;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.util.FileUtil;
import de.micromata.jira.rest.custom.util.ModelUtil;
import de.micromata.jira.rest.custom.util.ReportUtil;

public class TaskDist extends BaseClient {
    public static void main(String[] args) throws Exception {
        try {
            connect();
            Map<String, UserBean> users = loadConfigUsers();
            List<IssueSimplePO> issues = loadConfigTasks();
            ReportUtil.taskDistRpt(users, issues);
        } finally {
            disConnect();
        }
        // searchProjects();
    }

    private static List<IssueSimplePO> loadConfigTasks() throws Exception {
        List<String> issueIds = FileUtil.releaseTasks();
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.ISSUE_KEY, EOperator.IN, issueIds.toArray(new String[issueIds.size()]))
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

    private static Map<String, UserBean> loadConfigUsers() throws Exception {
        Set<String> devs = FileUtil.allusers();
        Future<List<UserBean>> usersFeature = restClient.getUserClient().getAssignableUserForProject("CNTMAT", 0, 200);
        return usersFeature.get().stream()// .filter(u -> u.getName() != null && devs.contains(u.getName()))
                .sorted(Comparator.comparing(UserBean::getName)).collect(Collectors.toMap(u -> u.getName(), u -> u));
    }
}
