
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/* There is single Bathroom to be used in a Voting agency for both Democrats(D) and Republicans(R) *
 This single Bathroom which can accomodate 3 people at most * each person takes f(N) secs to do his thing.
  f(N) is a function of the person's name and returns varying number * CONDITION: At any given time, 
  the bathroom cannot have a mixed set of people i.e. * CONDITION: Bathroom can have at most 3 people * 
  these combinations aren't allowed (2D, 1R) or (1D,1R) * These are allowed (), (3D), (2D), (1R) i.e. pure Republicans or Pure Democrats 
  * While the bathroom is occupied people are to wait in a queue * What is the most optimal system where you would manage people in this queue,
   so that * the most eligible person instants gets to use the bathroom whenever its has room, based on above conditions*/

public class Bathroom implements Runnable{
    static class Person {
        public Integer id;
        public String party;

        public Person(String party, Integer id) {
            this.id = id;
            this.party = party;
        }
    }

    ExecutorService bathroomService = Executors.newFixedThreadPool(3);
    static BlockingQueue<Person> rWaitlist = new LinkedBlockingQueue<>();
    static BlockingQueue<Person> dWaitlist = new LinkedBlockingQueue<>();
    AtomicInteger rCount = new AtomicInteger(0);
    AtomicInteger dCount = new AtomicInteger(0);

    private int f(int id) {
        return (int) (Math.random()*id) + 1;
    }

    private Runnable useWashroom(String party, int time) {
        return () -> {
            System.out.println("Party "+party+" in washroom for "+time+" seconds.");
            if ("R".equals(party)) {
                System.out.println("rCount = "+rCount.incrementAndGet());
            } else {
                System.out.println("dCount = "+dCount.incrementAndGet());
            }
            try {
            Thread.sleep(time*1000);
            } catch(Exception e){
                System.err.println("Exception while sleep()."+e);
            }
            System.out.println("Party "+party+" exiting washroom after "+time+" seconds.");
            if ("R".equals(party)) {
                System.out.println("rCount = "+rCount.decrementAndGet());
            } else {
                System.out.println("dCount = "+dCount.decrementAndGet());
            }
        };
    }

    @Override
    public void run() {
        while (true) { 
            if (dCount.get() == 0 && !rWaitlist.isEmpty()) {
                System.out.println("Checking Republicans queue.");
                while (rCount.get() < 3 && !rWaitlist.isEmpty()) {
                    Person p = rWaitlist.remove();
                    bathroomService.submit(useWashroom(p.party, f(p.id)));
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println("Exception while sleep()."+ex);
            }
            if (rCount.get() == 0 && !dWaitlist.isEmpty()) {
                System.out.println("Checking Democrats queue.");
                while (dCount.get() < 3 && !dWaitlist.isEmpty()) {
                    Person p = dWaitlist.remove();
                    bathroomService.submit(useWashroom(p.party, f(p.id)));
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println("Exception while sleep()."+ex);
            }
        }
    }

    private static void joinWaitList(Person p) {
        System.out.println("Person joining the waitlist.");
        if ("R".equals(p.party)) {
            rWaitlist.add(p);
        } else {
            dWaitlist.add(p);
        }
    }

    public static void main(String[] args) {
        Bathroom bathroom = new Bathroom();
        Thread t1 = new Thread(bathroom);

        // Add some people 
        joinWaitList(new Person("R", 10));
        joinWaitList(new Person("R", 11));
        joinWaitList(new Person("D", 20));
        joinWaitList(new Person("R", 15));
        joinWaitList(new Person("D", 10));

        t1.start();

        joinWaitList(new Person("R", 16));
        joinWaitList(new Person("D", 3));
    }

}
