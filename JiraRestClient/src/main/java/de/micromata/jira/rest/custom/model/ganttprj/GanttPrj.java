package de.micromata.jira.rest.custom.model.ganttprj;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GanttPrj {
    private LocalDate prjStart;
    private List<GPTask> tasks = new ArrayList<>();
    private List<GPResource> resources = new ArrayList<>();
    private List<GPAllocation> allocations = new ArrayList<>();

    public LocalDate getPrjStart() {
        return prjStart;
    }

    public void setPrjStart(LocalDate prjStart) {
        this.prjStart = prjStart;
    }

    public List<GPTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<GPTask> tasks) {
        this.tasks = tasks;
    }

    public void addTasks(GPTask task) {
        this.tasks.add(task);
    }

    public List<GPResource> getResources() {
        return resources;
    }

    public void setResources(List<GPResource> resources) {
        this.resources = resources;
    }

    public void addResources(GPResource resource) {
        this.resources.add(resource);
    }

    public List<GPAllocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<GPAllocation> allocations) {
        this.allocations = allocations;
    }

    public void addAllocations(GPAllocation allocation) {
        this.allocations.add(allocation);
    }
}
