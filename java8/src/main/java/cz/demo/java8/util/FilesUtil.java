package cz.demo.java8.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FilesUtil {
    public static void main(String[] args) throws IOException {
        // filterFiles();
        readFile();
    }

    public static void filterFiles() throws IOException {
        // File[] files = new File(".").listFiles((dir, name) -> true);
        Files.walk(new File(".").toPath(), FileVisitOption.FOLLOW_LINKS)// .filter(p -> p.endsWith(".java"))
                .forEach(p -> System.out.println(p.toFile().getAbsolutePath()));
    }

    public static void readFile() throws IOException {
        Optional<Path> anyFile = Files.walk(new File(".").toPath(), FileVisitOption.FOLLOW_LINKS)
                .filter(p -> p.toFile().getName().endsWith(".java")).findAny();
        if (anyFile.isPresent()) {
            Optional<String> clzDeclare = Files.lines(anyFile.get()).parallel()
                    .filter(s -> s != null && s.trim().startsWith("public class1")).peek(s -> System.out.println(s))
                    .findAny();
            System.out.println(clzDeclare.orElse("none"));
        }
    }
}
