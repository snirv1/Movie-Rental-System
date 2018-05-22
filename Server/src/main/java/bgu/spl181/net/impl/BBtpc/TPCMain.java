package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.MessageEncoderDecoderImpl;
import bgu.spl181.net.api.bidi.*;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TPCMain {
    public static void main(String[] args) {
        try {
           // System.out.println( "main :"+ InetAddress.getLocalHost());
        }catch (Exception e){}
        MovieSharedData movieSharedData = ReadFromJson();
        Server tpcServer = Server.threadPerClient(
                Integer.decode(args[0]).intValue(),
                ()-> new MovieRentalProtocol(movieSharedData),
                ()-> new MessageEncoderDecoderImpl<>()
        );
        tpcServer.serve();


    }



    public static MovieSharedData ReadFromJson() {
        JsonUsers jsonUsers;
        JsonMovies jsonMovies;
        Gson gson = new Gson();
        try {
            FileReader userReader = new FileReader("Database/Users.json");
            FileReader movieSReader = new FileReader("Database/Movies.json");
            jsonUsers = gson.fromJson(userReader, JsonUsers.class);
            jsonMovies = gson.fromJson(movieSReader, JsonMovies.class);

            CopyOnWriteArrayList<UserMovieRental> usersList = jsonUsers.getUsers();
            CopyOnWriteArrayList<Movie> moviesList = jsonMovies.getMovies();
            ConcurrentHashMap usersMap = new ConcurrentHashMap();
            for (UserMovieRental user : usersList) {
                usersMap.put(user.getUserName(), user);
            }
            MovieSharedData movieSharedData = new MovieSharedData(usersMap, moviesList);
            return movieSharedData;
        } catch (FileNotFoundException ex) {}
        return null;
    }

}
