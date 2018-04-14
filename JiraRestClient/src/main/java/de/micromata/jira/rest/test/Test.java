package de.micromata.jira.rest.test;

import de.micromata.jira.rest.core.Const;

import java.time.LocalDate;
import java.util.concurrent.atomic.DoubleAccumulator;

public class Test {
    public static void main(String[] args) {
        printHolidays();
    }

    private static void printHolidays() {
//        LocalDate date = LocalDate.of(2018, 1, 6);
//        for (; date.getYear() <= 2018; date = date.plusDays(7)) {
//            System.out.println(date.format(Const.YEAR2DAY_FMT));
//            System.out.println(date.plusDays(1).format(Const.YEAR2DAY_FMT));
//        }

        DoubleAccumulator sumValAcc = new DoubleAccumulator((a, b) -> a + b, 0);
        System.out.println(sumValAcc.doubleValue());
        sumValAcc.accumulate(10);
        System.out.println(sumValAcc.doubleValue());
        sumValAcc.accumulate(15);
        System.out.println(sumValAcc.doubleValue());
        sumValAcc.accumulate(-1);
        System.out.println(sumValAcc.doubleValue());
    }
}
