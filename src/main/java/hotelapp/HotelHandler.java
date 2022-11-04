package hotelapp;

import java.util.*;

public class HotelHandler {
    private Map<String, Hotel> hotelMap = new TreeMap<>(String::compareTo);

    public void insertHotels(Hotel[] hotels){

        for(Hotel hotel: hotels){
            this.hotelMap.put(hotel.getId(), hotel);
        }
    }

    public Hotel findHotelId(String hotelId){

        Hotel hotel = this.hotelMap.get(hotelId);
        return hotel;
    }
    public void writeOutput(ReviewHandler reviewHandlerOld, String outputFileName){
        for(String hotelId: this.hotelMap.keySet()){
            Helper.writeFile(outputFileName, this.hotelMap.get(hotelId).toString());
                for(Review review: reviewHandlerOld.findReviewsByHotelId(hotelId, true)){
                    Helper.writeFile(outputFileName, review.toString());
                }
                    Helper.writeFile(outputFileName, System.lineSeparator());
        }
    }

    /**
     * This method responsible for displaying the hotel.
     * @param hotel of the hotel.
     * */
    public void displayHotel(Hotel hotel){
        if(hotel == null){
            System.out.println("No hotel found");
            return;
        }
        String result = "";

        result += ("The hotel details are: "+System.lineSeparator());
        result += hotel.getName() + System.lineSeparator();
        result += hotel+System.lineSeparator();
        System.out.println(result);

    }
}
