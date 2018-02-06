package de.micromata.jira.rest.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.google.gson.Gson;

import de.micromata.jira.rest.JiraRestClient;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.domain.ProjectBean;
import de.micromata.jira.rest.core.domain.WorklogBean;
import de.micromata.jira.rest.core.jql.*;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.WorklogSimplePO;
import de.micromata.jira.rest.custom.util.ModelUtil;

public class SearchClient extends BaseClient {
    protected static final String ISSUEKEY_TO_SEARCH = "MATSUP-1";
    protected static final String PROJECT_TO_SEARCH = "MATSUP";
    static final Gson gson = new Gson();
    public static JiraRestClient restClient;

    public static void main(String[] args) throws Exception {
        restClient = connect();
        searchIssues();
        // searchProjects();
        disConnect();
    }



    private static void searchProjects() throws Exception {
        final Future<ProjectBean> future = restClient.getProjectClient().getProjectByKey(PROJECT_TO_SEARCH);
        final ProjectBean project = future.get();
        System.out.println(gson.toJson(project));
    }

    private static void searchIssues() throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.PROJECT, EOperator.EQUALS, "CNTMAT")
                // .and().addCondition(EField.STATUS, EOperator.EQUALS, STATUS_OPEN)
                // .and().addCondition(EField.UPDATED, EOperator.GREATER_THAN, "2018-01-15")
                .and().addCondition(EField.ISSUE_KEY, EOperator.EQUALS, "CNTMAT-5357")
                .orderBy(SortOrder.ASC, EField.CREATED);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.ISSUE_TYPE, EField.PRIORITY, EField.WORKLOG);
        // jsb.addExpand(EField.TRANSITIONS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        Map<String, IssueSimplePO> issueMap = new HashMap<>();
        List<WorklogSimplePO> worklogs = new ArrayList<>();
        jqlSearchResult.getIssues().forEach(issue -> {
            issueMap.put(issue.getKey(), ModelUtil.simpleIssue(issue));
            String issueKey = issue.getKey();
            WorklogBean worklogBean = issue.getFields().getWorklog();
            if (worklogBean != null && worklogBean.getWorklogs() != null) {
                worklogBean.getWorklogs().forEach(item -> worklogs.add(ModelUtil.simpleWorklogItem(issueKey, item)));
            }
        });
        System.out.println(issueMap);
        System.out.println(worklogs);
    }

    private static void searchWorkLog() throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.PROJECT, EOperator.EQUALS, "CNTMAT")
                // .and().addCondition(EField.STATUS, EOperator.EQUALS, STATUS_OPEN)
                .and().addCondition(EField.ISSUE_KEY, EOperator.EQUALS, "CNTMAT-1995")
                .orderBy(SortOrder.ASC, EField.CREATED);
        jsb.setJql(jql);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.ISSUE_TYPE, EField.PRIORITY,
                EField.TIME_SPENT);
        jsb.addExpand(EField.TRANSITIONS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        Gson gson = new Gson();
        jqlSearchResult.getIssues().forEach(issue -> System.out.println(gson.toJson(issue)));
    }

}
