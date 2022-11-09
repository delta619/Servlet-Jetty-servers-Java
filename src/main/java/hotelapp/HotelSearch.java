package hotelapp;

import servers.httpServer.HotelHandler;
import servers.httpServer.HttpServer;
import servers.jettyServer.JettyServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** The driver class for project 4
 * Copy your project 3 classes to the hotelapp package.
 * The main function should take the following command line arguments:
 * -hotels hotelFile -reviews reviewsDirectory -threads numThreads -output filepath
 * (order may be different)
 * and read general information about the hotels from the hotelFile (a JSON file),
 * as read hotel reviews from the json files in reviewsDirectory (can be multithreaded or
 * single-threaded).
 * The data should be loaded into data structures that allow efficient search.
 * If the -output flag is provided, hotel information (about all hotels and reviews)
 * should be output into the given file.
 * Then in the main method, you should start an HttpServer that responds to
 * requests about hotels and reviews.
 * See pdf description of the project for the requirements.
 * You are expected to add other classes and methods from project 3 to this project,
 * and take instructor's / TA's feedback from a code review into account.
 * Please download the input folder from Canvas.
 */

public class HotelSearch {


    public static void main(String[] args) {

        Map<String, String> arg_map = handleCommandLineArgs(args);  // arguments handled
        int threads = Integer.parseInt(arg_map.get("-threads"));

        ThreadSafeHotelHandler hotelHandler = new ThreadSafeHotelHandler();
        ThreadSafeReviewHandler reviewHandler = new ThreadSafeReviewHandler();

        FileProcessor fp = new FileProcessor(reviewHandler, threads);

        Hotel[] hotels = fp.parseHotels(arg_map.get("-hotels"));
        hotelHandler.insertHotels(hotels);

        fp.initiateReviewInsertion(arg_map.get("-reviews"));


        if (!(arg_map.get("-reviews") == null)) {
            fp.initiateReviewInsertion(arg_map.get("-reviews"));
            try {
                fp.shutDownThreads();
            } catch (Exception e) {
                System.out.println("Some error in threads");
            }
        }


        if (arg_map.get("-output") == null) {
            reviewHandler.setUpWords();
//            runJettyServer(hotelHandler, reviewHandler, threads);
            runHttpServer(hotelHandler, reviewHandler, threads);

        } else {
            String outputFile = arg_map.get("-output");
            Helper.createOutputFiles(outputFile);
            hotelHandler.writeOutput(reviewHandler, outputFile);
        }

    }

    /**
     * This method is responsible for handling the command line argument passed.
     *
     * @param args the current filepath of review json file
     */
    static Map<String, String> handleCommandLineArgs(String[] args) {
        Map<String, String> arg_map = new HashMap<>();
        try {
            for (int i = 0; i < args.length; i += 2) {
                if (args[i].startsWith("-")) {
                    arg_map.put(args[i], args[i + 1]);
                }
            }
            if (!arg_map.containsKey("-threads")) {
                arg_map.put("-threads", "1");
            }
            if (!arg_map.containsKey("-output")) {
                arg_map.put("-output", null);
            }
            if (!arg_map.containsKey("-reviews")) {
                arg_map.put("-reviews", null);
            }
            if (arg_map.get("-hotels") == null) {
                throw new Exception("Please enter hotel file name.");
            }
        } catch (Exception e) {
            System.out.println("Invalid arguments, please try again.");
        }
        return arg_map;
    }

    /**
     * This method is responsible for processing user queries and process user input for each query.
     *
     * @param hotelHandler  hotelHandler
     * @param reviewHandler reviewHandler
     */
    public static void processUserQueries(ThreadSafeHotelHandler hotelHandler, ThreadSafeReviewHandler reviewHandler) {
        try {
            Scanner sc = new Scanner(System.in);
            do {
                System.out.println("\nPlease enter any of the below instructions.\nfind <hotelID>, findReviews <hotelID>, findWord <word>  or press Q to quit.");
                String[] instruction = sc.nextLine().split(" ");
                if (instruction.length == 2) {
                    switch (instruction[0]) {
                        case "f":
                            hotelHandler.displayHotel(hotelHandler.findHotelId(instruction[1]));
                            break;
                        case "r":
                            ReviewHandler.displayReviews(reviewHandler.findReviewsByHotelId(instruction[1], false));
                            break;
                        case "w":
                            reviewHandler.findWords(instruction[1]);
                            break;
                        default:
                            System.out.println("Please enter a valid instruction.");
                    }
                } else {
                    if (instruction.length == 1 && instruction[0].equalsIgnoreCase("q")) {
                        System.out.println("Good bye.");
                        return;
                    }
                    System.out.println("Please enter a valid instruction.");
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    public static void runJettyServer(ThreadSafeHotelHandler tsHotelHandler, ThreadSafeReviewHandler tsReviewHandler, int threads) {
        try {
            JettyServer server = new JettyServer(tsHotelHandler, tsReviewHandler);
            server.start();
        } catch (Exception e) {
            System.out.println("Error in starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runHttpServer(ThreadSafeHotelHandler tsHotelHandler, ThreadSafeReviewHandler tsReviewHandler, int threads) {
        try {
            HttpServer server = new HttpServer(threads);
            server.addMapping("/hotel", HotelHandler.class.toString());
            server.start();
        } catch (Exception e) {
            System.out.println("Error in starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}