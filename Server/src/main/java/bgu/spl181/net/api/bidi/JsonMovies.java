package bgu.spl181.net.api.bidi;


import java.util.concurrent.CopyOnWriteArrayList;

public class JsonMovies {

    /**
     * This class use to read the input from the Json file.
     */


    public CopyOnWriteArrayList<Movie> movies;


    public CopyOnWriteArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(CopyOnWriteArrayList<Movie> movies) {
        this.movies = movies;
    }

}


