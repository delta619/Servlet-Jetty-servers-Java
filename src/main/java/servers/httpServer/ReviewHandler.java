package servers.httpServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Review;
import hotelapp.ThreadSafeHotelHandler;
import hotelapp.ThreadSafeReviewHandler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import static servers.httpServer.HttpRequest.send405Response;

public class ReviewHandler implements HttpHandler{
    ThreadSafeReviewHandler threadSafeReviewHandler;


    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {

        if(request.method.equals("GET")){
            getHotelReviews(request, writer);
            return;
        }

        send405Response("method", writer);

    }
    /**
     * This method handles the GET request for the hotel reviews
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    private void getHotelReviews(HttpRequest request, PrintWriter writer) {
        try {
            JsonObject jsonObject = new JsonObject();
            if (request.params.get("hotelId") == null || request.params.get("num") == null) {
                jsonObject.addProperty("hotelId", "invalid");
                HttpRequest.send404JsonResponse("hotelId", writer);
                return;
            }
            String hotelId = request.params.get("hotelId");
            int num = Integer.parseInt(request.params.get("num"));
            jsonObject.addProperty("hotelId", hotelId);
            JsonArray jsonArr = threadSafeReviewHandler.findReviewsByHotelIdJson(hotelId, num);
            if(jsonArr.size() == 0){
                HttpRequest.send404JsonResponse("hotelId", writer);
            }
            jsonObject.add("reviews", jsonArr);

            HttpRequest.sendSuccessJsonResponse(jsonObject, writer);

        } catch (Exception e) {
            e.printStackTrace();
            HttpRequest.send405Response("hotelId", writer);
        }

    }
    @Override
    public void setAttribute(Object data) {
        this.threadSafeReviewHandler = (ThreadSafeReviewHandler) data;

    }
}
