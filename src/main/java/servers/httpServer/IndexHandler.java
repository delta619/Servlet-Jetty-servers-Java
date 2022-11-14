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

public class IndexHandler implements HttpHandler {

    ThreadSafeReviewHandler threadSafeReviewHandler;

    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        if(request.method.equals("GET")){
            getWordIndexReviews(request, writer);
            return;
        }
        send405Response("method", writer);
    }

    /**
     * This method handles the GET request for the word reviews
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
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

            JsonArray jsonArr = threadSafeReviewHandler.findWordsJson(word, num);
            if (jsonArr.size() == 0) {
                HttpRequest.send404JsonResponse("word", writer);
                return;
            }
            jsonObject.addProperty("word", word);
            jsonObject.add("reviews", jsonArr);

            HttpRequest.sendSuccessJsonResponse(jsonObject, writer);

        } catch (Exception e) {
            e.printStackTrace();
            HttpRequest.send405Response(null, writer);
        }

    }

    @Override
    public void setAttribute(Object data) {
        this.threadSafeReviewHandler = (ThreadSafeReviewHandler) data;

    }

}
