package de.micromata.jira.rest.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlSearchBean;
import de.micromata.jira.rest.core.jql.SortOrder;
import de.micromata.jira.rest.core.util.RestException;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.util.ModelUtil;

public class ChangeStatusClient extends BaseClient {

    public static void main(String[] args) throws Exception {
        try {
            restClient = connect();
            Map<String, IssueSimplePO> result = searchIssues();
            // Map<String, IssueSimplePO> result = new HashMap<>();
            // result.put("CNTMAT-139", null);
            updateIssueStatus(result);
        } finally {
            disConnect();
        }

        // searchProjects();

    }

    private static void updateIssueStatus(Map<String, IssueSimplePO> result) throws URISyntaxException, IOException, RestException {
        // 331, verify, 341 close
        for (String key : result.keySet()) {
            try {
                restClient.getIssueClient().updateIssueTransitionByKey(key, 331);
                restClient.getIssueClient().updateIssueTransitionByKey(key, 341);
                System.out.println("update: " + key);
            } catch (Exception e) {
                System.out.println("update failed: " + key);
            }
        }
    }


    private static Map<String, IssueSimplePO> searchIssues() throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        LocalDate splitDay = LocalDate.now().minusDays(91);
        String jql = builder.addCondition(EField.PROJECT, EOperator.EQUALS, "CNTMAT").and()
                .addCondition(EField.STATUS, EOperator.EQUALS, "Resolved").and()
                .addCondition(EField.RESOLUTION_DATE, EOperator.LESS_THAN, splitDay.format(Const.YEAR2DAY_FMT))
                // .and().addCondition(EField.ISSUE_KEY, EOperator.EQUALS, "CNTMAT-139")
                .orderBy(SortOrder.ASC, EField.CREATED);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.ISSUE_TYPE, EField.PRIORITY, EField.WORKLOG,
                EField.SUMMARY);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        Map<String, IssueSimplePO> issueMap = new TreeMap<>();
        jqlSearchResult.getIssues().forEach(issue -> {
            issueMap.put(issue.getKey(), ModelUtil.simpleIssue(issue));
        });
        System.out.println(issueMap.size());
        System.out.println(issueMap);
        return issueMap;
    }
}
