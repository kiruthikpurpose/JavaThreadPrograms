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
