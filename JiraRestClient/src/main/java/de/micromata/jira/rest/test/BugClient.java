package de.micromata.jira.rest.test;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.jql.*;
import de.micromata.jira.rest.custom.model.DailyBugStatus;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.util.ModelUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class BugClient extends BaseClient {
    private static final EField[] BUG_RESULT_FIELDS = { EField.ISSUE_KEY, EField.STATUS, EField.PRIORITY,
            EField.SUMMARY, EField.ASSIGNEE, EField.OWNER, EField.ISSUE_TYPE, EField.RESOLUTION, EField.RESOLUTION_DATE,
            EField.CREATED };

    public static void main(String[] args) throws Exception {
        try {
            connect();
            LocalDate fromDay = LocalDate.now().minusDays(9);
            Map<String, IssueSimplePO> bugMap = new HashMap<>();
            List<IssueSimplePO> changedBugs = loadBugs(fromDay);
            changedBugs.forEach(bug -> bugMap.put(bug.getKey(), bug));
            List<IssueSimplePO> openedBugs = openedBugs();
            openedBugs.forEach(bug -> bugMap.put(bug.getKey(), bug));
            // bugMap.forEach((k, v) -> {
            // String fmt = "%12s, %4s, %12s, %20s, %s, %s";
            // System.out.println(String.format(fmt, v.getKey(), v.getStatus(), v.getPriority(), v.getOwner(),
            // v.getSummary(), v.getSelfUrl()));
            // });
            List<DailyBugStatus> list = calcDailyBugStatus(bugMap, fromDay);
        } finally {
            disConnect();
        }
    }

    private static List<DailyBugStatus> calcDailyBugStatus(Map<String, IssueSimplePO> bugMap, LocalDate fromDay) {
        TreeMap<LocalDate, DailyBugStatus> bugStatusMap = new TreeMap<>();
        LocalDate now = LocalDate.now();
        for (LocalDate from = fromDay; from.isBefore(now.plusDays(1)); from = from.plusDays(1)) {
            bugStatusMap.put(from, new DailyBugStatus(from));
        }
        Set<String> openedBugs = new TreeSet<>();
        DailyBugStatus todayBugStatus = new DailyBugStatus();
        todayBugStatus.setDay(now);
        bugStatusMap.put(now, todayBugStatus);
        bugMap.forEach((k, v) -> {
            String issueKey = v.getKey();
            LocalDate resolutionDate = v.getResolutionDate();
            if (resolutionDate == null) {
                openedBugs.add(issueKey);
                resolutionDate = fromDay.minusDays(1);
            }
            LocalDate createDate = v.getCreatedDate();
            bugStatusMap.computeIfPresent(createDate, (k1, v1) -> {
                v1.addtCreatedBug(issueKey);
                return v1;
            });
            bugStatusMap.computeIfPresent(resolutionDate, (k1, v1) -> {
                v1.addtFixedBug(issueKey);
                return v1;
            });
        });
        bugStatusMap.get(now).setOpenedBugs(openedBugs);

        for (LocalDate from = now; from.isAfter(fromDay); from = from.minusDays(1)) {
            DailyBugStatus bugStatus = bugStatusMap.get(from);
            DailyBugStatus preDayBugStatus = bugStatusMap.get(from.minusDays(1));
            bugStatus.getOpenedBugs().forEach(preDayBugStatus::addOpenedBug);
            bugStatus.getFixedBugs().forEach(preDayBugStatus::addOpenedBug);
            bugStatus.getCreatedBugs().forEach(s -> preDayBugStatus.getOpenedBugs().remove(s));
        }

        System.out.println(openedBugs);
        System.out.println(bugStatusMap);
        bugStatusMap.values().forEach(bs -> {
            String fmt = "%10s\n opened=%4d {%s}\n fixed=%4d {%s}\n raised=%4d {%s} \n\n";
            System.out.println(String.format(fmt, bs.getDay().format(Const.YEAR2DAY_FMT), bs.getOpenedBugs().size(),
                    bs.getOpenedBugs(), bs.getFixedBugs().size(), bs.getFixedBugs(), bs.getCreatedBugs().size(),
                    bs.getCreatedBugs()));
        });
        return new ArrayList(bugStatusMap.values());
    }

    private static List<IssueSimplePO> loadBugs(LocalDate fromDate) throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder
                .addCondition(EField.PROJECT, EOperator.IN, Const.PROJECTS.toArray(new String[Const.PROJECTS.size()]))
                .and().addCondition(EField.UPDATED, EOperator.GREATER_THAN_EQUALS, fromDate.format(Const.YEAR2DAY_FMT))
                .and().addCondition(EField.ISSUE_TYPE, EOperator.EQUALS, JqlConstants.ISSUETYPE_BUG).and()
                .addCondition(EField.PRIORITY, EOperator.IN, "P1", "P2", "P3").orderBy(SortOrder.ASC, EField.PRIORITY);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(BUG_RESULT_FIELDS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        return jqlSearchResult.getIssues().stream().map(ModelUtil::simpleIssue).collect(Collectors.toList());
    }

    private static List<IssueSimplePO> openedBugs() throws Exception {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder
                .addCondition(EField.PROJECT, EOperator.IN, Const.PROJECTS.toArray(new String[Const.PROJECTS.size()]))
                .and().addCondition(EField.RESOLUTION, EOperator.EQUALS, "Unresolved").and()
                .addCondition(EField.ISSUE_TYPE, EOperator.EQUALS, JqlConstants.ISSUETYPE_BUG).and()
                .addCondition(EField.PRIORITY, EOperator.IN, "P1", "P2", "P3").orderBy(SortOrder.ASC, EField.PRIORITY);
        jsb.setJql(jql);
        jsb.setMaxResults(1000);
        jsb.addField(BUG_RESULT_FIELDS);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        return jqlSearchResult.getIssues().stream().map(ModelUtil::simpleIssue).collect(Collectors.toList());
    }

}
