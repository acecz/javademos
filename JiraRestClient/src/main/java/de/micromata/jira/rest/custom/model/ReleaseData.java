package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.custom.util.FileUtil;

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
            userDataMap.computeIfAbsent(owner, key -> new UserReleaseData()).addIssueData(po);
        });
        bugs.forEach(po -> {
            adjustIssue4Gantt(po);
            String owner = po.getOwner();
            userDataMap.computeIfAbsent(owner, key -> new UserReleaseData()).addIssueData(po);
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
            int days = (int) Math.ceil(estHrs / 8D);
            LocalDate startDay = po.getDueDate();
            days--;
            for (; ; ) {
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
                po.setAssignee("none");
            }
            if (po.getOwner() == null) {
                po.setOwner("none");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
