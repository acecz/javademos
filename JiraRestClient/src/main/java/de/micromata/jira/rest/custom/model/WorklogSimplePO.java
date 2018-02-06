package de.micromata.jira.rest.custom.model;

import java.time.LocalDate;

public class WorklogSimplePO {
    private String issueKey;
    private String userId;
    private String userName;
    private String startTime;
    private LocalDate startDate;
    private Integer timeSpentSeconds;

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "WorklogSimplePO{" +
            "issueKey='" + issueKey + '\'' +
            ", userId='" + userId + '\'' +
            ", userName='" + userName + '\'' +
            ", startTime='" + startTime + '\'' +
            ", startDate=" + startDate +
            ", timeSpentSeconds=" + timeSpentSeconds +
            '}';
    }
}
