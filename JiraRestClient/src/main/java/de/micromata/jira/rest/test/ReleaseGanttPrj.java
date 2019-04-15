package de.micromata.jira.rest.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.micromata.jira.rest.JiraRestClient;
import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.jql.*;
import de.micromata.jira.rest.core.util.JsonUtil;
import de.micromata.jira.rest.core.util.StringUtil;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.ReleaseData;
import de.micromata.jira.rest.custom.model.ganttprj.GPAllocation;
import de.micromata.jira.rest.custom.model.ganttprj.GPResource;
import de.micromata.jira.rest.custom.model.ganttprj.GPTask;
import de.micromata.jira.rest.custom.model.ganttprj.GanttPrj;
import de.micromata.jira.rest.custom.util.IssueQueryUtil;
import de.micromata.jira.rest.custom.util.ModelUtil;
import de.micromata.jira.rest.custom.util.ReportUtil;

public class ReleaseGanttPrj extends BaseClient {
    static final String resource_fmt = "<resource id=\"%s\" name=\"%s\" function=\"Default:0\" contacts=\"\" phone=\"\"><rate name=\"standard\" value=\"1\"/></resource>";
    static final String allocation_fmt = " <allocation task-id=\"%s\" resource-id=\"%s\" function=\"Default:0\" responsible=\"true\" load=\"100.0\"/>";

    static final Map<String, String> statusColorMap = new HashMap() {
        {
            put("resolved", "#00ff00");
            put("verified", "#00ff00");
            put("closed", "#00ff00");
            put("in progress", "#0000ff");
        }
    };

    public static void main(String[] args) throws Exception {
        try {
            //connect();
            ReleaseData releaseData = new ReleaseData();
            releaseData.setDevStart(LocalDate.of(2019, 03, 25));
            releaseData.setDevEnd(LocalDate.of(2019, 04, 30));
            List<IssueSimplePO> releaseTasks = releaseTasks1(restClient, "\"R6.6.0\"");
            releaseData.setTasks(releaseTasks);
            GanttPrj ganttPrj = adjustForGantt(releaseTasks);
            generateGanFile(ganttPrj);
        } finally {
           // disConnect();
        }
        // searchProjects();
    }

    private static void generateGanFile(GanttPrj ganttPrj) throws Exception {
        List<String> allTpl = Files.readAllLines(Paths.get("release_gan_tpl.xml"));
        LinkedList<String> allLines = new LinkedList();
        for (String line : allTpl) {
            if (line == null) {
                continue;
            }
            allLines.add(line);
            if (line.trim().equals("</taskproperties>")) {
                allLines.addAll(generateGanTasks(ganttPrj));
            }
            if (line.trim().equals("<resources>")) {
                ganttPrj.getResources().stream().map(r -> String.format(resource_fmt, r.getId(), r.getMemberId()))
                        .forEach(allLines::add);
            }
            if (line.trim().equals("<allocations>")) {
                ganttPrj.getAllocations().stream()
                        .map(a -> String.format(allocation_fmt, a.getTaskId(), a.getResourceId()))
                        .forEach(allLines::add);
            }
        }
        Files.write(Paths.get("release" + System.currentTimeMillis() + ".gan"), allLines, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
    }

    private static List<String> generateGanTasks(GanttPrj ganttPrj) {
        List<String> taskLines = new ArrayList<>();
        for (GPTask task : ganttPrj.getTasks()) {
            taskLines.addAll(task.buildGanttXmlLines());
        }
        return taskLines;
    }

    private static GanttPrj adjustForGantt(List<IssueSimplePO> releaseTasks) {
        GanttPrj ganttPrj = new GanttPrj();
        Set<String> members = new HashSet<>();
        Map<String, GPTask> taskMap = new HashMap<>();
        Map<String, String> allocationMap = new HashMap<>();
        releaseTasks.forEach(issue -> {
            String id = issue.getId();
            String member = issue.getOwner();
            members.add(member);
            allocationMap.put(id, member);
            GPTask task = convertIssue2Task(issue);
            taskMap.put(task.getId(), task);
        });
        Set<String> subTasks = new HashSet<>();
        taskMap.forEach((k, v) -> {
            GPTask task = taskMap.get(v.getParentId());
            if (task != null) {
                task.addSubIssues(v);
                subTasks.add(k);
            }
        });
        subTasks.forEach(id -> taskMap.remove(id));
        ganttPrj.setTasks(new ArrayList<>(taskMap.values()));
        members.forEach(s -> ganttPrj.addResources(new GPResource(Math.abs(s.hashCode()) + "", s)));
        allocationMap.forEach((k, v) -> ganttPrj.addAllocations(new GPAllocation(k, Math.abs(v.hashCode()) + "")));
        return ganttPrj;
    }

    private static GPTask convertIssue2Task(IssueSimplePO issue) {
        GPTask task = new GPTask();
        LocalDate dueDate = issue.getDueDate();
        if (dueDate == null) {
            dueDate = LocalDate.now();
        }
        int estDays = ReportUtil.hour2day(issue.getEstHour());
        String status = issue.getStatus();
        String id = issue.getId();
        task.setId(id);
        task.setParentId(issue.getParentId());
        task.setName(buildGanttUnresolvedTaskLine(issue));
        task.setColor(calcColor(dueDate, status));
        task.setStart(ReportUtil.calcNatureStart(dueDate, estDays).format(Const.YEAR2DAY_FMT));
        task.setDuration(ReportUtil.hour2day(issue.getEstHour()) + "");
        task.setThirdDate(LocalDate.now().format(Const.YEAR2DAY_FMT));
        return task;
    }

    private static String calcColor(LocalDate dueDate, String status) {
        String color = statusColorMap.getOrDefault(status.toLowerCase(), "#ffff00");
        if (!dueDate.isAfter(LocalDate.now()) && ("#ffff00".equals(color) || "#0000ff".equals(color))) {
            color = "#ff0000";
        }
        return color;
    }

    public static List<IssueSimplePO> releaseTasks(JiraRestClient restClient, String release) throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.PROJECT, EOperator.IN, "CNTMAT", "MATSUP").and()
                .addCondition(EField.FIX_VERSION, EOperator.EQUALS, release)// .and()
                // .addCondition(EField.ISSUE_TYPE, EOperator.NOT_EQUALS, JqlConstants.ISSUETYPE_BUG)
                .orderBy(SortOrder.ASC, EField.PRIORITY);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(IssueQueryUtil.ISSUE_QUERY_FIELDS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        List<IssueSimplePO> issuePOs = jqlSearchResult.getIssues().stream().map(ModelUtil::simpleIssue)
                .collect(Collectors.toList());
        Files.write(Paths.get("data-1.json"),
                issuePOs.stream().map(IssueSimplePO::toString).collect(Collectors.toList()), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        return issuePOs;
    }

    public static List<IssueSimplePO> releaseTasks1(JiraRestClient restClient, String release) throws Exception {
        List<IssueSimplePO> issuePOs = Files.readAllLines(Paths.get("data-1.json")).stream()
                .map(s -> JsonUtil.str2obj(s, IssueSimplePO.class)).collect(Collectors.toList());
        return issuePOs;
    }

    private static String buildGanttUnresolvedTaskLine(IssueSimplePO issue) {
        return String.format("%s[%s][%s][%s]%s", issue.getKey(), issue.getIssueType(), issue.getPriority(),
                issue.getStatus(), StringUtil.filterSpecialChar(issue.getSummary(), " "));
    }
}
