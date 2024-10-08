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
