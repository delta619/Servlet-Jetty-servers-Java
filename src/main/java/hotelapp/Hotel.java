package hotelapp;

import com.google.gson.annotations.SerializedName;

public class Hotel implements Comparable<Hotel> {

    @SerializedName("f")
    private String name;
    private String id;
    @SerializedName("ad")
    private String address;
    @SerializedName("ci")
    private String city;

    @SerializedName("pr")
    private String state;

    private Double latitude;

    private Double longitude;

    public Hotel(String name, String id, String address, Double lat, Double lng, String city, String state){
        this.name = name;
        this.id = id;
        this.address = address;
        this.latitude = lat;
        this.longitude = lng;
        this.city = city;
        this.state = state;

    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }
    public Double getLatitude(){
        return this.latitude;
    }
    public Double getLongitude(){
        return this.longitude;
    }
    public String getAddress(){
        return this.address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    @Override
    public int compareTo(Hotel hotel) {
        return getId().compareTo(hotel.getId());
    }

    @Override
    public String toString() {
        String result = System.lineSeparator() + "********************";
        result += System.lineSeparator() + getName() + ": " + getId();
        result += System.lineSeparator() + getAddress();
        result += System.lineSeparator() + getCity() + ", " + getState();
        return result;
//        ********************
//        Hilton San Francisco Union Square: 25622
//        333 O'Farrell St.
//        San Francisco, CA

    }
}
