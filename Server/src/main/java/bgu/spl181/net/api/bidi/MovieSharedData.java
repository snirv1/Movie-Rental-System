package bgu.spl181.net.api.bidi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * a class that contain thecurrent data of the movie rental system.
 * this class received a command from the protocol and exectuing them
 * this class aslo update the json files each time a REQUEST is exectuing
 */
public class MovieSharedData extends SharedData{

    CopyOnWriteArrayList<Movie> movieList;
    Object lock;
    Object lockUpdateServiceJson;
    Object lockUpdateUserJson;

    /**
     * constructor
     * @param userMovieRentalMap map of user by username
     * @param movieList a list of movie list
     */
    public MovieSharedData(ConcurrentHashMap<String,User> userMovieRentalMap ,CopyOnWriteArrayList<Movie> movieList) {
        super(userMovieRentalMap);
        this.movieList = movieList;
        this.lock = new Object();
        this.lockUpdateUserJson = new Object();
        this.lockUpdateServiceJson = new Object();

    }

    /**
     *
     * @param connectionId
     * @return if the connectionId is logged in in the system
     */
    protected boolean isLoggedIn(Integer connectionId){
        if (mapOfLoggedInUsersByConnectedIds.containsKey(connectionId)) {
            return true;
        }
        else {return false;}
    }

    /**
     *
     * @param connectionId
     * @return if the connection id that related to the username is the admin
     */
    protected boolean isAdmin(Integer connectionId){
        if(isLoggedIn(connectionId) && ((UserMovieRental)mapOfLoggedInUsersByConnectedIds.get(connectionId)).isAdmin()){
            return true;
        }else {return false;}
    }


    /**
     * execute balance info request
     * @param connectionId of the usename related to the connection id
     * @return the message to return to the connection id
     */
    protected String commandRequestBalanceInfo(Integer connectionId) {
        UserMovieRental user = (UserMovieRental)mapOfLoggedInUsersByConnectedIds.get(connectionId);
        long userBalance = user.getBalance();
        return "ACK balance " + userBalance;
    }


    /**
     * execute balance add request
     * @param connectionId of the username related to the connection id
     * @param amount of the copies that will added
     * @return  the message to return to the connection id
     */
    protected String commandRequestBalanceAdd(Integer connectionId, String amount) {
        Long amountAslong = Long.decode(amount);
        UserMovieRental user = (UserMovieRental)mapOfLoggedInUsersByConnectedIds.get(connectionId);
        long newBalance = user.addBalance(amountAslong);
        user.setBalance(newBalance);
        updateUserJson();
        updateServiceJson();
        return  "ACK balance " + newBalance + " added " + amount;
    }

    /**
     * execute movie info request
     * @param movieName the movie that his info will send
     * @return the message to send to the client
     */
    protected String commandRequestMovieInfo(String movieName) {
        String ret;
        if(movieName == null){
            ret = "\"";
            for (Movie movie : movieList){
                ret =ret + movie.getName() + "\" \"";
            }
            ret = ret.substring(0, ret.length() -2);
            return "ACK info " + ret ;
        }
        else {
            for (Movie movie : movieList){
                if(movie.getName().equals(movieName)){
                    ret = movie.toString();
                    return "ACK info " + ret  ;
                }
            }
            return "ERROR request info failed";
        }
    }


    /**
     * execute movie rent request
     * @param connectionId of the username related to the connection id
     * @param movieName to be rent
     * @return string array that will contain message to client and the broadcast message
     */

