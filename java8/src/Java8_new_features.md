# Java 8 新特性简介

## 哪些新特性
Java8 引入许多新的特性，详细列表见 [What's New in JDK 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html)
和 [Release Notes for JDK 8 and JDK 8 Update Releases](http://www.oracle.com/technetwork/java/javase/8all-relnotes-2226344.html)。

以下为对我们日常编程影响较大的特征:

- FunctionalInterface
- Lambda Expressions
- Stream API of Collections
- Optional
- java.time package
- CompletableFuture

主要特征的关联关系:
- Functional programming -> FunctionalInterface, Stream
- Stream -> Default/static methods in interfaces
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
    public class Test{
    }
    ```
    - lambda expressions
    - method references
    - constructor references
- 典型用法
    - Runnable
    - Sort
    - Compare
    -


### 底层实现
- 不易Debug和异常处理


## Stream
### 概念
- Stream
- Stream 和 Collection

### 样式和和示例

### 并行处理
- 特点
- 限制

### 底层实现


## Utilities And Functions

### Java Time Package

### Optional

### CompletableFuture

### Try-with-resource





