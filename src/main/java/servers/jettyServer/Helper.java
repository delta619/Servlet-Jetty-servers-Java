package servers.jettyServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.Review;
import hotelapp.ReviewWithFreq;

import java.util.ArrayList;
public class Helper {

    public static Object reviewResponseGenerator (boolean success, ArrayList<Review> reviews){
        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("hotelId", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("hotelId", reviews.get(0).getHotelId());

        JsonArray jsonArray = new JsonArray();
        for(Review review : reviews){
            JsonObject reviewObject = new JsonObject();
            reviewObject.addProperty("reviewId", review.getReviewId());
            reviewObject.addProperty("title", review.getTitle());
            reviewObject.addProperty("user", review.getUserNickname());
            reviewObject.addProperty("reviewText", review.getReviewText());
            reviewObject.addProperty("date", review.getReviewSubmissionDate().toString());
            jsonArray.add(reviewObject);
        }
        jsonObject.add("reviews", jsonArray);
        return jsonObject;
    }

    public static Object hotelResponseGenerator (boolean success, Hotel hotel){
        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("hotelId", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("hotelId", hotel.getId());
        jsonObject.addProperty("name", hotel.getName());
        jsonObject.addProperty("addr", hotel.getAddress());
        jsonObject.addProperty("city", hotel.getCity());
        jsonObject.addProperty("state", hotel.getState());
        jsonObject.addProperty("lat", hotel.getLatitude());
        jsonObject.addProperty("lon", hotel.getLongitude());


        return jsonObject;
    }


    public static Object wordResponseGenerator (boolean success, String word, ArrayList<ReviewWithFreq> reviews){

        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("word", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("word", word);

        JsonArray jsonArray = new JsonArray();
        for(ReviewWithFreq review : reviews){
            JsonObject reviewObject = new JsonObject();
            reviewObject.addProperty("reviewId", review.getReviewId());
            reviewObject.addProperty("title", review.getTitle());
            reviewObject.addProperty("user", review.getNickname());
            reviewObject.addProperty("reviewText", review.getReviewText());
            reviewObject.addProperty("date", review.getReviewSubmissionDate().toString());

            jsonArray.add(reviewObject);
        }
        jsonObject.add("reviews", jsonArray);
        return jsonObject;

    }
}
