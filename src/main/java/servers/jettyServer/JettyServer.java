package servers.jettyServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    // FILL IN CODE
    private static final int PORT = 8090;

    /**
     * Function that starts the server
     * @throws Exception throws exception if access failed
     */
    public  void start() throws Exception {
        Server server = new Server(PORT); // jetty server

        ServletHandler handler = new ServletHandler();

        // FILL IN CODE:
        // Map end points to servlets
        // Note: you should also create servlet classes in this package

        server.setHandler(handler);

        server.start();
        server.join();
    }

}
