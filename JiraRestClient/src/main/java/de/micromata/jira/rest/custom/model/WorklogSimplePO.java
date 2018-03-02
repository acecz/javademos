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

    private String workDesc;

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

    public String getWorkDesc() {
        return workDesc;
    }

    public void setWorkDesc(String workDesc) {
        this.workDesc = workDesc;
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
