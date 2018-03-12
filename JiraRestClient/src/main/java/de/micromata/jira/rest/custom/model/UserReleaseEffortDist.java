package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.Const;

import java.util.Map;
import java.util.TreeMap;

public class UserReleaseEffortDist {
    private Double taskTotal = 0D;
    private Double bugTotal = 0D;
    private Double spentTaskTotal = 0D;
    private Double spentBugTotal = 0D;
    private Map<String, Double> taskPriorityMap = new TreeMap<>();
    private Map<String, Double> bugPriorityMap = new TreeMap<>();

    public Double getTaskTotal() {
        return taskTotal;
    }

    public void setTaskTotal(Double taskTotal) {
        this.taskTotal = taskTotal;
    }

    public Double getBugTotal() {
        return bugTotal;
    }

    public void setBugTotal(Double bugTotal) {
        this.bugTotal = bugTotal;
    }

    public Double getSpentTaskTotal() {
        return spentTaskTotal;
    }

    public void setSpentTaskTotal(Double spentTaskTotal) {
        this.spentTaskTotal = spentTaskTotal;
    }

    public Double getSpentBugTotal() {
        return spentBugTotal;
    }

    public void setSpentBugTotal(Double spentBugTotal) {
        this.spentBugTotal = spentBugTotal;
    }

    public Map<String, Double> getTaskPriorityMap() {
        return taskPriorityMap;
    }

    public void setTaskPriorityMap(Map<String, Double> taskPriorityMap) {
        this.taskPriorityMap = taskPriorityMap;
    }

    public Map<String, Double> getBugPriorityMap() {
        return bugPriorityMap;
    }

    public void setBugPriorityMap(Map<String, Double> bugPriorityMap) {
        this.bugPriorityMap = bugPriorityMap;
    }

    public void adjustEffort(boolean isBug, IssueSimplePO po) {
        double ee = po.getEstHour();
        boolean resolved = !Const.UNRESOLVED_ISSUE_STATUS.contains(po.getStatus());
        String priority = po.getPriority();
        if (isBug) {
            bugTotal += ee;
            bugPriorityMap.put(priority, bugPriorityMap.getOrDefault(priority, 0D) + ee);
            if (resolved) {
                spentBugTotal += ee;
            }
        } else {
            taskTotal += ee;
            taskPriorityMap.put(priority, taskPriorityMap.getOrDefault(priority, 0D) + ee);
            if (resolved) {
                spentTaskTotal += ee;
            }
        }


    }
}
