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

public class ReviewController implements HttpHandler{
    ThreadSafeHotelHandler threadSafeHotelHandler;
    ThreadSafeReviewHandler threadSafeReviewHandler;


    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {

        if(request.method.equals("GET")){
            getHotelReviews(request, writer);
            return;
        }

        send405Response("method", writer);

    }

    private void getHotelReviews(HttpRequest request, PrintWriter writer) {
        // reviews?hotelId=491&count=5
        try {
            JsonObject jsonObject = new JsonObject();
            if (request.params.get("hotelId") == null || request.params.get("num") == null) {
                jsonObject.addProperty("hotelId", "invalid");
                HttpRequest.send404JsonResponse("hotelId", writer);
                return;
            }
            String hotelId = request.params.get("hotelId");
            int num = Integer.parseInt(request.params.get("num"));

            if (threadSafeHotelHandler.findHotelId(hotelId) == null) {
                HttpRequest.send404JsonResponse("hotelId", writer);
                return;
            }
            jsonObject.addProperty("hotelId", hotelId);
            ArrayList<Review> requiredReviews = new ArrayList<>();

            TreeSet<Review> allReviews = threadSafeReviewHandler.findReviewsByHotelId(hotelId, true);

            int count = 0;
            for (Review review : allReviews) {
                if (count == num) {
                    break;
                }
                requiredReviews.add(review);
                count++;
            }




            JsonArray jsonArray = new JsonArray();
            for(Review review : requiredReviews){
                JsonObject reviewObject = new JsonObject();
                reviewObject.addProperty("reviewId", review.getReviewId());
                reviewObject.addProperty("title", review.getTitle());
                reviewObject.addProperty("user", review.getUserNickname());
                reviewObject.addProperty("reviewText", review.getReviewText());
                reviewObject.addProperty("date", review.getReviewSubmissionDate().toString());
                jsonArray.add(reviewObject);
            }
            jsonObject.add("reviews", jsonArray);

            HttpRequest.sendSuccessJsonResponse(jsonObject, writer);



        } catch (Exception e) {
            e.printStackTrace();
            HttpRequest.send405Response("hotelId", writer);
        }

    }
    @Override
    public void setAttribute(Object data) {
        HashMap<String, Object> dataMap = (HashMap<String, Object>) data;

        assert dataMap.get(ThreadSafeHotelHandler.class.getName()) instanceof ThreadSafeHotelHandler;
        threadSafeHotelHandler = (ThreadSafeHotelHandler) dataMap.get(ThreadSafeHotelHandler.class.getName());

        assert dataMap.get(ThreadSafeReviewHandler.class.getName()) instanceof ThreadSafeReviewHandler;
        threadSafeReviewHandler = (ThreadSafeReviewHandler) dataMap.get(ThreadSafeReviewHandler.class.getName());
    }
}
