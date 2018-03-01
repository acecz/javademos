package de.micromata.jira.rest.core;

import java.time.format.DateTimeFormatter;

public interface Const {
    DateTimeFormatter YAER2MS_TZ_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    DateTimeFormatter YEAR2DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String USER_PROP_NAME = "members.properties";
    String TASK_LIST_NAME = "tasks.txt";
}
