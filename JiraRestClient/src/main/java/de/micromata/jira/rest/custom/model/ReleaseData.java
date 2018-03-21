package de.micromata.jira.rest.custom.model;

import de.micromata.jira.rest.core.Const;
import de.micromata.jira.rest.custom.util.FileUtil;
import de.micromata.jira.rest.custom.util.ReportUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ReleaseData {
    private LocalDate devStart;
    private LocalDate devEnd;
    private double ac = 0;
    private double ev = 0;
    private double etc = 0;
    private double ra = 0;
    private List<IssueSimplePO> tasks;
    private List<IssueSimplePO> bugs;
    private Map<String, UserInfo> userMap = new HashMap<>();
    private Map<String, UserReleaseData> userDataMap = new TreeMap<>();

    public Map<String, UserReleaseData> getUserDataMap() {
        return userDataMap;
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

    public void setTasks(List<IssueSimplePO> tasks) {
        this.tasks = tasks;
    }

    public void setBugs(List<IssueSimplePO> bugs) {
        this.bugs = bugs;
    }

    public void adjustForGantt() {
        tasks = tasks == null ? new ArrayList<>() : tasks;
        tasks.forEach(po -> {
            String owner = po.getOwner();
            userDataMap.computeIfAbsent(owner, key -> new UserReleaseData(key)).addTaskIssueData(po);
        });
        userDataMap.values().forEach(urd -> {
            UserInfo user = calcUserVal(urd);
            urd.setAc(user.getAc());
            urd.setRa(user.getRa());
            addTimeData(urd);
            urd.setDevStart(user.getStart());
            urd.setDevEnd(user.getEnd());
            urd.getEvIssues().sort(Comparator.comparing(IssueSimplePO::getPriority));
            urd.getLeftIssues().sort(Comparator.comparing(IssueSimplePO::getPriority));
        });
    }

    private void addTimeData(UserReleaseData urd) {
        ac += urd.getAc();
        ev += urd.getEv();
        etc += urd.getEtc();
        ra += urd.getRa();
    }

    public double getAc() {
        return ac;
    }

    public double getEv() {
        return ev;
    }

    public double getEtc() {
        return etc;
    }

    public double getRa() {
        return ra;
    }

    private UserInfo calcUserVal(UserReleaseData data) {
        UserInfo user = userMap.computeIfAbsent(data.getUserName(), k -> {
            UserInfo ui = new UserInfo();
            ui.setUserName(k);
            ui.setStart(getDevStart());
            ui.setEnd(devEnd);
            return ui;
        });
        try {
            Set<LocalDate> holidays = FileUtil.holidayDates();
            LocalDate now = LocalDate.now();
            double userAc = 0;
            double userRa = 0;
            for (LocalDate day = user.getStart(); user.getEnd().isAfter(day); day = day.plusDays(1)) {
                if (holidays.contains(day)) {
                    continue;
                }
                if (day.isBefore(now)) {
                    userAc += 8;
                } else {
                    userRa += 8;
                }
            }
            user.setAc(userAc);
            user.setRa(userRa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
