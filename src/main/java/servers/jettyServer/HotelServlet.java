package servers.jettyServer;

import hotelapp.Hotel;
import hotelapp.ThreadSafeHotelHandler;
import org.apache.commons.text.StringEscapeUtils;
import servers.httpServer.HttpRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.http.HttpResponse;

public class HotelServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try{
            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            PrintWriter out = response.getWriter();

            if(hotelId == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(Helper.hotelResponseGenerator(false, null));
                return;
            }
            ThreadSafeHotelHandler tsHotelHandler = (ThreadSafeHotelHandler) getServletContext().getAttribute("hotelController");
            if(tsHotelHandler.findHotelId(hotelId) == null){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(Helper.hotelResponseGenerator(false, null));
                return;
            }
            Hotel hotel = tsHotelHandler.findHotelId(hotelId);
            out.print(Helper.hotelResponseGenerator(true, hotel));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
