package hotelapp;

import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/** This class contains methods to parse and manage threads */
public class FileProcessor {
    private ThreadSafeReviewHandler threadSafeReviewHandler;
    private ExecutorService poolOfThreads;
    private Phaser phaser;
    FileProcessor(ThreadSafeReviewHandler threadSafeReviewHandler, int numberOfThreads){
        this.threadSafeReviewHandler = threadSafeReviewHandler;
        poolOfThreads = Executors.newFixedThreadPool(numberOfThreads);
        phaser = new Phaser();
    }

    /** This class as about the operations in a single thread  */
    class Worker implements Runnable{
        Path path;
        public Worker(Path path){
            this.path = path;
        }

        @Override
        public void run(){
            try{
                parseReviews(this.path.toString());
            }
            finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /** This method waits and shuts down the thread operations.*/
    public void shutDownThreads() throws InterruptedException{
        try{
            phaser.awaitAdvance(phaser.getPhase());
        } finally {
            poolOfThreads.shutdown();
            poolOfThreads.awaitTermination(3, TimeUnit.SECONDS);
        }
    }

    /** This method is responsible for parsing hotels form the given file
     * @param filepath the filepath of hotel json file
     * @return Hotel list of hotels
     * */
    public Hotel[] parseHotels(String filepath){

        Gson gson = new Gson();
        String hotelsFilePath = new File(filepath).getAbsolutePath();

        try (FileReader br = new FileReader(hotelsFilePath)) {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(br);
            JsonArray jsonArr = jo.getAsJsonArray("sr");
            Hotel[] hotels = gson.fromJson(jsonArr, Hotel[].class);

        return hotels;

        } catch (IOException e) {
            System.out.println("Could not read the file:" + e);
        }
     return null;
    }

    /** This method is responsible for traversing through review files and submitting worker of each file to a thread.
     * @param filepath the filepath of review json file
     * */
    public void traverseReviewFiles(String directory) {
        if(directory == null)return;
        Path p = Paths.get(directory);
        try (DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(p)) {
            for (Path path : pathsInDir) {
                if(Files.isDirectory(path)){
                    traverseReviewFiles(path.toString());
                }
                if (path.toString().endsWith(".json")){
                    Worker worker = new Worker(path);
                    phaser.register();
                    poolOfThreads.submit(worker);
                }
            }
        } catch (IOException e) {
            System.out.println("Can not open directory: " + directory);
        }
    }

    /** This method is called from the main method regarding initiation the review insertion.
     * @param directory the initial directory to start traversing.
     * */
    public void initiateReviewInsertion(String parentReviewPath){
        traverseReviewFiles(parentReviewPath);
    }

    /** This method is responsible for parsing each review file and inserting it to threadsafe review handler.
     * @param filepath the current filepath of review json file
     * */
    public void parseReviews(String currReviewPath){
        ArrayList<Review> reviews = new ArrayList<>();

            String reviewsFilePath = new File(currReviewPath).getAbsolutePath();

            try (FileReader br = new FileReader(reviewsFilePath)) {
                JsonParser parser = new JsonParser();
                JsonObject jo = (JsonObject) parser.parse(br);
                JsonArray jsonArr = jo.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection").getAsJsonArray("review");

                for(JsonElement e: jsonArr){
                    JsonObject review = e.getAsJsonObject();
                    String hotelId = review.get("hotelId").getAsString();
                    String reviewId = review.get("reviewId").getAsString();
                    int ratingOverall = review.get("ratingOverall").getAsInt();
                    String title = review.get("title").getAsString();
                    String reviewText = review.get("reviewText").getAsString();
                    String nickname = review.get("userNickname").getAsString();
                    String reviewSubmissionTime = review.get("reviewSubmissionTime").getAsString();

                    Review reviewObj = new Review(hotelId, reviewId, ratingOverall, title, reviewText, nickname, reviewSubmissionTime);

                    reviews.add(reviewObj);
                }
            } catch (IOException e) {
                System.out.println("Could not read the file:" + e);
            }
            this.threadSafeReviewHandler.insertReviews(reviews);
    }
}
