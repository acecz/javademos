package de.micromata.jira.rest.custom.model;

public enum IssueGroupStatus {
    FINISHED, UNDERGOING, PENDING;

    public static IssueGroupStatus checkStatus(String status) {
        status = status.toUpperCase();
        switch (status) {
        case "OPEN":
            return UNDERGOING;
        case "REOPEN":
            return UNDERGOING;
        case "IN PROGRESS":
            return UNDERGOING;
        case "PENDING":
            return PENDING;
        case "RESOLVED":
            return FINISHED;
        case "CLOSED":
            return FINISHED;
        default:
            return UNDERGOING;
        }
    }

}
