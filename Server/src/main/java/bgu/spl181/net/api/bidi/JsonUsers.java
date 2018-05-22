package bgu.spl181.net.api.bidi;


import java.util.concurrent.CopyOnWriteArrayList;

public class JsonUsers {

    /**
     * This class use to read the input from the Json file.
     */


    public CopyOnWriteArrayList<UserMovieRental> users;




    public void setUsers(CopyOnWriteArrayList<UserMovieRental> users) {
        this.users = users;
    }




    public CopyOnWriteArrayList<UserMovieRental> getUsers() {
            return users;
        }

}
