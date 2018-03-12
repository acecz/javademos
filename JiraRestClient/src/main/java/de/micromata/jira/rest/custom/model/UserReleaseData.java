package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.jql.JqlConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserReleaseData {
    private String userName;
    private UserReleaseEffortDist effortDist = new UserReleaseEffortDist();
    private Map<String, List<IssueSimplePO>> priorityTaskMap = new TreeMap<>();
    private Map<String, List<IssueSimplePO>> priorityBugMap = new TreeMap<>();

    public UserReleaseData() {
    }

    public UserReleaseData(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, List<IssueSimplePO>> getPriorityTaskMap() {
        return priorityTaskMap;
    }

    public void setPriorityTaskMap(Map<String, List<IssueSimplePO>> priorityTaskMap) {
        this.priorityTaskMap = priorityTaskMap;
    }

    public Map<String, List<IssueSimplePO>> getPriorityBugMap() {
        return priorityBugMap;
    }

    public void setPriorityBugMap(Map<String, List<IssueSimplePO>> priorityBugMap) {
        this.priorityBugMap = priorityBugMap;
    }

    public UserReleaseEffortDist getEffortDist() {
        return effortDist;
    }

    public void setEffortDist(UserReleaseEffortDist effortDist) {
        this.effortDist = effortDist;
    }

    public void addIssueData(IssueSimplePO po) {
        boolean isBug = po.getIssueType().equalsIgnoreCase(JqlConstants.ISSUETYPE_BUG);
        effortDist.adjustEffort(isBug, po);
        String priority = po.getPriority();
        if (isBug) {
            priorityBugMap.computeIfAbsent(priority, key -> new ArrayList<>()).add(po);
        } else {
            priorityTaskMap.computeIfAbsent(priority, key -> new ArrayList<>()).add(po);
        }
    }
}
