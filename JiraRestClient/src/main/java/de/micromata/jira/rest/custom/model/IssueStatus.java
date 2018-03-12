package de.micromata.jira.rest.custom.model;

public enum IssueStatus {
    Open("Open"), ReOpen("ReOpen"), In_Progress("'In Progress'"), Pending("Pending"), Resolved("Resolved"), Closed(
            "Closed");
    public final String strVal;

    private IssueStatus(String strVal) {
        this.strVal = strVal;
    }
}
