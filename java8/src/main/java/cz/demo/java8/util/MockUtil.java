package cz.demo.java8.util;

import cz.demo.java8.model.Student;

import java.util.Arrays;
import java.util.List;

public class MockUtil {
    public static final List<Student> MOCK_STUDENTS = Arrays.asList(new Student("A", 12, Student.Gender.FEMALE),
            new Student("B", 12, Student.Gender.MALE), new Student("C", 13, Student.Gender.FEMALE),
            new Student("D", 13, Student.Gender.MALE), new Student("E", 14, Student.Gender.FEMALE),
            new Student("F", 12, Student.Gender.FEMALE), new Student("G", 15, Student.Gender.FEMALE),
            new Student("H", 13, Student.Gender.FEMALE), new Student("I", 16, Student.Gender.MALE),
            new Student("J", 14, Student.Gender.MALE), new Student("K", 11, Student.Gender.FEMALE),
            new Student("L", 12, Student.Gender.FEMALE), new Student("M", 12, Student.Gender.MALE));
}
