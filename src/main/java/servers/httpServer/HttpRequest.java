package servers.httpServer;

import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/** A class that represents an http request */
public class HttpRequest {

    Map<String, String> params = new HashMap<>();
    String url;
    String method;
    String route;

    HttpRequest(String request) {
        String[] token = request.split(" ");
        method = token[0].trim();
        url = token[1].trim();
        this.setRoute();
        this.getParams();
        Logger.getLogger("HttpRequest").info("Request: " + request);
    }

    /**
     * This method sets the route of the request by parsing the url
     */
    public void setRoute(){
        if(url.contains("?")){
            this.route = url.substring(0, url.indexOf("?"));
        }else{
            this.route = url;
        }

    }
    /**
     * This method gets the route for the handler
     */
    public String getRoute(){
        return route;
    }

    /**
     * This method parses the url and gets the parameters
     */
    private void getParams() {
        String[] urlParts = url.split("\\?");
        if(urlParts.length > 1) {
            String[] paramsParts = urlParts[1].split("&");
            for(String param : paramsParts) {
                String[] keyValue = param.split("=");
                if(keyValue.length > 1) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }


    /**
     * This method sends a 200 OK response with the json object
     * @param json JsonObject to be sent
     * @param writer PrintWriter of the response
     */
    public static void sendSuccessJsonResponse(JsonObject json, PrintWriter writer) {
        json.addProperty("success", true);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: application/json");
        writer.println();
        writer.println(json);
        writer.flush();
        Logger.getLogger("HttpRequest").info("Response: " + json);
    }


    /**
     * This method sends a 404 response with a json body
     * @param key the key that was not found
     * @param writer PrintWriter of the response
     */
    public static void send404JsonResponse(String key, PrintWriter writer) {

        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty(key, "invalid");

        writer.println("HTTP/1.1 404 Not Found");
        writer.println("Content-Type: application/json");
        writer.println("Content-Length: " + json.toString().length());
        writer.println();
        writer.println(json);
        writer.flush();
        Logger.getLogger("HttpRequest").warning("Response: " + json);

    }

    /**
     * This method sends a 405 response to the client
     * @param key key of the invalid parameter
     * @param writer PrintWriter of the response
     */
    public static void send405Response(String key, PrintWriter writer) {

        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty(key, "invalid");

        writer.println("HTTP/1.1 405 Bad Request");
        writer.println("Content-Type: application/json");
        writer.println();
        writer.println(json);
        writer.flush();
        Logger.getLogger("HttpRequest").warning("Response: " + json);
    }

    /**
     * This method handles the GET request for the hotel info
     * @param key the error key
     * @param writer PrintWriter of the response
     */
    public static void send500Response(String key, PrintWriter writer) {

        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty(key, "invalid");

        writer.println("HTTP/1.1 405 Bad Request");
        writer.println("Content-Type: text/plain");
        writer.println();
        writer.println(json);
        writer.flush();
        Logger.getLogger("HttpRequest").warning("Response: " + json);
    }

    @Override
    public String toString() {
        return url;
    }
}
