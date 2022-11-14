package servers.httpServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.Hotel;
import hotelapp.ThreadSafeHotelHandler;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

import static servers.httpServer.HttpRequest.send405Response;

public class WeatherHandler implements HttpHandler {
    ThreadSafeHotelHandler threadSafeHotelHandler;


    public String[] getWeatherInfo(String latitude, String longitude) {
        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;

        try {
            String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&" + "longitude=" + longitude + "&current_weather=true";
            URL url = new URL(urlString);

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // HTTPS uses port 443
            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);

            // output stream for the secure socket
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());

            out.println(request); // send a request to the server
            out.flush();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // use input stream to read server's response
            String line;
            StringBuffer sb = new StringBuffer();
            String body = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);

                // curly braces are used to indicate the beginning and end of a JSON object
                if (checkBrack(line)) {
                    body += line;
                }
            }
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(body);
            String temperature = jo.get("current_weather").getAsJsonObject().get("temperature").getAsString();
            String windspeed = jo.get("current_weather").getAsJsonObject().get("windspeed").getAsString();

            return new String[]{temperature, windspeed};

        } catch (Exception e){
            System.out.println("Exception lat long");
            e.printStackTrace();
        }
        finally {
            try {
                // close the streams and the socket
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }

        }

        return new String[]{"NA", "NA"};
    }
    public boolean checkBrack(String s){
        // if string contains curly braces, return true

        if(s.contains("{") || s.contains("}")){
            return true;
        }
        return false;
    };

    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;
    }

    private void getHotelInfo(HttpRequest request, PrintWriter writer) {
        try{
            String hotelId = request.params.get("hotelId");
            if(hotelId == null){
                HttpRequest.send404JsonResponse("hotelId", writer);
                return;
            }
            JsonObject hotelJsonObj = threadSafeHotelHandler.getHotelInfoJson(hotelId);
            if(hotelJsonObj == null){
                HttpRequest.send404JsonResponse("hotelId", writer);
                return;
            }
            String lat = hotelJsonObj.get("lat").getAsString();
            String lon = hotelJsonObj.get("lon").getAsString();

            String[] weatherInfo = getWeatherInfo(lat, lon);
            String temperature = weatherInfo[0];
            String windspeed = weatherInfo[1];

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("hotelId", hotelJsonObj.get("hotelId").getAsString());
            jsonObject.addProperty("name", hotelJsonObj.get("name").getAsString());
            jsonObject.addProperty("temperature", temperature);
            jsonObject.addProperty("windspeed", windspeed);

            HttpRequest.sendSuccessJsonResponse(jsonObject, writer);


        } catch (Exception e){
            e.printStackTrace();
            HttpRequest.send405Response(null, writer);
        }
    }
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {

        if(request.method.equals("GET")){
            getHotelInfo(request, writer);
            return;

        }

        send405Response("method", writer);
    }

    @Override
    public void setAttribute(Object data) {
        this.threadSafeHotelHandler = (ThreadSafeHotelHandler) data;
    }
}
