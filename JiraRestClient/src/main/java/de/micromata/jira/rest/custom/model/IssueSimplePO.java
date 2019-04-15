package de.micromata.jira.rest.custom.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.micromata.jira.rest.core.util.JsonUtil;

public class IssueSimplePO {
    private String key;
    private String id;
    private String status;
    private String assignee;
    private String owner;
    private String summary;
    private String issueType;
    private String priority;
    private String selfUrl;
    private Double estHour;
    private Double sumEstHour;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dueDate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate naturalStartDay;
    private String resolution;
    private String parentId;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate resolutionDate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdDate;
    private TreeSet<IssueSimplePO> subIssues = new TreeSet<>(Comparator.comparing(IssueSimplePO::getId));

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(String selfUrl) {
        this.selfUrl = selfUrl;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getEstHour() {
        return estHour;
    }

    public void setEstHour(Double estHour) {
        this.estHour = estHour;
    }

    public Double getSumEstHour() {
        return sumEstHour;
    }

    public void setSumEstHour(Double sumEstHour) {
        this.sumEstHour = sumEstHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getNaturalStartDay() {
        return naturalStartDay;
    }

    public void setNaturalStartDay(LocalDate naturalStartDay) {
        this.naturalStartDay = naturalStartDay;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return JsonUtil.dbgStr(this);
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public TreeSet<IssueSimplePO> getSubIssues() {
        return subIssues;
    }

    public void addSubIssue(IssueSimplePO subIssue) {
        this.subIssues.add(subIssue);
    }
}
