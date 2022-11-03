package hotelapp;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ReviewWithFreq implements Comparable<ReviewWithFreq> {

    private String word;
    private int frequency;
    private Review review;

    private Map<String, Integer> wordFrequency = new HashMap<String, Integer>();

    public ReviewWithFreq(Review review, String word){
        this.review = review;
        this.word = word;
        this.frequency = Helper.countWords(review.getReviewText(), word);
    }

    public int getFrequency() {
        return frequency;
    }

    public int getWordFrequency(String word){
        return this.wordFrequency.get(word);
    }

    public String getReviewText() {
        return review.getReviewText();
    }

    public String getWord() {
        return word;
    }

    public String getReviewId(){
        return this.review.getReviewId();
    }
    public LocalDate getReviewSubmissionDate(){
        return this.review.getReviewSubmissionDate();
    }
    public String toString() {
        String result = System.lineSeparator() + "--------------------" + System.lineSeparator();
        result += "Review by " + this.review.getUserNickname() + " on " + getReviewSubmissionDate() + System.lineSeparator();
        result += "Rating: " + this.review.getRatingOverall() + System.lineSeparator();
        result += "Hotel: " + this.review.getHotelId() + System.lineSeparator();
        result += "ReviewId: " + getReviewId() + System.lineSeparator();
        result += "Title: " + this.review.getTitle() + System.lineSeparator();
        result += "Review Text: " + getReviewText() + System.lineSeparator();
        result += "Word count of " + word + " - " + getFrequency() + System.lineSeparator();


        return result;
    }

    @Override
    public int compareTo(ReviewWithFreq o) {
        return getReviewText().compareTo(o.getReviewText());
    }
}
