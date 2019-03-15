package de.micromata.jira.rest.test;

import java.time.LocalDate;
import java.util.List;

import de.micromata.jira.rest.custom.model.IssueSimplePO;
import de.micromata.jira.rest.custom.model.ReleaseData;
import de.micromata.jira.rest.custom.util.IssueQueryUtil;
import de.micromata.jira.rest.custom.util.ReportUtil;

public class ReleaseGantt extends BaseClient {
    static final String releaseTaskFile = "taskR60.txt";
    static final String releaseBugFile = "bugR60.txt";

    public static void main(String[] args) throws Exception {
        try {
            connect();
            ReleaseData releaseData = new ReleaseData();
            releaseData.setDevStart(LocalDate.of(2019, 01, 28));
            releaseData.setDevEnd(LocalDate.of(2019, 02, 28));
            List<IssueSimplePO> releaseTasks = IssueQueryUtil.releaseTasks(restClient, "\"R6.5.0\"");
            releaseData.setTasks(releaseTasks);
            // List<IssueSimplePO> ongoingBugs = IssueQueryUtil.devOngoingBugs(restClient);
            // releaseData.setBugs(ongoingBugs);
            releaseData.adjustForGantt();
            ReportUtil.ganttReport(releaseData);
        } finally {
            disConnect();
        }
        // searchProjects();
    }
}
