package de.micromata.jira.rest.core;

import java.time.format.DateTimeFormatter;

public interface Const {
    DateTimeFormatter YAER2MS_TZ_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    DateTimeFormatter YEAR2DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
