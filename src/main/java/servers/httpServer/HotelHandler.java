package servers.httpServer;
import com.google.gson.JsonObject;
import hotelapp.ThreadSafeHotelHandler;
import java.io.PrintWriter;

import static servers.httpServer.HttpRequest.*;

public class HotelHandler implements HttpHandler{

    ThreadSafeHotelHandler threadSafeHotelHandler;

    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        if(request.method.equals("GET")){
            getHotelInfo(request, writer);
            return;
        }
        send405Response("method", writer);
    }

    /**
     * This method handles the GET request for the hotel info
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    private void getHotelInfo(HttpRequest request, PrintWriter writer) {

        if (request.params.containsKey("hotelId")) {
            String hotelId = request.params.get("hotelId");
            JsonObject jsonResponse = threadSafeHotelHandler.getHotelInfoJson(hotelId);
            if (jsonResponse != null) {
                HttpRequest.sendSuccessJsonResponse(jsonResponse, writer);
                return;
            }
        }
            send404JsonResponse("hotelId", writer);
    }

    /**
     * This method handles the GET request for the hotel info
     * @param data Object that contains the Threadsafehoteldata data
     */
    @Override
    public void setAttribute(Object data) {
        this.threadSafeHotelHandler = (ThreadSafeHotelHandler) data;
    }
}
