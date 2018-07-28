package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.util.JsonUtil;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

public class DailyBugStatus {
    private LocalDate day;
    private Set<String> openedBugs = new TreeSet<>();
    private Set<String> createdBugs = new TreeSet<>();
    private Set<String> fixedBugs = new TreeSet<>();
    private Set<String> pendingBugs = new TreeSet<>();
    private Set<String> monitorBugs = new TreeSet<>();

    public DailyBugStatus() {
    }

    public DailyBugStatus(LocalDate day) {
        this.day = day;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Set<String> getOpenedBugs() {
        return openedBugs;
    }

    public void setOpenedBugs(Set<String> openedBugs) {
        this.openedBugs = openedBugs;
    }

    public void addOpenedBug(String bugKey) {
        this.openedBugs.add(bugKey);
    }

    public Set<String> getCreatedBugs() {
        return createdBugs;
    }

    public void setCreatedBugs(Set<String> createdBugs) {
        this.createdBugs = createdBugs;
    }

    public Set<String> getFixedBugs() {
        return fixedBugs;
    }

    public void setFixedBugs(Set<String> fixedBugs) {
        this.fixedBugs = fixedBugs;
    }

    public Set<String> getPendingBugs() {
        return pendingBugs;
    }

    public void setPendingBugs(Set<String> pendingBugs) {
        this.pendingBugs = pendingBugs;
    }

    public Set<String> getMonitorBugs() {
        return monitorBugs;
    }

    public void setMonitorBugs(Set<String> monitorBugs) {
        this.monitorBugs = monitorBugs;
    }

    public void addtFixedBug(String issueKey) {
        this.fixedBugs.add(issueKey);
    }

    public void addtCreatedBug(String issueKey) {
        this.createdBugs.add(issueKey);
    }

    @Override
    public String toString() {
        return JsonUtil.dbgStr(this);
    }
}
