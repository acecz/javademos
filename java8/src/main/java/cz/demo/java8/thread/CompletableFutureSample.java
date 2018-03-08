package cz.demo.java8.thread;

import cz.demo.java8.model.Case;
import cz.demo.java8.model.Task;
import cz.demo.java8.util.MockUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CompletableFutureSample {
    public static void main(String[] args) {
        taskExecutionSample();
    }

    public static void taskExecutionSample() {
        Task task = MockUtil.mockTask();
        Executor executor = Executors.newFixedThreadPool(10);
        CompletableFuture<Void> taskCf = CompletableFuture.runAsync(() -> System.out.println("Task Start"), executor);
        for (Case c : task.getCases()) {
            taskCf = taskCf.thenRun(() -> c.execute(executor));
        }
        taskCf.thenRun(() -> System.out.println("Task End"));
        taskCf.complete(null);
    }
}
