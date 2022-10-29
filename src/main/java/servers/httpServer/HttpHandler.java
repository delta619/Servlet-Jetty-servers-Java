package servers.httpServer;

import java.io.PrintWriter;

/** Contains a method to process an http request from the client.  */
public interface HttpHandler {

    /**
     * Handles an http get request from the client
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    void processRequest(HttpRequest request, PrintWriter writer);

    void setAttribute(Object data);
}
