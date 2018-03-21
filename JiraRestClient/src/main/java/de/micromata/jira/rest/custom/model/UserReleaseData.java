package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.core.jql.JqlConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserReleaseData {
    private String userName;
    private double ac = 0;
    private double ev = 0;
    private double etc = 0;
    private double ra = 0;
    private LocalDate devStart;
    private LocalDate devEnd;
    private List<IssueSimplePO> evIssues = new ArrayList<>();
    private List<IssueSimplePO> leftIssues = new ArrayList<>();

    public UserReleaseData() {
    }

    public UserReleaseData(String userName) {
        System.out.println(userName);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public double getAc() {
        return ac;
    }

    public void setAc(double ac) {
        this.ac = ac;
    }

    public double getEv() {
        return ev;
    }

    public double getEtc() {
        return etc;
    }

    public List<IssueSimplePO> getEvIssues() {
        return evIssues;
    }

    public List<IssueSimplePO> getLeftIssues() {
        return leftIssues;
    }

    public double getRa() {
        return ra;
    }

    public void setRa(double ra) {
        this.ra = ra;
    }

    public LocalDate getDevStart() {
        return devStart;
    }

    public void setDevStart(LocalDate devStart) {
        this.devStart = devStart;
    }

    public LocalDate getDevEnd() {
        return devEnd;
    }

    public void setDevEnd(LocalDate devEnd) {
        this.devEnd = devEnd;
    }

    public void addTaskIssueData(IssueSimplePO po) {
        boolean resolved = Const.RESOLVED_ISSUE_STATUS.contains(po.getStatus());
        if (resolved) {
            evIssues.add(po);
            ev += po.getEstHour();
        } else {
            etc += po.getEstHour();
            leftIssues.add(po);
        }
    }
}
