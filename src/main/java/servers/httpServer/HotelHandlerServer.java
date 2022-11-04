package servers.httpServer;

import hotelapp.Hotel;
import hotelapp.ThreadSafeHotelHandler;

public class HotelHandlerServer {
    HotelHandlerServer(ThreadSafeHotelHandler handler, String extra){
        Hotel h = handler.findHotelId("12539");

        System.out.println(h.toString());
    }
}
