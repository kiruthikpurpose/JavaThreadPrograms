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
