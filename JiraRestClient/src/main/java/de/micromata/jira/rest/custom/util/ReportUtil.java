package de.micromata.jira.rest.custom.util;

import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.WorklogSimplePO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ReportUtil {
    public static final TreeSet<String> HOLIDAYS = new TreeSet<>();

    public static void issueCsvReport(Map<String, IssueSimplePO> issueMap) {
    }

    public static void worklogCsvReport(List<WorklogSimplePO> worklogs) {
        worklogs = reCalcWorkLogs(worklogs);
    }

    private static List<WorklogSimplePO> reCalcWorkLogs(List<WorklogSimplePO> worklogs) {
        return null;
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
}
