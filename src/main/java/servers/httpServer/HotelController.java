package servers.httpServer;

import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.ThreadSafeHotelHandler;

import java.io.PrintWriter;
import java.util.HashMap;

import static servers.httpServer.HttpRequest.*;

public class HotelController implements HttpHandler{

    ThreadSafeHotelHandler threadSafeHotelHandler;

    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        if(request.method.equals("GET")){
            getHotelInfo(request, writer);
            return;
        }


        send405Response("method", writer);
    }

    private void getHotelInfo(HttpRequest request, PrintWriter writer){
        // hotelInfo?hotelId=491
        JsonObject jsonObject  = new JsonObject();


        if(request.params.containsKey("hotelId")) {
            String hotelId = request.params.get("hotelId");
            Hotel hotel = threadSafeHotelHandler.findHotelId(hotelId);
            if(hotel != null) {
                jsonObject.addProperty("hotelId", hotel.getId());
                jsonObject.addProperty("name", hotel.getName());
                jsonObject.addProperty("addr", hotel.getAddress());
                jsonObject.addProperty("city", hotel.getCity());
                jsonObject.addProperty("state", hotel.getState());
                jsonObject.addProperty("lat", hotel.getLatitude());
                jsonObject.addProperty("lon", hotel.getLongitude());
                sendSuccessJsonResponse(jsonObject, writer);
            } else {
                send404JsonResponse("hotelId", writer);
            }
        } else {
            send404JsonResponse("hotelId", writer);
        }
    }

    @Override
    public void setAttribute(Object data) {
        HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
        assert dataMap.get(ThreadSafeHotelHandler.class.getName()) instanceof ThreadSafeHotelHandler;
        threadSafeHotelHandler = (ThreadSafeHotelHandler) dataMap.get(ThreadSafeHotelHandler.class.getName());
    }
}
