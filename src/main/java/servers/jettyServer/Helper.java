package servers.jettyServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
public class Helper {

    public static Object reviewResponseGenerator (boolean success, JsonArray jsonArr){
        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("hotelId", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("hotelId", jsonArr.get(0).getAsJsonObject().get("hotelId").getAsString());

        JsonArray jsonArray = new JsonArray();
        jsonObject.add("reviews", jsonArr);
        return jsonObject;
    }

    public static Object hotelResponseGenerator (boolean success, JsonObject jsonResponse){
        if(!success){
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("hotelId", "invalid");
            return jsonObj;
        }
        jsonResponse.addProperty("success", true);
        return jsonResponse;
    }


    public static Object wordResponseGenerator (boolean success, String word, JsonArray jsonArr){

        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("word", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("word", word);

        jsonObject.add("reviews", jsonArr);
        return jsonObject;

    }

    public static Object weatherResponseGenerator (boolean success, JsonObject hotelObj, String temperature, String windspeed){
        JsonObject jsonObject = new JsonObject();
        if(!success){
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("hotelId", "invalid");
            return jsonObject;
        }
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("hotelId", hotelObj.get("hotelId").getAsString());
        jsonObject.addProperty("name", hotelObj.get("name").getAsString());
        jsonObject.addProperty("temperature", temperature);
        jsonObject.addProperty("windspeed", windspeed);

        return jsonObject;
    }

}
