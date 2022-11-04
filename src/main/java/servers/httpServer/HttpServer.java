package servers.httpServer;

import java.util.Map;

/** Implements an http server using raw sockets */
public class HttpServer {
    private Map<String, String> handlers; // maps each url path to the appropriate handler



    public void addMapping(String key, String value){
        this.handlers.put(key, value);
    }

}
