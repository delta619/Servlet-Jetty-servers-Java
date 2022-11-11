package servers.httpServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.ReviewWithFreq;
import hotelapp.ThreadSafeHotelHandler;
import hotelapp.ThreadSafeReviewHandler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static servers.httpServer.HttpRequest.send405Response;

public class IndexController implements HttpHandler {

    ThreadSafeHotelHandler threadSafeHotelHandler;
    ThreadSafeReviewHandler threadSafeReviewHandler;

    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        if(request.method.equals("GET")){
            getWordIndexReviews(request, writer);
            return;

        }

        send405Response("method", writer);

    }

    private void getWordIndexReviews(HttpRequest request, PrintWriter writer) {
        // index?word=waffles&num=5
        try {
            JsonObject jsonObject = new JsonObject();
            if (request.params.get("word") == null || request.params.get("num") == null) {
                HttpRequest.send404JsonResponse("word", writer);
                return;
            }
            String word = request.params.get("word");
            int num = Integer.parseInt(request.params.get("num"));

            ArrayList<ReviewWithFreq> wordReviewList = threadSafeReviewHandler.findWords(word);
            if (wordReviewList == null) {
                HttpRequest.send404JsonResponse("word", writer);
                return;
            }
            num = Math.min(num, wordReviewList.size());
            ArrayList<ReviewWithFreq> requiredReviews = new ArrayList<>();
            int count = 0;
            for (ReviewWithFreq review : wordReviewList) {
                if (count == num) {
                    break;
                }
                requiredReviews.add(review);
                count++;
            }


            jsonObject.addProperty("word", word);

            JsonArray jsonArray = new JsonArray();
            for(ReviewWithFreq review : requiredReviews){
                JsonObject reviewObject = new JsonObject();
                reviewObject.addProperty("reviewId", review.getReviewId());
                reviewObject.addProperty("title", review.getTitle());
                reviewObject.addProperty("user", review.getNickname());
                reviewObject.addProperty("reviewText", review.getReviewText());
                reviewObject.addProperty("date", review.getReviewSubmissionDate().toString());

                jsonArray.add(reviewObject);
            }
            jsonObject.add("reviews", jsonArray);
            HttpRequest.sendSuccessJsonResponse(jsonObject, writer);

        } catch (Exception e) {
            e.printStackTrace();
            HttpRequest.send405Response(null, writer);
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
