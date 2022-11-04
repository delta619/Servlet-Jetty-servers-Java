package servers.jettyServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.Review;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeSet;

public class Helper {

    public static Object hotelResponseGenerator (boolean success, String hotelId){
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("success", success);
        jsonObject.addProperty("hotelId", hotelId);

        return new Gson().toJson(jsonObject);
    }

    public static Object reviewResponseGenerator (boolean success, ArrayList<Review> reviews){
        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("hotelId", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("hotelId", reviews.get(0).getHotelId());

        JsonElement reviewJsonTree = new Gson().toJsonTree(reviews);
        jsonObject.add("reviews", reviewJsonTree);
        return jsonObject;
    }

}