    protected String[] commandRequestMovieRent(Integer connectionId ,String movieName) {
        String[] result= new String[2];
        UserMovieRental user = (UserMovieRental) mapOfLoggedInUsersByConnectedIds.get(connectionId);
        Movie movie = getMovieFromListByMovieName(movieName);
        synchronized (lock){
        if (movie == null || movie.bannedCountries.contains(user.getCountry()) ||
                user.isRentingMovie(movieName) ||
               user.getBalance() < movie.getPrice()) {
            result[0]="ERROR request rent failed";
            return result;
        }
       // while(!movie.lock.compareAndSet(false,true));
        if (movie.getAvailableAmount() == 0){
            result[0]="ERROR request rent failed";
            return result;
        }else {
            user.getMoviesList().add(movie);
            user.setBalance(user.getBalance() - movie.getPrice());
            movie.setAvailableAmount(movie.getAvailableAmount() - 1);
          //  movie.lock.set(false);
            updateUserJson();
            updateServiceJson();
            result[0]="ACK rent " +"\"" + movieName + "\"" + " success";
            result[1] = "BROADCAST movie " + "\"" + movie.getName() + "\"" +" "+ movie.getAvailableAmount()+" "+ movie.getPrice();
            return result;
        }
        }
    }

    /**
     * execute movie return request
     * @param connectionId of the username related to the connection id
     * @param movieName to be rent
     * @return string array that will contain message to client and the broadcast message
     */

    protected String[] commandRequestReturnMovie(Integer connectionId, String movieName) {
        String[] result= new String[2];
        synchronized (lock) {
            UserMovieRental user = (UserMovieRental) mapOfLoggedInUsersByConnectedIds.get(connectionId);
            Movie movie = getMovieFromListByMovieName(movieName);
            if (movie == null || !user.isRentingMovie(movieName)) {
                result[0]="ERROR request return failed";
                return result;
            } else {
                BaseMovie baseMovie =  user.getBaseMovieFromListByMovieName(movieName);
                user.getMoviesList().remove(baseMovie);
                movie.setAvailableAmount(movie.getAvailableAmount() + 1);
                updateUserJson();
                updateServiceJson();
                result[0]= "ACK return " + "\"" + movieName + "\""+ " success";
                result[1] = "BROADCAST movie " + "\"" + movie.getName() + "\"" +" "+ movie.getAvailableAmount()+" "+ movie.getPrice();
                return result;
            }
        }
    }
    /**
     * execute add movie by admin request
     * @param connectionId of the username related to the connection id
     * @param movieName to be rent
     * @param amount the amount of the movie's copy to be added
     * @param price the price of the movie
     * @param bannedCountry a list of the banned country -optional
     * @return string array that will contain message to client and the broadcast message
     */

    protected String[] commandRequestAdminAddMovie(Integer connectionId, String movieName , int amount , int price , List<String> bannedCountry) {
        synchronized (lock) {
            String[] result= new String[2];
            Movie movie = getMovieFromListByMovieName(movieName);
            if (!isAdmin(connectionId) || amount <= 0 || price <= 0 || movie != null) {
                result[0]="ERROR request addmovie failed";
                return result ;
            } else {
                long id = 0;
                OptionalLong optionalId = movieList.stream()
                        .mapToLong(m -> m.getId())
                        .max();
                if (optionalId.isPresent()) {
                    id = optionalId.getAsLong();
                }
                Movie movieToAdd = new Movie(id + 1, movieName, price, bannedCountry, amount);
                movieList.add(movieToAdd);
                updateUserJson();
                updateServiceJson();
                result[0]= "ACK addmovie " + "\"" + movieName + "\""+ " success";
                result[1] = "BROADCAST movie " + "\"" + movieToAdd.getName() + "\"" +" "+ movieToAdd.getAvailableAmount()+" "+ movieToAdd.getPrice();
                return result;
            }
        }
    }

