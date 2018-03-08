package cz.demo.java8.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class Case {
    private int index;
    private List<Action> actions;

    public Case() {
    }

    public Case(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void addActions(Action act) {
        if (actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(act);
    }

    public void execute(Executor executor) {
        System.out.println(String.format("case %d start", index));

        System.out.println(String.format("case %d end", index));
    }
}
