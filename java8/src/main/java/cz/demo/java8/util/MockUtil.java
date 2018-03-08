package cz.demo.java8.util;

import cz.demo.java8.model.Action;
import cz.demo.java8.model.Case;
import cz.demo.java8.model.Student;
import cz.demo.java8.model.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class MockUtil {
    public static final List<Student> MOCK_STUDENTS = Arrays.asList(new Student("A", 12, Student.Gender.FEMALE),
            new Student("B", 12, Student.Gender.MALE), new Student("C", 13, Student.Gender.FEMALE),
            new Student("D", 13, Student.Gender.MALE), new Student("E", 14, Student.Gender.FEMALE),
            new Student("F", 12, Student.Gender.FEMALE), new Student("G", 15, Student.Gender.FEMALE),
            new Student("H", 13, Student.Gender.FEMALE), new Student("I", 16, Student.Gender.MALE),
            new Student("J", 14, Student.Gender.MALE), new Student("K", 11, Student.Gender.FEMALE),
            new Student("L", 12, Student.Gender.FEMALE), new Student("M", 12, Student.Gender.MALE));

    private static Task mockTask = null;

    public static Task mockTask() {
        if (mockTask != null) {
            return mockTask;
        }
        mockTask = new Task();
        IntStream.range(1, 9).forEach(i -> {
            Case cs = new Case(i);
            mockTask.addCase(cs);
            IntStream.range(1, 5).forEach(j -> {
                Action act = new Action(i * 10 + j);
                cs.addActions(act);
            });
        });
        return mockTask;
    }
}
