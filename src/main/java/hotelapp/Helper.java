package hotelapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.RecursiveAction;

public class Helper {

    /**
     * This method return the count of word in a string after removing punctuation.
     * @param line the parent sentence
     * @param checkWord the word for which we need to find the frequency.
     * @return frequency of the checkWord in the line.
     * */
    public static int countWords(String line, String checkWord){
        line = line.replaceAll("\\p{Punct}", " ");
        int cnt = 0;
        for(String word: line.split(" ")){
            if(word.equalsIgnoreCase(checkWord)){
                cnt++;
            }
        }
        return cnt;
    };

    /**
     * This method writes the str in the filename.
     * @param filename
     * @param str the data which needs to be written.
     * */
    public static void writeFile(String filename, String str){
        try {
            FileWriter myWriter = new FileWriter(filename, true);
            myWriter.append(str);
            myWriter.close();
        }catch (Exception e){
            System.out.println("Error occurred while writing.");
        }
    }

    /**
     * This method creates the ouput file.
     * @param outputFile the output file name.
     * */
    public static void createOutputFiles(String outputFile){

        File myObj1 = new File(outputFile);

        try {
            myObj1.createNewFile();

            PrintWriter writer1 = new PrintWriter(outputFile);
            writer1.close();


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}


