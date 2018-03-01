package de.micromata.jira.rest.custom.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.Const;

public class FileUtil {
    public static Set<String> developers() throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Properties prop = new Properties();
        prop.load(new FileReader(path + Const.USER_PROP_NAME));
        return prop.entrySet().stream()
                .filter(ent -> (ent.getValue() != null && ent.getValue().toString().startsWith("dev")))
                .map(ent -> ent.getKey().toString()).collect(Collectors.toSet());
    }

    public static Set<String> allusers() throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Properties prop = new Properties();
        prop.load(new FileReader(path + Const.USER_PROP_NAME));
        return prop.entrySet().stream().filter(ent -> (ent.getValue() != null)).map(ent -> ent.getKey().toString().trim())
                .collect(Collectors.toSet());
    }

    public static List<String> releaseTasks() throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        return Files.readAllLines(new File(path, Const.TASK_LIST_NAME).toPath()).stream()
                .filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toList());
    }
}
