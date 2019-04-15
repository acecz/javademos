package de.micromata.jira.rest.core;

import java.time.format.DateTimeFormatter;
import java.util.*;

public interface Const {
    String RESOURCE_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    String RELEASE_DIR = "release";
    String HOLIDAYS_FILE = "holidays.txt";
    DateTimeFormatter YAER2MS_TZ_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    DateTimeFormatter YEAR2DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter YAER2MS_NO_SPACE = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    String USER_PROP_NAME = "members.properties";
    String TASK_LIST_NAME = "tasks.txt";
    Set<String> RESOLVED_ISSUE_STATUS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Closed", "Resolved")));
    String ANONYMOUS_USER = "anonymous";
    List<String> PROJECTS = Arrays.asList("CNTMAT", "MATSUP");
}
