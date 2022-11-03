package hotelapp;

import java.io.*;
import java.util.*;

public class ReviewHandler {
    private  Map<String, TreeSet<Review>> hotelReviewMap = new HashMap<>();
    private  Map<String, TreeSet<ReviewWithFreq>> wordToReviews = new HashMap<>();

    public void insertReviews(ArrayList<Review> reviews){
        for (Review review : reviews) {
            if (!hotelReviewMap.containsKey(review.getHotelId())) {
                TreeSet<Review> temp = new TreeSet<>(new Comparator<Review>() {
                    @Override
                    public int compare(Review r1, Review r2) {
                        if(r1.getReviewSubmissionDate().equals(r2.getReviewSubmissionDate())){
                            return r1.getReviewId().compareTo(r2.getReviewId());
                        }
                        return r2.getReviewSubmissionDate().compareTo(r1.getReviewSubmissionDate());
                    }
                });
                this.hotelReviewMap.put(review.getHotelId(), temp);
            }
            this.hotelReviewMap.get(review.getHotelId()).add(review);
        }
    }
    public void setUpWords(){
        HashSet<String> stopWords = getStopWords();
        for (String hotelId: hotelReviewMap.keySet()){
            TreeSet<Review> hotelReviews = hotelReviewMap.get(hotelId);
            for(Review currReview: hotelReviews){
                for (String word: currReview.getReviewTextWords(true)) {
                    word = word.toLowerCase(); // storing all the keys in lowercase.
                    if (stopWords.contains(word)) {
                        continue;
                    }
                    if (!wordToReviews.containsKey(word)) {
                        TreeSet<ReviewWithFreq> emptyReviewsTree = new TreeSet<>(new Comparator<ReviewWithFreq>() {
                            public int compare(ReviewWithFreq r1, ReviewWithFreq r2) {
                                if (r1.getFrequency() == r2.getFrequency()) {
                                    if(r2.getReviewSubmissionDate().equals(r1.getReviewSubmissionDate())){
                                        return r1.getReviewId().compareTo(r2.getReviewId());
                                    }
                                    return r2.getReviewSubmissionDate().compareTo(r1.getReviewSubmissionDate());
                                }
                                return r2.getFrequency() - r1.getFrequency();
                            }
                        });
                        wordToReviews.put(word, emptyReviewsTree);
                    }
                    wordToReviews.get(word).add(new ReviewWithFreq(currReview, word));
                }
            }
        }
    }

    public TreeSet<Review> findReviewsByHotelId(String hotelId, Boolean printFormat){
        TreeSet<Review> reviews = new TreeSet<>();
        if(printFormat){
            if(hotelReviewMap.containsKey(hotelId)){
                return hotelReviewMap.get(hotelId);
            }
        }else{
            if(!hotelReviewMap.containsKey(hotelId)){
                System.out.println("No Reviews for hotelID - " + hotelId);
            }else{
                for(Review review: hotelReviewMap.get(hotelId)){
                    System.out.println(review);
                }
            }
        }
        return reviews;
    }

    public static HashSet<String> getStopWords(){
        HashSet<String> m = new HashSet<>();
         String WORD_FILE = "input/stop_words.txt";
        try {
            File file = new File(WORD_FILE);
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bf.readLine()) != null) {
                for (String word : line.trim().split("\\s")) {
                    if (!word.isEmpty())
                        m.add(word.toLowerCase());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return m;
    }

    public void findWords(String word){
        word = word.toLowerCase(); // convert to lowercase before findind in hashmap because keys are stored in lowercase
        if(!wordToReviews.containsKey(word)){
            System.out.println("No reviews found with word " + word);
            return;
        }
        for(ReviewWithFreq review: wordToReviews.get(word)){
            System.out.println(review);
        }
    }

    /**
     * This method responsible for displaying the reviews.
     * @param reviews of the hotel.
     * */
    public static void displayReviews(TreeSet<Review> reviews){
        for(Review review: reviews){
            System.out.println(review);
        }
    }

}