package cz.demo.java8.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class FJCountTask extends RecursiveTask<Long> {
    private static final long serialVersionUID = -3611254198265061729L;
    public static final long offset = 1000000000L;
    public static final String fmt = "%d-%d-%s-%s";
    public static final int threshold = 200000000;
    private long start;
    private long end;

    public FJCountTask(long start, long end) {
        System.out.println(String.format(fmt, offset + start, offset + end, "S", Thread.currentThread().getId()));
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        // 如果任务足够小就计算任务
        boolean canCompute = (end - start) <= threshold;
        if (canCompute) {
            for (long i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            long middle = (start + end) / 2;
            FJCountTask leftTask = new FJCountTask(start, middle);
            FJCountTask rightTask = new FJCountTask(middle + 1, end);

            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            // 等待任务执行结束合并其结果
            long leftResult = leftTask.join();
            long rightResult = rightTask.join();

            // 合并子任务
            sum = leftResult + rightResult;
        }
        System.out
                .println("a\t" + String.format(fmt, offset + start, offset + end, "E", Thread.currentThread().getId()));
        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool forkjoinPool = new ForkJoinPool();

        // 生成一个计算任务，计算1+2+3+4
        FJCountTask task = new FJCountTask(1, 999999999);

        // 执行一个任务
        Future<Long> result = forkjoinPool.submit(task);

        try {
            System.out.println(result.get());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
