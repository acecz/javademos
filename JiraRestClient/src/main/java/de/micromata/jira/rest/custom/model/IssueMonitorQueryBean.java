package de.micromata.jira.rest.custom.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueMonitorQueryBean {
    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> monitorIssues = new ArrayList<>();

    private List<String> allIssues = new ArrayList<>();

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<String> getMonitorIssues() {
        return monitorIssues;
    }

    public void setMonitorIssues(List<String> monitorIssues) {
        this.monitorIssues = monitorIssues;
    }

    public List<String> getAllIssues() {
        return allIssues;
    }

    public void setAllIssues(List<String> allIssues) {
        this.allIssues = allIssues;
    }
}
