package de.micromata.jira.rest.custom.model;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReleaseData {
    private LocalDate devStart;
    private LocalDate devEnd;
    private List<IssueSimplePO> tasks;
    private List<IssueSimplePO> bugs;

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
        
    }
}
