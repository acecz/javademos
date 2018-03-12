package de.micromata.jira.rest.custom.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.Const;

public class FileUtil {
    private static final TreeSet<String> HOLIDAYS = new TreeSet<>();
    private static final TreeSet<LocalDate> HOLIDAY_DATES = new TreeSet<>();

    public static Set<LocalDate> holidayDates() throws IOException {
        synchronized (FileUtil.class) {
            if (HOLIDAY_DATES.isEmpty()) {
                Files.readAllLines(new File(Const.RESOURCE_PATH, Const.HOLIDAYS_FILE).toPath()).stream()
                        .filter(s -> s != null && !s.trim().isEmpty()).peek(HOLIDAYS::add)
                        .forEach(s -> HOLIDAY_DATES.add(LocalDate.parse(s, Const.YEAR2DAY_FMT)));
            }
        }
        return HOLIDAY_DATES;
    }

    public static Set<String> holidays() throws IOException {
        synchronized (FileUtil.class) {
            if (HOLIDAYS.isEmpty()) {
                Files.readAllLines(new File(Const.RESOURCE_PATH, Const.HOLIDAYS_FILE).toPath()).stream()
                        .filter(s -> s != null && !s.trim().isEmpty()).peek(HOLIDAYS::add)
                        .forEach(s -> HOLIDAY_DATES.add(LocalDate.parse(s, Const.YEAR2DAY_FMT)));
            }
        }
        return HOLIDAYS;
    }

    public static Set<String> developers() throws Exception {
        Properties prop = new Properties();
        prop.load(new FileReader(Const.RESOURCE_PATH + Const.USER_PROP_NAME));
        return prop.entrySet().stream()
                .filter(ent -> (ent.getValue() != null && ent.getValue().toString().startsWith("dev")))
                .map(ent -> ent.getKey().toString()).collect(Collectors.toSet());
    }

    public static Set<String> allusers() throws Exception {
        Properties prop = new Properties();
        prop.load(new FileReader(Const.RESOURCE_PATH + Const.USER_PROP_NAME));
        return prop.entrySet().stream().filter(ent -> (ent.getValue() != null))
                .map(ent -> ent.getKey().toString().trim()).collect(Collectors.toSet());
    }

    public static List<String> releaseTasks() throws Exception {
        return Files.readAllLines(new File(Const.RESOURCE_PATH, Const.TASK_LIST_NAME).toPath()).stream()
                .filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toList());
    }

    public static List<String> releaseTasks(String releaseTaskFile) throws IOException {
        return Files.readAllLines(new File(Const.RESOURCE_PATH + Const.RELEASE_DIR, releaseTaskFile).toPath()).stream()
                .filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toList());
    }
}
