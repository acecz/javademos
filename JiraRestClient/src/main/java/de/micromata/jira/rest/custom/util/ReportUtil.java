package de.micromata.jira.rest.custom.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.UserBean;
import de.micromata.jira.rest.core.util.StringUtil;
import de.micromata.jira.rest.custom.model.*;

public class ReportUtil {
    public static final String WORK_LOGS_FILE_PREFIX = "worklogs";
    public static final String USER_WORKLOG_SUMMARY_FILE_PREFIX = "user-worklog-summary";
    public static final TreeSet<String> HOLIDAYS = new TreeSet<>();

    public static void issueCsvReport(IssueMonitorQueryBean issueQb, Map<String, IssueSimplePO> issueMap) {
    }

    public static void worklogCsvReport(IssueMonitorQueryBean issueQb, Map<String, IssueSimplePO> issueMap,
            List<WorklogSimplePO> worklogs) throws Exception {
        worklogs = worklogs.stream().filter(wl -> wl.getWorkDate().isAfter(issueQb.getStartDate().minusDays(1)))
                .collect(Collectors.toList());
        originalWorklog(issueMap, worklogs);
        userDayWorkLogCsv(issueQb, worklogs);
        issueDayWorkLogCsv(issueQb, worklogs);
    }

    private static void originalWorklog(Map<String, IssueSimplePO> issueMap, List<WorklogSimplePO> worklogs) {
        List<String> wlCsvRows = new ArrayList<>();
        wlCsvRows.add("IssueKey,type,status,User,Day,Worklog,WorkDesc,IssueSummary");
        String wlRowFmt = "%s,%s,%s,%s,%s,%f,%s,%s";
        worklogs.forEach(wl -> {
            IssueSimplePO issue = issueMap.get(wl.getIssueKey());
            String row = String.format(wlRowFmt, wl.getIssueKey(), issue.getIssueType(), issue.getStatus(),
                    wl.getUserId(), wl.getWorkDate().format(Const.YEAR2DAY_FMT), wl.getTimeSpentHours(),
                    wl.getWorkDesc(), StringUtil.filterSpecialChar(issue.getSummary(), " "));
            wlCsvRows.add(row);
        });
        try {
            File csv = new File(WORK_LOGS_FILE_PREFIX + "-" + LocalDate.now().format(Const.YEAR2DAY_FMT) + ".csv");
            if (csv.exists()) {
                csv.delete();
            }
            Files.write(csv.toPath(), wlCsvRows, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void issueDayWorkLogCsv(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> worklogs) {
        CsvMatrix issueDayWlMatrix = calcIssueDayWlMatrix(issueQb, worklogs);
        List<String> csvRows = new ArrayList<>();
        String header = "User," + issueDayWlMatrix.getColumnSet().stream().collect(Collectors.joining(","));
        csvRows.add(header);
        issueDayWlMatrix.getRowColValMap().forEach((k, v) -> {
            StringBuilder row = new StringBuilder();
            row.append(k).append(",");
            issueDayWlMatrix.getColumnSet().forEach(day -> {
                Double val = v.get(day);
                if (val == null) {
                    row.append(",");
                } else {
                    row.append(val).append(",");
                }
            });
            row.deleteCharAt(row.length() - 1);
            csvRows.add(row.toString());
        });
        try {
            File csv = new File("issueWorkLog.csv");
            if (csv.exists()) {
                csv.delete();
            }
            Files.write(csv.toPath(), csvRows, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CsvMatrix calcIssueDayWlMatrix(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> worklogs) {
        Set<String> issueRowMap = new TreeSet<>(
                worklogs.stream().map(wl -> wl.getIssueKey()).sorted().collect(Collectors.toSet()));
        Set<String> dayColumnMap = new TreeSet<>();
        LocalDate startDate = issueQb.getStartDate();
        LocalDate endDate = issueQb.getEndDate();
        for (; startDate.isBefore(endDate); startDate = startDate.plusDays(1)) {
            dayColumnMap.add(startDate.format(Const.YEAR2DAY_FMT));
        }
        Map<String, Map<String, Double>> rcvMap = new HashMap<>();
        worklogs.forEach(e -> {
            String issueKey = e.getIssueKey();
            String day = e.getWorkDate().format(Const.YEAR2DAY_FMT);
            Map<String, Double> dayWlMap = rcvMap.get(issueKey);
            if (dayWlMap == null) {
                dayWlMap = new HashMap<>();
                rcvMap.put(issueKey, dayWlMap);
            }
            Double val = dayWlMap.get(day);
            val = val == null ? 0D : val;
            dayWlMap.put(day, val + e.getTimeSpentHours());
        });
        CsvMatrix csvMatrix = new CsvMatrix();
        csvMatrix.setColumnSet(dayColumnMap);
        csvMatrix.setRowSet(issueRowMap);
        csvMatrix.setRowColValMap(rcvMap);
        return csvMatrix;
    }

    private static void userDayWorkLogCsv(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> worklogs)
            throws Exception {
        CsvMatrix userDayWlMatrix = calcUserDayWlMatrix(issueQb, worklogs);
        List<String> csvRows = new ArrayList<>();
        Set<String> allaUsers = FileUtil.allusers();
        String header = "User," + userDayWlMatrix.getColumnSet().stream().collect(Collectors.joining(","));
        csvRows.add(header);
        userDayWlMatrix.getRowColValMap().entrySet().stream().filter(e -> allaUsers.contains(e.getKey()))
                .sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e -> {
                    StringBuilder row = new StringBuilder();
                    row.append(e.getKey()).append(",");
                    DoubleAccumulator sumValAcc = new DoubleAccumulator((a, b) -> a + b, 0);
                    userDayWlMatrix.getColumnSet().forEach(day -> {
                        Double val = e.getValue().get(day);
                        if (val == null) {
                            row.append(",");
                        } else {
                            sumValAcc.accumulate(val);
                            row.append(val).append(",");
                        }
                    });
                    row.append(sumValAcc.doubleValue());
                    csvRows.add(row.toString());
                });
        try {
            File csv = new File(
                    USER_WORKLOG_SUMMARY_FILE_PREFIX + "-" + LocalDate.now().format(Const.YEAR2DAY_FMT) + ".csv");
            if (csv.exists()) {
                csv.delete();
            }
            Files.write(csv.toPath(), csvRows, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CsvMatrix calcUserDayWlMatrix(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> worklogs) {
        Set<String> userRowMap = new TreeSet<>(
                worklogs.stream().map(wl -> wl.getUserId()).sorted().collect(Collectors.toSet()));
        Set<String> dayColumnMap = new TreeSet<>();
        LocalDate startDate = issueQb.getStartDate();
        LocalDate endDate = issueQb.getEndDate();
        for (; startDate.isBefore(endDate); startDate = startDate.plusDays(1)) {
            dayColumnMap.add(startDate.format(Const.YEAR2DAY_FMT));
        }
        dayColumnMap.add("SUM");
        Map<String, Map<String, Double>> rcvMap = new HashMap<>();
        worklogs.forEach(e -> {
            String userId = e.getUserId();
            String day = e.getWorkDate().format(Const.YEAR2DAY_FMT);
            Map<String, Double> dayWlMap = rcvMap.get(userId);
            if (dayWlMap == null) {
                dayWlMap = new HashMap<>();
                rcvMap.put(userId, dayWlMap);
            }
            Double val = dayWlMap.get(day);
            val = val == null ? 0D : val;
            dayWlMap.put(day, val + e.getTimeSpentHours());
        });
        CsvMatrix csvMatrix = new CsvMatrix();
        csvMatrix.setColumnSet(dayColumnMap);
        csvMatrix.setRowSet(userRowMap);
        csvMatrix.setRowColValMap(rcvMap);
        return csvMatrix;
    }

    private static LocalDate calcNextWlDay(LocalDate nextWlDay, TreeSet<String> holidays, boolean startInHoliday) {
        if (startInHoliday) {
            return nextWlDay.plusDays(1);
        }
        LocalDate day = nextWlDay.plusDays(1);
        while (holidays.contains(day.format(Const.YEAR2DAY_FMT))) {
            day = day.plusDays(1);
        }
        return day;
    }

    public static TreeSet<String> getHolidays() {
        if (HOLIDAYS.size() > 0) {
            return HOLIDAYS;
        }
        try {
            Set<String> set = Files.readAllLines(new File("holidays.txt").toPath()).stream()
                    .filter(s -> s != null && s.trim().length() == 10).collect(Collectors.toSet());
            HOLIDAYS.addAll(set);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HOLIDAYS;
    }

    public static void taskDistRpt(Map<String, UserBean> users, List<IssueSimplePO> issues) {
        List<String> headers = new ArrayList();
        headers.addAll(Arrays.asList("Key", "Summary", "Priority", "Status", "Assignee", "Owner", "originalEstimate"));
        Map<String, List<IssueSimplePO>> userIssueMap = new TreeMap<>();
        issues.forEach(issue -> {
            String assignee = issue.getAssignee();
            List<IssueSimplePO> userIssues = userIssueMap.get(assignee);
            if (userIssues == null) {
                userIssues = new ArrayList<>();
                userIssueMap.put(assignee, userIssues);
            }
            userIssues.add(issue);
        });
        int userCnt = userIssueMap.size();
        headers.addAll(userIssueMap.keySet());
        List<String> svcCtts = new ArrayList<>();
        svcCtts.add(headers.stream().collect(Collectors.joining(",")));
        AtomicInteger atomInt = new AtomicInteger(0);
        userIssueMap.forEach((k, v) -> {
            v.forEach(issue -> {
                List<String> issueCtt = new ArrayList<>();
                issueCtt.addAll(Arrays.asList(issue.getKey(), StringUtil.filterSpecialChar(issue.getSummary()),
                        issue.getPriority(), issue.getStatus(), issue.getAssignee(), issue.getOwner(),
                        Double.valueOf(issue.getEstHour() / 8).toString()));
                for (int i = 0; i < userCnt; i++) {
                    if (atomInt.get() == i && issue.getEstHour() != null) {
                        issueCtt.add(Double.valueOf(issue.getEstHour() / 8).toString());
                    } else {
                        issueCtt.add("");
                    }
                }
                svcCtts.add(issueCtt.stream().collect(Collectors.joining(",")));
            });
            atomInt.incrementAndGet();
        });
        try {
            File f = new File("IssueDist.csv");
            if (f.exists()) {
                f.delete();
            }
            Files.write(f.toPath(), svcCtts, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ganttReport(ReleaseData data) {
        List<String> mdlines = new ArrayList<>();
        mdlines.add("```mermaid");
        mdlines.add("gantt");
        mdlines.add("dateFormat  YYYY-MM-DD");
        mdlines.add("title Release Issues " + data.getDevStart().format(Const.YEAR2DAY_FMT) + " ~ "
                + data.getDevEnd().format(Const.YEAR2DAY_FMT));

        mdlines.addAll(globalSection(data));

        UserReleaseData noneUserData = data.getUserDataMap().remove(Const.ANONYMOUS_USER);
        if (noneUserData != null) {
            mdlines.addAll(userGanttSection(noneUserData));
        }
        data.getUserDataMap().values().stream().sorted(Comparator.comparing(UserReleaseData::getEtc).reversed())
                .forEach(ud -> mdlines.addAll(userGanttSection(ud)));

        mdlines.add("```");

        try {
            File csv = new File("ReleaseGantt.md");
            if (csv.exists()) {
                csv.delete();
            }
            Files.write(csv.toPath(), mdlines, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Collection<? extends String> globalSection(ReleaseData data) {
        List<String> lines = new ArrayList<>();
        double ac = data.getAc(), etc = data.getEtc(), ra = data.getRa(), ev = data.getEv();
        lines.add("section Global");
        String glbTask = String.format("global-status total=%d ac=%d ev=%d etc=%d ra=%d: crit, %s, %s",
                hour2day(ev + etc), hour2day(ac), hour2day(ev), hour2day(etc), hour2day(ra),
                data.getDevStart().format(Const.YEAR2DAY_FMT), data.getDevEnd().format(Const.YEAR2DAY_FMT));
        lines.add(glbTask);
        return lines;
    }

    private static Collection<? extends String> userGanttSection(UserReleaseData ud) {
        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add("section " + ud.getUserName());
        lines.add(buildGanttSectionTitle(ud.getUserName(), ud));
        ud.getEvIssues().forEach(issue -> {
            lines.add(buildGanttResolvedTaskLine(issue));
        });
        ud.getLeftIssues().forEach(issue -> {
            lines.add(buildGanttUnresolvedTaskLine(issue));
        });
        lines.add("");
        return lines;
    }

    private static String buildGanttUnresolvedTaskLine(IssueSimplePO issue) {
        LocalDate now = LocalDate.now();
        boolean inProgress = issue.getStatus().toLowerCase().contains("progress");
        if (!inProgress) {
            now = now.plusDays(1);
        }
        return String.format("%s[%s][%s][%s]%s: %s %s, %s", issue.getKey(), issue.getIssueType(), issue.getPriority(),
                issue.getStatus(), substr(StringUtil.filterSpecialChar(issue.getSummary(), " "), 50),
                inProgress ? "active," : "", now.format(Const.YEAR2DAY_FMT),
                now.plusDays(hour2day(issue.getEstHour())).format(Const.YEAR2DAY_FMT));
    }

    private static String buildGanttResolvedTaskLine(IssueSimplePO issue) {
        LocalDate now = LocalDate.now();
        return String.format("%s[%s][%s]%s: done, %s, %s", issue.getKey(), issue.getIssueType(), issue.getPriority(),
                substr(StringUtil.filterSpecialChar(issue.getSummary(), " "), 50),
                now.minusDays(hour2day(issue.getEstHour())).format(Const.YEAR2DAY_FMT), now.format(Const.YEAR2DAY_FMT));
    }

    private static String substr(String s, int i) {
        if (s == null) {
            return "";
        }
        if (s.length() > i) {
            return s.substring(0, i);
        }
        return s;
    }

    private static String issueGanttStatus(String status) {
        if (status.toLowerCase().contains("in progress")) {
            return "active,";
        }
        if (Const.RESOLVED_ISSUE_STATUS.contains(status)) {
            return "done,";
        }
        return "";
    }

    private static String buildGanttSectionTitle(String userName, UserReleaseData ud) {
        double ac = ud.getAc(), etc = ud.getEtc(), ra = ud.getRa(), ev = ud.getEv();
        String glbTask = String.format(userName + "   total=%d used=%d earned=%d etc=%d rest=%d: crit,done, %s, %s",
                hour2day(ev + etc), hour2day(ac), hour2day(ev), hour2day(etc), hour2day(ra),
                ud.getDevStart().format(Const.YEAR2DAY_FMT), ud.getDevEnd().format(Const.YEAR2DAY_FMT));
        return glbTask;
    }

    public static int hour2day(double val) {
        return BigDecimal.valueOf(val).divide(BigDecimal.valueOf(8), BigDecimal.ROUND_CEILING).intValue();
    }

    public static LocalDate calcNatureStart(LocalDate dueDate, int duration) {
        if (duration <= 0) {
            return dueDate;
        }
        TreeSet<String> holidays = getHolidays();
        LocalDate start = dueDate;
        while (duration > 0) {
            String startDay = start.format(Const.YEAR2DAY_FMT);
            start = start.minusDays(1);
            if (!holidays.contains(startDay)) {
                duration -= 1;
            }
        }
        return start.plusDays(1);
    }
}
