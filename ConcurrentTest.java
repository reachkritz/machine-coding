
import java.util.concurrent.atomic.AtomicBoolean;



public class ConcurrentTest {

    static class Hydrogen extends Thread {
        Counter counter;
        public Hydrogen(Counter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            int i = 0;
            while (i<100) {  
                if (counter.getLock()) {
                    if (counter.decrementHydrogen()) {
                        System.out.println("H");
                        i++;
                    }
                    counter.releaseLock();
                }
            }
        }
    }
    
    static class Oxygen extends Thread {
        Counter counter;
        public Oxygen(Counter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            int i = 0;
            while (i<100) {
                if (counter.getLock()) {
                    if (counter.decrementOxygen()) {
                        System.out.println("O");
                        i++;
                    }
                    counter.releaseLock();
                }
            }
        }
    }

    static class Counter {
        AtomicBoolean isLocked;
        int hydrogen;
        int oxygen;

        public Counter(AtomicBoolean isLocked, int hydrogen, int oxygen) {
            this.isLocked = isLocked;
            this.hydrogen = hydrogen;
            this.oxygen = oxygen;
        }

        public boolean getLock() {
            return isLocked.compareAndSet(false, true);
        }

        public void releaseLock() {
            isLocked.compareAndSet(true, false);
        }

        public boolean decrementHydrogen() {
            if (this.hydrogen > 0) {
                this.hydrogen--;
                if (this.hydrogen == 0 && this.oxygen == 0) {
                    this.hydrogen = 2;
                    this.oxygen = 1;
                }
                return true;
            }
            return false;
        }

        public boolean decrementOxygen() {
            if (this.oxygen > 0) {
                this.oxygen--;
                if (this.hydrogen == 0 && this.oxygen == 0) {
                    this.hydrogen = 2;
                    this.oxygen = 1;
                }
                return true;
            }
            return false;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Counter c = new Counter(new AtomicBoolean(false), 2, 1);

        Hydrogen thread = new Hydrogen(c);
        thread.start();
        Oxygen thread2 = new Oxygen(c);
        thread2.start();

        thread.join();
        thread2.join();
    }
}