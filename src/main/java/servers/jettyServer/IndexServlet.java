package servers.jettyServer;

import hotelapp.Review;
import hotelapp.ReviewWithFreq;
import hotelapp.ThreadSafeHotelHandler;
import hotelapp.ThreadSafeReviewHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class IndexServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        response.setContentType("application/json");

        try{
            ThreadSafeReviewHandler tsReviewHandler = (ThreadSafeReviewHandler) getServletContext().getAttribute("reviewController");
            PrintWriter out = response.getWriter();
            String word = "";
            int count = 0;

            try {

                word = request.getParameter("word");
                word = StringEscapeUtils.escapeHtml4(word);
                String num = request.getParameter("num");
                count = Integer.parseInt(num);

                if (word == null || num == null || word.equals("") || num.equals("")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println(Helper.wordResponseGenerator(false, null, null));
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println(Helper.wordResponseGenerator(false, null, null));
                return;
            }

            ArrayList<ReviewWithFreq> allReviews = tsReviewHandler.findWords(word);

            // get count number or max of reviews from allReviews
            int max = Math.min(count, allReviews.size());
            ArrayList<ReviewWithFreq> reviews = new ArrayList<>();
            for(int i = 0; i < max; i++){
                reviews.add(allReviews.get(i));
            }

            out.print(Helper.wordResponseGenerator(true, word, reviews));

        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
