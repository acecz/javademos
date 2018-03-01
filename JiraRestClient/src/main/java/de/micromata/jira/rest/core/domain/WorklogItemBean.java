package de.micromata.jira.rest.core.domain;

import com.google.gson.annotations.Expose;

public class WorklogItemBean {
    @Expose
    private String self;
    @Expose
    private UserBean author;
    private UserBean updateAuthor;

    private String comment;

    private String created;

    private String updated;
    @Expose
    private String started;
    @Expose
    private Integer timeSpentSeconds;
    @Expose
    private Integer id;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public UserBean getUpdateAuthor() {
        return updateAuthor;
    }

    public void setUpdateAuthor(UserBean updateAuthor) {
        this.updateAuthor = updateAuthor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
