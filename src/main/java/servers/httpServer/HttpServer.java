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

    private int port = 8090;
    private static boolean isServerActive = false;
    private String SHUTDOWN = "SHUTDOWN";
    private String EOT = "EOT";
    private Phaser phaser;
    private ExecutorService poolOfThreads;

    private Map<String, String> handlers; // maps each url path to the appropriate handler
    public HttpServer(int threads) {
        isServerActive = true;
        this.handlers = new HashMap<>();
        poolOfThreads = Executors.newFixedThreadPool(threads);
        phaser = new Phaser();

    }

    public void start() {
        try{
            ServerSocket server = new ServerSocket(port);
            while(isServerActive) {

                Socket socket = server.accept();
                phaser.register();
                poolOfThreads.submit(new ClientTask(socket));
            }

        } catch (Exception e) {
            System.out.println("Error in starting server");
            e.printStackTrace();
        }
    }



    public void addMapping(String key, String value){
        this.handlers.put(key, value);
    }


    private class ClientTask implements Runnable {
        private final Socket connectionSocket;

        private ClientTask(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }

        @Override
        public void run() {
            System.out.println("A client connected.");

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);

                String input;
                while (!connectionSocket.isClosed()) {
                    input = reader.readLine();
                    System.out.println("Server received: " + input); // echo the same string to the console

                    if (input.equals(EOT)) {
                        System.out.println("Server: Closing socket.");
                        connectionSocket.close();
                    } else if (input.equals(SHUTDOWN)) {
                        isServerActive = false;
                        System.out.println("Server: Shutting down.");
                        connectionSocket.close();
                    } else {
                        processIncomingRequest(input, out);
                        out.println("HTTP/1.1 200 OK");
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

                phaser.arriveAndDeregister();
            }

        }
    }

    private void processIncomingRequest(String input, PrintWriter out){
        String[] tokens = input.split(" ");
        String path = tokens[1];// incoming request will be of the form HTTp1.1 GET /path
        // path = /hotels

        String handler = handlers.get(path);
        if(handler == null){
            System.out.println("No handler found for path: " + path);
            out.println("HTTP/1.1 404 Not Found");
        }
        else{
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + handler.length());
            out.println("");
            out.println(handler);
        }

    }


}


