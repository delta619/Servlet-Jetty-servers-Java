package servers.jettyServer;

import hotelapp.ThreadSafeHotelHandler;
import hotelapp.ThreadSafeReviewHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    // FILL IN CODE
    private static final int PORT = 8090;

    /**
     * Function that starts the server
     * @throws Exception throws exception if access failed
     */

    ThreadSafeReviewHandler tsReviewHandler;
    ThreadSafeHotelHandler tsHotelHandler;
    public JettyServer(ThreadSafeHotelHandler tsHotelHandler, ThreadSafeReviewHandler tsReviewHandler){

        this.tsReviewHandler = tsReviewHandler;
        this.tsHotelHandler = tsHotelHandler;


    }
    public  void start() throws Exception {
        Server server = new Server(PORT); // jetty server

        ServletContextHandler handler = new ServletContextHandler();


        // FILL IN CODE:
        // Map end points to servlets
        handler.addServlet(ReviewServlet.class,"/reviews" );
        handler.addServlet(HotelServlet.class,"/hotelInfo" );

        // Note: you should also create servlet classes in this package
        handler.setAttribute("reviewController", tsReviewHandler );
        handler.setAttribute("hotelController", tsHotelHandler );

        server.setHandler(handler);

        server.start();
        server.join();
    }

}
