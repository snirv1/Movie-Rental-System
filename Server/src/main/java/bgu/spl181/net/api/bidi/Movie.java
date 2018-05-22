package bgu.spl181.net.api.bidi;


import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *
 * object of a movie
 */
public class Movie extends  BaseMovie{

    protected long price;
    protected List<String> bannedCountries;
    protected  long availableAmount;//TODO need to write the int into String to json
    protected  long totalAmount;//TODO need to write the int into String to json
    protected transient AtomicBoolean lock;

    /**
     *
     * @param id movie id
     * @param name movie name
     * @param price movie price
     * @param bannedCountries a list of banned country for this movie
     * @param totalAmount represent the number of the amount of copies
     */
    public Movie(long id, String name, Integer price, List<String> bannedCountries, int totalAmount) {
        super(id ,name);
        this.price = price;
        this.availableAmount = totalAmount;
        this.totalAmount =totalAmount;
        this.bannedCountries = bannedCountries;
        this.lock = new AtomicBoolean(false);
    }

    /**
     * override the mothod toString
     *
     * @return return a string that describe the movie as required
     */
    @Override
    public String toString() {
        if(bannedCountries != null && !bannedCountries.isEmpty()) {
            String bannedCountries = "";
            for (String country : this.bannedCountries) {
                bannedCountries = bannedCountries + "\""+ country +"\"" + " ";
            }
            bannedCountries = bannedCountries.substring(0, bannedCountries.length() - 1);
            return  "\"" +name +"\""+ " "  +availableAmount  + " " + price   +" "+ bannedCountries;
        }
        else {return    "\"" +name +"\""+ " "  +availableAmount  + " " + price ;}

        }


    public long getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public void setBannedCountries(List<String> bannedCountries) {
        this.bannedCountries = bannedCountries;
    }

    public long getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(long availableAmount) {
        this.availableAmount = availableAmount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
