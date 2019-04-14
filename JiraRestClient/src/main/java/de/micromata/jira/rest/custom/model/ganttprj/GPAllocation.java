package de.micromata.jira.rest.custom.model.ganttprj;

public class GPAllocation {
    String taskId;
    String resourceId;
    String function = "Default:0";
    String responsible = "true";
    String load = "100.0";

    public GPAllocation(String taskId, String resourceId) {
        this.taskId = taskId;
        this.resourceId = resourceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }
}
