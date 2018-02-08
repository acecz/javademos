package de.micromata.jira.rest.custom.model;

import java.time.LocalDate;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.util.StringUtil;

public class WorklogSimplePO {
    public static final String CSV_HEADER = "IssueKey,UserId,UserName,Date,WorkHours";

    private String issueKey;

    private String userId;

    private String userName;

    private LocalDate workDate;

    private Double timeSpentHours;

    public static WorklogSimplePO createNew(String issueKey, String userId, String userName, LocalDate startDate,
            Double hours) {
        WorklogSimplePO newOne = new WorklogSimplePO();
        newOne.setIssueKey(issueKey);
        newOne.setUserId(userId);
        newOne.setUserName(userName);
        newOne.setWorkDate(startDate);
        newOne.setTimeSpentHours(hours);
        return newOne;

    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

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

    public Double getTimeSpentHours() {
        return timeSpentHours;
    }

    public void setTimeSpentHours(Double timeSpentHours) {
        this.timeSpentHours = timeSpentHours;
    }

    @Override
    public String toString() {
        return "WorklogSimplePO{" + "issueKey='" + issueKey + '\'' + ", userId='" + userId + '\'' + ", userName='"
                + userName + '\'' + ", workDate='" + workDate + '\'' + ", timeSpentHours=" + timeSpentHours + '}';
    }

    public String toCsvString() {
        // public static final String CSV_HEADER="IssueKey,UserId,UserName,Date,WorkHours";
        return String.format("%s,%s,%s,%s,%s", issueKey, userId, StringUtil.filterSpecialChar(userName),
                workDate.format(Const.YEAR2DAY_FMT), timeSpentHours);
    }
}
