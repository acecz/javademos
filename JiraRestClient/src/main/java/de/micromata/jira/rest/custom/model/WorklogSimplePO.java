package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.util.StringUtil;

import java.time.LocalDate;

public class WorklogSimplePO {
    public static final String CSV_HEADER = "IssueKey,UserId,UserName,Date,WorkHours";
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
        return "WorklogSimplePO{" + "issueKey='" + issueKey + '\'' + ", userId='" + userId + '\'' + ", userName='"
                + userName + '\'' + ", startTime='" + startTime + '\'' + ", startDate=" + startDate
                + ", timeSpentSeconds=" + timeSpentSeconds + '}';
    }

    public String toCsvString() {
        // public static final String CSV_HEADER="IssueKey,UserId,UserName,Date,WorkHours";
        return String.format("%s,%s,%s,%s,%s", issueKey, userId, StringUtil.filterSpecialChar(userName),
                startDate.format(Const.YEAR2DAY_FMT), timeSpentSeconds / 3600);
    }
}
