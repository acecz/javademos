package de.micromata.jira.rest.core;

import java.time.format.DateTimeFormatter;

public interface Const {
    DateTimeFormatter LOCAL_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}
