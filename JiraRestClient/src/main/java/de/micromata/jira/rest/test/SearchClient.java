package de.micromata.jira.rest.test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.google.gson.Gson;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.domain.ProjectBean;
import de.micromata.jira.rest.core.domain.WorklogBean;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlSearchBean;
import de.micromata.jira.rest.core.jql.SortOrder;
import de.micromata.jira.rest.custom.model.IssueMonitorQueryBean;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.WorklogSimplePO;
import de.micromata.jira.rest.custom.util.ModelUtil;
import de.micromata.jira.rest.custom.util.ReportUtil;

public class SearchClient extends BaseClient {

    protected static final String PROJECT_TO_SEARCH = "MATSUP";

    public static void main(String[] args) throws Exception {
        try {
            restClient = connect();
            searchIssues();
            File worklogsCsv = new File(
                    ReportUtil.WORK_LOGS_FILE_PREFIX + "-" + LocalDate.now().format(Const.YEAR2DAY_FMT) + ".csv");
            File userWorklogSummaryCsv = new File(ReportUtil.USER_WORKLOG_SUMMARY_FILE_PREFIX + "-"
                    + LocalDate.now().format(Const.YEAR2DAY_FMT) + ".csv");
            UpdateIssueClient.updateWorkLogIssueAttachment("CNTMAT-5992", Arrays.asList(worklogsCsv, userWorklogSummaryCsv),
                    new HashSet<String>() {
                        {
                            add(ReportUtil.WORK_LOGS_FILE_PREFIX);
                            add(ReportUtil.USER_WORKLOG_SUMMARY_FILE_PREFIX);
                        }
                    });
        } finally {
            disConnect();
        }

        // searchProjects();

    }

    private static void searchProjects() throws Exception {
        final Future<ProjectBean> future = restClient.getProjectClient().getProjectByKey(PROJECT_TO_SEARCH);
        final ProjectBean project = future.get();
        System.out.println(gson.toJson(project));
    }

    private static void searchIssues() throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        IssueMonitorQueryBean issueQb = new IssueMonitorQueryBean();
        LocalDate now = LocalDate.now();
        // now=LocalDate.of(2018,04,01);
        issueQb.setStartDate(now.minusDays(14));
        // issueQb.setStartDate(LocalDate.of(2018,03,01));
        issueQb.setEndDate(now);

        String jql = builder.addCondition(EField.PROJECT, EOperator.IN, "CNTMAT", "MATSUP").and()
                .addCondition(EField.UPDATED, EOperator.GREATER_THAN_EQUALS,
                        issueQb.getStartDate().minusDays(7).format(Const.YEAR2DAY_FMT))
                .orderBy(SortOrder.ASC, EField.CREATED);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.ISSUE_TYPE, EField.PRIORITY, EField.WORKLOG,
                EField.SUMMARY);
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
        ReportUtil.issueCsvReport(issueQb, issueMap);
        ReportUtil.worklogCsvReport(issueQb, issueMap, worklogs);

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
