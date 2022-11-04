package servers.jettyServer;

import com.google.gson.Gson;
import hotelapp.Review;
import hotelapp.ThreadSafeHotelHandler;
import hotelapp.ThreadSafeReviewHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.http.HttpStatus;
import servers.httpServer.HttpRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.TreeSet;

public class ReviewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response){

        try {

            ThreadSafeReviewHandler tsReviewHandler = (ThreadSafeReviewHandler) getServletContext().getAttribute("reviewController");
            ThreadSafeHotelHandler tsHotelHandler = (ThreadSafeHotelHandler) getServletContext().getAttribute("hotelController");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();

            String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
            String num = StringEscapeUtils.escapeHtml4(request.getParameter("num"));

            if(hotelId == null || num == null){
                response.setStatus(HttpStatus.BAD_REQUEST_400);
                out.print(Helper.reviewResponseGenerator(false, null));
                return;
            }
            int requiredCount = Integer.parseInt(num);

            if(tsHotelHandler.findHotelId(hotelId) == null){
                response.setStatus(HttpStatus.NOT_FOUND_404);
                out.print(Helper.reviewResponseGenerator(false, null));
                return;
            }

            TreeSet<Review> allReviews = tsReviewHandler.findReviewsByHotelId(hotelId, true);

            requiredCount = Math.min(requiredCount, allReviews.size());
            ArrayList<Review> requiredReviews = new ArrayList<Review>();
            int count = 0;
            for(Review review : allReviews){
                if(count == requiredCount){
                    break;
                }
                requiredReviews.add(review);
                count++;
            }


            out.print(Helper.reviewResponseGenerator(true, requiredReviews));
            out.flush();

        } catch (Exception e){

            System.out.println("SOME Error occurred in get of reviews : "+ e.getMessage());
        }




    }
}
