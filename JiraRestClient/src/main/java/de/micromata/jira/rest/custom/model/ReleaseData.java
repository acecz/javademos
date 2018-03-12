package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.custom.util.FileUtil;
import de.micromata.jira.rest.custom.util.ReportUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ReleaseData {
    private LocalDate devStart;
    private LocalDate devEnd;
    private List<IssueSimplePO> tasks;
    private List<IssueSimplePO> bugs;
    private Map<String, UserReleaseData> userDataMap = new TreeMap<>();

    public Map<String, UserReleaseData> getUserDataMap() {
        return userDataMap;
    }

    public void setUserDataMap(Map<String, UserReleaseData> userDataMap) {
        this.userDataMap = userDataMap;
    }

    public LocalDate getDevStart() {
        return devStart;
    }

    public void setDevStart(LocalDate devStart) {
        this.devStart = devStart;
    }

    public LocalDate getDevEnd() {
        return devEnd;
    }

    public void setDevEnd(LocalDate devEnd) {
        this.devEnd = devEnd;
    }

    public List<IssueSimplePO> getTasks() {
        return tasks;
    }

    public void setTasks(List<IssueSimplePO> tasks) {
        this.tasks = tasks;
    }

    public List<IssueSimplePO> getBugs() {
        return bugs;
    }

    public void setBugs(List<IssueSimplePO> bugs) {
        this.bugs = bugs;
    }

    public void adjustForGantt() {
        tasks = tasks == null ? new ArrayList<>() : tasks;
        bugs = bugs == null ? new ArrayList<>() : bugs;
        tasks.forEach(po -> {
            adjustIssue4Gantt(po);
            String owner = po.getOwner();
            userDataMap.computeIfAbsent(owner, key -> new UserReleaseData(key)).addIssueData(po);
        });
        bugs.forEach(po -> {
            adjustIssue4Gantt(po);
            String owner = po.getOwner();
            userDataMap.computeIfAbsent(owner, key -> new UserReleaseData(key)).addIssueData(po);
        });
        userDataMap.values().forEach(urd -> {
            urd.getPriorityBugMap().forEach((k, v) -> v.sort(Comparator.comparing(IssueSimplePO::getNaturalStartDay)));
        });
    }

    private void adjustIssue4Gantt(IssueSimplePO po) {
        if (po.getEstHour() == null) {
            po.setEstHour(0D);
        }
        if (po.getDueDate() == null) {
            po.setDueDate(devEnd);
        }
        calcNaturalStartDate(po);
    }

    private void calcNaturalStartDate(IssueSimplePO po) {
        try {
            Set<LocalDate> holidays = FileUtil.holidayDates();
            Double estHrs = po.getEstHour();
            int days = ReportUtil.hour2day(estHrs);
            LocalDate startDay = po.getDueDate();
            if (days > 0) {
                startDay = po.getDueDate().minusDays(1);
            }
            days--;
            for (;;) {
                if (days <= 0) {
                    break;
                }
                startDay = startDay.minusDays(1);
                if (holidays.contains(startDay)) {
                    continue;
                }
                days--;
            }
            po.setNaturalStartDay(startDay);
            if (po.getAssignee() == null) {
                po.setAssignee(Const.ANONYMOUS_USER);
            }
            if (po.getOwner() == null) {
                po.setOwner(Const.ANONYMOUS_USER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
