package de.micromata.jira.rest.custom.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CsvMatrix {
    private Set<String> columnSet = new TreeSet<>();

    private Set<String> rowSet = new TreeSet<>();

    private Map<String, Map<String, Double>> rowColValMap = new HashMap<>();

    public Map<String, Map<String, Double>> getRowColValMap() {
        return rowColValMap;
    }

    public void setRowColValMap(Map<String, Map<String, Double>> rowColValMap) {
        this.rowColValMap = rowColValMap;
    }

    public Set<String> getColumnSet() {
        return columnSet;
    }

    public void setColumnSet(Set<String> columnSet) {
        this.columnSet = columnSet;
    }

    public Set<String> getRowSet() {
        return rowSet;
    }

    public void setRowSet(Set<String> rowSet) {
        this.rowSet = rowSet;
    }
}
