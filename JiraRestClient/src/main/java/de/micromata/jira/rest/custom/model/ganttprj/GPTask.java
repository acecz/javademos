package de.micromata.jira.rest.custom.model.ganttprj;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class GPTask {
    private static final String XML_FMT = "<task id=\"%s\" name=\"%s\" color=\"%s\" meeting=\"false\" start=\"%s\" duration=\"%s\" complete=\"0\" thirdDate=\"%s\" thirdDate-constraint=\"0\" expand=\"true\"%s>";
    String id;
    String name;
    String color;
    String meeting = "false";
    String start;
    String duration;
    int complete = 0;
    String thirdDate;
    String thirdDate_constraint = "0";
    int priority = 5;
    String parentId;
    String expand = "true";
    private TreeSet<GPTask> subIssues = new TreeSet<>(Comparator.comparing(GPTask::getId));

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMeeting() {
        return meeting;
    }

    public void setMeeting(String meeting) {
        this.meeting = meeting;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public String getThirdDate() {
        return thirdDate;
    }

    public void setThirdDate(String thirdDate) {
        this.thirdDate = thirdDate;
    }

    public String getThirdDate_constraint() {
        return thirdDate_constraint;
    }

    public void setThirdDate_constraint(String thirdDate_constraint) {
        this.thirdDate_constraint = thirdDate_constraint;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public TreeSet<GPTask> getSubIssues() {
        return subIssues;
    }

    public void setSubIssues(TreeSet<GPTask> subIssues) {
        this.subIssues = subIssues;
    }

    public void addSubIssues(GPTask subIssue) {
        this.subIssues.add(subIssue);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> buildGanttXmlLines() {
        List<String> lines = new ArrayList<>();
        if (subIssues.size() > 0) {
            lines.add(String.format(XML_FMT, id, name, color, start, duration, thirdDate, ""));
            subIssues.forEach(si -> lines.addAll(si.buildGanttXmlLines()));
            lines.add("</task>");
        } else {
            lines.add(String.format(XML_FMT, id, name, color, start, duration, thirdDate, "/"));
        }
        return lines;
    }
}
