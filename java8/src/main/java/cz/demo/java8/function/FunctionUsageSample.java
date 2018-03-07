package cz.demo.java8.function;

import cz.demo.java8.model.Student;
import cz.demo.java8.util.MockUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FunctionUsageSample {
    public static void main(String[] args) {
        // runnableSample();
        sortSample();
    }

    public static void runnableSample() {
        CompletableFuture.runAsync(() -> System.out.println(LocalDateTime.now()));
    }

    public static void sortSample() {
        // MockUtil.MOCK_STUDENTS.sort(Comparator.comparing(Student::getAge));
        List<Student> ageSortedList = MockUtil.MOCK_STUDENTS.stream().sorted(Comparator.comparing(Student::getAge))
                .collect(Collectors.toList());
        System.out.println(ageSortedList);
    }
}
