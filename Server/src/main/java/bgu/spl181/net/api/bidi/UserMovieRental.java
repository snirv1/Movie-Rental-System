package bgu.spl181.net.api.bidi;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * represent the user of movie service rental
 */
public class UserMovieRental extends User{

    private String country;
    private long balance;
    private  List<BaseMovie> movies;



    public UserMovieRental(String userName, String password, String type , int connectionId, String country, int balance, List<BaseMovie> moviesList) { //TODO connection id where to initiate
        super(userName, password, type , connectionId);
        this.country = country;
        this.balance = balance;
        this.movies = moviesList;

    }

    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<BaseMovie> getMoviesList() {
        return movies;
    }

    public void setMoviesList(List<BaseMovie> moviesList) {
        this.movies = moviesList;
    }

    protected long addBalance(long balanceToAdd){
        return balance + balanceToAdd;

    }

    public boolean isAdmin(){
        if(type.equals("admin")){
            return true;
        }else {
            return false;}
    }

    public boolean isRentingMovie(String movieName){
        Optional<BaseMovie> movieOptional = movies.stream().filter((m)-> m.getName().equals(movieName)).findAny();
        if(movieOptional.isPresent()){
            return true;
        }
        else {
            return false;
        }
    }
    protected BaseMovie getBaseMovieFromListByMovieName(String movieName) {//TODO
        Optional<BaseMovie> movieOptional = movies.stream().filter((m) -> m.getName().equals(movieName)).findAny();
        if (movieOptional.isPresent()) {
            return movieOptional.get();
        } else {
            return null;
        }
    }
}