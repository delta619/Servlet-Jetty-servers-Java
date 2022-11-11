package servers.httpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/** Implements an http server using raw sockets */
public class HttpServer {

    private int port = 8080;
    private static boolean isServerActive = false;
    private String SHUTDOWN = "SHUTDOWN";
    private String EOT = "EOT";
    private Phaser phaser;
    private ExecutorService poolOfThreads;

    private  Map<String, Object> attributesMap = new HashMap<>();
    private  HttpHandler hotelController = new HotelController();
    private  HttpHandler reviewController = new ReviewController();
    private  HttpHandler indexController = new IndexController();
    private HttpHandler weatherController = new WeatherController();

    public HttpServer(int threads) {
        isServerActive = true;
        poolOfThreads = Executors.newFixedThreadPool(threads);
        phaser = new Phaser();
    }

    public void addMapping(String key, Object value){
        this.attributesMap.put(key, value);
    }

    public void setAttributeControllers() {
        hotelController.setAttribute(attributesMap);
        reviewController.setAttribute(attributesMap);
        indexController.setAttribute(attributesMap);
        weatherController.setAttribute(attributesMap);
    }

    private void processIncomingRequest(String input, PrintWriter out){

        HttpRequest request = new HttpRequest(input);

        if(request.url.startsWith("/hotelInfo")) {
            hotelController.processRequest(request, out);
        } else if(request.url.startsWith("/review")) {
            reviewController.processRequest(request, out);
        } else if (request.url.startsWith("/index")) {
            indexController.processRequest(request, out);
        } else if (request.url.startsWith("/weather")) {
            weatherController.processRequest(request, out);
        }
        else {
            if(request.method.equals("GET")){
                HttpRequest.send404JsonResponse("url", out);
                return;
            }else{
                HttpRequest.send405Response("method", out);
            }
        }
        out.flush();


    }

    public void start() {
        try{
            ServerSocket server = new ServerSocket(port);
            while(isServerActive) {
                System.out.println("Waiting for connection");
                Socket socket = server.accept();
                phaser.register();
                poolOfThreads.submit(new ClientTask(socket));
            }

        } catch (Exception e) {
            System.out.println("Error in starting server");
            e.printStackTrace();
        }
    }
    private class ClientTask implements Runnable {
        private final Socket connectionSocket;

        private ClientTask(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }

        @Override
        public void run() {

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);

                String input;
                while (!connectionSocket.isClosed()) {
                    input = reader.readLine();
                        if(input.startsWith("GET") || input.startsWith("POST")) {
                            System.out.println("Server: Received request: " + input);
                            processIncomingRequest(input, out);
                            connectionSocket.close();
                            phaser.arriveAndDeregister();
                        }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    if (connectionSocket != null)
                        connectionSocket.close();
                } catch (IOException e) {
                    System.out.println("Can't close the socket : " + e);
                }

            }
        }
    }


}


