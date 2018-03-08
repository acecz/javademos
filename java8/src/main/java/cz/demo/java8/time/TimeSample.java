package cz.demo.java8.time;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeSample {
    static final LocalDate nowDate = LocalDate.now();
    static final LocalTime nowTime = LocalTime.now();
    static final Instant nowInstant = Instant.now();
    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
    static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // LocalDate, LocalTime, LocalDateTime, Instant, Duration, Period, DateTimeFormatter
    public static void main(String[] args) {
        // localDateSample();
        // localTimeSample();
        // localDateTimeSample();
        // instantSample();
        durationSample();
    }

    public static void localDateSample() {
        System.out.println(nowDate.format(dateFormatter));
        // System.out.println(nowDate.format(timeFormatter));
        // System.out.println(nowDate.format(dateTimeFormatter));
    }

    public static void localTimeSample() {
        // System.out.println(nowTime.format(dateFormatter));
        System.out.println(nowTime.format(timeFormatter));
        // System.out.println(nowTime.format(dateTimeFormatter));
    }

    public static void localDateTimeSample() {
        LocalDateTime dateTime = LocalDateTime.of(nowDate, nowTime);
        System.out.println(dateTime.format(dateFormatter));
        System.out.println(dateTime.format(timeFormatter));
        System.out.println(dateTime.format(dateTimeFormatter));
    }

    public static void instantSample() {
        System.out.println(dateFormatter.withZone(ZoneId.systemDefault()).format(nowInstant));
        // System.out.println(dateFormatter.format(nowInstant));
        System.out.println(timeFormatter.withZone(ZoneId.systemDefault()).format(nowInstant));
        System.out.println(dateTimeFormatter.withZone(ZoneId.systemDefault()).format(nowInstant));
    }

    public static void durationSample() {
        System.out.println(Duration.between(nowTime.plusHours(2), nowTime).abs().getSeconds());
        // System.out.println(Duration.between(nowDate.plusDays(2), nowDate).abs().getSeconds());
    }

    public static void periodSample() {
        // System.out.println(Period.between(nowTime.plusHours(2), nowTime).abs().getSeconds());
        System.out.println(Period.between(nowDate.plusDays(2), nowDate).getDays());
    }
}
