package cz.demo.java8.function;

import cz.demo.java8.model.Student;
import cz.demo.java8.util.MockUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FunctionCreation {
    public static void main(String[] args) {
        // instance();
        // constructorReferences();
        methodReferences();
    }

    public static void instance() {
        Predicate<Student> ageFilter = new Predicate<Student>() {
            @Override
            public boolean test(Student student) {
                return student.getAge() != null && student.getAge() == 12;
            }
        };
        List<Student> ttYrSts = MockUtil.MOCK_STUDENTS.stream().filter(ageFilter).collect(Collectors.toList());
        System.out.println(ttYrSts);
    }

    public static void constructorReferences() {
        StringBuilder nameContact = MockUtil.MOCK_STUDENTS.stream().collect(StringBuilder::new,
                (s, st) -> s.append(st.getName()), (s1, s2) -> s1.append(s2));
        System.out.println(nameContact);
    }

    public static void methodReferences() {
        List<String> randomNums = new Random().longs().limit(10).boxed().map(String::valueOf).peek(System.out::println)
                .collect(Collectors.toList());
        System.out.println(randomNums);
    }

}
