package de.micromata.jira.rest.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.micromata.jira.rest.JiraRestClient;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlSearchBean;
import de.micromata.jira.rest.core.jql.SortOrder;
import de.micromata.jira.rest.core.util.JsonUtil;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.ReleaseData;
import de.micromata.jira.rest.custom.util.IssueQueryUtil;
import de.micromata.jira.rest.custom.util.ModelUtil;

public class ReleaseGanttProject extends BaseClient {
    static final String releaseTaskFile = "taskR60.txt";
    static final String releaseBugFile = "bugR60.txt";

    public static void main(String[] args) throws Exception {
        try {
            connect();
            ReleaseData releaseData = new ReleaseData();
            releaseData.setDevStart(LocalDate.of(2019, 03, 25));
            releaseData.setDevEnd(LocalDate.of(2019, 04, 30));
            List<IssueSimplePO> releaseTasks = releaseTasks(restClient, "\"R6.6.0\"");
            // List<IssueSimplePO> releaseTasks = IssueQueryUtil.releaseTasks(restClient, "\"R6.6.0\"");
            releaseData.setTasks(releaseTasks);
            // List<IssueSimplePO> ongoingBugs = IssueQueryUtil.devOngoingBugs(restClient);
            // releaseData.setBugs(ongoingBugs);
            releaseData.adjustForGantt();
            writeCSVforGanntProject(releaseTasks);
        } finally {
            disConnect();
        }
        // searchProjects();
    }

    private static void writeCSVforGanntProject(List<IssueSimplePO> releaseTasks) {
        List<String> csvLines = new ArrayList<>();
        String taskHeaderStr = "ID, Name, Begin date, End date, Duration, Completion, Cost, Coordinator, Predecessors, Outline number, Resources, Assignments, Task color, Web Link, Notes";
        csvLines.add(taskHeaderStr);

    }

    static List<IssueSimplePO> releaseTasks(JiraRestClient restClient, String release) throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.PROJECT, EOperator.IN, "CNTMAT", "MATSUP").and()
                .addCondition(EField.FIX_VERSION, EOperator.EQUALS, release)// .and()
                // .addCondition(EField.ISSUE_TYPE, EOperator.NOT_EQUALS, JqlConstants.ISSUETYPE_BUG)
                // .addCondition(EField.ISSUE_KEY, EOperator.IN, "MATSUP-1477", "MATSUP-1686", "MATSUP-1687",
                // "MATSUP-1688", "MATSUP-1689", "MATSUP-1691")
                .orderBy(SortOrder.ASC, EField.PRIORITY);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(IssueQueryUtil.ISSUE_QUERY_FIELDS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        List<IssueSimplePO> list = jqlSearchResult.getIssues().stream().map(ModelUtil::simpleIssue)
                .collect(Collectors.toList());
        Files.write(Paths.get("test.json"), JsonUtil.obj2Str(list).getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        return list;
    }
}
