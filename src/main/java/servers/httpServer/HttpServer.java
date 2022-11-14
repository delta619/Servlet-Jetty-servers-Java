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
    private ExecutorService poolOfThreads;

    private Map<String, Object> routeMap = new HashMap<>();
    private Map<String, Object> ObjectMap = new HashMap<>();

    public HttpServer(int threads) {
        isServerActive = true;
        poolOfThreads = Executors.newFixedThreadPool(threads);
    }
    public void start() {
        try{
            ServerSocket server = new ServerSocket(port);
            while(isServerActive) {
                System.out.println("Waiting for connection");
                Socket socket = server.accept();
                poolOfThreads.submit(new RequestWorker(socket));
            }

        } catch (Exception e) {
            System.out.println("Error in starting server");
            e.printStackTrace();
        }
    }

    /**
     * This method adds a route to the routeMap
     * @param route route to be added
     * @param controller handler to be added
     */
    public void addRouteMapping(String route, Object controller){
        this.routeMap.put(route, controller);
    }

    /**
     * This method adds an object to the objectMap
     * @param className handlername of the route
     * @param value object to be added
     */
    public void addObjectMapping(String className, Object value) {
        this.ObjectMap.put(className, value);
    }

    /**
     * This method gets the handler/controller from the routemap as controller
     * @param route handlername of the route
     *
     */
    public HttpHandler getController(String route) {
        try{
            Class<?> handlerClass = Class.forName(routeMap.get(route).toString());
            HttpHandler handler = (HttpHandler) handlerClass.newInstance();
            handler.setAttribute(this.ObjectMap.get(handlerClass.getName()));
            return  handler;

        }
        catch (Exception e){
            System.out.println("Error in getting controller");
            e.printStackTrace();
        }

    return null;
    }

    /**
     * This method get the netry request from the client
     *
     * @param input input stream url
     * @param out output stream
     */
    private void processRequestEntry(String input, PrintWriter out){
        HttpRequest request;
        try{
            request = new HttpRequest(input);
        } catch (Exception e){
            HttpRequest.send500Response("url", out);
            return;
        }
        HttpHandler handler = getController(request.getRoute());

        if(handler != null) {
            handler.processRequest(request, out);
        }
        else {
            if(request.method.equals("POST")) {
                HttpRequest.send405Response("method", out);
            }
            else {
                HttpRequest.send404JsonResponse("route", out);
            }
        }
        out.flush();
    }

    private class RequestWorker implements Runnable {
        private final Socket connectionSocket;

        private RequestWorker(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }

        /**
         * This method gets the request from the client and process it
         */
        @Override
        public void run() {

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);

                String input;
                while (!connectionSocket.isClosed()) {
                    input = reader.readLine();
                        if(input.startsWith("GET") || input.startsWith("POST")) {
                            processRequestEntry(input, out);
                            connectionSocket.close();
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


