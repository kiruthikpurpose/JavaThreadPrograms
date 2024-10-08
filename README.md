## 1. Thread Creation

### Program Overview
This program demonstrates thread creation by extending the `Thread` class. A thread is created that prints numbers from 1 to 5, with a delay of 500 milliseconds between each number.

### Code
```java
class NumberPrinter extends Thread {
    public void run() {
        System.out.println("Thread running...");
        for (int i = 1; i <= 5; i++) {
            System.out.println(i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class ThreadCreation {
    public static void main(String[] args) {
        NumberPrinter thread = new NumberPrinter();
        thread.start();
    }
}
```

### Explanation
- **Thread Creation**: We create a new thread by extending the `Thread` class.
- **run() Method**: This method contains the code that the thread will execute. It prints numbers from 1 to 5.
- **Delay**: The `Thread.sleep(500)` method introduces a 500 milliseconds delay between prints.

### Sample Output
```
Thread running...
1
2
3
4
5
```

---

## 2. Runnable Interface

### Program Overview
This program demonstrates thread creation by implementing the `Runnable` interface. Two threads print numbers alternatively from 1 to 10, ensuring synchronization.

### Code
```java
class NumberPrinter implements Runnable {
    private static int count = 1;
    private final int id;
    private static final Object lock = new Object();
    private static final int MAX_COUNT = 10;

    public NumberPrinter(int id) {
        this.id = id;
    }

    public void run() {
        while (count <= MAX_COUNT) {
            synchronized (lock) {
                System.out.println("Thread " + (id == 1 ? "A" : "B") + ": " + count);
                count++;
                lock.notifyAll();
                if (count <= MAX_COUNT) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}

public class RunnableInterface {
    public static void main(String[] args) {
        Thread threadA = new Thread(new NumberPrinter(1));
        Thread threadB = new Thread(new NumberPrinter(2));
        threadA.start();
        threadB.start();
    }
}
```

### Explanation
- **Runnable Interface**: This approach allows us to implement the `Runnable` interface, which is a common way to create threads.
- **Synchronization**: We use the `synchronized` keyword to control access to shared resources (`count` in this case) and ensure that threads print in an alternating fashion.

### Sample Output
```
Thread A: 1
Thread B: 2
Thread A: 3
Thread B: 4
...
```

---

## 3. Thread Joining

### Program Overview
This program demonstrates thread joining by spawning two threads: Thread A (prints numbers) and Thread B (prints letters). Thread B starts only after Thread A finishes.

### Code
```java
class NumberPrinter extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Thread A: " + i);
        }
    }
}

class LetterPrinter extends Thread {
    public void run() {
        for (char ch = 'A'; ch <= 'E'; ch++) {
            System.out.println("Thread B: " + ch);
        }
    }
}

public class ThreadJoining {
    public static void main(String[] args) {
        NumberPrinter threadA = new NumberPrinter();
        LetterPrinter threadB = new LetterPrinter();
        threadA.start();
        try {
            threadA.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        threadB.start();
    }
}
```

### Explanation
- **Thread Joining**: The `join()` method is used to make sure that Thread B only starts after Thread A has completed its execution.
- **Order of Execution**: This guarantees that numbers are printed before letters.

### Sample Output
```
Thread A: 1
Thread A: 2
...
Thread B: A
Thread B: B
...
```

---

## 4. Producer-Consumer Problem

### Program Overview
This program simulates the Producer-Consumer problem using a bounded buffer of fixed size. A producer generates numbers, while a consumer reads them, with synchronization to prevent race conditions.

### Code
```java
import java.util.LinkedList;

class BoundedBuffer {
    private final LinkedList<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;

    public synchronized void produce(int value) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait();
        }
        buffer.add(value);
        System.out.println("Producer produced: " + value);
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        int value = buffer.removeFirst();
        System.out.println("Consumer consumed: " + value);
        notifyAll();
        return value;
    }
}

class Producer extends Thread {
    private final BoundedBuffer buffer;

    public Producer(BoundedBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                buffer.produce(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Consumer extends Thread {
    private final BoundedBuffer buffer;

    public Consumer(BoundedBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                buffer.consume();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class ProducerConsumer {
    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer();
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);
        producer.start();
        consumer.start();
    }
}
```

### Explanation
- **Bounded Buffer**: We use a linked list to simulate the buffer, with synchronized methods to produce and consume items.
- **Synchronization**: The `wait()` and `notifyAll()` methods prevent race conditions and ensure that the producer and consumer operate correctly.

### Sample Output
```
Producer produced: 1
Consumer consumed: 1
Producer produced: 2
...
```

---

## 5. Thread Pool

### Program Overview
This program implements a custom thread pool with a fixed number of worker threads, a task queue, and mechanisms to start and stop the pool.

### Code
```java
import java.util.concurrent.*;

class ThreadPool {
    private final WorkerThread[] workers;
    private final BlockingQueue<Runnable> taskQueue;
    private boolean isStopped = false;

    public ThreadPool(int numThreads) {
        workers = new WorkerThread[numThreads];
        taskQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    public synchronized void submit(Runnable task) throws IllegalStateException {
        if (isStopped) throw new IllegalStateException("ThreadPool is stopped");
        taskQueue.offer(task);
    }

    public synchronized void shutdown() {
        isStopped = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Runnable task;
                    task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    if (isStopped) break;
                }
            }
        }
    }
}

public class CustomThreadPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(3);
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            threadPool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + ": " + taskId);
            });
        }
        threadPool.shutdown();
    }
}
```

### Explanation
- **Thread Pool**: The `ThreadPool` class manages worker threads and tasks submitted for execution.
- **BlockingQueue**: This is used to store tasks waiting to be executed. The worker threads continuously take tasks from this queue and execute them.
- **Shutdown Mechanism**: The thread pool can be shut down, ensuring all tasks in the queue are executed before termination.

### Sample Output
```
Thread-0: 1
Thread-1: 1
...
```
