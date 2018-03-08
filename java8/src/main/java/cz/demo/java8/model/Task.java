package cz.demo.java8.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private List<Case> cases;

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public void addCase(Case cs) {
        if (cases == null) {
            cases = new ArrayList<>();
        }
        this.cases.add(cs);
    }
}
