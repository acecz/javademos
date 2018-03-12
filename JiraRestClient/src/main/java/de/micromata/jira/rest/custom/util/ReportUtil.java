package de.micromata.jira.rest.custom.util;

import java.io.File;
import java.io.IOException;
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
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.domain.UserBean;
import de.micromata.jira.rest.core.util.StringUtil;
import de.micromata.jira.rest.custom.model.CsvMatrix;
import de.micromata.jira.rest.custom.model.IssueMonitorQueryBean;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.ReleaseData;
import de.micromata.jira.rest.custom.model.WorklogSimplePO;

public class ReportUtil {
    public static final TreeSet<String> HOLIDAYS = new TreeSet<>();

    public static void issueCsvReport(IssueMonitorQueryBean issueQb, Map<String, IssueSimplePO> issueMap) {
    }

    public static void worklogCsvReport(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> worklogs)
        throws Exception {
        worklogs = worklogs.stream().filter(wl -> wl.getWorkDate().isAfter(issueQb.getStartDate().minusDays(1)))
            .collect(Collectors.toList());
        // worklogs = worklogs.stream().filter(wl -> wl.getWorkDate().isAfter(issueQb.getStartDate().minusDays(1)))
        // .collect(Collectors.toList());
        originalWorklog(worklogs);
        userDayWorkLogCsv(issueQb, worklogs);
        issueDayWorkLogCsv(issueQb, worklogs);
    }

    private static void originalWorklog(List<WorklogSimplePO> worklogs) {
        List<String> wlCsvRows = new ArrayList<>();
        wlCsvRows.add("IssueKey,User,Day,Worklog,WorkDesc");
        String wlRowFmt = "%s,%s,%s,%f,%s";
        worklogs.forEach(wl -> {
            String row = String.format(wlRowFmt, wl.getIssueKey(), wl.getUserId(),
                wl.getWorkDate().format(Const.YEAR2DAY_FMT), wl.getTimeSpentHours(), wl.getWorkDesc());
            wlCsvRows.add(row);
        });
        try {
            File csv = new File("worklogs.csv");
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
            userDayWlMatrix.getColumnSet().forEach(day -> {
                Double val = e.getValue().get(day);
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
            File csv = new File("userWorkLog.csv");
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
            File holidays = new File("holidays.txt");
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

    public static void ganttReport(ReleaseData issues) {
    }
}
