# Java 8 新特性简介

[TOC]

## 哪些新特性

Java8 引入许多新的特性，详细列表见 [What's New in JDK 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html)
和 [Release Notes for JDK 8 and JDK 8 Update Releases](http://www.oracle.com/technetwork/java/javase/8all-relnotes-2226344.html)。

以下为对我们日常编程影响较大的特征:

- FunctionalInterface
- Lambda Expressions
- Stream API
- Optional
- java.time package
- CompletableFuture
- Utilities

主要特征的关联关系:
- Functional programming -> FunctionalInterface, Stream
- Stream -> Default/static methods in interfaces (java.util.List)
- FunctionalInterface -> Lambda Expressions, Method references to Lambda


## Functional Programming
### 概念
- 函数

数学中的函数定义: In mathematics, a function is a **relation** between a set of **inputs** and a set of permissible **outputs** with the property that each input is related to **exactly** one output. [Function (mathematics) - Wikipedia](https://en.wikipedia.org/wiki/Function_(mathematics))。
- FunctionalInterface:

_(Java Doc)_ An informative annotation type used to indicate that an interface type declaration is intended to be a functional interface as defined by the Java Language Specification.
Conceptually, a functional **interface** has **exactly one abstract method**.
Note that instances of functional interfaces can be created with **lambda expressions, method references, or constructor references**.
- Lambda Expressions

如anonymous class是接口或父类的便利实现形式，Lambda Expression是FunctionalInterface的一种便利实现方式。
- 形式映射

函数的三要素:定义域，值域，映射规则，对应Java方法即: 入参，返回值，内部行为。不管lambda expressions, method references, 或 constructor references，都是去依照FunctionalInterface的方法签名去实现具体行为。

- 行为参数化

函数（FunctionalInterface） 变成了一等公民，可以当作参数来传递了。这需要只熟悉OO程序员做思想上的转变。

### 样式和和示例
- 函数的创建形式
    - 实例化
    ```java
    Predicate<Student> ageFilter = new Predicate<Student>() {
                @Override
                public boolean test(Student student) {
                    return student.getAge() != null && student.getAge() == 12;
                }
            };
            List<Student> ttYrSts = MockUtil.MOCK_STUDENTS.stream().filter(ageFilter).collect(Collectors.toList());
    ```
    - lambda expressions and constructor references

      ```java
      StringBuilder nameContact = MockUtil.MOCK_STUDENTS.stream().collect(StringBuilder::new,
                      (s, st) -> s.append(st.getName()), (s1, s2) -> s1.append(s2));
      System.out.println(nameContact);
      ```

      ​

    - method references

      ```java
      List<String> randomNums = new Random().longs().limit(10).boxed().map(String::valueOf).peek(System.out::println)
                      .collect(Collectors.toList());
      System.out.println(randomNums);
      ```

      ​

      ​

- 典型用法
    - Runnable

      ```java
      CompletableFuture.runAsync(() -> System.out.println(LocalDateTime.now()));
      ```

      ​

    - Sort or Compare

      ```java
      // MockUtil.MOCK_STUDENTS.sort(Comparator.comparing(Student::getAge));
      List<Student> ageSortedList = MockUtil.MOCK_STUDENTS.stream().sorted(Comparator.comparing(Student::getAge))
          .collect(Collectors.toList());
      System.out.println(ageSortedList);
      ```

      ​


### 底层实现

- invokedynamic

  ```java
   public static void sortSample();
      Code:
         0: getstatic     #5                  // Field cz/demo/java8/util/MockUtil.MOCK_STUDENTS:Ljava/util/List;
         3: invokeinterface #6,  1            // InterfaceMethod java/util/List.stream:()Ljava/util/stream/Stream;
         8: invokedynamic #7,  0              // InvokeDynamic #1:apply:()Ljava/util/function/Function;
        13: invokestatic  #8                  // InterfaceMethod java/util/Comparator.comparing:(Ljava/util/function/Function;)Ljava/util/Comparator;
        16: invokeinterface #9,  2            // InterfaceMethod java/util/stream/Stream.sorted:(Ljava/util/Comparator;)Ljava/util/stream/Stream;

  ```

  It is a new JVM instruction which allows a compiler to generate code which calls methods with a looser specification than was previously possible -- if you know what "[duck typing](http://en.wikipedia.org/wiki/Duck_typing)" is, invokedynamic basically allows for duck typing. There's not too much you as a Java programmer can do with it; if you're a tool creator, though, you can use it to build more flexible, more efficient JVM-based languages. [Here](http://blog.headius.com/2008/09/first-taste-of-invokedynamic.html) is a really sweet blog post that gives a lot of detail.  [stackoverflow](https://stackoverflow.com/questions/6638735/whats-invokedynamic-and-how-do-i-use-it)


- 不易Debug和异常处理


## Stream
### 概念
- Stream

  [JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) A sequence of elements supporting sequential and parallel aggregate operations.

- Stream 和 Collection

  [JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) Collections and streams, while bearing some superficial similarities, have different goals. Collections are primarily concerned with the efficient management of, and access to, their elements. By contrast, streams do not provide a means to directly access or manipulate their elements, and are instead concerned with declaratively describing their source and the computational operations which will be performed in aggregate on that source. However, if the provided stream operations do not offer the desired functionality, the [`BaseStream.iterator()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/BaseStream.html#iterator--) and [`BaseStream.spliterator()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/BaseStream.html#spliterator--) operations can be used to perform a controlled traversal.

  [JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html) Streams differ from collections in several ways:

  - No storage. A stream is not a data structure that stores elements; instead, it conveys elements from a source such as a data structure, an array, a generator function, or an I/O channel, through a pipeline of computational operations.
  - Functional in nature. An operation on a stream produces a result, but does not modify its source. For example, filtering a `Stream` obtained from a collection produces a new `Stream` without the filtered elements, rather than removing elements from the source collection.
  - Laziness-seeking. Many stream operations, such as filtering, mapping, or duplicate removal, can be implemented lazily, exposing opportunities for optimization. For example, "find the first `String` with three consecutive vowels" need not examine all the input strings. Stream operations are divided into intermediate (`Stream`-producing) operations and terminal (value- or side-effect-producing) operations. Intermediate operations are always lazy.
  - Possibly unbounded. While collections have a finite size, streams need not. Short-circuiting operations such as `limit(n)` or `findFirst()` can allow computations on infinite streams to complete in finite time.
  - Consumable. The elements of a stream are only visited once during the life of a stream. Like an [`Iterator`](https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html), a new stream must be generated to revisit the same elements of the source.

### 样式和和示例

```java
 @SuppressWarnings("unchecked")
    public void autoAssignCasesToTasksTestPlan(Integer planOid) throws Exception {
        TestPlanEbo testplanEbo = get(planOid);
        PlanCaseMapQueryBean pcmQb = new PlanCaseMapQueryBean();
        pcmQb.setTestPlanOid(planOid);
        Set<Integer> planCases = ((List<PlanCaseMapEbo>) new PlanCaseMapDlo().list(pcmQb.getQueryReq())).stream()
                .map(PlanCaseMapEbo::getCaseOid).collect(Collectors.toSet());
        if (planCases.isEmpty()) {
            // TODO caozhe throw exception?
            return;
        }
        QueryReq<TestCasePCMapEbo> qr = new QueryReq<>(QueryTypeEnum.NATIVE);
        qr.setCls(TestCasePCMapEbo.class);
        qr.setSelect(String.format("SELECT tpm.* FROM %s tpm, %s pcm WHERE tpm.%s=pcm.%s AND pcm.%s=%d",
                TestCasePCMapEbo.DB_TABLE_NAME, PlanCaseMapEbo.DB_TABLE_NAME, TestCasePCMapEbo.ATTR_CaseOid,
                PlanCaseMapEbo.ATTR_CaseOid, PlanCaseMapEbo.ATTR_TestPlanOid, planOid));
        qr.setWhere(null);
        List<TestCasePCMapEbo> planPrecs = QueryUtil.executeQuery(getEntityManager(), qr);
        Set<String> planPrecCodes = planPrecs.stream().map(TestCasePCMapEbo::getPcCode).collect(Collectors.toSet());
        Map<String, Integer> allCodeIdxMap = PreconditionUtil.calcAllPrecIndexMap(planPrecCodes);
        // group case precondition codes
        Map<Integer, Set<String>> casePrecsMap = planPrecs.stream().collect(Collectors.groupingBy(
                TestCasePCMapEbo::getCaseOid, Collectors.mapping(TestCasePCMapEbo::getPcCode, Collectors.toSet())));
        Map<Integer, BitSet> casePrecCodesMap = new HashMap<>();
        // add case without precondition
        BitSet emptyBs = new BitSet();
        planCases.stream().filter(e -> !casePrecsMap.containsKey(e)).forEach(e -> casePrecCodesMap.put(e, emptyBs));
        // convert precondition codes to BitSet
        casePrecsMap.forEach((k, v) -> casePrecCodesMap.put(k, PreconditionUtil.calcCodesNum(v, allCodeIdxMap)));
        // group cases by precondition BitSet
        Map<BitSet, List<Integer>> taskCaseMap = casePrecCodesMap.entrySet().stream().collect(
                Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        createAssignedTaskCaseMap(testplanEbo, taskCaseMap.values());
    }
```

```java
 public static void streamSample() {
        List<String> uselessList = MockUtil.MOCK_STUDENTS.stream().limit(3).map(s -> s.toString())
                .flatMap(s -> Arrays.stream(s.split("E|e"))).collect(Collectors.toList());
        System.out.println(uselessList);
    }
```



### 并行处理

- 样式：

  ```java
  MockUtil.MOCK_STUDENTS.stream(). ...
  MockUtil.MOCK_STUDENTS.parallelStream(). ...
  ```

  ​


- 特点

  - 充分利用多核心CPU的计算能力
  - 并行处理的数据相互没有依赖
  - 使用的各个函数是无状态的
  - 收集器规则不复杂

- 分析与限制

  - 影响因素: 数据量，CPU核心数，数据结构，计算资源需求比重，分解算法，合并操作，中间操作


  - 使用并行流的注意点
    - 有疑问或不确定，测量
    - 留意自动装箱
    - 避免某些顺序流比并行流快的坑, (limit,findFirst...)
    - 衡量流处理的整体计算成本，一般计算成本比重越高，越适合并行流。
    - 评估数据量和计算机核数
    - 考虑流中数据结构是否易于分解，以及流中间行为对数据分解的影响
    - 最终收集合并行为的影响


  - [[Should I always use a parallel stream when possible?](https://stackoverflow.com/questions/20375176/should-i-always-use-a-parallel-stream-when-possible)]
  - [Parallel stream processing in Java 8 – performance of sequential vs. parallel stream processing](https://blog.oio.de/2016/01/22/parallel-stream-processing-in-java-8-performance-of-sequential-vs-parallel-stream-processing/)

- 技术

  - Fork/Join Framework 

    Fork/Join parallelism is among the simplest and most effective design techniques for obtaining good parallel performance.
    The fork operation starts a new parallel fork/join subtask. The join operation causes the current task not to proceed until the forked subtask has completed. Fork/join algorithms, like other divide−and−conquer algorithms, are nearly always recursive, repeatedly splitting subtasks until they are small enough to solve using simple, short sequential methods. — [A Java Fork/Join Framework](http://gee.cs.oswego.edu/dl/papers/fj.pdf)  - Doug Lea

    [Fork and Join: Java Can Excel at Painless Parallel Programming Too!](http://www.oracle.com/technetwork/articles/java/fork-join-422606.html)

    -  Work−Stealing 负载均衡测量

    ​

### 底层实现

[[深入理解Java Stream流水线](http://www.cnblogs.com/CarpenterLee/p/6637118.html)]


## Utilities And Functions

### Java Time Package

- Why do we need a new date and time library?  [Java SE 8 Date and Time](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html)
  - Old data-time issue
    - thread-safe issue (mutable)
    - poor API design
  - Core Ideas
    - Immutable-value classes
    - Domain-driven design
    - Separation of chronologies


- [Package Hierarchies](https://docs.oracle.com/javase/8/docs/api/java/time/package-tree.html)

- 示例

  ```java
   public static String time2Str(Instant instant, TimePattern pattern) {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.pattern).withZone(ZoneId.systemDefault());
          return formatter.format(instant);
   }

  public static String duration2Str(Duration duration) {
          long millis = duration.toMillis() % 1000;
          long seconds = duration.getSeconds();
          return String.format("%d:%02d:%02d.%03d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60), millis);
      }
  ```

  ```java
     
  TimeUtil.durationMinSec2Str(Duration.between(startTimestamp, endTimestamp)
  ```

  class: TimeSample

### Optional

- 引入原因

  Java 8 has introduced a new class Optional in java.util package. It is used to represent a value is present or absent. The main advantage of this new construct is that No more too many null checks and `NullPointerException`. It avoids any runtime `NullPointerExceptions` and supports us in developing clean and neat Java APIs or Applications. Like Collections and arrays, it is also a Container to hold at most one value. Let us explore this new construct with some useful examples.

  Advantages of Java 8 Optional:

  1. Null checks are not required.
  2. No more NullPointerException at run-time.
  3. We can develop clean and neat APIs.
  4. No more Boiler plate code

  [Java 8 Optional In Depth](https://www.mkyong.com/java8/java-8-optional-in-depth/)

### Files

- 遍历文件（夹）

  ```java
  public static void filterFiles() throws IOException {
          // File[] files = new File(".").listFiles((dir, name) -> true);
          Files.walk(new File(".").toPath(), FileVisitOption.FOLLOW_LINKS)
                  .forEach(p -> System.out.println(p.toFile().getAbsolutePath()));
      }
  ```

  ​

- 读取文件

  ```java
  public static void readFile() throws IOException {
          Optional<Path> anyFile = Files.walk(new File(".").toPath(), FileVisitOption.FOLLOW_LINKS)
                  .filter(p -> p.toFile().getName().endsWith(".java")).findAny();
          if (anyFile.isPresent()) {
              Optional<String> clzDeclare = Files.lines(anyFile.get()).parallel()
                      .filter(s -> s != null && s.trim().startsWith("public class")).peek(s -> System.out.println(s))
                      .findAny();
              System.out.println(clzDeclare.orElse("none"));
          }
      }
  ```

  ​

- 写入文件

  ```java
  private static void originalWorklog(List<WorklogSimplePO> worklogs) {
          List<String> wlCsvRows = new ArrayList<>();
          wlCsvRows.add("IssueKey,User,Day,Worklog,WorkDesc");
          String wlRowFmt = "%s,%s,%s,%f,%s";
          worklogs.forEach(wl -> {
              String row = String.format(wlRowFmt, wl.getIssueKey(), wl.getUserId(),
                      wl.getWorkDate().format(Const.YEAR2DAY_FMT), wl.getTimeSpentHours(), wl.getWorkDesc());
              wlCsvRows.add(row);
          });
          try {
              File csv = new File("worklogs.csv");
              if (csv.exists()) {
                  csv.delete();
              }
              Files.write(csv.toPath(), wlCsvRows, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  ```

  ​

### Try-with-resource

- [The try-with-resources Statement](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)

  > Any object that implements `java.lang.AutoCloseable`, which includes all objects which implement `java.io.Closeable`, can be used as a resource.

- 示例

  ```java
  try (FileInputStream imgIs1 = new FileInputStream(benchmarkPath); FileInputStream imgIs2 = new FileInputStream(
                  comparePath);) {
              // do something
          } catch (Exception e) {
              log.error("compare imgs error! " + benchmarkPath, e);
              // throw new FunctionException(e);
          }
  ```

  ```java
  private static BufferedImage loadJPG(File resource) throws Exception {
          BufferedImage bi = null;
          try (FileInputStream in = new FileInputStream(resource)) {
              bi = ImageIO.read(in);
          } catch (Exception io) {
              log.error("Error happened when try to load the image from " + resource.getName());
              if (io.getCause().getMessage().contains("Unexpected end of ZLIB input stream")) {
                  log.error("resource maybe truncated, please check: " + resource.getAbsolutePath());
                  throw new FunctionException(3549, io, resource.getAbsolutePath());
              }
              throw io;
          }
          if (bi == null) {
              log.debug("bi is null.");
              throw new FunctionException(3538, resource.getAbsolutePath());
          }
          return bi;
      }
  ```

  ​

### CompletableFuture

// TODO



