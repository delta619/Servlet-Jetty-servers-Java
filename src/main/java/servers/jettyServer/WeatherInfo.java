package servers.jettyServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.Hotel;
import hotelapp.ThreadSafeHotelHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

public class WeatherInfo extends HttpServlet {
    private static final String host = "https://api.open-meteo.com/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // set json response
        response.setContentType("application/json");
        ThreadSafeHotelHandler tsHotelHandler = (ThreadSafeHotelHandler) getServletContext().getAttribute("hotelController");

         // get latitude and longitude from request
        try{
            String hotelId = request.getParameter("hotelId");

            PrintWriter out = response.getWriter();

            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            Hotel hotel = tsHotelHandler.findHotelId(hotelId);
            if(hotel == null){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(Helper.hotelResponseGenerator(false, null));
                return;
            }

            String[] weatherInfo = getWeatherInfo(hotel.getLatitude(), hotel.getLongitude());
            out.println(Helper.weatherResponseGenerator(true, tsHotelHandler.getHotelInfoJson(hotelId), weatherInfo[0], weatherInfo[1]));

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    /**
     * This method gets the weather info from the open-meteo api
     * @param latitude latitude of the hotel
     * @param longitude longitude of the hotel
     * @return String array of the weather info
     */
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

       return new String[]{"NA", "NA"}; // 0 -> temperature 1 -> windspeed
    }
    /**
     * This method checks if the line contains a curly brace
     * @param s line to check
     * @return true if the line contains a curly brace
     */
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


}
