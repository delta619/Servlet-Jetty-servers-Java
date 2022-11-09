package servers.httpServer;

import hotelapp.ThreadSafeHotelHandler;

import java.io.PrintWriter;

public class HotelHandler implements HttpHandler {

    private Hotel[] hotels;

    ThreadSafeHotelHandler hotelHandler;

    public HotelHandler(Hotel[] hotels) {
        this.hotels = hotels;
    }

    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {

         String hotelId = hotelName;

        ts.findHotelId(hotelId);

        writer.println(ts.findHotelId(hotelId););
    }


    @Override
    public void setAttribute(Object data) {
        this.hotelHandler = (ThreadSafeHotelHandler) data;

    }
}