    /**
     * execute remove movie by admin
     * @param connectionId the client connection id
     * @param movieName the movie to be remove
     * @return string array that will contain message to client and the broadcast message
     */
    protected String[] commandRequestAdminRemmovie(Integer connectionId,String movieName) {
        synchronized (lock) {
            String[] result= new String[2];
            Movie movie = getMovieFromListByMovieName(movieName);
            if (movie == null || !isAdmin(connectionId)) {
                result[0]="ERROR request remmovie failed";
                return result;
            }
           // while (!movie.lock.compareAndSet(false, true)) ;
            if (movie.getAvailableAmount() != movie.getTotalAmount()) {
                result[0]="ERROR request remmovie failed";
                return result;
            }
            movieList.remove(movie);
          //  movie.lock.set(false);
            updateUserJson();
            updateServiceJson();
            result[0]= "ACK remmovie " + "\"" + movieName + "\""+ " success";
            result[1] = "BROADCAST movie " + "\"" + movie.getName() + "\""+" "+"removed";
            return result;

        }
    }

    /**
     * execute change price by admin request
     * @param connectionId the client connection id
     * @param movieName the movie to be remove
     * @param price the new price
     * @return string array that will contain message to client and the broadcast message
     */
    protected String[] commandRequestAdminChangePrice(Integer connectionId , String movieName , int price) {
        synchronized (lock) {
            String[] result= new String[2];
            Movie movie = getMovieFromListByMovieName(movieName);
            if (movie == null || !isAdmin(connectionId) || price <= 0) {
                result[0]="ERROR request changeprice failed";
                return result;
            }
           // while (!movie.lock.compareAndSet(false, true)) ;
            movie.setPrice(price);
            //movie.lock.set(false);
            updateUserJson();
            updateServiceJson();
            result[0]= "ACK changeprice " + "\"" + movieName + "\"" + " success";
            result[1] = "BROADCAST movie " + "\"" + movie.getName() + "\"" +" "+ movie.getAvailableAmount()+" "+ movie.getPrice();
            return result;
        }
    }

    /**
     * check if the data block is legal according to the protocol
     * @param dataBlock
     * @return true is legal or false else
     */
    @Override
    protected boolean isValidDataBlock(String dataBlock) {
        if (dataBlock== null){return false;}
        String[] msg = dataBlock.split("=");
        if(msg.length == 2 && msg[0].equals("country")){return true;}
        else {return false;}
    }

    /**
     *
     * @param username the user name to add
     * @param password the new password
     * @param connectionId  the client connection id
     * @param dataBlock the user data block
     */
    @Override
    protected void addUser(String username , String password, int connectionId, String dataBlock) {
        String[] msg = dataBlock.split("=");
        String country = msg[1].substring(1,msg[1].length()-1);
        UserMovieRental userToAdd = new UserMovieRental(username, password, "normal" , connectionId , country, 0 , new LinkedList<>());
        mapOfRegisteredUsersByUsername.put(username,userToAdd);

    }


    /**
     *
     * @param movieName
     * @return return the {@link Movie related to the movie name
     */
    protected Movie getMovieFromListByMovieName(String movieName) {
        Optional<Movie> movieOptional = movieList.stream().filter((m) -> m.getName().equals(movieName)).findAny();
        if (movieOptional.isPresent()) {
            return movieOptional.get();
        } else {
            return null;
        }
    }

    /**
     * update the json movie file
     */
    @Override
    public  void updateServiceJson(){
        synchronized (lockUpdateServiceJson) {
            try (PrintWriter printer = new PrintWriter("Database/Movies.json")) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
                Gson writer = gsonBuilder.create();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("movies", writer.toJsonTree(movieList));
                printer.print(jsonObject);
            } catch (IOException ex) {
            }
        }
    }

    /**
     * update the user json file
     */
    @Override
    public  void updateUserJson()  {
        synchronized (lockUpdateUserJson) {
            try (PrintWriter printer = new PrintWriter("Database/Users.json")) {
                ArrayList<User> userAsArray = new ArrayList<>();
                for (Map.Entry<String, User> entry : mapOfRegisteredUsersByUsername.entrySet()) {
                    userAsArray.add(entry.getValue());
                }
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
                Gson writer = gsonBuilder.create();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("users", writer.toJsonTree(userAsArray));
                printer.print(jsonObject);
            } catch (IOException ex) {
            }
        }
    }

    public List<Movie> getMovieList() {
        return movieList;
    }
}