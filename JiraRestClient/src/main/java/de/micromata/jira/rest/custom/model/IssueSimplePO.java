package de.micromata.jira.rest.custom.model;

public class IssueSimplePO {
    private String key;
    private String id;
    private String issueType;
    private String priority;
    private String selfUrl;

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

    @Override
    public String toString() {
        return "IssueSimplePO{" +
            "key='" + key + '\'' +
            ", id='" + id + '\'' +
            ", issueType='" + issueType + '\'' +
            ", priority='" + priority + '\'' +
            ", selfUrl='" + selfUrl + '\'' +
            '}';
    }
}
