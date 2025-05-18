import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
/*  STORAGE

Shows - showId, time, theatreId, movie, ratings, ...
Theatres - theatreId, address, totalSeats

Bookings - bookingId, showId, userId, time, ...
< ShowSeats - theatreId, showId, seatId, bookingId >

Users - userId, name, ...

UserService
 - login
 - logout
 - register

ShowService
 - listsShows
 - addShow
 - listTheatresForShow

BookingService
 - showOccupancy(theatreId, showId)
 - makeBooking(theatreId, showId, List<seatIds>)
 - cancelBooking(bookingId)
 - viewBooking(userId)

*/

public class BookMyShow {
    static class Booking {
        public int bookingId;
        public int userId;
    
        public Booking(int bookingId, int userId) {
            this.bookingId = bookingId;
            this.userId = userId;
        }
    }
    static class Occupancy {
        public Semaphore flag = new Semaphore(1);
        public int bookingId;
    }

    static List<Booking> bookings = new ArrayList<>();
    static Map<Integer, Occupancy> seatMap = new HashMap<>(); 

    private static int makeBooking(Integer userId, List<Integer> seats) {
        int bookingId = UUID.randomUUID().version();
        int wait = (int) (Math.random()*5);
        System.out.println("User "+userId+" Trying to make a booking of seats = "+seats+" after sleep of "+wait+" seconds.");
        try {
            Thread.sleep(wait*1000);
        } catch (InterruptedException ex) {
        }
        List<Integer> acquiredSeats = new ArrayList<>();
        for (Integer seat : seats.stream().sorted().toList()) {
            if (seatMap.get(seat).flag.tryAcquire()) {
                acquiredSeats.add(seat);
            }
        }
        if (acquiredSeats.size() == seats.size()) {
            for (Integer seat : acquiredSeats) {
                seatMap.get(seat).bookingId = bookingId;
            }
            bookings.add(new Booking(bookingId, userId));
            System.out.println("Booking successful for user "+userId);
            return bookingId;
        } else {
            for (Integer seat : acquiredSeats) {
                seatMap.get(seat).flag.release();
            }
            System.out.println("Booking Failed for user "+userId);
            return -1;
        }
    }

    public static void main(String[] args) {
        for (int i=0; i<100; i++){
            seatMap.put(i, new Occupancy());
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        Future<Integer> result1 = threadPool.submit(() -> makeBooking(1, Arrays.asList(1,2,3)));
        Future<Integer> result2 = threadPool.submit(() -> makeBooking(2, Arrays.asList(2,3,4)));
        Future<Integer> result3 = threadPool.submit(() -> makeBooking(3, Arrays.asList(1,10)));
        Future<Integer> result4 = threadPool.submit(() -> makeBooking(4, Arrays.asList(3,4,5)));
        Future<Integer> result5 = threadPool.submit(() -> makeBooking(5, Arrays.asList(5)));

        try {
            result1.get();
            result2.get();
            result3.get();
            result4.get();
            result5.get();
        } catch (InterruptedException | ExecutionException ex) {
        }
    }
}
