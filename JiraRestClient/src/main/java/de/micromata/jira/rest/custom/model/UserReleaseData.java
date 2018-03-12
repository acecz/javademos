package de.micromata.jira.rest.custom.model;

import java.util.ArrayList;
import java.util.List;

public class UserReleaseData {
    private String userName;
    private String userDisplayName;
    private List<IssueSimplePO> userP1Tasks = new ArrayList<>();
    private List<IssueSimplePO> userP2Tasks = new ArrayList<>();
    private List<IssueSimplePO> userP3Tasks = new ArrayList<>();
    private List<IssueSimplePO> userP1Bugs = new ArrayList<>();
    private List<IssueSimplePO> userP2Bugs = new ArrayList<>();
    private List<IssueSimplePO> userP3Bugs = new ArrayList<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public List<IssueSimplePO> getUserP1Tasks() {
        return userP1Tasks;
    }

    public void setUserP1Tasks(List<IssueSimplePO> userP1Tasks) {
        this.userP1Tasks = userP1Tasks;
    }

    public void addUserP1Tasks(IssueSimplePO po) {
        this.userP1Tasks.add(po);
    }

    public List<IssueSimplePO> getUserP2Tasks() {
        return userP2Tasks;
    }

    public void setUserP2Tasks(List<IssueSimplePO> userP2Tasks) {
        this.userP2Tasks = userP2Tasks;
    }

    public void addUserP2Tasks(IssueSimplePO po) {
        this.userP2Tasks.add(po);
    }

    public List<IssueSimplePO> getUserP3Tasks() {
        return userP3Tasks;
    }

    public void setUserP3Tasks(List<IssueSimplePO> userP3Tasks) {
        this.userP3Tasks = userP3Tasks;
    }

    public void addUserP3Tasks(IssueSimplePO po) {
        this.userP3Tasks.add(po);
    }

    public List<IssueSimplePO> getUserP1Bugs() {
        return userP1Bugs;
    }

    public void setUserP1Bugs(List<IssueSimplePO> userP1Bugs) {
        this.userP1Bugs = userP1Bugs;
    }

    public void addUserP1Bugs(IssueSimplePO po) {
        this.userP1Bugs.add(po);
    }

    public List<IssueSimplePO> getUserP2Bugs() {
        return userP2Bugs;
    }

    public void setUserP2Bugs(List<IssueSimplePO> userP2Bugs) {
        this.userP2Bugs = userP2Bugs;
    }

    public void addUserP2Bugs(IssueSimplePO po) {
        this.userP2Bugs.add(po);
    }

    public List<IssueSimplePO> getUserP3Bugs() {
        return userP3Bugs;
    }

    public void setUserP3Bugs(List<IssueSimplePO> userP3Bugs) {
        this.userP3Bugs = userP3Bugs;
    }

    public void addUserP3Bugs(IssueSimplePO po) {
        this.userP2Bugs.add(po);
    }
}
