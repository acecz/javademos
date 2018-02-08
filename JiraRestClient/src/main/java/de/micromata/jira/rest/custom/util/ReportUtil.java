package de.micromata.jira.rest.custom.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.custom.model.CsvMatrix;
import de.micromata.jira.rest.custom.model.IssueMonitorQueryBean;
import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.WorklogSimplePO;

public class ReportUtil {
    public static final TreeSet<String> HOLIDAYS = new TreeSet<>();

    public static void issueCsvReport(IssueMonitorQueryBean issueQb, Map<String, IssueSimplePO> issueMap) {
    }

    public static void worklogCsvReport(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> worklogs) {
        List<WorklogSimplePO> rcWorklogs = reCalcWorkLogs(worklogs);
        CsvMatrix userDayWlMatrix = calcUserDayWlMatrix(issueQb, rcWorklogs);
        List<String> csvRows = new ArrayList<>();
        String header = "User," + userDayWlMatrix.getColumnSet().stream().collect(Collectors.joining(","));
        csvRows.add(header);
        userDayWlMatrix.getRowColValMap().forEach((k, v) -> {
            StringBuilder row = new StringBuilder();
            row.append(k).append(",");
            userDayWlMatrix.getColumnSet().forEach(day -> {
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
            Files.write(new File("testUserWl.csv").toPath(), csvRows, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static CsvMatrix calcUserDayWlMatrix(IssueMonitorQueryBean issueQb, List<WorklogSimplePO> rcWorklogs) {
        Set<String> userRowMap = new TreeSet<>(
                rcWorklogs.stream().map(wl -> wl.getUserId()).sorted().collect(Collectors.toSet()));
        Set<String> dayColumnMap = new TreeSet<>();
        LocalDate startDate = issueQb.getStartDate();
        LocalDate endDate = issueQb.getEndDate();
        for (; startDate.isBefore(endDate); startDate = startDate.plusDays(1)) {
            dayColumnMap.add(startDate.format(Const.YEAR2DAY_FMT));
        }
        Map<String, Map<String, Double>> rcvMap = new HashMap<>();
        rcWorklogs.forEach(e -> {
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

    private static List<WorklogSimplePO> reCalcWorkLogs(List<WorklogSimplePO> worklogs) {
        TreeSet<String> holidays = getHolidays();
        List<WorklogSimplePO> rtnWorklogs = new ArrayList<>();
        worklogs.forEach(wl -> rtnWorklogs.addAll(reCalcWorkLog(holidays, wl)));
        return rtnWorklogs;
    }

    private static Collection<? extends WorklogSimplePO> reCalcWorkLog(TreeSet<String> holidays, WorklogSimplePO wl) {
        Double wkHours = wl.getTimeSpentHours();
        Double rwkHrs = wkHours;
        if (rwkHrs <= 8) {
            WorklogSimplePO wls = WorklogSimplePO
                    .createNew(wl.getIssueKey(), wl.getUserId(), wl.getUserName(), wl.getWorkDate(), rwkHrs);
            return Arrays.asList(wls);
        }
        List<WorklogSimplePO> rtnWorklogs = new ArrayList<>();
        rwkHrs = rwkHrs - 8;
        WorklogSimplePO firstWl = WorklogSimplePO
                .createNew(wl.getIssueKey(), wl.getUserId(), wl.getUserName(), wl.getWorkDate(), 8D);
        rtnWorklogs.add(firstWl);
        LocalDate nextWlDay = wl.getWorkDate();
        boolean startInHoliday = holidays.contains(wl.getWorkDate().format(Const.YEAR2DAY_FMT));
        while (rwkHrs > 0) {
            nextWlDay = calcNextWlDay(nextWlDay, holidays, startInHoliday);
            WorklogSimplePO wls;
            if (rwkHrs > 8) {
                wls = WorklogSimplePO.createNew(wl.getIssueKey(), wl.getUserId(), wl.getUserName(), nextWlDay, 8D);
            } else {
                wls = WorklogSimplePO.createNew(wl.getIssueKey(), wl.getUserId(), wl.getUserName(), nextWlDay, rwkHrs);
            }
            rtnWorklogs.add(wls);
            rwkHrs = rwkHrs - 8;
        }
        return rtnWorklogs;
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
            System.out.println(holidays.getAbsolutePath());
            Set<String> set = Files.readAllLines(new File("holidays.txt").toPath()).stream()
                    .filter(s -> s != null && s.trim().length() == 10).collect(Collectors.toSet());
            HOLIDAYS.addAll(set);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HOLIDAYS;
    }
}
