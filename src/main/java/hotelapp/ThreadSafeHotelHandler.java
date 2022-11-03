package hotelapp;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This Class manages the methods of its parent class HotelHandler in a thread safe way.
 * */
public class ThreadSafeHotelHandler extends HotelHandler{


    private ReentrantReadWriteLock lock;
    public ThreadSafeHotelHandler() {
        super();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public Hotel findHotelId(String hotelId){
        try{
            this.lock.readLock().lock();
            return super.findHotelId(hotelId);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void insertHotels(Hotel[] hotels){
        try {
            this.lock.writeLock().lock();
            super.insertHotels(hotels);
        } finally {
            this.lock.writeLock().unlock();
        }
    }




}
