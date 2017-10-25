package cz.demo.java8.function;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FuncFormat {
    public static void main(String[] args) {
        rename();
        testVoid();
        testMatch();
    }

    private static void testMatch() {
        List<String> strs = Arrays.asList("a", "b", "c");
        Predicate<String> predicate = (String s) -> s.isEmpty();
        Consumer<String> consumer = (String s) -> s.isEmpty();
        strs.forEach((String s) -> s.isEmpty());
        strs.stream().filter((String s) -> s.isEmpty());
//        filter (java.util.function.Predicate<? super java.lang.String>)
//        in Stream cannot be applied to (java.util.function.Consumer<java.lang.String>)
//        strs.stream().filter(consumer);
        strs.stream().filter(String::isEmpty);

    }

    private static void testVoid() {
        Predicate<String> predicate = (String s) -> s.isEmpty();
        // Consumer: T -> Void, real: T -> boolean
        Consumer<String> consumer = (String s) -> s.isEmpty();

        //        incompatible types.
//        Required:
//        java.util.function.Consumer<String>
//        Found:
//        java.util.function.Predicate<String>

//        consumer = predicate;
    }

    private static void rename() {
        Runnable r = () -> System.out.println("rename");
        Object o = r;
        System.out.println(o);
        // ERR: Target type of a lambda conversion must be an interface
        // Object o1 = () -> System.out.println("rename");
    }
}
